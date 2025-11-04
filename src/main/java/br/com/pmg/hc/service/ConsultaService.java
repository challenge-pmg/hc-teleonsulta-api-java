package br.com.pmg.hc.service;

import java.util.List;

import br.com.pmg.hc.dao.ConsultaDAO;
import br.com.pmg.hc.dao.PacienteDAO;
import br.com.pmg.hc.dao.ProfissionalDAO;
import br.com.pmg.hc.dto.ConsultaRequest;
import br.com.pmg.hc.dto.ConsultaResponse;
import br.com.pmg.hc.dto.PacienteResponse;
import br.com.pmg.hc.dto.ProfissionalResponse;
import br.com.pmg.hc.exception.BusinessException;
import br.com.pmg.hc.exception.ResourceNotFoundException;
import br.com.pmg.hc.model.Consulta;
import br.com.pmg.hc.model.StatusConsulta;
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
    public ConsultaResponse criar(ConsultaRequest request) {
        var paciente = pacienteDAO.findById(request.pacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado"));
        var profissional = profissionalDAO.findById(request.profissionalId())
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado"));

        var status = toStatus(request.status());

        if (consultaDAO.existsBetweenPacienteEProfissionalEmHorario(paciente.getId(), profissional.getId(),
                request.dataHora())) {
            throw new BusinessException("Já existe uma consulta agendada para este horário");
        }

        var consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setProfissional(profissional);
        consulta.setDataHora(request.dataHora());
        consulta.setDescricao(request.descricao());
        consulta.setStatus(status);

        consultaDAO.persist(consulta);
        return toResponse(consulta);
    }

    @Transactional
    public ConsultaResponse atualizar(Long id, ConsultaRequest request) {
        var consulta = consultaDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));

        var paciente = pacienteDAO.findById(request.pacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado"));
        var profissional = profissionalDAO.findById(request.profissionalId())
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado"));

        var status = toStatus(request.status());

        if (!consulta.getPaciente().getId().equals(paciente.getId())
                || !consulta.getProfissional().getId().equals(profissional.getId())
                || !consulta.getDataHora().equals(request.dataHora())) {
            if (consultaDAO.existsBetweenPacienteEProfissionalEmHorario(paciente.getId(), profissional.getId(),
                    request.dataHora())) {
                throw new BusinessException("Já existe uma consulta agendada para este horário");
            }
        }

        consulta.setPaciente(paciente);
        consulta.setProfissional(profissional);
        consulta.setDataHora(request.dataHora());
        consulta.setDescricao(request.descricao());
        consulta.setStatus(status);

        var atualizada = consultaDAO.merge(consulta);
        return toResponse(atualizada);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public ConsultaResponse buscarPorId(Long id) {
        var consulta = consultaDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));
        return toResponse(consulta);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ConsultaResponse> listarTodas() {
        return consultaDAO.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public void remover(Long id) {
        var consulta = consultaDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));
        consultaDAO.delete(consulta);
    }

    private StatusConsulta toStatus(String status) {
        try {
            return StatusConsulta.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Status de consulta inválido");
        }
    }

    private ConsultaResponse toResponse(Consulta consulta) {
        var paciente = consulta.getPaciente();
        var profissional = consulta.getProfissional();

        var pacienteResponse = new PacienteResponse(
                paciente.getId(),
                paciente.getNome(),
                paciente.getEmail(),
                paciente.getRole(),
                paciente.getCpf(),
                paciente.getDataNascimento(),
                paciente.getTelefone(),
                paciente.getCriadoEm(),
                paciente.getAtualizadoEm());

        var profissionalResponse = new ProfissionalResponse(
                profissional.getId(),
                profissional.getNome(),
                profissional.getEmail(),
                profissional.getRole(),
                profissional.getEspecialidade(),
                profissional.getRegistroProfissional(),
                profissional.getTelefone(),
                profissional.getCriadoEm(),
                profissional.getAtualizadoEm());

        return new ConsultaResponse(
                consulta.getId(),
                pacienteResponse,
                profissionalResponse,
                consulta.getDataHora(),
                consulta.getDescricao(),
                consulta.getStatus());
    }
}
