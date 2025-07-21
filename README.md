# Sistema de Gerenciamento de Jogos Interclasse - IFS

## 1. Objetivo do Projeto

Este projeto consiste em um sistema Back-end, desenvolvido em Spring Boot, para gerenciar os jogos interclasse do Instituto Federal de Sergipe (IFS). O sistema cobre desde o cadastro de cursos, esportes, equipes e usuários, até a geração completa de um torneio, com lógicas avançadas para sorteio de grupos, chaveamento justo do mata-mata (1º vs 2º), tratamento de "byes" para ajuste de chave e agendamento de partidas sem conflitos de horário.

Este projeto foi desenvolvido como requisito para a disciplina de Web 1 do curso de Bacharelado em Sistemas de Informação.

## 2. Tecnologias Utilizadas

* **Java 17**: Linguagem de programação principal.
* **Spring Boot 3**: Framework principal para construção da aplicação.
    * **Spring Web**: Para criação dos endpoints da API RESTful.
    * **Spring Data JPA**: Para persistência de dados e comunicação com o banco.
    * **Spring Test**: Para a suíte de testes (unitários, integração e API).
* **Hibernate**: Implementação do JPA para mapeamento objeto-relacional (ORM).
* **Maven**: Ferramenta de gerenciamento de dependências e build do projeto.
* **H2 Database**: Banco de dados em memória utilizado para os testes de integração.
* **SQL Server**: Sistema de Gerenciamento de Banco de Dados (SGBD) utilizado em desenvolvimento.
* **JUnit 5, Mockito & AssertJ**: Bibliotecas para a escrita e execução dos testes.

## 3. Como Executar o Projeto

### Pré-requisitos

* Java Development Kit (JDK) 17 ou superior.
* Apache Maven 3.6 ou superior.
* Uma instância do SQL Server em execução.

### Configuração do Banco de Dados

1.  No seu SQL Server, crie um banco de dados com o nome `Server_WEB`.
2.  Habilite o modo de autenticação misto (SQL Server e Windows).
3.  Crie um novo login (ex: `sa`) e dê a ele permissões sobre o banco `Server_WEB`.
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

### 4.1. Gestão de Administradores (`/api/admin`)

#### `POST /api/admin/coordenadores`
* **Descrição**: Cadastra um novo coordenador.
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
* **Descrição**: Lista todos os coordenadores.
* **Resposta de Sucesso (200 OK)**: Uma lista de objetos `UsuarioResponseDTO`.

#### `PUT /api/admin/coordenadores/{matricula}`
* **Descrição**: Atualiza um coordenador.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "nome": "Nome Coordenador Atualizado",
      "senha": "novaSenha123"
    }
    ```
* **Resposta de Sucesso (200 OK)**: O objeto do coordenador atualizado.

#### `DELETE /api/admin/coordenadores/{matricula}`
* **Descrição**: Deleta um coordenador.
* **Resposta de Sucesso (200 OK)**: Mensagem de confirmação.

#### `POST /api/admin/arbitros`
* **Descrição**: Cadastra um novo árbitro.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "matricula": "arb001",
      "nome": "Árbitro Oficial",
      "senha": "senhaArbitro"
    }
    ```
* **Resposta de Sucesso (201 CREATED)**: O objeto do árbitro cadastrado.

#### `PUT /api/admin/arbitros/{matricula}`
* **Descrição**: Atualiza um árbitro.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
        "nome": "Juiz Atualizado",
        "senha": "outraSenha"
    }
    ```
* **Resposta de Sucesso (200 OK)**: O objeto do árbitro atualizado.

#### `DELETE /api/admin/arbitros/{matricula}`
* **Descrição**: Deleta um árbitro.
* **Resposta de Sucesso (200 OK)**: Mensagem de confirmação.

---

### 4.2. Gestão de Cursos (`/api/cursos`)

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
* **Descrição**: Lista todos os cursos.
* **Resposta de Sucesso (200 OK)**: Uma lista de objetos `CursoResponseDTO`.

#### `PUT /api/cursos/{id}`
* **Descrição**: Atualiza um curso.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "nome": "Ciência da Computação",
      "categoria": "SUPERIOR"
    }
    ```
* **Resposta de Sucesso (200 OK)**: O objeto do curso atualizado.

#### `DELETE /api/cursos/{id}`
* **Descrição**: Deleta um curso.
* **Resposta de Sucesso (200 OK)**: Mensagem de confirmação.

---

### 4.3. Gestão de Esportes (`/api/esportes`)

#### `POST /api/esportes`
* **Descrição**: Cadastra um novo esporte.
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
* **Descrição**: Lista todos os esportes.
* **Resposta de Sucesso (200 OK)**: Uma lista de objetos `EsporteResponseDTO`.

#### `PUT /api/esportes/{id}`
* **Descrição**: Atualiza um esporte.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "minAtletas": 2,
      "maxAtletas": 4
    }
    ```
* **Resposta de Sucesso (200 OK)**: O objeto do esporte atualizado.

#### `DELETE /api/esportes/{id}`
* **Descrição**: Deleta um esporte.
* **Resposta de Sucesso (200 OK)**: Mensagem de confirmação.

---

### 4.4. Gestão de Técnicos

#### `POST /api/coordenadores/{matriculaCoordenador}/tecnicos`
* **Descrição**: Permite que um coordenador cadastre um novo técnico.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "matricula": "tec001",
      "nome": "Professor Silva",
      "senha": "senha123"
    }
    ```
* **Resposta de Sucesso (201 CREATED)**: O objeto do técnico cadastrado.

#### `PUT /api/coordenadores/{matriculaCoordenador}/tecnicos/{matriculaTecnico}`
* **Descrição**: Permite que um coordenador atualize um técnico.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "nome": "Técnico Atualizado",
      "senha": "novaSenhaTecnico"
    }
    ```
* **Resposta de Sucesso (200 OK)**: O objeto do técnico atualizado.

#### `DELETE /api/coordenadores/{matriculaCoordenador}/tecnicos/{matriculaTecnico}`
* **Descrição**: Permite que um coordenador delete um técnico (desde que não esteja em uma equipe).
* **Resposta de Sucesso (200 OK)**: Mensagem de confirmação.

#### `GET /api/tecnicos`
* **Descrição**: Lista todos os técnicos cadastrados.
* **Resposta de Sucesso (200 OK)**: Uma lista de objetos `UsuarioResponseDTO`.

---

### 4.5. Gestão de Atletas e Equipes

#### `POST /api/tecnicos/{matriculaTecnico}/equipes`
* **Descrição**: Permite que um técnico cadastre uma nova equipe com seus atletas.
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

#### `POST /api/tecnicos/{matriculaTecnico}/atletas`
* **Descrição**: Permite que um técnico cadastre um novo atleta.
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

#### `PUT /api/tecnicos/{matriculaTecnico}/atletas/{matriculaAtleta}`
* **Descrição**: Permite que um técnico atualize os dados de um atleta.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "apelido": "Estrela",
      "telefone": "79911112222"
    }
    ```
* **Resposta de Sucesso (200 OK)**: O objeto do atleta atualizado.

#### `DELETE /api/tecnicos/{matriculaTecnico}/atletas/{matriculaAtleta}`
* **Descrição**: Permite que um técnico remova (desassocie) um atleta de sua equipe.
* **Resposta de Sucesso (200 OK)**: Mensagem de confirmação.

#### `GET /api/equipes`
* **Descrição**: Lista todas as equipes.
* **Resposta de Sucesso (200 OK)**: Uma lista de objetos `EquipeResponseDTO`.

#### `GET /api/atletas`
* **Descrição**: Lista todos os atletas.
* **Resposta de Sucesso (200 OK)**: Uma lista de objetos `AtletaResponseDTO`.

---

### 4.6. Gestão de Torneios e Partidas

#### `POST /api/torneios/iniciar`
* **Descrição**: Inicia um torneio, criando os grupos e as partidas da fase inicial.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "esporteId": 1,
      "categoria": "SUPERIOR"
    }
    ```
* **Resposta de Sucesso (201 CREATED)**: O objeto do torneio criado.

#### `POST /api/torneios/{torneioId}/avancar-fase`
* **Descrição**: Avança o torneio para a próxima fase do mata-mata.
* **Resposta de Sucesso (201 CREATED)**: Lista de novas partidas ou mensagem de campeão.

#### `GET /api/torneios`
* **Descrição**: Lista todos os torneios.
* **Resposta de Sucesso (200 OK)**: Uma lista de objetos `TorneioResponseDTO`.

---

### 4.7. Ações de Árbitros em Partidas

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

#### `POST /api/arbitros/{matriculaArbitro}/partidas/{partidaId}/wo`
* **Descrição**: Permite que um árbitro registre uma vitória por W.O.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "equipeVencedoraId": 1
    }
    ```
* **Resposta de Sucesso (200 OK)**: O objeto da partida atualizado.