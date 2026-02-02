# SIS-CONTROLE-FINANCEIRO

Sistema de controle financeiro desenvolvido com Spring Boot 3.4.11, Java 17 e PostgreSQL.

---

## 📚 Guias de Uso

Escolha o guia apropriado para sua necessidade:

### 💻 [README-LOCAL.md](README-LOCAL.md) - Desenvolvimento Local
Guia completo para rodar a aplicação localmente no IntelliJ IDEA ou via linha de comando.
- Configuração do ambiente local
- Execução no IntelliJ IDEA
- Troubleshooting de desenvolvimento

### 🚀 [README-PIPELINE.md](README-PIPELINE.md) - CI/CD com Jenkins
Guia completo para configurar e executar a pipeline Jenkins com SonarQube e observabilidade.
- Configuração do Jenkins
- Pipeline CI/CD completa
- Monitoramento e observabilidade

---

## 🎯 Sobre o Projeto

Sistema backend para controle financeiro com:
- ✅ API REST com Spring Boot
- ✅ Banco de dados PostgreSQL
- ✅ Documentação Swagger/OpenAPI
- ✅ Pipeline CI/CD automatizada
- ✅ Análise de código com SonarQube
- ✅ Observabilidade (Prometheus + Grafana + Loki)

## 🚀 Tecnologias

**Backend:**
- Java 17
- Spring Boot 3.4.11
- PostgreSQL 17
- Maven 3.11.0
- Lombok 1.18.30
- SpringDoc OpenAPI 2.8.5

**DevOps:**
- Docker & Docker Compose
- Jenkins 2.479.1
- SonarQube 9.0-community
- Prometheus, Grafana, Loki

## 📦 Pré-requisitos

- Ubuntu 22.04.5 LTS (Jammy)
- Java 17
- Maven 3.11.0
- Docker 20.10+
- Docker Compose 2.0+
- IntelliJ IDEA (recomendado)

## ⚡ Quick Start

### Desenvolvimento Local
```bash
# 1. Clone o projeto
git clone https://github.com/samueljdev/SIS-CONTROLE-FINANCEIRO.git
cd SIS-CONTROLE-FINANCEIRO

# 2. Inicie o PostgreSQL
docker compose up -d db-postgresql

# 3. Abra no IntelliJ IDEA e configure:
#    Run → Edit Configurations → Environment variables:
#    DB_USERNAME=admin;DB_PASSWORD=admin
#    VM options: -Dspring.profiles.active=local

# 4. Execute a aplicação (Shift + F10)
```

### Acessar Aplicação
- API: http://localhost:8089
- Swagger: http://localhost:8089/swagger-ui/index.html
- Health: http://localhost:8089/actuator/health

## 📖 Documentação Completa

- **[README-LOCAL.md](README-LOCAL.md)** → Desenvolvimento local detalhado
- **[README-PIPELINE.md](README-PIPELINE.md)** → CI/CD e Jenkins detalhado

## 👥 Autores

- Thiago - [@thiago-jv](https://github.com/samueljdev)
- Samuel - [@samueljdev](https://github.com/samueljdev)

---

**Última atualização:** Fevereiro 2026
