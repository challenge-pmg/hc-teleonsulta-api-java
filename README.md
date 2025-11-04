# HC Teleconsulta API

API REST construÃ­da com Quarkus 3.29.0 e Java 21 para gerenciar pacientes, profissionais, consultas e feedbacks da sprint 4 do projeto HC Teleconsulta.

## âœ… PrÃ©-requisitos
- Java 21 instalado
- Maven 3.9+
- Acesso Ã  VPN/whitelist da FIAP para conectar no banco Oracle

## ðŸš€ Executando em ambiente local
1. Instale as dependÃªncias e suba a aplicaÃ§Ã£o em modo dev:
   ```bash
   ./mvnw clean quarkus:dev
   ```
2. A API ficarÃ¡ disponÃ­vel em `http://localhost:8080` e o Swagger em `http://localhost:8080/q/swagger-ui`.

> A aplicaÃ§Ã£o utiliza o banco de dados Oracle da FIAP. As credenciais jÃ¡ estÃ£o configuradas em `src/main/resources/application.properties`.

## ðŸŒ IntegraÃ§Ã£o com o front-end
- O front-end em React + Vite + Tailwind ainda **nÃ£o foi deployado**.
- Durante o desenvolvimento, as requisiÃ§Ãµes serÃ£o originadas de `http://localhost:5173`. Essa URL jÃ¡ estÃ¡ liberada via CORS.
- Quando o front for publicado (por exemplo, na Vercel), basta adicionar a nova origem no `application.properties`, na propriedade `quarkus.http.cors.origins`.

## ðŸ§± Tecnologias e extensÃµes utilizadas
- Quarkus REST (`quarkus-rest`, `quarkus-rest-jackson`)
- Hibernate ORM & Validator
- Agroal Connection Pool
- JDBC Oracle
- SmallRye OpenAPI

## ðŸ“ Estrutura principal
```
src/main/java/br/com/pmg/hc/
â”œâ”€â”€ model/
â”œâ”€â”€ dao/
â”œâ”€â”€ service/
â”œâ”€â”€ resource/
â”œâ”€â”€ exception/
â””â”€â”€ dto/
```

## ðŸ”’ Controle de acesso
O modelo de domÃ­nio utiliza heranÃ§a de `Usuario` para `Paciente` e `Profissional`, com atribuiÃ§Ã£o de roles (`ADMIN`, `PACIENTE`, `PROFISSIONAL`).

## ðŸ§ª Testes
Ainda nÃ£o hÃ¡ suÃ­te automatizada. Utilize ferramentas como Insomnia ou Postman para validar as rotas expostas.

## Autenticacao e Autorizacao (Sprint 4)
- Envie o cabecalho `X-Usuario-Id` com o identificador do usuario (tabela T_TDSPW_PGR_USUARIO).
- As regras de permissao seguem os papeis ADMIN, PACIENTE e PROFISSIONAL definidos para esta sprint.

## Banco de Dados (Oracle) â€” Sprint 4
Ordem de execuÃ§Ã£o para correÃ§Ã£o:
a) Abrir o arquivo `db/ddl/create_pgr_sprint4.sql` no SQL Developer (ou SQL*Plus) e executar.
b) Ajustar `src/main/resources/application.properties` com as credenciais (se necessÃ¡rio).
c) Rodar a API: `mvn clean quarkus:dev`.
d) (Opcional para reset) Executar `db/ddl/drop_pgr_sprint4.sql` e depois o CREATE novamente.

Exemplo de execuÃ§Ã£o no SQL*Plus:
```
sqlplus rm562312/fiap25@//oracle.fiap.com.br:1521/orcl @db/ddl/create_pgr_sprint4.sql
```

> O front-end ainda nÃ£o foi deployado; durante a sprint, use `http://localhost:5173` (CORS) para testes locais.
