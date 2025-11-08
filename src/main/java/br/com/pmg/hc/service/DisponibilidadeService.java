package br.com.pmg.hc.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import br.com.pmg.hc.dao.DisponibilidadeDAO;
import br.com.pmg.hc.dao.ProfissionalDAO;
import br.com.pmg.hc.dto.DisponibilidadeRequest;
import br.com.pmg.hc.dto.DisponibilidadeResponse;
import br.com.pmg.hc.exception.BusinessException;
import br.com.pmg.hc.exception.ResourceNotFoundException;
import br.com.pmg.hc.model.DisponibilidadeAtendimento;
import br.com.pmg.hc.model.Profissional;
import br.com.pmg.hc.model.StatusDisponibilidade;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DisponibilidadeService {

    @Inject
    DisponibilidadeDAO disponibilidadeDAO;

    @Inject
    ProfissionalDAO profissionalDAO;

    public List<DisponibilidadeResponse> listarLivres(Long profissionalId, LocalDate dataInicial, LocalDate dataFinal) {
        validarProfissional(profissionalId);
        return disponibilidadeDAO.findDisponiveis(profissionalId, dataInicial, dataFinal).stream()
                .map(this::toResponse)
                .toList();
    }

    public DisponibilidadeResponse criar(DisponibilidadeRequest request) {
        Profissional profissional = validarProfissional(request.profissionalId());
        LocalDateTime dataHora = request.dataHora();
        if (dataHora.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Nao eh possivel cadastrar disponibilidade no passado");
        }
        if (disponibilidadeDAO.existsByProfissionalAndHorario(profissional.getId(), dataHora)) {
            throw new BusinessException("Ja existe uma disponibilidade neste horario para o profissional");
        }

        var disponibilidade = new DisponibilidadeAtendimento();
        disponibilidade.setProfissional(profissional);
        disponibilidade.setDataHora(dataHora);
        disponibilidade.setStatus(StatusDisponibilidade.LIVRE);

        disponibilidade = disponibilidadeDAO.create(disponibilidade);
        return toResponse(disponibilidade);
    }

    public void remover(Long id) {
        var disponibilidade = disponibilidadeDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidade nao encontrada"));
        if (disponibilidade.getStatus() == StatusDisponibilidade.RESERVADA) {
            throw new BusinessException("Nao eh possivel remover uma disponibilidade reservada");
        }
        disponibilidadeDAO.delete(id);
    }

    public DisponibilidadeAtendimento reservar(Long disponibilidadeId) {
        var disponibilidade = disponibilidadeDAO.findById(disponibilidadeId)
                .orElseThrow(() -> new BusinessException("Disponibilidade informada nao existe"));
        if (disponibilidade.getStatus() != StatusDisponibilidade.LIVRE) {
            throw new BusinessException("Horario nao esta mais disponivel");
        }
        if (disponibilidade.getDataHora().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Nao eh possivel reservar horarios no passado");
        }
        boolean reservado = disponibilidadeDAO.reservar(disponibilidadeId);
        if (!reservado) {
            throw new BusinessException("Horario escolhido acabou de ser reservado por outra pessoa");
        }
        disponibilidade.setStatus(StatusDisponibilidade.RESERVADA);
        return disponibilidade;
    }

    public void liberar(Long disponibilidadeId) {
        disponibilidadeDAO.liberar(disponibilidadeId);
    }

    public DisponibilidadeAtendimento buscarPorId(Long id) {
        return disponibilidadeDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidade nao encontrada"));
    }

    private DisponibilidadeResponse toResponse(DisponibilidadeAtendimento disponibilidade) {
        return new DisponibilidadeResponse(
                disponibilidade.getId(),
                disponibilidade.getProfissional().getId(),
                disponibilidade.getProfissional().getUsuario().getNome(),
                disponibilidade.getDataHora(),
                disponibilidade.getStatus());
    }

    private Profissional validarProfissional(Long profissionalId) {
        return profissionalDAO.findById(profissionalId)
                .orElseThrow(() -> new BusinessException("Profissional informado nao existe"));
    }
}
