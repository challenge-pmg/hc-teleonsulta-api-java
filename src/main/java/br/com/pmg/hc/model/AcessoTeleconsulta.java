package br.com.pmg.hc.model;

import java.time.LocalDateTime;

public class AcessoTeleconsulta {

    private Long id;
    private Consulta consulta;
    private LocalDateTime horarioAcesso;
    private String resultado;

    public AcessoTeleconsulta() {
    }

    public AcessoTeleconsulta(Long id, Consulta consulta, LocalDateTime horarioAcesso, String resultado) {
        this.id = id;
        this.consulta = consulta;
        this.horarioAcesso = horarioAcesso;
        this.resultado = resultado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }

    public LocalDateTime getHorarioAcesso() {
        return horarioAcesso;
    }

    public void setHorarioAcesso(LocalDateTime horarioAcesso) {
        this.horarioAcesso = horarioAcesso;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}
