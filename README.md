# Radioscan API

API backend de uma aplicação de clínica/telemedicina: cadastro e login de pacientes/funcionários (com JWT e OAuth2 do Google), agendamento de consultas, e um fluxo de laudo de raio-X por IA (upload de imagem via URL pré-assinada no S3 e classificação automática por um modelo de Machine Learning).

---

## Stack

- **Java 25** + **Spring Boot 4.1.0**
- **Spring Data JPA** + **PostgreSQL** (driver `org.postgresql`)
- **Spring Security** com:
   - Autenticação própria via **JWT** (`io.jsonwebtoken` / jjwt)
   - **OAuth2 login com Google** (`spring-boot-starter-security-oauth2-client`)
- **springdoc-openapi** (Swagger UI) para documentação interativa da API
- **AWS SDK v2** (`software.amazon.awssdk`) — módulos `s3` e `sqs`, usados para
  gerar URLs pré-assinadas de upload de imagens
- **Lombok** para reduzir boilerplate (getters/setters/construtores)
- **springboot3-dotenv** para carregar variáveis de um arquivo `.env` em
  desenvolvimento local
- **H2** (banco em memória, usado nos testes)
- **Maven Wrapper** (`mvnw`/`mvnw.cmd`) — não é necessário ter o Maven
  instalado globalmente


## Arquitetura / estrutura do projeto

Pacote raiz: `com.douglaasph.clinic_api`

```
config/
  aws/        -> integração com S3 (URLs pré-assinadas de upload)
  security/   -> JWT, OAuth2, CORS, configuração geral de segurança
controllers/  -> endpoints REST + DTOs de entrada/saída
exceptions/   -> exceções de domínio + handler global (respostas de erro padronizadas)
models/
  entities/   -> entidades JPA
  entities/enums/ -> enums de domínio (status de agendamento, tipo, cargo, papel, status de processamento de IA)
repositories/ -> Spring Data JPA repositories
services/     -> regras de negócio
utils/        -> utilitários (ex: extrair usuário autenticado do contexto de segurança)
```


## Módulos do domínio

| Módulo | Controller | Entidades relacionadas | Resumo |
|---|---|---|---|
| Autenticação | `AuthController`, `RefreshTokenController` | `User`, `RefreshToken` | Login por email/senha, login via Google OAuth2, emissão e renovação de JWT |
| Usuários | `UserController` | `User`, `UserPrincipal` | Dados da conta autenticada, atualização de dados/senha |
| Pacientes | `PatientController` | `Patient` | Cadastro e dados de pacientes |
| Funcionários | `EmployeeController` | `Employee` | Cadastro e dados de funcionários (médicos/staff) |
| Agendamentos | `AppointmentController` | `Appointment` | Criação, listagem e revisão de consultas |
| Laudo de Raio-X | `XRayReportController` | `XRayReport` | Geração de URL pré-assinada para upload da imagem; consulta do laudo (resultado da IA + diagnóstico médico final) |
| Administração | `AdminController` | — | Métricas/dashboard administrativo |


### Fluxo de laudo por IA (resumo)

1. `XRayReportController` pede ao `StorageGateway` uma URL pré-assinada de
   upload (S3).
2. O cliente sobe a imagem direto no S3 usando essa URL.
3. A partir daí, o processamento é **assíncrono e externo a esta API**: o
   upload dispara um pipeline (S3 → SNS → SQS → Lambda com um modelo de IA)
   que roda a inferência e grava o resultado direto na coluna `aiResult` da
   tabela `x_ray_report`, via `UPDATE ... WHERE s3_key = ...`.
4. `processingStatus` (`ProcessingStatus`: `AWAITING_AI` → `PROCESSED_BY_IA` →
   `AWAITING_VALIDATION_BY_DOCTOR` → `VALIDATED_BY_DOCTOR`) reflete em que
   etapa o laudo está; o campo `finalMedicalDiagnosis` só é preenchido depois
   que um médico valida o resultado da IA.
5. `releasedToPatient` controla se o paciente já pode ver o laudo.
> A infraestrutura desse pipeline (bucket S3, SNS, SQS, Lambda, RDS) fica em
> um repositório Terraform separado, simulado localmente via LocalStack.
> Repositório: https://github.com/DouglaasPH/terraform-radioscan


## Pré-requisitos

- **JDK 25**
- **PostgreSQL** acessível (local, Docker, ou RDS/LocalStack) — não precisa
  instalar Maven, o projeto usa o Maven Wrapper (`./mvnw`)
- Opcional, só se for testar o upload de imagens: acesso a um endpoint S3
  compatível (LocalStack local ou AWS real)


## Configuração (`.env`)

O projeto usa [`springboot3-dotenv`](https://github.com/paulschwarz/spring-dotenv)
para carregar um `.env` na raiz do projeto em desenvolvimento local. Copie o
`.env.example` para `.env` e preencha:

```dotenv
# Banco de dados
DB_HOST=localhost
DB_PORT=5432
DB_NAME=clinicdb
DB_USER=root
DB_PASSWORD=root
 
# JWT
JWT_SECRET_KEY=<string longa e aleatória, mínimo ~62 caracteres>
JWT_EXPIRATION_IN_MINUTES=5
 
# OAuth2 - Google
OAUTH2_GOOGLE_CLIENT_ID=<client id do Google Cloud Console>
OAUTH2_GOOGLE_CLIENT_SECRET=<client secret do Google Cloud Console>
```

As variáveis de AWS (`aws.endpoint`, `aws.region`, `aws.s3.bucket-name`) têm
valor padrão direto no `application.properties`, mas também podem ser
sobrescritas por env var (`AWS_ENDPOINT`, `AWS_REGION`, `AWS_S3_BUCKET_NAME`)
— variável de ambiente do sistema operacional tem prioridade sobre o valor do
`application.properties` no Spring Boot.


## Rodando localmente

```powershell
.\mvnw spring-boot:run
```

A API sobe por padrão na porta definida em `SERVER_PORT`/`server.port`
(se não configurado, o padrão do Spring Boot é `8080`).

Com `spring.jpa.hibernate.ddl-auto=update` no `application.properties`, o
Hibernate cria/atualiza o schema do banco automaticamente na primeira
conexão — não é necessário rodar migrations manuais.


## Documentação da API (Swagger)

Com o `springdoc-openapi-starter-webmvc-ui` no classpath, a documentação
interativa fica disponível com a aplicação rodando em:

- Swagger UI: `http://localhost:<porta>/swagger-ui.html`
- OpenAPI JSON: `http://localhost:<porta>/v3/api-docs`


## Testes

```powershell
.\mvnw test
```


## Build e deploy (Docker + LocalStack)

Este repositório já inclui (na raiz):

- `Dockerfile` — multi-stage build usando o Maven Wrapper e `eclipse-temurin:25`
- `dockerignore`
- `build_and_push.ps1` — builda a imagem e publica no ECR do LocalStack

Fluxo resumido:

```powershell
.\build_and_push.ps1 -ProjectName "radioscan" -Tag "v1"
```

Isso builda a imagem localmente e publica no repositório ECR já criado pelo
Terraform. Depois, no projeto de infraestrutura, um `terraform apply` faz o
ECS puxar a imagem nova e redeployar o serviço automaticamente.

> Consulte o `README.md` do repositório de infraestrutura (https://github.com/DouglaasPH/terraform-radioscan) para o passo a
> passo completo (subir o LocalStack, aplicar a infra, publicar a imagem,
> variáveis de ambiente que o ECS injeta no container, etc.).


## Estrutura de pastas

```
src/main/java/com/douglaasph/clinic_api/
├── ClinicApiApplication.java
├── config/
│   ├── aws/
│   │   ├── S3Config.java
│   │   └── StorageGateway.java
│   └── security/
│       ├── CorsConfig.java
│       ├── JwtFilter.java
│       └── SecurityConfig.java
├── controllers/
│   ├── AdminController.java
│   ├── AppointmentController.java
│   ├── AuthController.java
│   ├── EmployeeController.java
│   ├── PatientController.java
│   ├── RefreshTokenController.java
│   ├── UserController.java
│   ├── XRayReportController.java
│   └── dto/
│       ├── admin/
│       ├── appointment/
│       ├── auth/
│       ├── employee/
│       ├── patient/
│       └── user/
├── exceptions/
│   ├── AppointmentConflictException.java
│   ├── DatabaseException.java
│   ├── ResourceNotFoundException.java
│   ├── TokenException.java
│   └── handler/
│       ├── GlobalExceptionHandler.java
│       └── StandardError.java
├── models/
│   └── entities/
│       ├── Appointment.java
│       ├── Employee.java
│       ├── Patient.java
│       ├── RefreshToken.java
│       ├── User.java
│       ├── UserPrincipal.java
│       ├── XRayReport.java
│       └── enums/
│           ├── AppointmentStatus.java
│           ├── AppointmentType.java
│           ├── Position.java
│           ├── ProcessingStatus.java
│           └── Roles.java
├── repositories/
│   ├── AppointmentRepository.java
│   ├── EmployeeRepository.java
│   ├── PatientRepository.java
│   ├── RefreshTokenRepository.java
│   ├── UserRepository.java
│   └── XRayReportRepository.java
├── services/
│   ├── AdminService.java
│   ├── AppointmentService.java
│   ├── AuthService.java
│   ├── EmployeeService.java
│   ├── JWTService.java
│   ├── PatientService.java
│   ├── RefreshTokenService.java
│   ├── UserService.java
│   └── XRayReportService.java
└── utils/
    └── SecurityUtils.java
```