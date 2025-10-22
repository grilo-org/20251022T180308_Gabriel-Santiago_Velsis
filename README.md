# 📝 Velsis Usuários

Aplicação **fullstack** para cadastro e gerenciamento de usuários, construída com **Spring Boot (Java 21)** no backend e **Vue 3 (Vite)** no frontend.

## ✅ Funcionalidades

* Criar usuários
* Atualizar usuários
* Excluir usuários
* Alterar nome, documento, endereço (com ViaCEP) e data de nascimento

---

## 🚀 Tecnologias Utilizadas

### 🔧 Backend

* ☕ **Java 21**
* ⚡ **Spring Boot 3.5.6**
* 🗄️ **Spring Data JPA**
* ✅ **Spring Validation**
* 🐬 **MySQL**
* ✨ **Lombok**

### 🎨 Frontend

* 🖼️ **Vue 3**
* ⚡ **Vite**
* 📦 **Pinia** (state management)
* 🛣️ **Vue Router**
* 🌐 **Axios**

---

## 📂 Estrutura do Projeto

```
 ┣ 📂 back/      → API REST (Spring Boot)
 ┣ 📂 front/     → Interface (Vue 3 + Vite)
 ┣ 📂 postman/   → Coleção de requisições (JSON)
 ┗ 📄 README.md
```

---

## ⚙️ Como Rodar Localmente

### 🐬 1. Banco de Dados (MySQL)

Crie o banco de dados **usuarios_db**:

```sql
CREATE DATABASE usuarios_db;
```

### 🔧 2. Backend (Spring Boot)

```bash
cd back
./mvnw spring-boot:run
```

📌 API disponível em: **[http://localhost:8080/usuarios](http://localhost:8080/usuarios)**

### 🎨 3. Frontend (Vue 3 + Vite)

```bash
cd front
npm install
npm run dev
```

📌 Aplicação disponível em: **[http://localhost:5173](http://localhost:5173)**

---

## 📌 Endpoints da API

**Base URL:** `/usuarios`

| Método | Endpoint     | Descrição                           |
| ------ | ------------ | ----------------------------------- |
| POST   | `/`          | Criar novo usuário                  |
| GET    | `/`          | Listar todos os usuários (resumido) |
| GET    | `/{id}`      | Buscar usuário por ID               |
| DELETE | `/{id}`      | Excluir usuário permanentemente     |
| PATCH  | `/name`      | Atualizar nome do usuário           |
| PATCH  | `/birthDate` | Atualizar data de nascimento        |
| PATCH  | `/address`   | Atualizar endereço (via CEP)        |
| PATCH  | `/document`  | Atualizar documento (CPF/ID)        |
| PUT    | `/`          | Atualizar todos os dados do usuário |

---

## 👨‍💻 Autor

Desenvolvido por **Gabriel Santiago** 🚀
