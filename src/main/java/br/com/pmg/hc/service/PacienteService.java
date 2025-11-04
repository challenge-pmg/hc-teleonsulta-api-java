package br.com.pmg.hc.service;

import java.util.List;

import br.com.pmg.hc.dao.PacienteDAO;
import br.com.pmg.hc.dto.PacienteRequest;
import br.com.pmg.hc.dto.PacienteResponse;
import br.com.pmg.hc.exception.BusinessException;
import br.com.pmg.hc.exception.ResourceNotFoundException;
import br.com.pmg.hc.model.Paciente;
import br.com.pmg.hc.model.Role;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PacienteService {

    @Inject
    PacienteDAO pacienteDAO;

    @Transactional
    public PacienteResponse criar(PacienteRequest request) {
        pacienteDAO.findByEmail(request.email()).ifPresent(p -> {
            throw new BusinessException("Já existe um paciente cadastrado com este e-mail");
        });

        var paciente = new Paciente();
        paciente.setNome(request.nome());
        paciente.setEmail(request.email());
        paciente.setSenha(request.senha());
        paciente.setRole(Role.PACIENTE);
        paciente.setCpf(request.cpf());
        paciente.setDataNascimento(request.dataNascimento());
        paciente.setTelefone(request.telefone());

        pacienteDAO.persist(paciente);
        return toResponse(paciente);
    }

    @Transactional
    public PacienteResponse atualizar(Long id, PacienteRequest request) {
        var paciente = pacienteDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado"));

        pacienteDAO.findByEmail(request.email()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new BusinessException("Já existe outro paciente com este e-mail");
            }
        });

        paciente.setNome(request.nome());
        paciente.setEmail(request.email());
        paciente.setSenha(request.senha());
        paciente.setRole(Role.PACIENTE);
        paciente.setCpf(request.cpf());
        paciente.setDataNascimento(request.dataNascimento());
        paciente.setTelefone(request.telefone());

        var atualizado = pacienteDAO.merge(paciente);
        return toResponse(atualizado);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public PacienteResponse buscarPorId(Long id) {
        var paciente = pacienteDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado"));
        return toResponse(paciente);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<PacienteResponse> listarTodos() {
        return pacienteDAO.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public void remover(Long id) {
        var paciente = pacienteDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado"));
        pacienteDAO.delete(paciente);
    }

    private PacienteResponse toResponse(Paciente paciente) {
        return new PacienteResponse(
                paciente.getId(),
                paciente.getNome(),
                paciente.getEmail(),
                paciente.getRole(),
                paciente.getCpf(),
                paciente.getDataNascimento(),
                paciente.getTelefone(),
                paciente.getCriadoEm(),
                paciente.getAtualizadoEm());
    }
}
