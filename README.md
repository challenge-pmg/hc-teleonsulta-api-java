# HC Teleconsulta API — Sprint 4

API REST em Quarkus responsável por gerenciar usuários, pacientes, profissionais e consultas do projeto HC Teleconsulta. Todo o escopo está alinhado aos requisitos da Sprint 4 (sem integrações extras ou automações de migração).

## Requisitos
- Java 21
- Maven 3.9+
- Acesso VPN/whitelist da FIAP para conectar ao Oracle

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
1. Abra `db/ddl/create_pgr_sprint4.sql` no SQL Developer (ou SQL*Plus) e execute integralmente.
2. Caso precise zerar a base, execute `db/ddl/drop_pgr_sprint4.sql` e depois rode o script de criação novamente.

## Execução local
1. Ajuste as credenciais em `src/main/resources/application.properties` se necessário (por padrão já apontam para o ambiente da FIAP).
2. Inicie a aplicação: `./mvnw clean quarkus:dev`
3. Teste `http://localhost:8080/hello` para verificar se o servidor está no ar.
4. Acesse a documentação automática em `http://localhost:8080/q/swagger-ui`.

## Configuração de credenciais (Segurança)
- O arquivo `application.properties` não armazena usuário/senha diretamente. Utilize variáveis de ambiente:
  - `DB_USERNAME`
  - `DB_PASSWORD`
- Exemplos:
  - **Linux/macOS:** `export DB_USERNAME=rm562312` e `export DB_PASSWORD=fiap25`
  - **Windows PowerShell:** `$Env:DB_USERNAME = 'rm562312'` e `$Env:DB_PASSWORD = 'fiap25'`
- Em produção, configure as mesmas variáveis no servidor (ou no provedor de hosting) para evitar vazamento de credenciais.

## CORS e front-end
- Front-end publicado em `https://hc-teleconsulta-pg.vercel.app/`.
- Para desenvolvimento local, utilize `http://localhost:5173`.
- `src/main/resources/application.properties` já libera as duas origens; ajuste se a URL mudar.

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
   - Escolha **New -> Web Service -> Docker**.
   - Selecione este repositório (branch `main`); o Render utilizará o `Dockerfile`.
   - Defina as variáveis `DB_USERNAME`, `DB_PASSWORD` (e outras, se necessário).
   - Conclua o deploy e teste `GET /hello` ou `/q/swagger-ui` no domínio fornecido.

## Teste / avaliação com outro Oracle
- Execute `db/ddl/create_pgr_sprint4.sql` no schema Oracle escolhido (SQL Developer ou SQL*Plus) para criar as tabelas. Use `drop_pgr_sprint4.sql` caso precise limpar antes de recriar.
- Antes de iniciar a API, defina as variáveis de ambiente:
  - `DB_USERNAME` e `DB_PASSWORD` para o usuário e senha do schema.
  - Opcional: `QUARKUS_DATASOURCE_JDBC_URL` se a URL não for `jdbc:oracle:thin:@//oracle.fiap.com.br:1521/orcl`.
- Execução local com Maven:
  ```bash
  export DB_USERNAME=usuario_proprio
  export DB_PASSWORD=senha_propria
  export QUARKUS_DATASOURCE_JDBC_URL=jdbc:oracle:thin:@//host:porta/service
  ./mvnw clean quarkus:dev
  ```
  No PowerShell, use `$Env:VAR = 'valor'` em vez de `export`.
- Execução com Docker/Render: informe as mesmas variáveis na configuração do container/serviço antes de subir a imagem.
- Com a API no ar, valide `GET /hello`, `GET /q/swagger-ui` e os CRUDs (`/usuarios`, `/pacientes`, `/profissionais`, `/consultas`) no Insomnia ou Postman.

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
