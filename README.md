# HC Teleconsulta API

API REST construída com Quarkus 3.29.0 e Java 21 para gerenciar pacientes, profissionais, consultas e feedbacks da sprint 4 do projeto HC Teleconsulta.

## ✅ Pré-requisitos
- Java 21 instalado
- Maven 3.9+
- Acesso à VPN/whitelist da FIAP para conectar no banco Oracle

## 🚀 Executando em ambiente local
1. Instale as dependências e suba a aplicação em modo dev:
   ```bash
   ./mvnw clean quarkus:dev
   ```
2. A API ficará disponível em `http://localhost:8080` e o Swagger em `http://localhost:8080/q/swagger-ui`.

> A aplicação utiliza o banco de dados Oracle da FIAP. As credenciais já estão configuradas em `src/main/resources/application.properties`.

## 🌐 Integração com o front-end
- O front-end em React + Vite + Tailwind ainda **não foi deployado**.
- Durante o desenvolvimento, as requisições serão originadas de `http://localhost:5173`. Essa URL já está liberada via CORS.
- Quando o front for publicado (por exemplo, na Vercel), basta adicionar a nova origem no `application.properties`, na propriedade `quarkus.http.cors.origins`.

## 🧱 Tecnologias e extensões utilizadas
- Quarkus REST (`quarkus-rest`, `quarkus-rest-jackson`)
- Hibernate ORM & Validator
- Agroal Connection Pool
- JDBC Oracle
- SmallRye OpenAPI

## 📁 Estrutura principal
```
src/main/java/br/com/pmg/hc/
├── model/
├── dao/
├── service/
├── resource/
├── exception/
└── dto/
```

## 🔒 Controle de acesso
O modelo de domínio utiliza herança de `Usuario` para `Paciente` e `Profissional`, com atribuição de roles (`ADMIN`, `PACIENTE`, `PROFISSIONAL`).

## 🧪 Testes
Ainda não há suíte automatizada. Utilize ferramentas como Insomnia ou Postman para validar as rotas expostas.
