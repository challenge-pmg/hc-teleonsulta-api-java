package br.com.pmg.hc.model;

public class Profissional {

    private Long id;
    private Usuario usuario;
    private TipoProfissionalSaude tipoProfissional;
    private String crm;
    private StatusCadastro status;

    public Profissional() {
    }

    public Profissional(Long id, Usuario usuario, TipoProfissionalSaude tipoProfissional, String crm,
            StatusCadastro status) {
        this.id = id;
        this.usuario = usuario;
        this.tipoProfissional = tipoProfissional;
        this.crm = crm;
        this.status = status;
    }

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
