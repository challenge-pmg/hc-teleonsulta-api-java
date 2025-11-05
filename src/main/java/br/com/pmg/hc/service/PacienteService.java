package br.com.pmg.hc.service;

import java.util.List;

import br.com.pmg.hc.dao.ConsultaDAO;
import br.com.pmg.hc.dao.PacienteDAO;
import br.com.pmg.hc.dao.UsuarioDAO;
import br.com.pmg.hc.dto.PacienteRequest;
import br.com.pmg.hc.dto.PacienteResponse;
import br.com.pmg.hc.exception.BusinessException;
import br.com.pmg.hc.exception.ResourceNotFoundException;
import br.com.pmg.hc.model.Paciente;
import br.com.pmg.hc.model.StatusCadastro;
import br.com.pmg.hc.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PacienteService {

    @Inject
    PacienteDAO pacienteDAO;

    @Inject
    UsuarioDAO usuarioDAO;

    @Inject
    ConsultaDAO consultaDAO;

    public List<PacienteResponse> listarTodos() {
        return pacienteDAO.findAll().stream().map(this::toResponse).toList();
    }

    public PacienteResponse buscarPorId(Long id) {
        var paciente = pacienteDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado"));
        return toResponse(paciente);
    }

    public PacienteResponse criar(PacienteRequest request) {
        usuarioDAO.findByEmail(request.email()).ifPresent(u -> {
            throw new BusinessException("Já existe um usuário com este e-mail");
        });
        pacienteDAO.findByCpf(request.cpf()).ifPresent(p -> {
            throw new BusinessException("Já existe um paciente com este CPF");
        });

        var usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(request.senha());

        var paciente = new Paciente();
        paciente.setUsuario(usuario);
        paciente.setCpf(request.cpf());
        paciente.setSexo(request.sexo());
        paciente.setDataNascimento(request.dataNascimento());
        paciente.setTelefone(request.telefone());
        paciente.setCidade(request.cidade());
        paciente.setStatus(request.status() != null ? request.status() : StatusCadastro.ATIVO);

        paciente = pacienteDAO.create(paciente);
        return toResponse(paciente);
    }

    public PacienteResponse atualizar(Long id, PacienteRequest request) {
        var paciente = pacienteDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado"));

        usuarioDAO.findByEmail(request.email()).ifPresent(usuario -> {
            if (!usuario.getId().equals(paciente.getUsuario().getId())) {
                throw new BusinessException("Já existe um usuário com este e-mail");
            }
        });

        pacienteDAO.findByCpf(request.cpf()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new BusinessException("Já existe um paciente com este CPF");
            }
        });

        var usuario = paciente.getUsuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(request.senha());

        paciente.setCpf(request.cpf());
        paciente.setSexo(request.sexo());
        paciente.setDataNascimento(request.dataNascimento());
        paciente.setTelefone(request.telefone());
        paciente.setCidade(request.cidade());
        paciente.setStatus(request.status() != null ? request.status() : StatusCadastro.ATIVO);

        paciente = pacienteDAO.update(paciente);
        return toResponse(paciente);
    }

    public void remover(Long id) {
        if (consultaDAO.existsByPaciente(id)) {
            throw new BusinessException("Não é possível remover o paciente com consultas vinculadas");
        }
        pacienteDAO.delete(id);
    }

    private PacienteResponse toResponse(Paciente paciente) {
        var usuario = paciente.getUsuario();
        return new PacienteResponse(
                paciente.getId(),
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                paciente.getSexo(),
                paciente.getDataNascimento(),
                paciente.getCpf(),
                paciente.getTelefone(),
                paciente.getCidade(),
                paciente.getStatus());
    }
}
