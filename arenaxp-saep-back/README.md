# ArenaXP — Sistema de Controle de Estoque & Equipamentos

Projeto prático desenvolvido para o **Preparatório SAEP 2026 (SENAI — Técnico em Desenvolvimento de Sistemas)**. O sistema realiza o gerenciamento completo de equipamentos de e-sports/eventos, controle de entradas e saídas, alertas de estoque mínimo e auditoria de movimentações por usuário autenticado.

---

## Tecnologias Utilizadas

* **Back-end:** Java 17+, Spring Boot 3.3.5, Spring Web, Spring Data JPA, Hibernate
* **Banco de Dados:** MySQL 8+ (`arenaxp_db`)
* **Front-end:** HTML5, CSS3, JavaScript (Fetch API com suporte a sessão/cookies)
* **Segurança:** Autenticação baseada em sessão HTTP (`AuthInterceptor`) e senhas armazenadas com hash criptográfico SHA-256

---

## Estrutura de Pastas do Projeto

```text
arenaxp-saep-2026/
├── Arena_xp_Back/            # API REST e regras de negócio (Spring Boot / Maven)
│   ├── src/
│   │   └── main/
│   │       ├── java/br/com/arenaxp/
│   │       └── resources/application.properties
│   └── pom.xml
├── ArenaXP_Front/            # Interface web responsiva
│   ├── index.html
│   ├── login.html
│   ├── style.css
│   └── script.js
├── database/                 # Modelagem e carga inicial do banco
│   └── arenaxp_db.sql
├── .gitignore
└── README.md
```

---

## Credenciais de Acesso (Carga Inicial)

O banco de dados já é inicializado com **3 usuários** (1 administrador e 2 operadores), **5 equipamentos** e **4 movimentações** para testes imediatos:

| Perfil | Nome | E-mail | Senha |
| :--- | :--- | :--- | :--- |
| **Administrador** | Administrador ArenaXP | `admin@arenaxp.com` | `ArenaXP@2026#Admin` |
| **Operador 1** | Lucas Almeida (Operador) | `operador1@arenaxp.com` | `Operador@2026` |
| **Operador 2** | Mariana Costa (Operador) | `operador2@arenaxp.com` | `Operador@2026` |

---

## Como Executar o Projeto

### 1. Banco de Dados (MySQL)
1. Certifique-se de que o serviço do **MySQL 8+** está em execução na porta `3306`.
2. Execute o script `database/arenaxp_db.sql` no MySQL Workbench (ou cliente SQL de sua preferência) para criar o schema `arenaxp_db`, as tabelas e a carga inicial.
   * *Nota:* Caso prefira iniciar o banco vazio, o próprio Spring Boot cria o schema automaticamente (`createDatabaseIfNotExist=true`) e popula a carga inicial via `DataInitializer.java` sem duplicar registros ao reiniciar.

### 2. Back-end (Spring Boot)
1. Abra o arquivo `Arena_xp_Back/pom.xml` no **IntelliJ IDEA** (como projeto Maven).
2. Verifique o arquivo `Arena_xp_Back/src/main/resources/application.properties`:
   * A conexão padrão está configurada com usuário `root` e senha vazia (ou via variáveis de ambiente `DB_USERNAME` e `DB_PASSWORD`). Caso o seu MySQL local utilize senha, ajuste a propriedade `spring.datasource.password`.
3. Execute a classe principal `ArenaxpApplication.java`.
4. A API estará disponível em `http://localhost:8080`.

### 3. Front-end (Interface Web)
1. Abra a pasta `ArenaXP_Front/` no **VS Code**.
2. Inicie a aplicação utilizando a extensão **Live Server** (porta padrão `5500`) a partir do arquivo `login.html`.
3. Acesse no navegador usando **`http://localhost:5500/login.html`** (utilize `localhost` na URL para manter o cookie de sessão compatível com o back-end).
