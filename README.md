# Sistema de Gerenciamento de Jogos Interclasse - IFS

## 1. Objetivo do Projeto

Este projeto consiste em um sistema Back-end, desenvolvido em Spring Boot, para gerenciar os jogos interclasse do Instituto Federal de Sergipe (IFS). O sistema cobre desde o cadastro de cursos, esportes, equipes e usuários, até a geração completa de um torneio, com fase de grupos e fase eliminatória (mata-mata), incluindo registro de resultados e pontuação.

Este projeto foi desenvolvido como requisito para a disciplina de Web 1 do curso de Bacharelado em Sistemas de Informação.

## 2. Tecnologias Utilizadas

* **Java 17**: Linguagem de programação principal.
* **Spring Boot 3**: Framework principal para construção da aplicação.
    * **Spring Web**: Para criação dos endpoints da API RESTful.
    * **Spring Data JPA**: Para persistência de dados e comunicação com o banco.
    * **Spring Test**: Para a suíte de testes (unitários, integração e API).
* **Hibernate**: Implementação do JPA para mapeamento objeto-relacional (ORM).
* **Maven**: Ferramenta de gerenciamento de dependências e build do projeto.
* **SQL Server**: Sistema de Gerenciamento de Banco de Dados (SGBD) utilizado.
* **JUnit 5, Mockito & AssertJ**: Bibliotecas para a escrita e execução dos testes.

## 3. Como Executar o Projeto

### Pré-requisitos

* Java Development Kit (JDK) 17 ou superior.
* Apache Maven 3.6 ou superior.
* Uma instância do SQL Server em execução.

### Configuração do Banco de Dados

1.  No seu SQL Server, crie um banco de dados com o nome `Server_WEB`.
2.  Habilite o modo de autenticação misto (SQL Server e Windows).
3.  Crie um novo login (ex: `dev_web`) e dê a ele permissões sobre o banco `Server_WEB`.
4.  Abra o arquivo `src/main/resources/application.properties` e configure as credenciais do seu banco de dados:

    ```properties
    spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=Server_WEB;encrypt=true;trustServerCertificate=true;
    spring.datasource.username=seu_usuario_aqui
    spring.datasource.password=sua_senha_aqui
    ```

### Execução

1.  Navegue até a pasta raiz do projeto (onde se encontra o arquivo `pom.xml`).
2.  Execute o seguinte comando no terminal:

    ```bash
    mvn spring-boot:run
    ```

A aplicação estará disponível em `http://localhost:8080`.

## 4. Documentação da API (Endpoints)

A seguir estão documentados todos os endpoints disponíveis na API.

---

### 4.1. Administrador

#### `POST /api/admin/coordenadores`
* **Descrição**: Cadastra um novo coordenador de curso no sistema. Esta é uma rota privilegiada.
* **Corpo da Requisição (JSON)**:
    ```json
    {
      "matricula": "coordSI01",
      "nome": "Prof. Coordenador de SI",
      "senha": "senhaSegura"
    }
    ```
* **Resposta de Sucesso (201 CREATED)**: O objeto do coordenador cadastrado.

---

### 4.2. Esportes

#### `POST /api/esportes`
* **Descrição**: Cadastra um novo esporte no sistema.
* **Corpo da Requisição (JSON)**:
    ```json
    {
      "nome": "Natação",
      "minAtletas": 1,
      "maxAtletas": 1
    }
    ```
* **Resposta de Sucesso (201 CREATED)**: O objeto do esporte cadastrado.

---

### 4.3. Coordenadores

#### `POST /api/coordenadores/{matriculaCoordenador}/tecnicos`
* **Descrição**: Permite que um coordenador cadastrado cadastre um novo técnico.
* **Parâmetros na URL**:
    * `matriculaCoordenador`: A matrícula do coordenador que está realizando a operação.
* **Corpo da Requisição (JSON)**:
    ```json
    {
      "matricula": "tec001",
      "nome": "Professor Silva",
      "senha": "senha123"
    }
    ```
* **Resposta de Sucesso (201 CREATED)**: O objeto do técnico cadastrado.

---

### 4.4. Técnicos

#### `POST /api/tecnicos/{matriculaTecnico}/equipes`
* **Descrição**: Permite que um técnico cadastrado cadastre uma nova equipe.
* **Parâmetros na URL**:
    * `matriculaTecnico`: A matrícula do técnico que está cadastrando a equipe.
* **Corpo da Requisição (JSON)**:
    ```json
    {
      "equipe": {
        "nome": "Os Invencíveis",
        "curso": { "id": 1 },
        "esporte": { "id": 1 }
      },
      "matriculasAtletas": ["atl001", "atl002", "atl003"]
    }
    ```
* **Resposta de Sucesso (201 CREATED)**: O objeto da equipe cadastrada.

---

### 4.5. Árbitros

#### `PUT /api/arbitros/{matriculaArbitro}/partidas/{partidaId}/resultado`
* **Descrição**: Permite que um árbitro registre o placar final de uma partida.
* **Parâmetros na URL**:
    * `matriculaArbitro`: A matrícula do árbitro.
    * `partidaId`: O ID da partida a ser atualizada.
* **Corpo da Requisição (JSON)**:
    ```json
    {
      "placarA": 25,
      "placarB": 20
    }
    ```
* **Resposta de Sucesso (200 OK)**: O objeto da partida atualizado.

---

### 4.6. Torneios

#### `POST /api/torneios/iniciar`
* **Descrição**: Inicia um novo torneio para um esporte e categoria. Gera os grupos e as partidas da fase inicial.
* **Corpo da Requisição (JSON)**:
    ```json
    {
      "esporteId": 1,
      "categoria": "SUPERIOR"
    }
    ```
* **Resposta de Sucesso (201 CREATED)**: O objeto do torneio criado.

#### `POST /api/torneios/{torneioId}/avancar-fase`
* **Descrição**: Verifica a fase atual de um torneio. Se todos os jogos da fase estiverem concluídos, gera as partidas da próxima fase do mata-mata.
* **Parâmetros na URL**:
    * `torneioId`: O ID do torneio a ser avançado.
* **Corpo da Requisição**: Vazio.
* **Resposta de Sucesso**:
    * **201 CREATED**: Retorna uma lista com as novas partidas geradas para a próxima fase.
    * **200 OK**: Retorna a mensagem "Torneio finalizado! Campeão determinado." se a última fase (a final) foi concluída.
* **Resposta de Erro (400 BAD_REQUEST)**: Retorna a mensagem "Ainda existem partidas agendadas. A fase atual não foi concluída." se a fase atual não terminou.