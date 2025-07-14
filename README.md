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
* **Descrição**: Cadastra um novo coordenador de curso no sistema.
* **Corpo da Requisição (Exemplo)**:
  ```json
  {
    "matricula": "coordSI01",
    "nome": "Prof. Coordenador de SI",
    "senha": "senhaSegura"
  }
  ```
* **Resposta de Sucesso (201 CREATED)**: O objeto do coordenador cadastrado.

#### `GET /api/admin/coordenadores`
* **Descrição**: Lista todos os coordenadores cadastrados.
* **Resposta de Sucesso (200 OK)**: Uma lista de objetos `UsuarioResponseDTO`.

---

### 4.2. Cursos

#### `POST /api/cursos`
* **Descrição**: Cadastra um novo curso.
* **Corpo da Requisição (Exemplo)**:
  ```json
  {
    "nome": "Engenharia de Software",
    "categoria": "SUPERIOR"
  }
  ```
* **Resposta de Sucesso (201 CREATED)**: O objeto do curso cadastrado.

#### `GET /api/cursos`
* **Descrição**: Lista todos os cursos cadastrados.
* **Resposta de Sucesso (200 OK)**: Uma lista de objetos `CursoResponseDTO`.

---

### 4.3. Esportes

#### `POST /api/esportes`
* **Descrição**: Cadastra um novo esporte no sistema.
* **Corpo da Requisição (Exemplo)**:
  ```json
  {
    "nome": "Natação",
    "minAtletas": 1,
    "maxAtletas": 1
  }
  ```
* **Resposta de Sucesso (201 CREATED)**: O objeto do esporte cadastrado.

#### `GET /api/esportes`
* **Descrição**: Lista todos os esportes cadastrados.
* **Resposta de Sucesso (200 OK)**: Uma lista de objetos `EsporteResponseDTO`.

---

### 4.4. Técnicos

#### `POST /api/coordenadores/{matriculaCoordenador}/tecnicos`
* **Descrição**: Permite que um coordenador cadastrado cadastre um novo técnico.
* **Corpo da Requisição (Exemplo)**:
  ```json
  {
    "matricula": "tec001",
    "nome": "Professor Silva",
    "senha": "senha123"
  }
  ```
* **Resposta de Sucesso (201 CREATED)**: O objeto do técnico cadastrado.

#### `GET /api/tecnicos`
* **Descrição**: Lista todos os técnicos cadastrados.
* **Resposta de Sucesso (200 OK)**: Uma lista de objetos `UsuarioResponseDTO`.

---

### 4.5. Equipes e Atletas

#### `POST /api/tecnicos/{matriculaTecnico}/atletas`
* **Descrição**: Permite que um técnico cadastre um novo atleta (aluno) no sistema.
* **Corpo da Requisição (Exemplo)**:
  ```json
  {
    "matricula": "atl999",
    "nome": "Novo Craque",
    "apelido": "Fenômeno",
    "telefone": "79999998888",
    "senha": "senha123"
  }
  ```
* **Resposta de Sucesso (201 CREATED)**: O objeto do atleta cadastrado.

#### `POST /api/tecnicos/{matriculaTecnico}/equipes`
* **Descrição**: Permite que um técnico cadastrado cadastre uma nova equipe.
* **Corpo da Requisição (Exemplo)**:
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

#### `GET /api/equipes`
* **Descrição**: Lista todas as equipes cadastradas com seus detalhes.
* **Resposta de Sucesso (200 OK)**: Uma lista de objetos `EquipeResponseDTO`.

#### `GET /api/atletas`
* **Descrição**: Lista todos os atletas cadastrados.
* **Resposta de Sucesso (200 OK)**: Uma lista de objetos `AtletaResponseDTO`.

---

### 4.6. Torneio e Partidas

#### `POST /api/torneios/iniciar`
* **Descrição**: Inicia um novo torneio para um esporte e categoria. Gera os grupos e as partidas da fase inicial.
* **Corpo da Requisição (Exemplo)**:
  ```json
  {
    "esporteId": 1,
    "categoria": "SUPERIOR"
  }
  ```
* **Resposta de Sucesso (201 CREATED)**: O objeto do torneio criado.

#### `PUT /api/arbitros/{matriculaArbitro}/partidas/{partidaId}/resultado`
* **Descrição**: Permite que um árbitro registre o placar final de uma partida.
* **Corpo da Requisição (Exemplo)**:
  ```json
  {
    "placarA": 25,
    "placarB": 20
  }
  ```
* **Resposta de Sucesso (200 OK)**: O objeto da partida atualizado.

#### `POST /api/torneios/{torneioId}/avancar-fase`
* **Descrição**: Verifica a fase atual de um torneio e, se concluída, gera as partidas da próxima fase do mata-mata.
* **Corpo da Requisição**: Vazio.
* **Resposta de Sucesso**: `201 CREATED` com a lista de novas partidas, ou `200 OK` com a mensagem de torneio finalizado.

#### `GET /api/torneios`
* **Descrição**: Lista todos os torneios iniciados com seus grupos e equipes.
* **Resposta de Sucesso (200 OK)**: Uma lista de objetos `TorneioResponseDTO`.