# SIS-CONTROLE-FINANCEIRO - Guia de CI/CD com Jenkins 🚀

Sistema de controle financeiro com pipeline completa Jenkins + SonarQube + Docker Hub + Observabilidade.

> 💻 **Para desenvolvimento local:** Consulte [README-LOCAL.md](README-LOCAL.md)

## 📋 Índice

- [Arquitetura CI/CD](#arquitetura-cicd)
- [Tecnologias DevOps](#tecnologias-devops)
- [Pré-requisitos](#pré-requisitos)
- [Configuração Inicial](#configuração-inicial)
- [Configurar Jenkins](#configurar-jenkins)
- [Executar Pipeline](#executar-pipeline)
- [Observabilidade](#observabilidade)
- [Troubleshooting](#troubleshooting)

## 🏗️ Arquitetura CI/CD

```
┌─────────────────────────────────────────────────────────────┐
│                         GitHub                              │
│              github.com/samueljdev/SIS-...                  │
└────────────────────┬────────────────────────────────────────┘
                     │ git pull
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                     Jenkins 2.479.1                         │
│  ┌──────────┬──────────┬─────────┬──────────┬──────────┐  │
│  │ Checkout │ SonarQube│ Quality │  Docker  │  Deploy  │  │
│  │  Branch  │ Analysis │  Gate   │   Build  │  to Env  │  │
│  └──────────┴──────────┴─────────┴──────────┴──────────┘  │
│       dev → develop    |         verify     push         │
│      prod → main       |                    dockerhub    │
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

## 🚀 Tecnologias DevOps

- **Jenkins 2.479.1** - Automação de CI/CD
- **SonarQube 9.0-community** - Análise de qualidade de código
- **Docker 20.10+** - Containerização
- **Docker Compose 2.0+** - Orquestração
- **Docker Hub** - Registro de imagens
- **Prometheus (latest)** - Coleta de métricas
- **Grafana (latest)** - Visualização de métricas e logs
- **Loki (latest)** - Agregação de logs
- **Promtail (latest)** - Coleta de logs dos containers

## 📦 Pré-requisitos

### Sistema Operacional
- **Ubuntu 22.04.5 LTS (Jammy)**
  ```bash
  lsb_release -a
  ```

### Software Necessário

- **Jenkins 2.479.1**
  ```bash
  jenkins --version
  ```

- **Docker 20.10+**
  ```bash
  docker --version
  ```

- **Docker Compose 2.0+**
  ```bash
  docker compose version
  ```

- **Conta Docker Hub**
  - Acesse: https://hub.docker.com
  - Crie uma conta e anote username/password

- **Conta GitHub com Personal Access Token**
  - Acesse: https://github.com/settings/tokens
  - Gere um token com permissões de `repo`
  - **Importante:** Salve o token, ele não será exibido novamente!

## ⚙️ Configuração Inicial

### 1. Clone o Repositório

```bash
git clone https://github.com/samueljdev/SIS-CONTROLE-FINANCEIRO.git
cd SIS-CONTROLE-FINANCEIRO
```

### 2. Subir SonarQube

```bash
# Iniciar SonarQube
docker compose up -d sonarqube
```

> 🔹 **O parâmetro `-d` (detached mode)** faz o container rodar em segundo plano, liberando o terminal.

```bash
# Aguarde 2-3 minutos (SonarQube demora para iniciar)
# Verifique logs:
docker logs -f sonar

# Aguarde a mensagem: "SonarQube is operational"
```

> ⏱️ **Importante:** SonarQube pode demorar 2-3 minutos para inicializar completamente. Seja paciente!

**Acesse:** http://localhost:9000
- **Login:** admin
- **Senha:** admin
- **Será solicitado alterar a senha no primeiro acesso**

> ⚠️ **Atenção:** Anote a nova senha! Você precisará dela para gerar o token.

### 3. Gerar Token do SonarQube

1. Acesse: http://localhost:9000
2. Login com `admin` e a nova senha
3. Clique no ícone do usuário (canto superior direito) → `My Account`
4. Aba `Security`
5. Em `Generate Tokens`:
   - **Name:** jenkins-token
   - **Type:** Global Analysis Token
   - **Expires:** Sem expiração
6. Clique em `Generate`
7. **COPIE O TOKEN** (não será exibido novamente)

Exemplo de token: `sqp_a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0`

## 🔧 Configurar Jenkins

### Passo 1: Instalar Plugins

1. Acesse Jenkins: http://localhost:8080
2. Login (admin/admin ou suas credenciais)
3. `Manage Jenkins` → `Manage Plugins` → `Available`
4. Pesquise e instale:
   - ✅ Docker Pipeline
   - ✅ SonarQube Scanner
   - ✅ Git
   - ✅ Credentials Binding
   - ✅ Pipeline
5. Reinicie o Jenkins se necessário

### Passo 2: Configurar Credentials

Acesse: `Manage Jenkins` → `Credentials` → `System` → `Global credentials` → `Add Credentials`

> 🔐 **Sobre Credentials no Jenkins:**
> 
> As credentials são armazenadas de forma segura e criptografada no Jenkins. Elas permitem que a pipeline acesse:
> - SonarQube (para análise de código)
> - Docker Hub (para publicar imagens)
> - GitHub (para clonar código)
> - Banco de dados (para deploy)

**Adicione as seguintes credentials:**

#### 1. Token do SonarQube
- **Kind:** Secret text
- **Scope:** Global
- **Secret:** `sqp_...` (token copiado anteriormente)
- **ID:** `SONAR_QUBE_TOKEN`
- **Description:** Token do SonarQube para análise
- Clique em `Create`

#### 2. Usuário do Banco
- **Kind:** Secret text
- **Scope:** Global
- **Secret:** `admin`
- **ID:** `DB_USERNAME`
- **Description:** Usuário do PostgreSQL
- Clique em `Create`

#### 3. Senha do Banco
- **Kind:** Secret text
- **Scope:** Global
- **Secret:** `admin`
- **ID:** `DB_PASSWORD`
- **Description:** Senha do PostgreSQL
- Clique em `Create`

#### 4. Docker Hub
- **Kind:** Username with password
- **Scope:** Global
- **Username:** seu_usuario_dockerhub
- **Password:** sua_senha_dockerhub
- **ID:** `dockerhub`
- **Description:** Credenciais Docker Hub
- Clique em `Create`

#### 5. GitHub
- **Kind:** Username with password
- **Scope:** Global
- **Username:** seu_usuario_github
- **Password:** seu_token_github (Personal Access Token)
- **ID:** `GIT`
- **Description:** Credenciais GitHub
- Clique em `Create`

### Passo 3: Configurar SonarQube Scanner

1. `Manage Jenkins` → `Global Tool Configuration`
2. Procure por `SonarQube Scanner`
3. Clique em `Add SonarQube Scanner`
4. **Name:** `SONAR_QUBE_SCANNER_SIS_FINACEIRO`
5. Marque `Install automatically`
6. Versão: Deixe a mais recente
7. Clique em `Save`

### Passo 4: Configurar SonarQube Server

1. `Manage Jenkins` → `Configure System`
2. Procure por `SonarQube servers`
3. Marque `Environment variables` → `Enable injection of SonarQube server configuration`
4. Clique em `Add SonarQube`
5. **Name:** `SONAR_QUBE_SIS-FINANCEIRO`
6. **Server URL:** `http://localhost:9000`
7. **Server authentication token:** Selecione `SONAR_QUBE_TOKEN`
8. Clique em `Save`

### Passo 5: Criar Pipeline no Jenkins

1. Jenkins → `New Item`
2. **Nome:** `DEV-PIPELINE-SIS-FINANCEIRO`
3. Tipo: `Pipeline`
4. Clique em `OK`
5. Na configuração do pipeline:

**General:**
- ✅ Marque `This project is parameterized`
- Clique em `Add Parameter` → `Choice Parameter`
  - **Name:** ENVIRONMENT
  - **Choices:** (um por linha)
    ```
    dev
    prod
    ```
  - **Description:** Selecione o ambiente de deploy

**Pipeline:**
- **Definition:** `Pipeline script from SCM`
- **SCM:** `Git`
- **Repository URL:** `https://github.com/samueljdev/SIS-CONTROLE-FINANCEIRO`
- **Credentials:** Selecione `GIT`
- **Branches to build:**
  - Deixe em branco (a pipeline escolhe automaticamente)
  - Ou especifique: `*/develop` para dev e `*/main` para prod
- **Script Path:** `Jenkinsfile`

6. Clique em `Save`

## ▶️ Executar Pipeline

### Primeira Execução

1. Acesse o job: `DEV-PIPELINE-SIS-FINANCEIRO`
2. Clique em `Build with Parameters`
3. **Selecione o ambiente:**
   - `dev` → Faz checkout da branch `develop`
   - `prod` → Faz checkout da branch `main`
4. Clique em `Build`

### Stages da Pipeline

A pipeline executa automaticamente as seguintes etapas:

1. ✅ **Checkout** - Clona a branch correta (develop ou main) baseado no ambiente selecionado
2. ✅ **Build** - Compila o código com Maven (`mvn clean package -DskipTests`)
3. ✅ **SonarQube Analysis** - Envia código para análise de qualidade no SonarQube
4. ✅ **Quality Gate** - Aguarda resultado do SonarQube (até 10 minutos) e valida se passou nos critérios
5. ✅ **Build Docker Image** - Cria imagem Docker com tag `ambiente-buildID` (ex: `dev-123`)
6. ✅ **Push Docker Image** - Envia imagem para seu repositório no Docker Hub
7. ✅ **Deploy** - Para container antigo (se existir) e executa novo container com a aplicação atualizada

> 📊 **Progress Visual:** Acompanhe cada stage no Jenkins - verde = sucesso, vermelho = falha

### Monitorar Execução

**No Jenkins:**
- Veja o progresso em tempo real
- Cada stage mostra sucesso (verde) ou falha (vermelho)
- Clique em `Console Output` para ver logs detalhados

**Tempo estimado:** 3-5 minutos (primeira execução pode levar mais)

## ✅ Verificar Deploy

Após a pipeline finalizar com sucesso (todas as stages verdes ✅), siga estes passos para confirmar que tudo está funcionando:

### 1. Verificar Container

```bash
# Listar containers
docker ps | grep app-financeiro

# Deve mostrar: app-financeiro running
```

### 2. Verificar Logs

```bash
# Ver logs do container
docker logs -f app-financeiro

# Aguarde a mensagem:
# "Started FinanceiroApplication in X seconds"
```

### 3. Testar Aplicação

```bash
# Health check
curl http://localhost:8089/actuator/health

# Resposta esperada: {"status":"UP"}
```

### 4. Verificar Docker Hub

1. Acesse: https://hub.docker.com
2. Login com suas credenciais
3. Vá até seu repositório: `seu_usuario/sis-financeiro`
4. Verifique a tag criada:
   - Exemplo: `dev-123` ou `prod-456`
   - Onde 123/456 é o número do build

### 5. Verificar SonarQube

1. Acesse: http://localhost:9000
2. Login com admin
3. Projeto: `SIS-FINANCEIRO`
4. Verifique:
   - Coverage
   - Code Smells
   - Bugs
   - Vulnerabilities
   - Security Hotspots

## 📊 Observabilidade

### Prometheus (Métricas)

**Acesse:** http://localhost:9090

**Queries úteis:**
```promql
# Taxa de requisições HTTP
rate(http_server_requests_seconds_count[5m])

# Uso de memória da JVM
jvm_memory_used_bytes{area="heap"}

# Tempo de resposta P95
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))

# Uptime da aplicação
process_uptime_seconds
```

### Grafana (Dashboards)

**Acesse:** http://localhost:3000
- **Usuário:** admin
- **Senha:** admin

**Configurar Datasources:**

1. `Configuration` → `Data Sources` → `Add data source`

**Prometheus:**
- Type: Prometheus
- URL: `http://prometheus:9090`
- Clique em `Save & Test`

**Loki:**
- Type: Loki
- URL: `http://loki:3100`
- Clique em `Save & Test`

**Importar Dashboards:**

1. `Dashboards` → `Import`
2. Upload dos arquivos JSON em `src/main/resources/grafana-json/`:
   - `dashboard-startup-spring-boot-metricas-inicializacao.json`
   - `dashboard-startup-spring-boot-logs-inicializacao.json`
3. Selecione o datasource Prometheus/Loki
4. Clique em `Import`

### Loki (Logs)

**Visualizar logs:**

1. Acesse Grafana: http://localhost:3000
2. `Explore` → Selecione `Loki`
3. Query: `{container_name="app-financeiro"}`
4. Clique em `Run query`

**Queries úteis:**
```logql
# Logs da aplicação
{container_name="app-financeiro"}

# Erros apenas
{container_name="app-financeiro"} |= "ERROR"

# Logs do PostgreSQL
{container_name="db-postgresql"}
```

### SonarQube (Qualidade)

**Acesse:** http://localhost:9000

**Configurar Quality Gate (Opcional):**

1. `Quality Gates` → `Create`
2. Defina condições:
   - Coverage < 80% → FAILED
   - Bugs > 0 → FAILED
   - Code Smells > 10 → WARNING
3. Aplique ao projeto `SIS-FINANCEIRO`

## 🌐 Endpoints

### Aplicação

| Endpoint | Descrição | Credenciais |
|---|---|---|
| http://localhost:8089 | API Principal | - |
| http://localhost:8089/swagger-ui/index.html | Documentação Swagger | - |
| http://localhost:8089/actuator/health | Health Check | - |
| http://localhost:8089/actuator/prometheus | Métricas | - |

### Infraestrutura

| Serviço | URL | Credenciais |
|---|---|---|
| **Jenkins** | http://localhost:8080 | admin/admin |
| **SonarQube** | http://localhost:9000 | admin/nova_senha |
| **Grafana** | http://localhost:3000 | admin/admin |
| **Prometheus** | http://localhost:9090 | - |
| **PostgreSQL** | localhost:5432 | admin/admin |


## 📁 Arquivos Importantes

```
SIS-CONTROLE-FINANCEIRO/
├── Jenkinsfile                  ← Pipeline definition
├── docker-compose.yml           ← Orquestração de serviços
├── Dockerfile                   ← Build da aplicação
├── sonar-project.properties     ← Configuração SonarQube
├── prometheus.yml               ← Configuração Prometheus
├── loki-config.yml              ← Configuração Loki
├── promtail-config.yml          ← Configuração Promtail
├── .env                         ← Variáveis de ambiente (criar)
└── README-PIPELINE.md           ← Este arquivo
```

## 🔄 Fluxo de Trabalho

### Desenvolvimento

1. Desenvolva em branch `develop`
2. Commit e push para GitHub
3. Execute pipeline com ambiente `dev`
4. Verifique qualidade no SonarQube
5. Teste a aplicação

### Produção

1. Merge `develop` → `main` (via Pull Request)
2. Execute pipeline com ambiente `prod`
3. Verifique Quality Gate passou
4. Monitore métricas no Grafana
5. Aplicação vai para produção

## 👥 Autores

- Thiago - [@thiago-jv](https://github.com/samueljdev)
- Samuel - [@samueljdev](https://github.com/samueljdev)

---

**Última atualização:** Fevereiro 2026
