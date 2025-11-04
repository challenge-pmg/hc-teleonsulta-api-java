package br.com.pmg.hc.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "T_TDSPW_PGR_MENSAGEM_AUTOMATICA")
public class MensagemAutomatica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensagem")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_consulta", nullable = false)
    private Consulta consulta;

    @Column(name = "horario_envio", nullable = false)
    private LocalDateTime horarioEnvio;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_mensagem", nullable = false, length = 30)
    private TipoMensagemAutomatica tipoMensagem;

    @Column(length = 255)
    private String conteudo;

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
