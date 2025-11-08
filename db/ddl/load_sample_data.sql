------------------------------------------------------------------
-- Script de carga de dados para testes locais / front-end
-- Execute depois de criar as tabelas (create_pgr_sprint4.sql).
-- Ajuste e rode em um schema Oracle vazio ou utilize como base.
------------------------------------------------------------------

-- Limpa registros (ordem respeita as FKs)
DELETE FROM T_TDSPW_PGR_ACESSO_TELECONSULTA;
DELETE FROM T_TDSPW_PGR_FEEDBACK_CONSULTA;
DELETE FROM T_TDSPW_PGR_MENSAGEM_AUTOMATICA;
DELETE FROM T_TDSPW_PGR_CONSULTA;
DELETE FROM T_TDSPW_PGR_DISPONIBILIDADE;
DELETE FROM T_TDSPW_PGR_PROFISSIONAL_SAUDE;
DELETE FROM T_TDSPW_PGR_PACIENTE;
DELETE FROM T_TDSPW_PGR_TIPO_PROFISSIONAL_SAUDE;
DELETE FROM T_TDSPW_PGR_USUARIO;

------------------------------------------------------------------
-- Tipos de profissional
------------------------------------------------------------------
INSERT INTO T_TDSPW_PGR_TIPO_PROFISSIONAL_SAUDE (nome_tipo) VALUES ('Clinico Geral');
INSERT INTO T_TDSPW_PGR_TIPO_PROFISSIONAL_SAUDE (nome_tipo) VALUES ('Enfermagem');
INSERT INTO T_TDSPW_PGR_TIPO_PROFISSIONAL_SAUDE (nome_tipo) VALUES ('Administrativo');

------------------------------------------------------------------
-- Usuarios + pacientes
------------------------------------------------------------------
INSERT INTO T_TDSPW_PGR_USUARIO (nome, email, senha, role) VALUES
('Ana Paciente', 'ana.paciente@hc.com', '123456', 'PACIENTE');

INSERT INTO T_TDSPW_PGR_USUARIO (nome, email, senha, role) VALUES
('Bruno Paciente', 'bruno.paciente@hc.com', '123456', 'PACIENTE');

INSERT INTO T_TDSPW_PGR_PACIENTE (id_usuario, cpf, sexo, dt_nascimento, telefone, cidade, status) VALUES
((SELECT id_usuario FROM T_TDSPW_PGR_USUARIO WHERE email = 'ana.paciente@hc.com'),
 '12345678901', 'F', DATE '1994-03-12', '11988887777', 'Sao Paulo', 'ATIVO');

INSERT INTO T_TDSPW_PGR_PACIENTE (id_usuario, cpf, sexo, dt_nascimento, telefone, cidade, status) VALUES
((SELECT id_usuario FROM T_TDSPW_PGR_USUARIO WHERE email = 'bruno.paciente@hc.com'),
 '98765432100', 'M', DATE '1990-07-02', '11977776666', 'Campinas', 'ATIVO');

------------------------------------------------------------------
-- Usuarios + profissionais
------------------------------------------------------------------
INSERT INTO T_TDSPW_PGR_USUARIO (nome, email, senha, role) VALUES
('Dr. Henrique Souza', 'henrique.prof@hc.com', '123456', 'PROFISSIONAL');

INSERT INTO T_TDSPW_PGR_USUARIO (nome, email, senha, role) VALUES
('Maria Fernanda Lima', 'maria.admin@hc.com', '123456', 'PROFISSIONAL');

INSERT INTO T_TDSPW_PGR_PROFISSIONAL_SAUDE (id_usuario, id_tipo_profissional, crm, status) VALUES
((SELECT id_usuario FROM T_TDSPW_PGR_USUARIO WHERE email = 'henrique.prof@hc.com'),
 (SELECT id_tipo_profissional FROM T_TDSPW_PGR_TIPO_PROFISSIONAL_SAUDE WHERE nome_tipo = 'Clinico Geral'),
 'CRM123456', 'ATIVO');

INSERT INTO T_TDSPW_PGR_PROFISSIONAL_SAUDE (id_usuario, id_tipo_profissional, crm, status) VALUES
((SELECT id_usuario FROM T_TDSPW_PGR_USUARIO WHERE email = 'maria.admin@hc.com'),
 (SELECT id_tipo_profissional FROM T_TDSPW_PGR_TIPO_PROFISSIONAL_SAUDE WHERE nome_tipo = 'Administrativo'),
 NULL, 'ATIVO');

------------------------------------------------------------------
-- Slots de disponibilidade (dois livres e dois reservados)
------------------------------------------------------------------
INSERT INTO T_TDSPW_PGR_DISPONIBILIDADE (id_profissional, data_hora, status) VALUES
((SELECT pr.id_profissional
  FROM T_TDSPW_PGR_PROFISSIONAL_SAUDE pr
       JOIN T_TDSPW_PGR_USUARIO u ON u.id_usuario = pr.id_usuario
  WHERE u.email = 'henrique.prof@hc.com'),
 TO_TIMESTAMP('2025-11-10 09:00', 'YYYY-MM-DD HH24:MI'),
 'LIVRE');

INSERT INTO T_TDSPW_PGR_DISPONIBILIDADE (id_profissional, data_hora, status) VALUES
((SELECT pr.id_profissional
  FROM T_TDSPW_PGR_PROFISSIONAL_SAUDE pr
       JOIN T_TDSPW_PGR_USUARIO u ON u.id_usuario = pr.id_usuario
  WHERE u.email = 'henrique.prof@hc.com'),
 TO_TIMESTAMP('2025-11-10 10:00', 'YYYY-MM-DD HH24:MI'),
 'RESERVADA');

INSERT INTO T_TDSPW_PGR_DISPONIBILIDADE (id_profissional, data_hora, status) VALUES
((SELECT pr.id_profissional
  FROM T_TDSPW_PGR_PROFISSIONAL_SAUDE pr
       JOIN T_TDSPW_PGR_USUARIO u ON u.id_usuario = pr.id_usuario
  WHERE u.email = 'maria.admin@hc.com'),
 TO_TIMESTAMP('2025-11-11 14:00', 'YYYY-MM-DD HH24:MI'),
 'LIVRE');

INSERT INTO T_TDSPW_PGR_DISPONIBILIDADE (id_profissional, data_hora, status) VALUES
((SELECT pr.id_profissional
  FROM T_TDSPW_PGR_PROFISSIONAL_SAUDE pr
       JOIN T_TDSPW_PGR_USUARIO u ON u.id_usuario = pr.id_usuario
  WHERE u.email = 'maria.admin@hc.com'),
 TO_TIMESTAMP('2025-11-11 15:00', 'YYYY-MM-DD HH24:MI'),
 'RESERVADA');

------------------------------------------------------------------
-- Consultas ligadas aos slots reservados
------------------------------------------------------------------
INSERT INTO T_TDSPW_PGR_CONSULTA (
    id_paciente,
    id_profissional,
    id_disponibilidade,
    id_usuario_agendador,
    data_hora,
    tipo_consulta,
    link_acesso,
    status)
VALUES (
    (SELECT p.id_paciente
     FROM T_TDSPW_PGR_PACIENTE p
          JOIN T_TDSPW_PGR_USUARIO u ON u.id_usuario = p.id_usuario
     WHERE u.email = 'ana.paciente@hc.com'),
    (SELECT pr.id_profissional
     FROM T_TDSPW_PGR_PROFISSIONAL_SAUDE pr
          JOIN T_TDSPW_PGR_USUARIO u ON u.id_usuario = pr.id_usuario
     WHERE u.email = 'henrique.prof@hc.com'),
    (SELECT d.id_disponibilidade
     FROM T_TDSPW_PGR_DISPONIBILIDADE d
          JOIN T_TDSPW_PGR_PROFISSIONAL_SAUDE pr ON pr.id_profissional = d.id_profissional
          JOIN T_TDSPW_PGR_USUARIO u ON u.id_usuario = pr.id_usuario
     WHERE u.email = 'henrique.prof@hc.com'
       AND d.data_hora = TO_TIMESTAMP('2025-11-10 10:00', 'YYYY-MM-DD HH24:MI')),
    NULL,
    TO_TIMESTAMP('2025-11-10 10:00', 'YYYY-MM-DD HH24:MI'),
    'TELECONSULTA',
    'https://meet.hc.com/ana-henrique',
    'AGENDADA');

INSERT INTO T_TDSPW_PGR_CONSULTA (
    id_paciente,
    id_profissional,
    id_disponibilidade,
    id_usuario_agendador,
    data_hora,
    tipo_consulta,
    link_acesso,
    status)
VALUES (
    (SELECT p.id_paciente
     FROM T_TDSPW_PGR_PACIENTE p
          JOIN T_TDSPW_PGR_USUARIO u ON u.id_usuario = p.id_usuario
     WHERE u.email = 'bruno.paciente@hc.com'),
    (SELECT pr.id_profissional
     FROM T_TDSPW_PGR_PROFISSIONAL_SAUDE pr
          JOIN T_TDSPW_PGR_USUARIO u ON u.id_usuario = pr.id_usuario
     WHERE u.email = 'maria.admin@hc.com'),
    (SELECT d.id_disponibilidade
     FROM T_TDSPW_PGR_DISPONIBILIDADE d
          JOIN T_TDSPW_PGR_PROFISSIONAL_SAUDE pr ON pr.id_profissional = d.id_profissional
          JOIN T_TDSPW_PGR_USUARIO u ON u.id_usuario = pr.id_usuario
     WHERE u.email = 'maria.admin@hc.com'
       AND d.data_hora = TO_TIMESTAMP('2025-11-11 15:00', 'YYYY-MM-DD HH24:MI')),
    NULL,
    TO_TIMESTAMP('2025-11-11 15:00', 'YYYY-MM-DD HH24:MI'),
    'PRESENCIAL',
    NULL,
    'AGENDADA');

COMMIT;
