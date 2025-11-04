package br.com.pmg.hc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "profissionais")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Profissional extends Usuario {

    @Column(nullable = false)
    private String especialidade;

    @Column(name = "registro_profissional", nullable = false, unique = true)
    private String registroProfissional;

    @Column(nullable = false)
    private String telefone;

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getRegistroProfissional() {
        return registroProfissional;
    }

    public void setRegistroProfissional(String registroProfissional) {
        this.registroProfissional = registroProfissional;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
