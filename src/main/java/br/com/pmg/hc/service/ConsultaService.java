package br.com.pmg.hc.service;

import java.util.List;

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

    public List<ConsultaResponse> listarTodas() {
        return consultaDAO.findAll().stream().map(this::toResponse).toList();
    }

    public ConsultaResponse buscarPorId(Long id) {
        var consulta = consultaDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));
        return toResponse(consulta);
    }

    public ConsultaResponse criar(ConsultaRequest request) {
        Paciente paciente = pacienteDAO.findById(request.pacienteId())
                .orElseThrow(() -> new BusinessException("Paciente informado não existe"));
        Profissional profissional = profissionalDAO.findById(request.profissionalId())
                .orElseThrow(() -> new BusinessException("Profissional informado não existe"));

        Usuario usuarioAgendador = null;
        if (request.usuarioAgendadorId() != null) {
            usuarioAgendador = usuarioDAO.findById(request.usuarioAgendadorId())
                    .orElseThrow(() -> new BusinessException("Usuário agendador não encontrado"));
        }

        validarTipoConsulta(request.tipoConsulta(), request.linkAcesso());

        var consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setProfissional(profissional);
        consulta.setUsuarioAgendador(usuarioAgendador);
        consulta.setDataHora(request.dataHora());
        consulta.setTipoConsulta(request.tipoConsulta());
        consulta.setLinkAcesso(request.tipoConsulta() == TipoConsulta.TELECONSULTA ? request.linkAcesso() : null);
        consulta.setStatus(StatusConsulta.AGENDADA);

        consulta = consultaDAO.create(consulta);
        return toResponse(consultaDAO.findById(consulta.getId()).orElse(consulta));
    }

    public ConsultaResponse atualizar(Long id, ConsultaRequest request) {
        var existente = consultaDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));

        Paciente paciente = pacienteDAO.findById(request.pacienteId())
                .orElseThrow(() -> new BusinessException("Paciente informado não existe"));
        Profissional profissional = profissionalDAO.findById(request.profissionalId())
                .orElseThrow(() -> new BusinessException("Profissional informado não existe"));

        Usuario usuarioAgendador = null;
        if (request.usuarioAgendadorId() != null) {
            usuarioAgendador = usuarioDAO.findById(request.usuarioAgendadorId())
                    .orElseThrow(() -> new BusinessException("Usuário agendador não encontrado"));
        }

        validarTipoConsulta(request.tipoConsulta(), request.linkAcesso());

        existente.setPaciente(paciente);
        existente.setProfissional(profissional);
        existente.setUsuarioAgendador(usuarioAgendador);
        existente.setDataHora(request.dataHora());
        existente.setTipoConsulta(request.tipoConsulta());
        existente.setLinkAcesso(request.tipoConsulta() == TipoConsulta.TELECONSULTA ? request.linkAcesso() : null);

        consultaDAO.update(existente);
        return toResponse(consultaDAO.findById(id).orElse(existente));
    }

    public ConsultaResponse atualizarStatus(Long id, ConsultaStatusRequest request) {
        consultaDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));
        consultaDAO.updateStatus(id, request.status());
        return buscarPorId(id);
    }

    public void remover(Long id) {
        consultaDAO.delete(id);
    }

    private void validarTipoConsulta(TipoConsulta tipoConsulta, String linkAcesso) {
        if (tipoConsulta == TipoConsulta.PRESENCIAL && linkAcesso != null) {
            throw new BusinessException("Consultas presenciais não devem possuir link de acesso");
        }
        if (tipoConsulta == TipoConsulta.TELECONSULTA && (linkAcesso == null || linkAcesso.isBlank())) {
            throw new BusinessException("Consultas de teleconsulta devem informar link de acesso");
        }
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
                agendador != null ? agendador.getId() : null,
                agendador != null ? agendador.getNome() : null,
                consulta.getDataHora(),
                consulta.getTipoConsulta(),
                consulta.getTipoConsulta() == TipoConsulta.TELECONSULTA ? consulta.getLinkAcesso() : null,
                consulta.getStatus(),
                consulta.getCriadoEm());
    }
}
