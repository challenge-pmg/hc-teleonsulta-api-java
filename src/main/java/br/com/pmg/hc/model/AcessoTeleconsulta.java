package br.com.pmg.hc.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "T_TDSPW_PGR_ACESSO_TELECONSULTA")
public class AcessoTeleconsulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acesso")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_consulta", nullable = false)
    private Consulta consulta;

    @Column(name = "horario_acesso")
    private LocalDateTime horarioAcesso;

    @Column(length = 50)
    private String resultado;

    @PrePersist
    void prePersist() {
        if (this.horarioAcesso == null) {
            this.horarioAcesso = LocalDateTime.now();
        }
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
