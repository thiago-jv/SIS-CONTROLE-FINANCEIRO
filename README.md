# SIS-CONTROLE-FINANCEIRO

Sistema de controle financeiro desenvolvido com Spring Boot, incluindo pipeline CI/CD completa com Jenkins e stack de observabilidade (Prometheus + Grafana + Loki).

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Configuração do Ambiente](#configuração-do-ambiente)
- [Como Executar Localmente](#como-executar-localmente)
- [Pipeline Jenkins](#pipeline-jenkins)
- [Observabilidade](#observabilidade)
- [Endpoints Importantes](#endpoints-importantes)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Troubleshooting](#troubleshooting)

## 🎯 Sobre o Projeto

Sistema backend para controle financeiro desenvolvido em Spring Boot 3.4.11 com Java 17, utilizando PostgreSQL como banco de dados e Docker para containerização. Implementa práticas de DevOps com pipeline automatizada, análise de código e monitoramento completo.

## 🚀 Tecnologias Utilizadas

### Backend
- **Java 17** - Linguagem de programação
- **Spring Boot 3.4.11** - Framework principal
- **Spring Boot Actuator** - Métricas e health checks
- **PostgreSQL 17** - Banco de dados relacional
- **Hibernate/JPA** - ORM para persistência
- **Maven** - Gerenciamento de dependências

### DevOps & CI/CD
- **Docker & Docker Compose** - Containerização e orquestração
- **Jenkins** - Automação de CI/CD
- **SonarQube 9.0** - Análise de qualidade de código
- **Docker Hub** - Registro de imagens Docker

### Observabilidade
- **Prometheus** - Coleta de métricas
- **Grafana** - Visualização de métricas e logs
- **Loki** - Agregação de logs
- **Promtail** - Coleta de logs dos containers
- **Micrometer** - Instrumentação de métricas

## 🏗️ Arquitetura

```
┌─────────────────────────────────────────────────────────────┐
│                         GitHub                              │
│                    (Código Fonte)                           │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                        Jenkins                              │
│  ┌──────────┬──────────┬─────────┬──────────┬──────────┐  │
│  │  Build   │ SonarQube│ Quality │  Docker  │  Deploy  │  │
│  │          │ Analysis │  Gate   │   Push   │          │  │
│  └──────────┴──────────┴─────────┴──────────┴──────────┘  │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                     Docker Compose                          │
│  ┌──────────────┬─────────────┬──────────────────────────┐ │
│  │  PostgreSQL  │  Spring App │    Observabilidade       │ │
│  │   (5432)     │    (8089)   │  - Prometheus (9090)     │ │
│  │              │             │  - Grafana (3000)        │ │
│  │              │             │  - Loki (3100)           │ │
│  │              │             │  - Promtail              │ │
│  └──────────────┴─────────────┴──────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## 📦 Pré-requisitos

Antes de começar, certifique-se de ter instalado em sua máquina:

- **Java 17** ou superior
  ```bash
  java -version
  ```

- **Maven 3.6+**
  ```bash
  mvn -version
  ```

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

- **Jenkins** (opcional, apenas para CI/CD)

## ⚙️ Configuração do Ambiente

### 1. Clone o Repositório

```bash
git clone https://github.com/samueljdev/SIS-CONTROLE-FINANCEIRO.git
cd SIS-CONTROLE-FINANCEIRO
```

### 2. Configuração Local (application-local.properties)

Crie o arquivo `src/main/resources/application-local.properties` (não será versionado):

```properties
# DATABASE
spring.datasource.url=jdbc:postgresql://localhost:5432/bdfinanceiro
spring.datasource.username=admin
spring.datasource.password=admin

# Outras configurações específicas do seu ambiente local
```

### 3. Variáveis de Ambiente

As seguintes variáveis de ambiente são necessárias:

**Obrigatórias (Produção):**
- `DB_USERNAME` - Usuário do banco de dados
- `DB_PASSWORD` - Senha do banco de dados

**Opcional (com valores default):**
- `DB_URL` - URL de conexão com o banco
  - Dev: `jdbc:postgresql://localhost:5432/bdfinanceiro`
  - Prod: `jdbc:postgresql://db-postgresql:5432/bdfinanceiro`

## 🏃 Como Executar Localmente

### Opção 1: Executar Apenas o Banco de Dados

```bash
# Inicia apenas o PostgreSQL
docker compose up -d db-postgresql

# Compila a aplicação
mvn clean package -DskipTests

# Executa a aplicação localmente
java -jar target/financeiro-backend.jar --spring.profiles.active=dev
```

### Opção 2: Executar Toda a Stack com Docker Compose

```bash
# Sobe todos os serviços (app + banco + observabilidade)
docker compose up -d

# Verificar logs
docker compose logs -f app-financeiro

# Parar todos os serviços
docker compose down
```

### Opção 3: Build e Execução Manual do Container

```bash
# Build da aplicação
mvn clean package -DskipTests

# Build da imagem Docker
docker build -t sis-financeiro:local .

# Executar container
docker run -d \
  --name app-financeiro \
  --network sis-controle-financeiro_network-new-financeiro \
  -p 8089:8089 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e DB_USERNAME=admin \
  -e DB_PASSWORD=admin \
  sis-financeiro:local
```

## 🔄 Pipeline Jenkins

### Configuração Inicial do Jenkins

1. **Instalar Plugins Necessários:**
   - Docker Pipeline
   - SonarQube Scanner
   - Git
   - Credentials Binding

2. **Configurar Credentials:**

   Acesse: `Manage Jenkins` → `Credentials` → `Global` → `Add Credentials`

   **Credentials necessárias:**
   
   | ID | Tipo | Descrição | Valor |
   |---|---|---|---|
   | `SONAR_QUBE_TOKEN` | Secret text | Token do SonarQube | `seu-token-aqui` |
   | `DB_USERNAME` | Secret text | Usuário do banco | `admin` |
   | `DB_PASSWORD` | Secret text | Senha do banco | `admin` |
   | `dockerhub` | Username/Password | Credenciais Docker Hub | `usuario/senha` |
   | `GIT` | Username/Password | Credenciais Git | `usuario/token` |

3. **Configurar SonarQube Scanner:**
   - `Manage Jenkins` → `Global Tool Configuration`
   - Adicionar `SonarQube Scanner` com nome: `SONAR_QUBE_SCANNER_SIS_FINACEIRO`

4. **Configurar SonarQube Server:**
   - `Manage Jenkins` → `Configure System` → `SonarQube servers`
   - Nome: `SONAR_QUBE_SIS-FINANCEIRO`
   - URL: `http://localhost:9000`

### Criar Pipeline no Jenkins

1. `New Item` → `Pipeline`
2. Nome: `DEV-PEPILINE-SIS-FINANCEIRO`
3. Em `Pipeline`:
   - Definition: `Pipeline script from SCM`
   - SCM: `Git`
   - Repository URL: `https://github.com/samueljdev/SIS-CONTROLE-FINANCEIRO`
   - Credentials: Selecione a credential `GIT`
   - Branch: `*/develop` (ou deixe em branco - a pipeline escolhe automaticamente)
   - Script Path: `Jenkinsfile`

### Executar Pipeline

1. Acesse o job criado
2. Clique em `Build with Parameters`
3. Selecione o ambiente:
   - `dev` - Desenvolvimento (branch: **develop**)
   - `prod` - Produção (branch: **main**)
4. Clique em `Build`

**Importante:** A pipeline automaticamente faz checkout da branch correta baseada no ambiente selecionado:
- Ambiente `dev` → Branch `develop`
- Ambiente `prod` → Branch `main`

### Stages da Pipeline

A pipeline executa as seguintes etapas:

1. **Checkout** - Seleciona a branch correta (develop ou main) baseado no ambiente
2. **Build** - Compila o código com Maven
3. **SonarQube Analysis** - Analisa qualidade do código
4. **Quality Gate** - Valida critérios de qualidade
5. **Build Docker Image** - Cria imagem Docker
6. **Push Docker Image** - Envia imagem para Docker Hub
7. **Deploy** - Executa container com a aplicação

## 📊 Observabilidade

### Prometheus (Métricas)

Acesse: http://localhost:9090

**Queries úteis:**
```promql
# Taxa de requisições HTTP
rate(http_server_requests_seconds_count[5m])

# Uso de memória da JVM
jvm_memory_used_bytes

# Tempo de resposta (percentil 95)
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))
```

### Grafana (Dashboards)

Acesse: http://localhost:3000
- **Usuário:** `admin`
- **Senha:** `admin`

**Datasources configurados:**
- Prometheus: `http://prometheus:9090`
- Loki: `http://loki:3100`

**Importar Dashboards:**

Os arquivos JSON dos dashboards estão localizados em: `src/main/resources/grafana-json/`

1. Dashboards → Import
2. Upload JSON:
   - `dashboard-startup-spring-boot.json` - Dashboard de métricas de startup
   - `dashboard-startup-spring-boot-logs.json` - Dashboard de logs

### Loki (Logs)

Os logs dos containers Docker são automaticamente coletados pelo Promtail e enviados para o Loki.

**Visualizar logs no Grafana:**
1. Explore → Loki
2. Query: `{container_name="app-financeiro"}`

### SonarQube (Qualidade de Código)

Acesse: http://localhost:9000
- **Usuário:** `admin`
- **Senha:** `admin`

## 🌐 Endpoints Importantes

### Aplicação

| Endpoint | Descrição |
|---|---|
| http://localhost:8089 | Aplicação principal |
| http://localhost:8089/swagger-ui/index.html | Documentação Swagger |
| http://localhost:8089/actuator/health | Health check |
| http://localhost:8089/actuator/prometheus | Métricas Prometheus |
| http://localhost:8089/actuator/info | Informações da aplicação |

### Infraestrutura

| Serviço | URL | Credenciais |
|---|---|---|
| **Jenkins** | http://localhost:8080 | admin/admin |
| **SonarQube** | http://localhost:9000 | admin/admin |
| **Grafana** | http://localhost:3000 | admin/admin |
| **Prometheus** | http://localhost:9090 | - |
| **PostgreSQL** | localhost:5432 | admin/admin |

## 🔐 Variáveis de Ambiente

### application.properties (Base)
Configurações comuns para todos os ambientes.

### application-dev.properties (Desenvolvimento)
- **Database:** localhost:5432
- **DDL:** auto-update
- **Logs:** DEBUG
- **Actuator:** Todos endpoints expostos

### application-prod.properties (Produção)
- **Database:** db-postgresql:5432
- **DDL:** validate apenas
- **Logs:** WARN
- **Actuator:** Apenas endpoints essenciais
- **Connection Pool:** Otimizado (20 conexões)
- **Performance:** Batch inserts habilitado

### application-local.properties (Local - não versionado)
Sobrescreve configurações para desenvolvimento local.
**Este arquivo está no .gitignore e nunca deve ser commitado.**

## 🐛 Troubleshooting

### Problema: Aplicação não conecta ao banco

**Solução:**
```bash
# Verifique se o PostgreSQL está rodando
docker ps | grep db-postgresql

# Se não estiver, inicie:
docker compose up -d db-postgresql

# Verifique os logs:
docker logs db-postgresql
```

### Problema: Erro de credenciais no Jenkins

**Solução:**
1. Verifique se as credentials foram criadas corretamente
2. IDs devem ser exatamente: `SONAR_QUBE_TOKEN`, `DB_USERNAME`, `DB_PASSWORD`, `dockerhub`
3. Re-execute a pipeline após criar/corrigir

### Problema: Prometheus não coleta métricas

**Solução:**
```bash
# Verifique se a aplicação expõe métricas
curl http://localhost:8089/actuator/prometheus

# Verifique configuração do Prometheus
docker exec prometheus cat /etc/prometheus/prometheus.yml

# Rebuild da imagem Docker se necessário
docker compose build --no-cache app-financeiro
docker compose up -d app-financeiro
```

### Problema: Encoding no Maven Build

**Solução:**
Se houver erro `MalformedInputException`, verifique encoding dos arquivos `.properties`:
```bash
# Todos devem ser UTF-8 sem BOM
file -i src/main/resources/*.properties
```

### Problema: Container não inicia

**Solução:**
```bash
# Visualize os logs completos
docker logs app-financeiro

# Verifique variáveis de ambiente
docker exec app-financeiro env | grep -E 'SPRING|DB'

# Reinicie o container
docker restart app-financeiro
```

## 📁 Estrutura do Projeto

```
SIS-CONTROLE-FINANCEIRO/
├── src/
│   ├── main/
│   │   ├── java/com/financeiro/
│   │   │   └── FinanceiroApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── application-local.properties (não versionado)
│   └── test/
├── docker-compose.yml
├── Dockerfile
├── Jenkinsfile
├── pom.xml
├── prometheus.yml
├── loki-config.yml
├── promtail-config.yml
├── grafana-dashboard-*.json
├── checkstyle.xml
└── README.md
```

![PAGE](https://github.com/thiago-jv/SIS-CONTROLE-FINANCEIRO/blob/main/page.png)


## 📝 Licença

Este projeto é de uso interno e educacional.

## 👥 Autores

Thiago - [@thiago-jv](https://github.com/samueljdev)
Samuel - [@samueljdev/](https://github.com/samueljdev)

---

**Última atualização:** Fevereiro 2026
