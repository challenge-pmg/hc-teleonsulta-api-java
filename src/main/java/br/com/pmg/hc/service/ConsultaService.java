package br.com.pmg.hc.service;

import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import br.com.pmg.hc.dao.ConsultaDAO;
import br.com.pmg.hc.dao.PacienteDAO;
import br.com.pmg.hc.dao.ProfissionalDAO;
import br.com.pmg.hc.dao.UsuarioDAO;
import br.com.pmg.hc.dto.ConsultaRequest;
import br.com.pmg.hc.dto.ConsultaResponse;
import br.com.pmg.hc.dto.ConsultaStatusRequest;
import br.com.pmg.hc.exception.BusinessException;
import br.com.pmg.hc.exception.ResourceNotFoundException;
import br.com.pmg.hc.model.Consulta;
import br.com.pmg.hc.model.DisponibilidadeAtendimento;
import br.com.pmg.hc.model.Paciente;
import br.com.pmg.hc.model.Profissional;
import br.com.pmg.hc.model.StatusConsulta;
import br.com.pmg.hc.model.TipoConsulta;
import br.com.pmg.hc.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ConsultaService {

    @Inject
    ConsultaDAO consultaDAO;

    @Inject
    PacienteDAO pacienteDAO;

    @Inject
    ProfissionalDAO profissionalDAO;

    @Inject
    UsuarioDAO usuarioDAO;

    @Inject
    DisponibilidadeService disponibilidadeService;

    @ConfigProperty(name = "app.teleconsulta.base-url", defaultValue = "https://meet.hc.com/")
    String teleconsultaBaseUrl;

    public List<ConsultaResponse> listarPorPaciente(Long pacienteId) {
        pacienteDAO.findById(pacienteId)
                .orElseThrow(() -> new BusinessException("Paciente informado nao existe"));
        return consultaDAO.findByPaciente(pacienteId).stream().map(this::toResponse).toList();
    }

    public List<ConsultaResponse> listarPorProfissional(Long profissionalId) {
        profissionalDAO.findById(profissionalId)
                .orElseThrow(() -> new BusinessException("Profissional informado nao existe"));
        return consultaDAO.findByProfissional(profissionalId).stream().map(this::toResponse).toList();
    }

    public ConsultaResponse buscarPorId(Long id) {
        var consulta = consultaDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta n?o encontrada"));
        return toResponse(consulta);
    }

    public ConsultaResponse criar(ConsultaRequest request) {
        Paciente paciente = pacienteDAO.findById(request.pacienteId())
                .orElseThrow(() -> new BusinessException("Paciente informado n?o existe"));
        Profissional profissional = profissionalDAO.findById(request.profissionalId())
                .orElseThrow(() -> new BusinessException("Profissional informado n?o existe"));

        Usuario usuarioAgendador = null;
        if (request.usuarioAgendadorId() != null) {
            usuarioAgendador = usuarioDAO.findById(request.usuarioAgendadorId())
                    .orElseThrow(() -> new BusinessException("Usu?rio agendador n?o encontrado"));
        }

        DisponibilidadeAtendimento disponibilidade = disponibilidadeService.reservar(request.disponibilidadeId());
        validarDisponibilidadeDoProfissional(profissional, disponibilidade);

        var consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setProfissional(profissional);
        consulta.setDisponibilidade(disponibilidade);
        consulta.setUsuarioAgendador(usuarioAgendador);
        consulta.setDataHora(disponibilidade.getDataHora());
        consulta.setTipoConsulta(request.tipoConsulta());
        consulta.setLinkAcesso(gerarLinkSeNecessario(request.tipoConsulta()));
        consulta.setStatus(StatusConsulta.AGENDADA);

        try {
            consulta = consultaDAO.create(consulta);
        } catch (RuntimeException e) {
            disponibilidadeService.liberar(disponibilidade.getId());
            throw e;
        }
        return toResponse(consultaDAO.findById(consulta.getId()).orElse(consulta));
    }

    public ConsultaResponse atualizar(Long id, ConsultaRequest request) {
        var existente = consultaDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta n?o encontrada"));

        Paciente paciente = pacienteDAO.findById(request.pacienteId())
                .orElseThrow(() -> new BusinessException("Paciente informado n?o existe"));
        Profissional profissional = profissionalDAO.findById(request.profissionalId())
                .orElseThrow(() -> new BusinessException("Profissional informado n?o existe"));

        Usuario usuarioAgendador = null;
        if (request.usuarioAgendadorId() != null) {
            usuarioAgendador = usuarioDAO.findById(request.usuarioAgendadorId())
                    .orElseThrow(() -> new BusinessException("Usu?rio agendador n?o encontrado"));
        }

        DisponibilidadeAtendimento disponibilidadeAtual = existente.getDisponibilidade();
        DisponibilidadeAtendimento novaDisponibilidade = null;
        if (!disponibilidadeAtual.getId().equals(request.disponibilidadeId())) {
            novaDisponibilidade = disponibilidadeService.reservar(request.disponibilidadeId());
            validarDisponibilidadeDoProfissional(profissional, novaDisponibilidade);
        }

        existente.setPaciente(paciente);
        existente.setProfissional(profissional);
        existente.setUsuarioAgendador(usuarioAgendador);
        TipoConsulta tipoAnterior = existente.getTipoConsulta();
        existente.setTipoConsulta(request.tipoConsulta());
        if (request.tipoConsulta() == TipoConsulta.TELECONSULTA) {
            if (tipoAnterior != TipoConsulta.TELECONSULTA) {
                existente.setLinkAcesso(gerarLinkSeNecessario(TipoConsulta.TELECONSULTA));
            }
        } else {
            existente.setLinkAcesso(null);
        }

        if (novaDisponibilidade != null) {
            existente.setDisponibilidade(novaDisponibilidade);
            existente.setDataHora(novaDisponibilidade.getDataHora());
        } else {
            existente.setDataHora(disponibilidadeAtual.getDataHora());
        }

        try {
            consultaDAO.update(existente);
            if (novaDisponibilidade != null) {
                disponibilidadeService.liberar(disponibilidadeAtual.getId());
            }
        } catch (RuntimeException e) {
            if (novaDisponibilidade != null) {
                disponibilidadeService.liberar(novaDisponibilidade.getId());
            }
            throw e;
        }
        return toResponse(consultaDAO.findById(id).orElse(existente));
    }

    public ConsultaResponse atualizarStatus(Long id, ConsultaStatusRequest request) {
        var consulta = consultaDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta n?o encontrada"));
        StatusConsulta novoStatus = request.status();
        consultaDAO.updateStatus(id, novoStatus);
        if (novoStatus == StatusConsulta.CANCELADA && consulta.getStatus() != StatusConsulta.CANCELADA) {
            disponibilidadeService.liberar(consulta.getDisponibilidade().getId());
        }
        return buscarPorId(id);
    }

    public void remover(Long id) {
        var consulta = consultaDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta n?o encontrada"));
        consultaDAO.delete(id);
        disponibilidadeService.liberar(consulta.getDisponibilidade().getId());
    }

    private void validarDisponibilidadeDoProfissional(Profissional profissional,
            DisponibilidadeAtendimento disponibilidade) {
        if (!disponibilidade.getProfissional().getId().equals(profissional.getId())) {
            disponibilidadeService.liberar(disponibilidade.getId());
            throw new BusinessException("Disponibilidade nao pertence ao profissional informado");
        }
        disponibilidade.setProfissional(profissional);
    }

    private String gerarLinkSeNecessario(TipoConsulta tipoConsulta) {
        if (tipoConsulta != TipoConsulta.TELECONSULTA) {
            return null;
        }
        String base = teleconsultaBaseUrl.endsWith("/") ? teleconsultaBaseUrl : teleconsultaBaseUrl + "/";
        return base + UUID.randomUUID();
    }

    private ConsultaResponse toResponse(Consulta consulta) {
        var paciente = consulta.getPaciente();
        var profissional = consulta.getProfissional();
        var agendador = consulta.getUsuarioAgendador();
        return new ConsultaResponse(
                consulta.getId(),
                paciente.getId(),
                paciente.getUsuario().getNome(),
                profissional.getId(),
                profissional.getUsuario().getNome(),
                consulta.getDisponibilidade().getId(),
                agendador != null ? agendador.getId() : null,
                agendador != null ? agendador.getNome() : null,
                consulta.getDataHora(),
                consulta.getTipoConsulta(),
                consulta.getLinkAcesso(),
                consulta.getStatus(),
                consulta.getCriadoEm());
    }
}
