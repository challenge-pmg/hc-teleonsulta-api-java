package br.com.pmg.hc.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "T_TDSPW_PGR_PROFISSIONAL_SAUDE")
public class Profissional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_profissional")
    private Long id;

    @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_usuario", unique = true, nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_tipo_profissional", nullable = false)
    private TipoProfissionalSaude tipoProfissional;

    @Column(length = 20, unique = true)
    private String crm;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusCadastro status = StatusCadastro.ATIVO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public TipoProfissionalSaude getTipoProfissional() {
        return tipoProfissional;
    }

    public void setTipoProfissional(TipoProfissionalSaude tipoProfissional) {
        this.tipoProfissional = tipoProfissional;
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public StatusCadastro getStatus() {
        return status;
    }

    public void setStatus(StatusCadastro status) {
        this.status = status;
    }
}
