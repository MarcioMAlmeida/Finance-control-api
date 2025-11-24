# 💰 Finance Control API

API RESTful completa para gestão de finanças pessoais, desenvolvida com foco em boas práticas de arquitetura, segurança e DevOps.

## 🚀 Sobre o Projeto

Este projeto é o backend de uma aplicação de controle financeiro. O objetivo principal foi construir uma solução robusta que simula um ambiente de produção real, saindo do básico "CRUD" e implementando autenticação segura, testes automatizados e containerização.

O sistema permite que usuários se cadastrem, façam login e gerenciem suas receitas e despesas de forma segura, onde cada usuário tem acesso estrito apenas aos seus próprios dados.

## 🛠 Tecnologias Utilizadas

* **Linguagem:** Java 17
* **Framework:** Spring Boot 3
* **Segurança:** Spring Security + JWT (JSON Web Token)
* **Banco de Dados:** PostgreSQL (Produção/Dev) e H2 (Testes)
* **Persistência:** Spring Data JPA
* **Testes:** JUnit 5 e Mockito
* **DevOps:** Docker e Docker Compose
* **Outros:** Lombok, Bean Validation

## ⚙️ Arquitetura e Padrões

* **API RESTful:** Uso adequado dos métodos HTTP e status codes.
* **Camadas:** Controller, Service, Repository.
* **DTOs (Data Transfer Objects):** Para isolar a camada de domínio da camada de apresentação.
* **Mappers:** Conversão limpa entre Entidades e DTOs.
* **Tratamento de Exceções:** Handler global para respostas de erro amigáveis e padronizadas.

## 🐳 Como Rodar (Via Docker) - Recomendado

A aplicação foi totalmente "dockerizada". Para rodar o projeto (API + Banco de Dados) sem precisar instalar o Java ou o Postgres na sua máquina, basta ter o **Docker** instalado.

1.  Clone o repositório:
    ```bash
    git clone [https://github.com/SEU-USUARIO/finance-control-api.git](https://github.com/SEU-USUARIO/finance-control-api.git)
    cd finance-control-api
    ```

2.  Gere o pacote da aplicação (necessário apenas na primeira vez ou após alterações no código):
    ```bash
    ./mvnw package
    ```
    *(No Windows, use `mvnw.cmd package`)*

3.  Suba os containers com o Docker Compose:
    ```bash
    docker compose up
    ```

A API estará disponível em: `http://localhost:8080`

## 🧪 Como Rodar os Testes

Para executar a suíte de testes unitários:

```bash
./mvnw test
```
## 🔌 Documentação da API (Endpoints Principais)

### Autenticação

* **Registrar Usuário**
    * `POST /usuarios`
    * Body:
      ```json
      { "nome": "...", "email": "...", "senha": "..." }
      ```

* **Login (Obter Token)**
    * `POST /login`
    * Body:
      ```json
      { "email": "...", "senha": "..." }
      ```
    * Retorno:
      ```json
      { "token": "eyJhbGciOiJIUz..." }
      ```

### Lançamentos (Requer Token Bearer)

* **Listar Lançamentos**
    * `GET /lancamentos`

* **Criar Lançamento**
    * `POST /lancamentos`
    * Body:
      ```json
      {
          "descricao": "Almoço",
          "valor": 25.90,
          "data": "2025-10-20",
          "tipo": "DESPESA"
      }
      ```

* **Atualizar Lançamento**
    * `PUT /lancamentos/{id}`
    * Body: (Mesmo formato da criação)

* **Deletar Lançamento**
    * `DELETE /lancamentos/{id}`

🔜 Próximos Passos
[ ] Desenvolvimento do App Mobile (Android/Kotlin) para consumir a API.

[ ] Implementação de Dashboard com resumo mensal.

[ ] Deploy na nuvem (AWS/Render).

## 🔌 Documentação Interativa (Swagger): Disponível em /swagger-ui/index.html ao rodar o projeto.
