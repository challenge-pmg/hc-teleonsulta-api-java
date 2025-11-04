package br.com.pmg.hc.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

import br.com.pmg.hc.dao.ConsultaDAO;
import br.com.pmg.hc.dao.PacienteDAO;
import br.com.pmg.hc.dao.ProfissionalDAO;
import br.com.pmg.hc.dto.ConsultaRequest;
import br.com.pmg.hc.dto.ConsultaResponse;
import br.com.pmg.hc.dto.ConsultaStatusRequest;
import br.com.pmg.hc.exception.BusinessException;
import br.com.pmg.hc.exception.ResourceNotFoundException;
import br.com.pmg.hc.model.Consulta;
import br.com.pmg.hc.model.Paciente;
import br.com.pmg.hc.model.Profissional;
import br.com.pmg.hc.model.Role;
import br.com.pmg.hc.model.StatusConsulta;
import br.com.pmg.hc.model.TipoConsulta;
import br.com.pmg.hc.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ConsultaService {

    @Inject
    ConsultaDAO consultaDAO;

    @Inject
    PacienteDAO pacienteDAO;

    @Inject
    ProfissionalDAO profissionalDAO;

    @Transactional
    public ConsultaResponse criar(Usuario solicitante, ConsultaRequest request) {
        var paciente = obterPaciente(request.pacienteId());
        var profissional = obterProfissional(request.profissionalId());

        garantirPermissaoCriarConsulta(solicitante, paciente);
        validarDisponibilidade(paciente, profissional, request.dataHora(), null);
        var link = normalizarLink(request.tipoConsulta(), request.linkAcesso());

        var consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setProfissional(profissional);
        consulta.setUsuarioAgendador(solicitante);
        consulta.setDataHora(request.dataHora());
        consulta.setTipoConsulta(request.tipoConsulta());
        consulta.setLinkAcesso(link);
        consulta.setStatus(StatusConsulta.AGENDADA);

        consultaDAO.persist(consulta);
        return ConsultaRepresentationBuilder.build(consulta, solicitante);
    }

    @Transactional
    public ConsultaResponse atualizar(Long id, Usuario solicitante, ConsultaRequest request) {
        var consulta = obterConsulta(id);
        var paciente = obterPaciente(request.pacienteId());
        var profissional = obterProfissional(request.profissionalId());

        garantirPermissaoAtualizarConsulta(solicitante, consulta);
        validarDisponibilidade(paciente, profissional, request.dataHora(), consulta.getId());
        var link = normalizarLink(request.tipoConsulta(), request.linkAcesso());

        consulta.setPaciente(paciente);
        consulta.setProfissional(profissional);
        consulta.setDataHora(request.dataHora());
        consulta.setTipoConsulta(request.tipoConsulta());
        consulta.setLinkAcesso(link);

        var atualizado = consultaDAO.merge(consulta);
        return ConsultaRepresentationBuilder.build(atualizado, solicitante);
    }

    @Transactional
    public ConsultaResponse atualizarStatus(Long id, Usuario solicitante, ConsultaStatusRequest request) {
        var consulta = obterConsulta(id);
        var novoStatus = request.status();

        if (novoStatus == null) {
            throw new BusinessException("Status da consulta obrigatorio");
        }

        if (consulta.getStatus() == novoStatus) {
            return ConsultaRepresentationBuilder.build(consulta, solicitante);
        }

        garantirPermissaoAlterarStatus(solicitante, consulta, novoStatus);

        if (consulta.getStatus() == StatusConsulta.CANCELADA || consulta.getStatus() == StatusConsulta.FALTOU) {
            throw new BusinessException("Nao e possivel alterar o status apos cancelamento ou falta");
        }

        consulta.setStatus(novoStatus);
        var atualizado = consultaDAO.merge(consulta);
        return ConsultaRepresentationBuilder.build(atualizado, solicitante);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public ConsultaResponse buscarPorId(Long id, Usuario solicitante) {
        var consulta = obterConsulta(id);
        garantirPermissaoVisualizarConsulta(solicitante, consulta);
        return ConsultaRepresentationBuilder.build(consulta, solicitante);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ConsultaResponse> listarTodas(Usuario solicitante) {
        return switch (solicitante.getRole()) {
            case ADMIN -> consultaDAO.findAll().stream()
                    .map(c -> ConsultaRepresentationBuilder.build(c, solicitante))
                    .toList();
            case PACIENTE -> pacienteDAO.findByUsuarioId(solicitante.getId())
                    .map(p -> consultaDAO.findByPaciente(p.getId()).stream()
                            .map(c -> ConsultaRepresentationBuilder.build(c, solicitante))
                            .toList())
                    .orElseGet(List::of);
            case PROFISSIONAL -> profissionalDAO.findByUsuarioId(solicitante.getId())
                    .map(p -> consultaDAO.findByProfissional(p.getId()).stream()
                            .map(c -> ConsultaRepresentationBuilder.build(c, solicitante))
                            .toList())
                    .orElseGet(List::of);
        };
    }

    @Transactional
    public void remover(Long id, Usuario solicitante) {
        garantirRole(solicitante, Role.ADMIN);
        var consulta = obterConsulta(id);
        consultaDAO.delete(consulta);
    }

    private void garantirPermissaoCriarConsulta(Usuario solicitante, Paciente paciente) {
        if (solicitante.getRole() == Role.ADMIN) {
            return;
        }
        if (solicitante.getRole() == Role.PACIENTE && paciente.getUsuario().getId().equals(solicitante.getId())) {
            return;
        }
        throw new BusinessException("Usuario sem permissao para agendar a consulta");
    }

    private void garantirPermissaoAtualizarConsulta(Usuario solicitante, Consulta consulta) {
        if (solicitante.getRole() == Role.ADMIN) {
            return;
        }
        if (consulta.getUsuarioAgendador() != null && consulta.getUsuarioAgendador().getId().equals(solicitante.getId())) {
            return;
        }
        throw new BusinessException("Usuario sem permissao para atualizar a consulta");
    }

    private void garantirPermissaoAlterarStatus(Usuario solicitante, Consulta consulta, StatusConsulta novoStatus) {
        if (solicitante.getRole() == Role.ADMIN) {
            return;
        }

        if (solicitante.getRole() == Role.PACIENTE) {
            var paciente = consulta.getPaciente();
            if (!paciente.getUsuario().getId().equals(solicitante.getId())) {
                throw new BusinessException("Paciente nao pode alterar consultas de terceiros");
            }
            if (novoStatus != StatusConsulta.CANCELADA) {
                throw new BusinessException("Paciente so pode cancelar a propria consulta");
            }
            return;
        }

        if (solicitante.getRole() == Role.PROFISSIONAL) {
            var profissional = consulta.getProfissional();
            if (!profissional.getUsuario().getId().equals(solicitante.getId())) {
                throw new BusinessException("Profissional nao pode alterar consultas de terceiros");
            }
            if (novoStatus != StatusConsulta.REALIZADA && novoStatus != StatusConsulta.FALTOU) {
                throw new BusinessException("Profissional so pode informar realizacao ou falta");
            }
            return;
        }

        throw new BusinessException("Usuario sem permissao para alterar o status");
    }

    private void garantirPermissaoVisualizarConsulta(Usuario solicitante, Consulta consulta) {
        if (solicitante.getRole() == Role.ADMIN) {
            return;
        }
        if (consulta.getPaciente().getUsuario().getId().equals(solicitante.getId())) {
            return;
        }
        if (consulta.getProfissional().getUsuario().getId().equals(solicitante.getId())) {
            return;
        }
        if (consulta.getUsuarioAgendador() != null && consulta.getUsuarioAgendador().getId().equals(solicitante.getId())) {
            return;
        }
        throw new BusinessException("Usuario sem permissao para visualizar a consulta");
    }

    private void validarDisponibilidade(Paciente paciente, Profissional profissional, LocalDateTime dataHora, Long consultaAtualId) {
        var existeConflito = consultaDAO.existsBetweenPacienteEProfissionalEmHorario(
                paciente.getId(),
                profissional.getId(),
                dataHora);

        if (!existeConflito) {
            return;
        }

        if (consultaAtualId == null) {
            throw new BusinessException("Ja existe uma consulta agendada para este horario");
        }

        var consulta = consultaDAO.findById(consultaAtualId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta nao encontrada"));

        if (!consulta.getDataHora().equals(dataHora)
                || !consulta.getPaciente().getId().equals(paciente.getId())
                || !consulta.getProfissional().getId().equals(profissional.getId())) {
            throw new BusinessException("Ja existe uma consulta agendada para este horario");
        }
    }

    private String normalizarLink(TipoConsulta tipoConsulta, String linkAcesso) {
        if (tipoConsulta == TipoConsulta.PRESENCIAL) {
            return null;
        }

        if (linkAcesso == null || linkAcesso.isBlank()) {
            throw new BusinessException("Link de acesso obrigatorio para teleconsulta");
        }

        try {
            var uri = new URI(linkAcesso);
            if (uri.getScheme() == null || uri.getHost() == null) {
                throw new BusinessException("Link de acesso invalido");
            }
        } catch (URISyntaxException ex) {
            throw new BusinessException("Link de acesso invalido");
        }

        return linkAcesso;
    }

    private Paciente obterPaciente(Long id) {
        return pacienteDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente nao encontrado"));
    }

    private Profissional obterProfissional(Long id) {
        return profissionalDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional nao encontrado"));
    }

    private Consulta obterConsulta(Long id) {
        return consultaDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta nao encontrada"));
    }

    private void garantirRole(Usuario usuario, Role rolePermitido) {
        if (usuario.getRole() != rolePermitido) {
            throw new BusinessException("Usuario sem permissao para esta operacao");
        }
    }

}
