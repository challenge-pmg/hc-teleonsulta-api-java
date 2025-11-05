package br.com.pmg.hc.model;

import java.time.LocalDateTime;

public class Feedback {

    private Long id;
    private Consulta consulta;
    private Integer nota;
    private String comentario;
    private LocalDateTime criadoEm;

    public Feedback() {
    }

    public Feedback(Long id, Consulta consulta, Integer nota, String comentario, LocalDateTime criadoEm) {
        this.id = id;
        this.consulta = consulta;
        this.nota = nota;
        this.comentario = comentario;
        this.criadoEm = criadoEm;
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

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
