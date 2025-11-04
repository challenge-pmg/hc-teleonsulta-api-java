package br.com.pmg.hc.service;

import br.com.pmg.hc.dto.ConsultaResponse;
import br.com.pmg.hc.dto.PacienteConsultaResumo;
import br.com.pmg.hc.dto.ProfissionalConsultaResumo;
import br.com.pmg.hc.model.Consulta;
import br.com.pmg.hc.model.Role;
import br.com.pmg.hc.model.TipoConsulta;
import br.com.pmg.hc.model.Usuario;

final class ConsultaRepresentationBuilder {

    private ConsultaRepresentationBuilder() {
    }

    static ConsultaResponse build(Consulta consulta, Usuario solicitante) {
        var paciente = consulta.getPaciente();
        var profissional = consulta.getProfissional();

        var pacienteResumo = new PacienteConsultaResumo(
                paciente.getId(),
                paciente.getUsuario().getId(),
                paciente.getUsuario().getNome(),
                paciente.getCpf());

        var profissionalResumo = new ProfissionalConsultaResumo(
                profissional.getId(),
                profissional.getUsuario().getId(),
                profissional.getUsuario().getNome(),
                profissional.getCrm(),
                profissional.getTipoProfissional().getNome());

        var link = linkVisivel(consulta, solicitante);

        return new ConsultaResponse(
                consulta.getId(),
                pacienteResumo,
                profissionalResumo,
                consulta.getDataHora(),
                consulta.getTipoConsulta(),
                link,
                consulta.getStatus(),
                consulta.getUsuarioAgendador() != null ? consulta.getUsuarioAgendador().getId() : null,
                consulta.getCriadoEm());
    }

    private static String linkVisivel(Consulta consulta, Usuario solicitante) {
        if (consulta.getTipoConsulta() == TipoConsulta.PRESENCIAL) {
            return null;
        }
        if (solicitante.getRole() == Role.ADMIN) {
            return null;
        }
        if (consulta.getPaciente().getUsuario().getId().equals(solicitante.getId())) {
            return consulta.getLinkAcesso();
        }
        if (consulta.getProfissional().getUsuario().getId().equals(solicitante.getId())) {
            return consulta.getLinkAcesso();
        }
        return null;
    }
}
