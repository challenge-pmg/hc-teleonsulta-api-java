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

## Configuracao de credenciais (Seguranca)
- O arquivo `application.properties` nao armazena usuario/senha diretamente. Utilize variaveis de ambiente:
  - `DB_USERNAME`
  - `DB_PASSWORD`
- Exemplos:
  - **Linux/macOS:** `export DB_USERNAME=rm562312` e `export DB_PASSWORD=fiap25`
  - **Windows PowerShell:** `$Env:DB_USERNAME = 'rm562312'` e `$Env:DB_PASSWORD = 'fiap25'`
- Em producao, configure as mesmas variaveis no servidor (ou no provedor de hosting) para evitar leaks de credenciais.

## CORS e front-end
- Front-end publicado em `https://hc-teleconsulta-pg.vercel.app/`.
- Para desenvolvimento local ainda vale `http://localhost:5173`.
- `src/main/resources/application.properties` ja libera as duas origens; ajuste se a URL mudar.

## Deploy com Docker / Render
1. Gere a imagem localmente:
   ```bash
   docker build -t hc-teleconsulta-api .
   ```
2. Execute em qualquer host:
   ```bash
   docker run -p 8080:8080 \
     -e DB_USERNAME=rm562312 \
     -e DB_PASSWORD=fiap25 \
     hc-teleconsulta-api
   ```
3. Para publicar no Render:
   - Escolha **New → Web Service → Docker**.
   - Selecione este repositório (branch `main`); o Render usará o `Dockerfile`.
   - Defina as variáveis `DB_USERNAME`, `DB_PASSWORD` (e outras, se necessário).
   - Conclua o deploy e teste `GET /hello` ou `/q/swagger-ui` no domínio fornecido.

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
