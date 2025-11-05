package br.com.pmg.hc.model;

import java.time.LocalDateTime;

public class MensagemAutomatica {

    private Long id;
    private Consulta consulta;
    private LocalDateTime horarioEnvio;
    private TipoMensagemAutomatica tipoMensagem;
    private String conteudo;

    public MensagemAutomatica() {
    }

    public MensagemAutomatica(Long id, Consulta consulta, LocalDateTime horarioEnvio,
            TipoMensagemAutomatica tipoMensagem, String conteudo) {
        this.id = id;
        this.consulta = consulta;
        this.horarioEnvio = horarioEnvio;
        this.tipoMensagem = tipoMensagem;
        this.conteudo = conteudo;
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

    public LocalDateTime getHorarioEnvio() {
        return horarioEnvio;
    }

    public void setHorarioEnvio(LocalDateTime horarioEnvio) {
        this.horarioEnvio = horarioEnvio;
    }

    public TipoMensagemAutomatica getTipoMensagem() {
        return tipoMensagem;
    }

    public void setTipoMensagem(TipoMensagemAutomatica tipoMensagem) {
        this.tipoMensagem = tipoMensagem;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
}
