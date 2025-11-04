package br.com.pmg.hc.service;

import java.util.List;

import br.com.pmg.hc.dao.ConsultaDAO;
import br.com.pmg.hc.dao.FeedbackDAO;
import br.com.pmg.hc.dto.ConsultaResponse;
import br.com.pmg.hc.dto.FeedbackRequest;
import br.com.pmg.hc.dto.FeedbackResponse;
import br.com.pmg.hc.exception.BusinessException;
import br.com.pmg.hc.exception.ResourceNotFoundException;
import br.com.pmg.hc.model.Consulta;
import br.com.pmg.hc.model.Feedback;
import br.com.pmg.hc.model.Role;
import br.com.pmg.hc.model.StatusConsulta;
import br.com.pmg.hc.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class FeedbackService {

    @Inject
    FeedbackDAO feedbackDAO;

    @Inject
    ConsultaDAO consultaDAO;

    @Transactional
    public FeedbackResponse criar(Usuario solicitante, FeedbackRequest request) {
        var consulta = obterConsulta(request.consultaId());
        garantirPermissaoCriarFeedback(solicitante, consulta);
        garantirConsultaElegivelParaFeedback(consulta);

        feedbackDAO.findByConsultaId(consulta.getId()).ifPresent(f -> {
            throw new BusinessException("Consulta ja possui feedback");
        });

        var feedback = new Feedback();
        feedback.setConsulta(consulta);
        feedback.setNota(request.nota());
        feedback.setComentario(request.comentario());

        feedbackDAO.persist(feedback);
        return toResponse(feedback, solicitante);
    }

    @Transactional
    public FeedbackResponse atualizar(Long id, Usuario solicitante, FeedbackRequest request) {
        var feedback = obterFeedback(id);
        var consulta = feedback.getConsulta();

        garantirPermissaoAlterarFeedback(solicitante, consulta);
        garantirConsultaElegivelParaFeedback(consulta);

        if (!consulta.getId().equals(request.consultaId())) {
            throw new BusinessException("Nao e possivel mover o feedback para outra consulta");
        }

        feedback.setNota(request.nota());
        feedback.setComentario(request.comentario());

        var atualizado = feedbackDAO.merge(feedback);
        return toResponse(atualizado, solicitante);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public FeedbackResponse buscarPorId(Long id, Usuario solicitante) {
        var feedback = obterFeedback(id);
        garantirPermissaoVisualizarFeedback(solicitante, feedback.getConsulta());
        return toResponse(feedback, solicitante);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<FeedbackResponse> listar(Usuario solicitante) {
        if (solicitante.getRole() == Role.ADMIN) {
            return feedbackDAO.findAll().stream()
                    .map(f -> toResponse(f, solicitante))
                    .toList();
        }
        if (solicitante.getRole() == Role.PACIENTE) {
            return feedbackDAO.findByPacienteUsuario(solicitante.getId()).stream()
                    .map(f -> toResponse(f, solicitante))
                    .toList();
        }
        throw new BusinessException("Usuario sem permissao para visualizar feedbacks");
    }

    @Transactional
    public void remover(Long id, Usuario solicitante) {
        garantirRole(solicitante, Role.ADMIN);
        var feedback = obterFeedback(id);
        feedbackDAO.delete(feedback);
    }

    private Feedback obterFeedback(Long id) {
        return feedbackDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback nao encontrado"));
    }

    private Consulta obterConsulta(Long id) {
        return consultaDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta nao encontrada"));
    }

    private void garantirPermissaoCriarFeedback(Usuario solicitante, Consulta consulta) {
        if (solicitante.getRole() != Role.PACIENTE) {
            throw new BusinessException("Apenas pacientes podem registrar feedback");
        }
        var paciente = consulta.getPaciente();
        if (!paciente.getUsuario().getId().equals(solicitante.getId())) {
            throw new BusinessException("Paciente nao pode registrar feedback de outra consulta");
        }
    }

    private void garantirPermissaoAlterarFeedback(Usuario solicitante, Consulta consulta) {
        if (solicitante.getRole() == Role.ADMIN) {
            return;
        }
        if (solicitante.getRole() == Role.PACIENTE
                && consulta.getPaciente().getUsuario().getId().equals(solicitante.getId())) {
            return;
        }
        throw new BusinessException("Usuario sem permissao para alterar o feedback");
    }

    private void garantirPermissaoVisualizarFeedback(Usuario solicitante, Consulta consulta) {
        if (solicitante.getRole() == Role.ADMIN) {
            return;
        }
        if (solicitante.getRole() == Role.PACIENTE
                && consulta.getPaciente().getUsuario().getId().equals(solicitante.getId())) {
            return;
        }
        throw new BusinessException("Usuario sem permissao para visualizar o feedback");
    }

    private void garantirConsultaElegivelParaFeedback(Consulta consulta) {
        if (consulta.getStatus() != StatusConsulta.REALIZADA) {
            throw new BusinessException("Feedback so pode ser registrado para consultas realizadas");
        }
    }

    private void garantirRole(Usuario usuario, Role rolePermitido) {
        if (usuario.getRole() != rolePermitido) {
            throw new BusinessException("Usuario sem permissao para esta operacao");
        }
    }

    private FeedbackResponse toResponse(Feedback feedback, Usuario solicitante) {
        ConsultaResponse consultaResponse = ConsultaRepresentationBuilder.build(feedback.getConsulta(), solicitante);
        return new FeedbackResponse(
                feedback.getId(),
                consultaResponse,
                feedback.getNota(),
                feedback.getComentario(),
                feedback.getCriadoEm());
    }
}
