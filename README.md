# Personal Finance Tracker API

Sistema de gerenciamento financeiro pessoal com autenticação JWT, controle de despesas e receitas, categorias personalizadas e relatórios financeiros.

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=souluanf_ecommerce-management-api&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=souluanf_ecommerce-management-api)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=souluanf_ecommerce-management-api&metric=coverage)](https://sonarcloud.io/summary/new_code?id=souluanf_ecommerce-management-api)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=souluanf_ecommerce-management-api&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=souluanf_ecommerce-management-api)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=souluanf_ecommerce-management-api&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=souluanf_ecommerce-management-api)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=souluanf_ecommerce-management-api&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=souluanf_ecommerce-management-api)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=souluanf_ecommerce-management-api&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=souluanf_ecommerce-management-api)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=souluanf_ecommerce-management-api&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=souluanf_ecommerce-management-api)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=souluanf_ecommerce-management-api&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=souluanf_ecommerce-management-api)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=souluanf_ecommerce-management-api&metric=bugs)](https://sonarcloud.io/summary/new_code?id=souluanf_ecommerce-management-api)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=souluanf_ecommerce-management-api&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=souluanf_ecommerce-management-api)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=souluanf_ecommerce-management-api&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=souluanf_golden-raspberry-awards-api)

## Sumário

- [Arquitetura](#arquitetura)
- [Funcionalidades](#funcionalidades)
- [Tecnologias](#tecnologias)
- [Frontend](#frontend)
- [Swagger](#swagger)
- [Requisitos](#requisitos)
- [Configuração](#configuração)
- [Execução](#execução)
- [Contato](#contato)

## Arquitetura

![Arquitetura da Aplicação](personal-finance-tracker.drawio.png)

A aplicação segue uma arquitetura de microserviços com três componentes principais:
- **Frontend (React):** Interface de usuário desenvolvida em React
- **Backend (Spring Boot):** API REST com autenticação JWT
- **Database (PostgreSQL):** Banco de dados relacional para persistência

## Funcionalidades

### Autenticação e Autorização
- Autenticação JWT com Spring Security
- Perfis de usuário (Admin/User)
- Controle de acesso baseado em roles

## Tecnologias

### Core
- Java 21
- Spring Boot 3.5.5
- Spring Security
- Spring Data JPA
- Maven 3.6+

### Banco de Dados
- MySQL 8.0
- Flyway (migrations)

### Documentação
- OpenAPI 3.0 (Swagger)
- Swagger UI

### Testes
- JUnit 5
- TestContainers
- Spring Boot Test

### DevOps
- Docker
- Docker Compose

## Frontend

### Acesso
- **URL:** [http://localhost:3000](http://localhost:3000)

### Funcionalidades Atuais

#### Tela de Login
- Autenticação de usuários com email e senha
- Validação de formulários
- Mensagens de erro personalizadas
- Redirecionamento após login bem-sucedido

#### Tela de Registro
- Cadastro de novos usuários
- Validação de campos obrigatórios
- Confirmação de senha
- Integração com backend para criação de conta

#### Dashboard (Em desenvolvimento)
- Visão geral das finanças pessoais
- Resumo de receitas e despesas
- Gráficos e indicadores financeiros

### Tecnologias do Frontend
- React 18
- TypeScript
- Material-UI (MUI)
- React Router
- Axios para requisições HTTP
- Context API para gerenciamento de estado

## Swagger

- **OpenAPI UI:** [http://localhost:8080/ecommerce-management/swagger-ui/index.html](http://localhost:8080/ecommerce-management/swagger-ui/index.html)
- **API Docs:** [http://localhost:8080/ecommerce-management/v3/api-docs](http://localhost:8080/ecommerce-management/v3/api-docs)

## Requisitos

- JDK 21
- Maven 3.6+
- Docker

## Configuração

**Instalação do JDK, Maven e Docker:**

- [Instruções para instalação do JDK](https://docs.oracle.com/en/java/javase/21/install/overview-jdk-installation.html)
- [Instruções para instalação do Maven](https://maven.apache.org/install.html)
- [Instruções para instalação do Docker](https://docs.docker.com/get-docker/)

## Execução

Copie as variáveis utilizadas

```bash
cp example.env .env
```

Execute o comando abaixo:

```bash
docker-compose up -d
```

### Autenticação

Após a inicialização da aplicação, é necessário criar um perfil de administrador e obter o token de autenticação para fazer as requisições aos endpoints protegidos.

1. **Criar usuário admin:** Use o endpoint `/auth/register` para criar um usuário com perfil ADMIN (já retorna o token)
2. **Obter token:** Use o endpoint `/auth/login` para obter o token JWT
3. **Usar token:** Adicione o token no header `Authorization: Bearer {token}` em todas as requisições

## Contato

Para suporte ou feedback:

- **Nome:** Heimy Dias
- **Email:**  [heimysantana@hotmail.com](mailto:heimysantana@hotmail.com)
- **LinkedIn:** [https://linkedin.com/in/heimydias](https://linkedin.com/in/heimydias)