# Sistema de Gerenciamento de Jogos Interclasse - IFS

## 1. Objetivo do Projeto

Este projeto consiste em um sistema Back-end, desenvolvido em Spring Boot, para gerenciar os jogos interclasse do Instituto Federal de Sergipe (IFS). O sistema cobre desde o cadastro de cursos, esportes, equipes e usuários, até a geração completa de um torneio, com lógicas avançadas para sorteio de grupos e agendamento de partidas. A API é protegida, utilizando autenticação baseada em JWT e autorização por papéis, e implementa paginação para consultas eficientes.

Este projeto foi desenvolvido como requisito para a disciplina de Web 1 do curso de Bacharelado em Sistemas de Informação.

## 2. Tecnologias Utilizadas

* **Java 17**: Linguagem de programação principal.
* **Spring Boot 3**: Framework principal para construção da aplicação.
    * **Spring Web**: Para criação dos endpoints da API RESTful.
    * **Spring Data JPA**: Para persistência de dados e comunicação com o banco.
    * **Spring Security**: Para autenticação e autorização baseada em JWT.
    * **Spring Test**: Para a suíte de testes (unitários, integração e API).
* **Hibernate**: Implementação do JPA para mapeamento objeto-relacional (ORM).
* **Maven**: Ferramenta de gerenciamento de dependências e build do projeto.
* **JWT (JSON Web Token)**: Para a geração e validação de tokens de autenticação.
* **H2 Database**: Banco de dados em memória utilizado para os testes.
* **SQL Server**: SGBD utilizado em desenvolvimento.
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

## 4. Segurança e Autenticação

A maioria dos endpoints desta API é protegida e requer um token de autenticação para ser acessada. O sistema utiliza **JSON Web Tokens (JWT)**.

### `POST /api/auth/login`
* **Descrição**: Autentica um usuário (Admin, Coordenador, Técnico, Árbitro ou Atleta) e retorna um token JWT. Este é um dos poucos endpoints públicos.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "matricula": "admin",
      "senha": "admin"
    }
    ```
* **Resposta de Sucesso (200 OK)**:
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZWMwMDEiLCJpYXQiOjE3MjQ0NDg..."
    }
    ```

### Uso do Token

Após obter o token, você deve incluí-lo em todas as chamadas para endpoints protegidos, utilizando o cabeçalho `Authorization` com o prefixo `Bearer`.

**Exemplo de Cabeçalho:**

Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZWMwMDEiLCJpYXQiOjE3MjQ0NDg...

Qualquer tentativa de acessar um endpoint protegido sem um token válido ou sem a permissão (`role`) necessária resultará em uma resposta `403 Forbidden`.

## 5. Documentação da API (Endpoints)

### Paginação

Todos os endpoints `GET` que retornam listas de recursos são paginados. Você pode controlar a paginação usando os seguintes parâmetros na URL:
* `page`: O número da página que você deseja (começando em 0).
* `size`: O número de itens por página.
* `sort`: O campo pelo qual você deseja ordenar, seguido de `,asc` ou `,desc`.

**Exemplo:** `GET /api/cursos?page=0&size=5&sort=nome,desc`

---

### 5.1. Gestão de Administradores (`/api/admin`)
* **Permissão Requerida**: `ROLE_ADMIN`

#### `POST /api/admin/coordenadores`
* **Descrição**: Cadastra um novo coordenador.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "matricula": "coordSI01",
      "nome": "Prof. Coordenador de SI",
      "senha": "senhaSegura123"
    }
    ```

#### `GET /api/admin/coordenadores`
* **Descrição**: Lista todos os coordenadores de forma paginada.

#### `GET /api/admin/coordenadores/{matricula}`
* **Descrição**: Busca um coordenador específico pela matrícula.

#### `PUT /api/admin/coordenadores/{matricula}`
* **Descrição**: Atualiza os dados de um coordenador.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "nome": "Nome Coordenador Atualizado",
      "senha": "novaSenhaSegura456"
    }
    ```

#### `DELETE /api/admin/coordenadores/{matricula}`
* **Descrição**: Deleta um coordenador.

#### `POST /api/admin/arbitros`
* **Descrição**: Cadastra um novo árbitro.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "matricula": "arb001",
      "nome": "Árbitro Oficial",
      "senha": "senhaArbitro123"
    }
    ```
#### `GET /api/admin/arbitros/{matricula}`
* **Descrição**: Busca um árbitro específico pela matrícula.

#### `PUT /api/admin/arbitros/{matricula}`
* **Descrição**: Atualiza os dados de um árbitro.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "nome": "Juiz Atualizado",
      "senha": "outraSenha456"
    }
    ```

#### `DELETE /api/admin/arbitros/{matricula}`
* **Descrição**: Deleta um árbitro.

---

### 5.2. Gestão de Cursos (`/api/cursos`)
* **Permissão Requerida**: `POST`, `PUT`, `DELETE` exigem autenticação. `GET` é público.

#### `POST /api/cursos`
* **Descrição**: Cadastra um novo curso.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "nome": "Engenharia de Software",
      "categoria": "SUPERIOR"
    }
    ```

#### `GET /api/cursos`
* **Descrição**: Lista todos os cursos de forma paginada.

#### `GET /api/cursos/{id}`
* **Descrição**: Busca um curso específico pelo ID.

#### `PUT /api/cursos/{id}`
* **Descrição**: Atualiza um curso.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "nome": "Ciência da Computação"
    }
    ```
#### `DELETE /api/cursos/{id}`
* **Descrição**: Deleta um curso.

---

### 5.3. Gestão de Esportes (`/api/esportes`)
* **Permissão Requerida**: `POST`, `PUT`, `DELETE` exigem autenticação. `GET` é público.

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

#### `GET /api/esportes`
* **Descrição**: Lista todos os esportes de forma paginada.

#### `GET /api/esportes/{id}`
* **Descrição**: Busca um esporte específico pelo ID.

#### `PUT /api/esportes/{id}`
* **Descrição**: Atualiza um esporte.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "minAtletas": 2,
      "maxAtletas": 4
    }
    ```

#### `DELETE /api/esportes/{id}`
* **Descrição**: Deleta um esporte.

---

### 5.4. Gestão de Técnicos
* **Permissão Requerida**: `ROLE_COORDENADOR` para criar. `GET` para qualquer autenticado.

#### `POST /api/coordenadores/{matriculaCoordenador}/tecnicos`
* **Descrição**: Permite que um coordenador cadastre um novo técnico.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "matricula": "tec001",
      "nome": "Professor Silva",
      "senha": "senhaTecnico123"
    }
    ```

#### `GET /api/tecnicos`
* **Descrição**: Lista todos os técnicos cadastrados de forma paginada.

#### `GET /api/tecnicos/{matricula}`
* **Descrição**: Busca um técnico específico pela matrícula.

---

### 5.5. Gestão de Atletas e Equipes
* **Permissão Requerida**: `ROLE_TECNICO`

#### `POST /api/tecnicos/{matriculaTecnico}/equipes`
* **Descrição**: Permite que um técnico cadastre uma nova equipe.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "equipe": {
        "nome": "Os Invencíveis",
        "cursoId": 1,
        "esporteId": 1
      },
      "matriculasAtletas": ["atl001", "atl002", "atl003"]
    }
    ```

#### `DELETE /api/tecnicos/{matriculaTecnico}/equipes/{equipeId}`
* **Descrição**: Permite que um técnico delete sua própria equipe.

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

#### `DELETE /api/tecnicos/{matriculaTecnico}/atletas/{matriculaAtleta}/db`
* **Descrição**: Deleta permanentemente um atleta do banco de dados.

#### `GET /api/equipes`
* **Descrição**: Lista todas as equipes de forma paginada.
* **Permissão Requerida**: Qualquer usuário autenticado.

#### `GET /api/equipes/{id}`
* **Descrição**: Busca uma equipe específica pelo ID.

#### `GET /api/atletas`
* **Descrição**: Lista todos os atletas de forma paginada.

#### `GET /api/atletas/{matricula}`
* **Descrição**: Busca um atleta específico pela matrícula.

---

### 5.6. Gestão de Torneios e Partidas
* **Permissão Requerida**: `POST` exige `ROLE_ADMIN`. `GET` é público.

#### `POST /api/torneios/iniciar`
* **Descrição**: Inicia um torneio, criando os grupos e as partidas da fase inicial.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "esporteId": 1,
      "categoria": "SUPERIOR"
    }
    ```

#### `POST /api/torneios/{torneioId}/avancar-fase`
* **Descrição**: Avança o torneio para a próxima fase do mata-mata.

#### `GET /api/torneios`
* **Descrição**: Lista todos os torneios.

#### `GET /api/torneios/{id}`
* **Descrição**: Busca um torneio específico pelo ID.

#### `GET /api/torneios/{torneioId}/partidas`
* **Descrição**: Lista todas as partidas de um torneio específico.

---

### 5.7. Ações de Árbitros em Partidas
* **Permissão Requerida**: `ROLE_ARBITRO`

#### `PUT /api/arbitros/{matriculaArbitro}/partidas/{partidaId}/resultado`
* **Descrição**: Permite que um árbitro registre o placar final de uma partida.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "placarA": 25,
      "placarB": 20
    }
    ```

#### `POST /api/arbitros/{matriculaArbitro}/partidas/{partidaId}/wo`
* **Descrição**: Permite que um árbitro registre uma vitória por W.O.
* **Corpo da Requisição (Exemplo)**:
    ```json
    {
      "equipeVencedoraId": 1
    }
    ```
#### `PUT /api/arbitros/{matriculaArbitro}/partidas/{partidaId}/reverter`
* **Descrição**: Reverte o resultado de uma partida (normal ou W.O), retornando-a ao status "AGENDADA".
