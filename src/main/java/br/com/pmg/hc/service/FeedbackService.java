package br.com.pmg.hc.service;

import java.util.List;

import br.com.pmg.hc.dao.ConsultaDAO;
import br.com.pmg.hc.dao.FeedbackDAO;
import br.com.pmg.hc.dto.ConsultaResponse;
import br.com.pmg.hc.dto.FeedbackRequest;
import br.com.pmg.hc.dto.FeedbackResponse;
import br.com.pmg.hc.dto.PacienteResponse;
import br.com.pmg.hc.dto.ProfissionalResponse;
import br.com.pmg.hc.exception.ResourceNotFoundException;
import br.com.pmg.hc.model.Feedback;
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
    public FeedbackResponse criar(FeedbackRequest request) {
        var consulta = consultaDAO.findById(request.consultaId())
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));

        var feedback = new Feedback();
        feedback.setConsulta(consulta);
        feedback.setNota(request.nota());
        feedback.setComentario(request.comentario());

        feedbackDAO.persist(feedback);
        return toResponse(feedback);
    }

    @Transactional
    public FeedbackResponse atualizar(Long id, FeedbackRequest request) {
        var feedback = feedbackDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback não encontrado"));

        var consulta = consultaDAO.findById(request.consultaId())
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));

        feedback.setConsulta(consulta);
        feedback.setNota(request.nota());
        feedback.setComentario(request.comentario());

        var atualizado = feedbackDAO.merge(feedback);
        return toResponse(atualizado);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public FeedbackResponse buscarPorId(Long id) {
        var feedback = feedbackDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback não encontrado"));
        return toResponse(feedback);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<FeedbackResponse> listarTodos() {
        return feedbackDAO.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public void remover(Long id) {
        var feedback = feedbackDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback não encontrado"));
        feedbackDAO.delete(feedback);
    }

    private FeedbackResponse toResponse(Feedback feedback) {
        var consulta = feedback.getConsulta();
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

        var consultaResponse = new ConsultaResponse(
                consulta.getId(),
                pacienteResponse,
                profissionalResponse,
                consulta.getDataHora(),
                consulta.getDescricao(),
                consulta.getStatus());

        return new FeedbackResponse(
                feedback.getId(),
                consultaResponse,
                feedback.getNota(),
                feedback.getComentario(),
                feedback.getCriadoEm());
    }
}
