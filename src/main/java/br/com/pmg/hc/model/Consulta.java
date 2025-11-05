package br.com.pmg.hc.model;

import java.time.LocalDateTime;

public class Consulta {

    private Long id;
    private Paciente paciente;
    private Profissional profissional;
    private Usuario usuarioAgendador;
    private LocalDateTime dataHora;
    private TipoConsulta tipoConsulta;
    private String linkAcesso;
    private StatusConsulta status;
    private LocalDateTime criadoEm;

    public Consulta() {
    }

    public Consulta(Long id, Paciente paciente, Profissional profissional, Usuario usuarioAgendador,
            LocalDateTime dataHora, TipoConsulta tipoConsulta, String linkAcesso, StatusConsulta status,
            LocalDateTime criadoEm) {
        this.id = id;
        this.paciente = paciente;
        this.profissional = profissional;
        this.usuarioAgendador = usuarioAgendador;
        this.dataHora = dataHora;
        this.tipoConsulta = tipoConsulta;
        this.linkAcesso = linkAcesso;
        this.status = status;
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
    }

    public Usuario getUsuarioAgendador() {
        return usuarioAgendador;
    }

    public void setUsuarioAgendador(Usuario usuarioAgendador) {
        this.usuarioAgendador = usuarioAgendador;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public TipoConsulta getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(TipoConsulta tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public String getLinkAcesso() {
        return linkAcesso;
    }

    public void setLinkAcesso(String linkAcesso) {
        this.linkAcesso = linkAcesso;
    }

    public StatusConsulta getStatus() {
        return status;
    }

    public void setStatus(StatusConsulta status) {
        this.status = status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
