package br.com.pmg.hc.service;

import java.util.List;

import br.com.pmg.hc.dao.ConsultaDAO;
import br.com.pmg.hc.dao.PacienteDAO;
import br.com.pmg.hc.dao.UsuarioDAO;
import br.com.pmg.hc.dto.PacienteRequest;
import br.com.pmg.hc.dto.PacienteResponse;
import br.com.pmg.hc.dto.UsuarioResumo;
import br.com.pmg.hc.exception.BusinessException;
import br.com.pmg.hc.exception.ResourceNotFoundException;
import br.com.pmg.hc.model.Paciente;
import br.com.pmg.hc.model.Role;
import br.com.pmg.hc.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PacienteService {

    @Inject
    PacienteDAO pacienteDAO;

    @Inject
    UsuarioDAO usuarioDAO;

    @Inject
    ConsultaDAO consultaDAO;

    @Transactional
    public PacienteResponse criar(Usuario solicitante, PacienteRequest request) {
        garantirRole(solicitante, Role.ADMIN);
        validarEmailDisponivel(request.email(), null);
        validarCpfDisponivel(request.cpf(), null);

        var usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(request.senha());
        usuario.setRole(Role.PACIENTE);
        usuarioDAO.persist(usuario);

        var paciente = new Paciente();
        paciente.setUsuario(usuario);
        paciente.setCpf(request.cpf());
        paciente.setSexo(request.sexo());
        paciente.setDataNascimento(request.dataNascimento());
        paciente.setTelefone(request.telefone());
        paciente.setCidade(request.cidade());
        if (request.status() != null) {
            paciente.setStatus(request.status());
        }

        pacienteDAO.persist(paciente);
        return toResponse(paciente);
    }

    @Transactional
    public PacienteResponse atualizar(Long id, Usuario solicitante, PacienteRequest request) {
        var paciente = obterPaciente(id);
        garantirRolePacienteOuAdmin(solicitante, paciente);

        validarEmailDisponivel(request.email(), paciente.getUsuario().getId());
        validarCpfDisponivel(request.cpf(), paciente.getId());

        var usuario = paciente.getUsuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(request.senha());

        paciente.setCpf(request.cpf());
        paciente.setSexo(request.sexo());
        paciente.setDataNascimento(request.dataNascimento());
        paciente.setTelefone(request.telefone());
        paciente.setCidade(request.cidade());

        if (request.status() != null) {
            garantirRole(solicitante, Role.ADMIN);
            paciente.setStatus(request.status());
        }

        var atualizado = pacienteDAO.merge(paciente);
        usuarioDAO.merge(usuario);
        return toResponse(atualizado);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public PacienteResponse buscarPorId(Long id, Usuario solicitante) {
        var paciente = obterPaciente(id);
        garantirRolePacienteOuAdmin(solicitante, paciente);
        return toResponse(paciente);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<PacienteResponse> listarTodos(Usuario solicitante) {
        garantirRole(solicitante, Role.ADMIN);
        return pacienteDAO.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public void remover(Long id, Usuario solicitante) {
        garantirRole(solicitante, Role.ADMIN);
        var paciente = obterPaciente(id);

        if (consultaDAO.existsByPaciente(paciente.getId())) {
            throw new BusinessException("Nao e possivel remover o paciente com consultas vinculadas");
        }
        if (consultaDAO.existsByUsuarioAgendador(paciente.getUsuario().getId())) {
            throw new BusinessException("Nao e possivel remover o paciente com consultas agendadas por ele");
        }

        pacienteDAO.delete(paciente);
    }

    private Paciente obterPaciente(Long id) {
        return pacienteDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente nao encontrado"));
    }

    private void validarEmailDisponivel(String email, Long usuarioAtualId) {
        usuarioDAO.findByEmail(email).ifPresent(usuarioExistente -> {
            if (usuarioAtualId == null || !usuarioExistente.getId().equals(usuarioAtualId)) {
                throw new BusinessException("Ja existe um usuario com este e-mail");
            }
        });
    }

    private void validarCpfDisponivel(String cpf, Long pacienteAtualId) {
        pacienteDAO.findByCpf(cpf).ifPresent(pacienteExistente -> {
            if (pacienteAtualId == null || !pacienteExistente.getId().equals(pacienteAtualId)) {
                throw new BusinessException("Ja existe um paciente com este CPF");
            }
        });
    }

    private void garantirRole(Usuario usuario, Role... rolesPermitidos) {
        for (var role : rolesPermitidos) {
            if (usuario.getRole() == role) {
                return;
            }
        }
        throw new BusinessException("Usuario sem permissao para esta operacao");
    }

    private void garantirRolePacienteOuAdmin(Usuario solicitante, Paciente paciente) {
        if (solicitante.getRole() == Role.ADMIN) {
            return;
        }
        if (solicitante.getRole() == Role.PACIENTE && solicitante.getId().equals(paciente.getUsuario().getId())) {
            return;
        }
        throw new BusinessException("Usuario sem permissao para esta operacao");
    }

    private PacienteResponse toResponse(Paciente paciente) {
        var usuario = paciente.getUsuario();
        var usuarioResumo = new UsuarioResumo(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getCriadoEm());

        return new PacienteResponse(
                paciente.getId(),
                usuarioResumo,
                paciente.getCpf(),
                paciente.getSexo(),
                paciente.getDataNascimento(),
                paciente.getTelefone(),
                paciente.getCidade(),
                paciente.getStatus());
    }
}
