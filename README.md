# Sistema de Rastreamento TMS

Este projeto é uma aplicação Web completa (Backend + Frontend) desenvolvida em Java com Spring Boot e Thymeleaf, simulando o módulo de rastreamento (tracking) de um sistema TMS (Transportation Management System).

O sistema permite o cadastro de encomendas, o registro de eventos de rastreio (ocorrências) e a consulta da timeline completa, implementando regras de negócio críticas para validar o workflow dos status.

## Como Executar o Projeto

1 - rodar o projeto  usando o executável `.jar` fornecido.

### Pré-requisitos
* Java 17+  (JDK)
* Servidor MySQL 8+

### Opção 1: Usando o Executável .jar

**1. Banco de Dados:**
* Garanta que você tenha um servidor MySQL 8+ rodando.
* Crie um banco de dados (schema) chamado `tms_tracking_db`.
* Execute o script DDL localizado em `src/main/resources/schema.sql` para criar as tabelas.

**2. Configuração (Se necessário):**
* Verifique o arquivo `src/main/resources/application.properties` e ajuste a porta (`3307`), usuário (`tms_user`) e senha (`tms_password`) do MySQL se forem diferentes dos seus. O `.jar` já contém esta configuração padrão.

**3. Executar (Instalação de Dependências não é necessária):**
* Abra um terminal na raiz do projeto (onde o `.jar` está).
* Rode o comando:
    ```bash
    java -jar tracking-0.0.1-SNAPSHOT.jar
    ```

**4. Acessar:**
* Abra seu navegador e acesse: **`http://localhost:8080/`**

### Opção 2: Usando Docker-Compose

**Pré-requisitos:**
* Apenas o **Docker Desktop** instalado e a correr.

**1. Executar:**
* Abra um terminal na raiz do projeto e rode:
    ```bash
    docker compose up --build
    ```
* (Aguarde 2-5 minutos no primeiro build).

**2. Acessar:**
* Abra o seu navegador e acesse: **`http://localhost:8080/`**

**3. Para Parar:**
* No terminal, prima `Ctrl + C` e depois rode `docker compose down`.

  ---
### Opção 3: Pelo Código-Fonte (Desenvolvimento)

**1. Instalar Dependências:**
* Clone o repositório.
* Importe o projeto como um projeto Maven na sua IDE (IntelliJ, Eclipse).
* A IDE irá baixar e instalar todas as dependências do `pom.xml` automaticamente.

**2. Banco de Dados e Configuração:**
* Siga o **Passo 1** da "Opção 1" para preparar o banco.
* **Ajuste** o `application.properties` com seu usuário e senha do MySQL.

**3. Rodar:**
* Encontre a classe `TrackingApplication.java` na sua IDE e clique em "Run" (Play 🟩).

**4. Acessar:**
* Abra seu navegador e acesse: **`http://localhost:8080/`**

---

##  Tecnologias Utilizadas (Stack)

* **Backend:** Java 17+ (Spring Boot 3.x, Spring Web)
* **Acesso a Dados:** Spring Data JPA, Hibernate
* **Banco de Dados:** MySQL 8+
* **Frontend:** Thymeleaf (Server-Side Rendering)
* **Mapeamento/DTOs:** MapStruct
* **Validação:** Jakarta Bean Validation (para DTOs)
* **Testes:** JUnit 5, Mockito
* **DevOps/deploy:** Docker (Docker-Compose)
* **Qualidade de Código:** Lombok
* **Build:** Apache Maven

## Recursos Implementados

* **Backend (API REST + Lógica de Serviço):**
  * API REST completa para gerenciamento de Encomendas e Ocorrências.
  * Tratamento de Exceções customizado (`@ControllerAdvice`) para erros 404 (Não Encontrado) e 400 (Regra de Negócio).
  * Validação de DTOs de entrada (`@Valid`).
  * Uso do padrão DTO (Input/Output) e Mappers (MapStruct) para desacoplamento.
* **Frontend (Thymeleaf):**
  * **Tela de Consulta de Status:** Permite ao usuário buscar uma encomenda pelo código e ver sua timeline completa, ordenada do mais recente para o mais antigo.
  * **Tela de Cadastro de Ocorrência:** Permite o registro de novos eventos de rastreio.
  * **Tela de Cadastro de Encomenda:** Permite a criação de novas encomendas no sistema.
* **Regras de Negócio (Critérios de Avaliação):**
  * **Impedimento de Conclusão:** O sistema impede o registro de qualquer novo evento se o status mais recente for `ENTREGUE`.
  * **Lógica de Reentrega:** Se o status mais recente for `NÃO ENTREGUE`, o próximo status só pode ser `SAÍDA PARA ENTREGA`.
  * **Validação de Duplicidade:** O sistema impede a criação de uma encomenda com um `trackingCode` que já existe.

##  Testes Unitários

Para garantir a Qualidade de Código, a camada de serviço (`TrackingService`) foi testada unitariamente com JUnit 5 e Mockito.

Os testes estão localizados em `src/test/java` e provam o funcionamento correto de:
* **`deveLancarExcecao_QuandoStatusJaEstiverEntregue`**: Prova que a regra de bloqueio de `ENTREGUE` funciona.
* **`deveLancarExcecao_QuandoStatusInvalidoAposNaoEntregue`**: Prova que a regra de `NAO_ENTREGUE` funciona.
* **`deveRegistrarComSucesso_QuandoRegrasValidas`**: Prova o "caminho feliz" do registro de ocorrência.
* **`deveLancarExcecao_QuandoEncomendaNaoForEncontrada`**: Prova o tratamento de erro 404.

##  API REST (Endpoints)

O backend também expõe uma API REST pura (usada pelo Postman nos testes iniciais, embora o frontend Thymeleaf acesse o Service diretamente).

* `POST /api/orders`: Cria uma nova encomenda.
* `GET /api/orders/{trackingCode}`: Consulta a timeline de uma encomenda.
* `POST /api/orders/{trackingCode}/events`: Registra uma nova ocorrência.
