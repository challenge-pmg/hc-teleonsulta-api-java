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

3. Para ter dados de exemplo no front, execute `db/ddl/load_sample_data.sql` (o arquivo limpa as tabelas e insere pacientes, profissionais, disponibilidades e consultas ficticias).



## Execução local

1. Ajuste as credenciais em `src/main/resources/application.properties` se necessário (por padrão já apontam para o ambiente da FIAP).

2. Inicie a aplicação: `./mvnw clean quarkus:dev`

3. Teste `http://localhost:8080/hello` para verificar se o servidor está no ar.

4. Acesse a documentação automática em `http://localhost:8080/q/swagger-ui`.



## Configura???o de credenciais (Seguran??a)



- O arquivo `application.properties` n?o armazena usu?rio/senha diretamente. Utilize vari?veis de ambiente:



  - `DB_USERNAME`



  - `DB_PASSWORD`



- Para controlar quem pode criar contas de profissional, ajuste `app.profissional.codigo-acesso` (ou a vari?vel `PROFISSIONAL_ACCESS_CODE`). Sem o c?digo correto o cadastro retorna erro 400.



- Exemplos:



  - **Linux/macOS:** `export DB_USERNAME=rm562312` e `export DB_PASSWORD=fiap25`



  - **Windows PowerShell:** `$Env:DB_USERNAME = 'rm562312'` e `$Env:DB_PASSWORD = 'fiap25'`



- Em produ???o, configure as mesmas vari?veis no servidor (ou no provedor de hosting) para evitar vazamento de credenciais.







## CORS e front-end

- Front-end publicado em `https://hc-teleconsulta-pg.vercel.app/`.

- Para desenvolvimento local, utilize `http://localhost:5173`.

- `src/main/resources/application.properties` já libera as duas origens; ajuste se a URL mudar.





- A API publicada no Render est? dispon?vel em `https://hc-teleonsulta-api-java.onrender.com`. Utilize esse host no front enquanto ele estiver ativo.

- Para confirmar que o CORS est? ativo, execute:

  ```bash
  curl -I -X OPTIONS https://hc-teleonsulta-api-java.onrender.com/auth/login \
       -H "Origin: http://localhost:5173"
  ```

  A resposta deve incluir `Access-Control-Allow-Origin: http://localhost:5173` e os m?todos liberados; se o header n?o aparecer, redeploy o servi?o no Render para aplicar o `application.properties` atual.

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



## Login e perfis



- O sistema agora trabalha apenas com dois perfis (`PACIENTE` e `PROFISSIONAL`). Profissionais englobam m??dicos, equipe de sa??de e colaborares administrativos.



- `POST /auth/login` recebe `{ "email": "...", "senha": "..." }` e devolve o `usuarioId`, o perfil e (quando aplic?vel) o `pacienteId` ou `profissionalId`.



- Para criar um profissional ? obrigat?rio informar `codigoFuncionario` no corpo da requisi??o; o valor precisa bater com `app.profissional.codigo-acesso`, impedindo que visitantes criem perfis internos.







## Agenda e consultas



- A agenda de cada profissional ? montada via `POST /disponibilidades` informando `profissionalId` e `dataHora`. Apenas slots futuros s?o aceitos e o endpoint `DELETE /disponibilidades/{id}` remove janelas ainda livres.



- `GET /disponibilidades?profissionalId=1&dataInicial=2025-11-10` retorna somente hor?rios livres e futuros daquele profissional para que o front mostre as op?es ao paciente.



- Para agendar, o paciente envia `POST /consultas` com `pacienteId`, `profissionalId`, `disponibilidadeId` e `tipoConsulta`. Em consultas presenciais o `linkAcesso` � omitido, e em teleconsultas o backend gera automaticamente esse link (base configurada em `app.teleconsulta.base-url`). O backend garante que o slot exista, perten?a ao profissional informado e esteja livre antes de reservar.



- As consultas n?o s?o mais listadas de forma global. Utilize `GET /consultas?pacienteId={id}` ou `GET /consultas?profissionalId={id}`; o backend valida o filtro e s? devolve as consultas daquela pessoa.



- Ao cancelar (`PUT /consultas/{id}/status` com `CANCELADA`) ou excluir uma consulta, o hor?rio volta automaticamente para a lista de disponibilidades.







## Endpoints principais



- `POST /auth/login`



- `CRUD /pacientes`



- `CRUD /profissionais` + `GET /profissionais/tipos` (cadastro protegido por c?digo)



- `CRUD /consultas`, `PUT /consultas/{id}/status` e listagem filtrada por paciente/profissional



- `GET/POST/DELETE /disponibilidades` para gerenciar a agenda











## Build

Para gerar o pacote (sem testes):

```

./mvnw clean package -DskipTests

```

Caso esteja em um ambiente sem Java 21, ajuste o `JAVA_HOME` antes de executar o comando.

