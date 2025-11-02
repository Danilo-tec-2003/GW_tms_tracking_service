# Sistema de Rastreamento TMS

Este projeto é uma aplicação Web completa (Backend + Frontend) desenvolvida em Java com Spring Boot e Thymeleaf, simulando o módulo de rastreamento (tracking) de um sistema TMS (Transportation Management System).

O sistema permite o cadastro de encomendas, o registro de eventos de rastreio (ocorrências) e a consulta da timeline completa, implementando regras de negócio críticas para validar o workflow dos status.

##  Tecnologias Utilizadas 

* **Backend:** Java 17+ (JDK 23), Spring Boot 3.x
* **Acesso a Dados:** Spring Data JPA, Hibernate
* **Banco de Dados:** MySQL 8+
* **Frontend:** Thymeleaf (Server-Side Rendering)
* **Mapeamento/DTOs:** MapStruct
* **Validação:** Jakarta Bean Validation (para DTOs)
* **Build:** Apache Maven

##  Recursos Implementados

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

## Diagrama da Arquitetura (Mermaid)

Este diagrama ilustra o fluxo de dados da aplicação, desde a requisição do usuário (via Thymeleaf) até o banco de dados.

```mermaid
graph TD;
    subgraph "Cliente (Navegador)"
        A[Usuário]
    end

    subgraph "Aplicação Spring Boot"
        B(WebController) -- Chama --> C{TrackingService};
        C -- Validação Falhou --> E(Exceções Customizadas);
        C -- Busca/Salva --> F[OrderRepository];
        C -- Busca/Salva --> G[OccurrenceRepository];
        C -- Converte --> H(Mappers - MapStruct);
        
        I(GlobalExceptionHandler) -- Captura --> E;
    end
    
    subgraph "Banco de Dados"
        J[(MySQL DB)]
    end

    A -- GET /consulta --> B;
    A -- POST /cadastro --> B;
    A -- POST /nova-encomenda --> B;

    F -- JPA --> J;
    G -- JPA --> J;
🚀 Como Executar o Projeto
Existem duas formas de rodar esta aplicação.

Opção 1: Usando o Executável .jar (Recomendado)
Esta é a forma mais simples de testar a aplicação finalizada.

Banco de Dados:

Garanta que você tenha um servidor MySQL 8+ rodando.

Crie um banco de dados (schema) chamado tms_tracking_db.

Execute o script src/main/resources/schema.sql neste banco para criar as tabelas.

Configuração:

Verifique o arquivo src/main/resources/application.properties e ajuste a porta (3307), usuário (tms_user) e senha (tms_password) do MySQL se forem diferentes dos seus.

Executar:

Abra um terminal na raiz do projeto.

Rode o comando (substitua o nome do .jar se for diferente):

Bash

java -jar tracking-0.0.1-SNAPSHOT.jar
Acessar:

Abra seu navegador e acesse: http://localhost:8080/

Opção 2: Pelo Código-Fonte (Desenvolvimento)
Clone o Repositório:

Bash

git clone [URL-DO-SEU-REPO]
cd [NOME-DO-REPO]
Banco de Dados:

Siga o Passo 1 da "Opção 1" para configurar o banco e rodar o schema.sql.

Rodar:

Importe o projeto como um projeto Maven na sua IDE (IntelliJ, Eclipse).

Configure o JDK 17+ (o projeto foi desenvolvido no JDK 23).

Encontre a classe TrackingApplication.java e clique em "Run" (Play 🟩).

Acessar:

Abra seu navegador e acesse: http://localhost:8080/

 API REST (Endpoints)
O backend também expõe uma API REST pura (usada pelo Postman nos testes iniciais, embora o frontend Thymeleaf acesse o Service diretamente).

POST /api/orders: Cria uma nova encomenda.

GET /api/orders/{trackingCode}: Consulta a timeline de uma encomenda.

POST /api/orders/{trackingCode}/events: Registra uma nova ocorrência.


###