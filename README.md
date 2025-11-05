# HC Teleconsulta API — Sprint 4

API REST em Quarkus responsável por gerenciar usuários, pacientes, profissionais e consultas do projeto HC Teleconsulta. Todo o escopo está alinhado aos requisitos da Sprint 4 (sem integrações extras ou automações de migração).

## Requisitos
- Java 21
- Maven 3.9+
- Acesso à VPN/whitelist da FIAP para conectar ao Oracle

## Estrutura do projeto
```
src/main/java/br/com/pmg/hc/
 ├── model/
 ├── dao/
 ├── service/
 ├── resource/
 ├── exception/
 └── dto/
src/main/resources/
db/ddl/
```

## Banco de Dados (Oracle)
1. Abrir `db/ddl/create_pgr_sprint4.sql` no SQL Developer (ou SQL*Plus) e executar integralmente.
2. Caso precise zerar a base, execute `db/ddl/drop_pgr_sprint4.sql` e depois o script de criação novamente.

## Execução local
1. Ajuste as credenciais em `src/main/resources/application.properties` se necessário (por padrão já apontam para o ambiente da FIAP).
2. Inicie a aplicação: `./mvnw clean quarkus:dev`
3. Teste `http://localhost:8080/hello` para checar se o servidor está de pé.
4. Acesse a documentação automática em `http://localhost:8080/q/swagger-ui`.

## CORS e front-end
- O front-end ainda não está deployado. Utilize `http://localhost:5173` durante os testes locais.
- No `application.properties` há um comentário indicando onde incluir a URL de produção quando o front estiver publicado.

## Endpoints principais (Sprint 4)
- `POST /usuarios` — cadastro de usuários (roles ADMIN, PACIENTE, PROFISSIONAL)
- `CRUD /pacientes`
- `CRUD /profissionais` + listagem de tipos (`GET /profissionais/tipos`)
- `CRUD /consultas` + atualização de status (`PUT /consultas/{id}/status`)

Todos os fluxos utilizam JDBC puro via `DataSource`, respeitando as entidades definidas nos scripts oficiais da sprint.

## Build
Para gerar o pacote (sem testes):
```
./mvnw clean package -DskipTests
```
Caso esteja em um ambiente sem Java 21, ajuste o `JAVA_HOME` antes de executar o comando.
