# SIS-CONTROLE-FINANCEIRO - Guia de Desenvolvimento Local 💻

Sistema de controle financeiro desenvolvido com Spring Boot. Este guia foca na **execução local para desenvolvimento**.

> 📘 **Para CI/CD e Jenkins:** Consulte [README-PIPELINE.md](README-PIPELINE.md)

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Pré-requisitos](#pré-requisitos)
- [Configuração do Ambiente](#configuração-do-ambiente)
- [Como Executar](#como-executar)
- [Endpoints da Aplicação](#endpoints-da-aplicação)
- [Troubleshooting](#troubleshooting)

## 🎯 Sobre o Projeto

Sistema backend para controle financeiro desenvolvido em Spring Boot 3.4.11 com Java 17, utilizando PostgreSQL como banco de dados.

## 🚀 Tecnologias Utilizadas

- **Java 17** - Linguagem de programação
- **Spring Boot 3.4.11** - Framework principal
- **Spring Boot Actuator** - Métricas e health checks
- **PostgreSQL 17** - Banco de dados relacional
- **Hibernate/JPA** - ORM para persistência
- **Maven 3.11.0** - Gerenciamento de dependências
- **Lombok 1.18.30** - Redução de código boilerplate
- **SpringDoc OpenAPI 2.8.5** - Documentação da API
- **Docker** - Para rodar PostgreSQL localmente

## 📦 Pré-requisitos

Certifique-se de ter instalado em sua máquina:

### Sistema Operacional
- **Ubuntu 22.04.5 LTS (Jammy)** ou superior

### Software Necessário

- **Java 17**
  ```bash
  java -version
  ```

- **Maven 3.11.0** ou superior
  ```bash
  mvn -version
  ```

- **IntelliJ IDEA** (recomendado para desenvolvimento)
  - Download: https://www.jetbrains.com/idea/download/

- **Docker 20.10+**
  ```bash
  docker --version
  ```

- **Docker Compose 2.0+**
  ```bash
  docker compose version
  ```

- **Git**
  ```bash
  git --version
  ```

## ⚙️ Configuração do Ambiente

### 1. Clone o Repositório

```bash
git clone https://github.com/samueljdev/SIS-CONTROLE-FINANCEIRO.git
cd SIS-CONTROLE-FINANCEIRO
```

### 2. Criar application-local.properties

Crie o arquivo `src/main/resources/application-local.properties`:

```properties
# DATABASE
spring.datasource.url=jdbc:postgresql://localhost:5432/bdfinanceiro
spring.datasource.username=admin
spring.datasource.password=admin

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# LOGGING
logging.level.com.financeiro=DEBUG
```

**Nota:** Este arquivo está no `.gitignore` e não será versionado.

## 🏃 Como Executar no IntelliJ IDEA

**Passo 1: Iniciar o PostgreSQL**
```bash
docker compose up -d db-postgresql
```
> 🔹 **O que significa `-d`?**
> 
> O parâmetro `-d` (detached mode) faz o container rodar em **segundo plano**, liberando o terminal para você continuar usando.
> - **Com `-d`**: Container roda em background, terminal fica livre
> - **Sem `-d`**: Container roda em primeiro plano, terminal fica ocupado exibindo logs
>
> Para ver os logs depois: `docker logs -f db-postgresql`

> 💡 **Por que apenas o PostgreSQL?**
> 
> Rodamos apenas o banco de dados via Docker porque:
> - ✅ A **aplicação Spring Boot** roda diretamente no IntelliJ (permite debug, hot reload e desenvolvimento ágil)
> - ✅ Os **outros serviços** (Prometheus, Grafana, Loki, SonarQube) são para observabilidade/CI/CD, não necessários para desenvolvimento local
> - ✅ Isso **economiza recursos** da máquina e acelera o desenvolvimento
>
> Se precisar de toda a stack (observabilidade), use: `docker compose up -d`

**Passo 2: Abrir Projeto no IntelliJ**
1. Abra o IntelliJ IDEA
2. `File` → `Open` → Selecione a pasta do projeto
3. Aguarde a indexação e download de dependências Maven (pode levar alguns minutos)

**Passo 3: Configurar Run Configuration (Primeira vez)**
1. Localize a classe `FinanceiroApplication.java` em `src/main/java/com/financeiro/`
2. Clique com botão direito → `Run 'FinanceiroApplication'`
3. A aplicação vai tentar rodar e depois você configura corretamente

**Passo 4: Ajustar Configuração**
1. `Run` → `Edit Configurations`
2. Selecione `FinanceiroApplication`
3. Configure os seguintes campos:

   **VM options:**
   ```
   -Dspring.profiles.active=local
   ```

   **Environment variables:**
   ```
   DB_USERNAME=admin;DB_PASSWORD=admin
   ```

4. Clique em `Apply` e `OK`

**Passo 5: Executar a Aplicação**
- Clique no botão Run (▶️) ou pressione `Shift + F10`
- Aguarde a inicialização (cerca de 10-15 segundos)
- Verifique os logs no console do IntelliJ
- Quando aparecer "Started FinanceiroApplication", está pronto!

**Passo 6: Testar**
```bash
# Via terminal
curl http://localhost:8089/actuator/health

# Ou acesse no navegador:
# http://localhost:8089/actuator/health
# http://localhost:8089/swagger-ui/index.html
```

## ✅ Verificar se Está Rodando

```bash
# Health check
curl http://localhost:8089/actuator/health

# Resposta esperada: {"status":"UP"}

# Verificar no navegador:
# - Swagger UI: http://localhost:8089/swagger-ui/index.html
# - Actuator: http://localhost:8089/actuator
```

## 🌐 Endpoints da Aplicação

| Endpoint | Descrição |
|---|---|
| http://localhost:8089 | Aplicação principal |
| http://localhost:8089/swagger-ui/index.html | Documentação Swagger/OpenAPI |
| http://localhost:8089/actuator/health | Health check |
| http://localhost:8089/actuator/info | Informações da aplicação |
| http://localhost:8089/actuator/prometheus | Métricas Prometheus |
| http://localhost:5432 | PostgreSQL (via Docker) |

**Credenciais PostgreSQL:**
- **Host:** localhost
- **Port:** 5432
- **Database:** bdfinanceiro
- **Username:** admin
- **Password:** admin


## 📁 Estrutura do Projeto

```
SIS-CONTROLE-FINANCEIRO/
├── src/
│   ├── main/
│   │   ├── java/com/financeiro/
│   │   │   └── FinanceiroApplication.java
│   │   └── resources/
│   │       ├── application.properties (base)
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── application-local.properties (criar - não versionado)
│   └── test/
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README-LOCAL.md (este arquivo)
```

## 👥 Autores

- Thiago - [@thiago-jv](https://github.com/samueljdev)
- Samuel - [@samueljdev](https://github.com/samueljdev)

---

**Última atualização:** Fevereiro 2026
