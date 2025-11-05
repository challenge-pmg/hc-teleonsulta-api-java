# HC Teleconsulta API  Sprint 4

API REST em Quarkus responsvel por gerenciar usurios, pacientes, profissionais e consultas do projeto HC Teleconsulta. Todo o escopo est alinhado aos requisitos da Sprint 4 (sem integraes extras ou automaes de migrao).

## Requisitos
- Java 21
- Maven 3.9+
- Acesso  VPN/whitelist da FIAP para conectar ao Oracle

## Estrutura do projeto
```
src/main/java/br/com/pmg/hc/
  model/
  dao/
  service/
  resource/
  exception/
  dto/
src/main/resources/
db/ddl/
```

## Banco de Dados (Oracle)
1. Abrir `db/ddl/create_pgr_sprint4.sql` no SQL Developer (ou SQL*Plus) e executar integralmente.
2. Caso precise zerar a base, execute `db/ddl/drop_pgr_sprint4.sql` e depois o script de criao novamente.

## Execuo local
1. Ajuste as credenciais em `src/main/resources/application.properties` se necessrio (por padro j apontam para o ambiente da FIAP).
2. Inicie a aplicao: `./mvnw clean quarkus:dev`
3. Teste `http://localhost:8080/hello` para checar se o servidor est de p.
4. Acesse a documentao automtica em `http://localhost:8080/q/swagger-ui`.

## CORS e front-end
- O front-end ainda no est deployado. Utilize `http://localhost:5173` durante os testes locais.
- No `application.properties` h um comentrio indicando onde incluir a URL de produo quando o front estiver publicado.

## Endpoints principais (Sprint 4)
- `POST /usuarios`  cadastro de usurios (roles ADMIN, PACIENTE, PROFISSIONAL)
- `CRUD /pacientes`
- `CRUD /profissionais` + listagem de tipos (`GET /profissionais/tipos`)
- `CRUD /consultas` + atualizao de status (`PUT /consultas/{id}/status`)

Todos os fluxos utilizam JDBC puro via `DataSource`, respeitando as entidades definidas nos scripts oficiais da sprint.

## Build
Para gerar o pacote (sem testes):
```
./mvnw clean package -DskipTests
```
Caso esteja em um ambiente sem Java 21, ajuste o `JAVA_HOME` antes de executar o comando.
