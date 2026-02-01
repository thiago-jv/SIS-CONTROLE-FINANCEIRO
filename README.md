# Sistema de Controle Financeiro

Sistema de controle financeiro desenvolvido com Spring Boot, PostgreSQL e stack de observabilidade com Prometheus e Grafana.

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.4.11**
- **PostgreSQL**
- **Docker & Docker Compose**
- **Prometheus** - Coleta de métricas
- **Grafana** - Visualização e dashboards
- **Micrometer** - Instrumentação de métricas
- **Spring Actuator** - Endpoints de monitoramento

## 📋 Pré-requisitos

- Java 17+
- Maven 3.6+
- Docker e Docker Compose
- Git

## 🔧 Configuração

### 1. Clone o repositório

```bash
git clone <url-do-repositorio>
cd SIS-CONTROLE-FINANCEIRO
```

### 2. Inicie os serviços com Docker

```bash
docker compose up -d
```

Isso irá iniciar:
- **PostgreSQL** na porta 5432
- **Aplicação** na porta 8089
- **Prometheus** na porta 9090
- **Grafana** na porta 3000

### 3. Compile e execute a aplicação (desenvolvimento local)

```bash
./mvnw clean install
./mvnw spring-boot:run
```

## 🌐 Endpoints Disponíveis

### Aplicação
- **API**: http://localhost:8089
- **Swagger UI**: http://localhost:8089/swagger-ui/index.html

### Monitoramento e Observabilidade

#### Health Check
- **URL**: http://localhost:8089/actuator/health
- **Descrição**: Verifica o status de saúde da aplicação e seus componentes (database, disk space, etc.)

#### Métricas Prometheus
- **URL**: http://localhost:8089/actuator/prometheus
- **Descrição**: Endpoint com todas as métricas no formato Prometheus

#### Todos os Endpoints Actuator
- **URL**: http://localhost:8089/actuator
- **Endpoints disponíveis**:
  - `/actuator/health` - Status de saúde
  - `/actuator/info` - Informações da aplicação
  - `/actuator/metrics` - Lista de métricas disponíveis
  - `/actuator/prometheus` - Métricas no formato Prometheus
  - `/actuator/env` - Variáveis de ambiente
  - `/actuator/loggers` - Configuração de logs

### Prometheus
- **URL**: http://localhost:9090
- **Descrição**: Interface do Prometheus para consultar métricas
- **Exemplos de queries**:
  ```promql
  # Taxa de requisições HTTP
  rate(http_server_requests_seconds_count[1m])
  
  # Latência p95 das requisições
  histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))
  
  # Uso de memória JVM
  jvm_memory_used_bytes
  ```

### Grafana
- **URL**: http://localhost:3000
- **Credenciais**: admin / admin
- **Descrição**: Plataforma de visualização e dashboards

## 📊 Configurando Grafana

### 1. Adicionar Prometheus como Data Source

1. Acesse http://localhost:3000
2. Login: admin / admin
3. Vá em **Configuration** → **Data Sources**
4. Clique em **Add data source**
5. Selecione **Prometheus**
6. Configure:
   - **URL**: `http://prometheus:9090`
   - Clique em **Save & Test**

### 2. Importar Dashboard Pré-configurado

1. Vá em **Dashboards** → **Import**
2. Use o ID: **11378** (Spring Boot 2.1 Statistics)
3. Selecione o data source Prometheus
4. Clique em **Import**

### 3. Dashboards Recomendados

- **11378** - Spring Boot 2.1 Statistics
- **4701** - JVM (Micrometer)
- **12900** - Spring Boot 2.1 System Monitor

## 🔍 Métricas Disponíveis

A aplicação expõe automaticamente:

- **JVM**: Memória, threads, garbage collection
- **HTTP**: Requisições, latência, status codes
- **Database**: Conexões do pool, queries
- **Sistema**: CPU, disco, memória
- **Aplicação**: Métricas customizadas

## 🗂️ Estrutura do Projeto

```
SIS-CONTROLE-FINANCEIRO/
├── src/
│   ├── main/
│   │   ├── java/com/financeiro/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   └── test/
├── docker-compose.yml
├── Dockerfile
├── prometheus.yml
├── pom.xml
└── README.md
```

## ⚙️ Perfis de Ambiente

### Development (dev)
```properties
spring.profiles.active=dev
```
- Expõe todos os endpoints do actuator
- Show SQL habilitado
- Detalhes completos de health check

### Production (prod)
```properties
spring.profiles.active=prod
```
- Expõe apenas health e info
- Detalhes de health apenas quando autorizado
- Configurações otimizadas para produção

## 🐳 Comandos Docker Úteis

```bash
# Iniciar todos os serviços
docker compose up -d

# Ver logs
docker compose logs -f app-financeiro

# Parar todos os serviços
docker compose down

# Parar e remover volumes
docker compose down -v

# Rebuild da aplicação
docker compose up -d --build app-financeiro
```

## 📝 Variáveis de Ambiente

### Banco de Dados
- `SPRING_DATASOURCE_URL`: jdbc:postgresql://db-postgresql:5432/bdfinanceiro
- `SPRING_DATASOURCE_USERNAME`: admin
- `SPRING_DATASOURCE_PASSWORD`: admin

### Aplicação
- `SPRING_PROFILES_ACTIVE`: dev ou prod
- `SERVER_PORT`: 8089

## 🧪 Testes

```bash
# Executar testes
./mvnw test

# Executar com coverage
./mvnw clean test jacoco:report
```

## 📚 Documentação da API

Após iniciar a aplicação, acesse a documentação Swagger:
- http://localhost:8089/swagger-ui/index.html

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT.
