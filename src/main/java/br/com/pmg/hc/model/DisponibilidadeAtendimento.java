package br.com.pmg.hc.model;

import java.time.LocalDateTime;

public class DisponibilidadeAtendimento {

    private Long id;
    private Profissional profissional;
    private LocalDateTime dataHora;
    private StatusDisponibilidade status;
    private LocalDateTime criadoEm;

    public DisponibilidadeAtendimento() {
    }

    public DisponibilidadeAtendimento(Long id, Profissional profissional, LocalDateTime dataHora,
            StatusDisponibilidade status, LocalDateTime criadoEm) {
        this.id = id;
        this.profissional = profissional;
        this.dataHora = dataHora;
        this.status = status;
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public StatusDisponibilidade getStatus() {
        return status;
    }

    public void setStatus(StatusDisponibilidade status) {
        this.status = status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
