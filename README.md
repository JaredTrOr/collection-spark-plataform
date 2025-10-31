# 👾 Collectible E-Commerce API

Jared Alexander Trujillo Ortiz
NAO ID: 3347
**Date:** October 27, 2025  

---

A lightweight and secure REST API for a collectible items e-commerce platform. This project is built from scratch using Java and the **Spark Java** micro-framework, with a strong focus on professional, decoupled, and secure architecture.

## ✨ Features

* **Product Catalog:** Full CRUD (Create, Read, Update, Delete) for collectible items.
* **Secure by Design:**
    * **Prevents SQL Injection:** Uses `PreparedStatement` for all database queries.
    * **Prevents Mass Assignment:** Uses DTOs (Data Transfer Objects) to separate API requests from database models.
    * **Safe Credentials:** Reads database credentials from an environment-ignored `.properties` file.
* **Scalable Architecture:** Built on a decoupled 3-layer (Controller-Service-Repository) pattern with a centralized `ApiRouter`.

## 💻 Tech Stack

* **Backend:** Java 17+
* **Framework:** Spark Java (v2.9.4)
* **Database:** PostgreSQL
* **JSON Library:** Google Gson
* **Build Tool:** Apache Maven

## 🏛️ Architecture

This API is built using modern, decoupled design patterns to ensure it is maintainable, testable, and secure.

1.  **Layer Architecture:**
    * **Controller Layer:** Handles all HTTP requests and responses. It's the only layer that "speaks" JSON and HTTP.
    * **Service Layer:** Contains all business logic (e.g., "is this username taken?", "hash this password," "validate this price").
    * **Repository Layer:** Handles all database communication. It's the only layer that "speaks" SQL.

2.  **Dependency Inversion (The "D" in SOLID):**
    * Services and Controllers depend on `interfaces` (like `ItemRepository`, `UserRepository`) instead of concrete classes.
    * This allows us to "plug in" different implementations, (e.g., swap `PostgresUserRepository` for a `MySqlUserRepository`) without changing any business logic.
    * We use a **Generic `CrudRepository` Interface** to keep our code DRY.

3.  **DTO (Data Transfer Object) Pattern:**
    * We use DTOs (e.g., `UserCreateRequest`, `UserResponse`) for all API communication.
    * This ensures a stable API "contract" and acts as a firewall, preventing clients from sending unwanted data (mass assignment) or receiving sensitive data (like the `passwordHash`).

4.  **Centralized Router:**
    * The `ApiRouter` class is responsible for "wiring" all the dependencies together (manual dependency injection) and registering all controller routes.
    * This keeps the main `Main.java` file extremely clean and simple.

## 🚀 Getting Started

### Prerequisites

* Java (JDK 17 or newer)
* Apache Maven
* PostgreSQL (must be installed and running)

### 1. Set Up the Database

1.  Open `psql` or your preferred SQL client and create a new database.
    ```sql
    CREATE DATABASE collectibles;
    ```
2.  You will need to create the `users` and `items` tables. Make sure your tables are configured to use `UUID` as their primary key.

    **Example `items` table:**
    ```sql
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

    CREATE TABLE items (
        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        name VARCHAR(255) NOT NULL,
        description TEXT,
        price DECIMAL(10, 2) NOT NULL,
        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
        inventory INT NOT NULL DEFAULT 0 CHECK (inventory >= 0)
    );
    ```
    **Example `users` table:**
    ```sql
    CREATE TABLE users (
        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        name VARCHAR(255) NOT NULL,
        username VARCHAR(100) NOT NULL UNIQUE,
        email VARCHAR(255) NOT NULL UNIQUE,
        password VARCHAR(255) NOT NULL,
        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
        deleted_at TIMESTAMP WITH TIME ZONE
    );
    ```

### 2. Configure Credentials

1.  Navigate to the `src/main/resources/` directory.
2.  Create a new file named `config.properties`.
3.  Add your database credentials to this file:

    ```properties
    # -- PostgreSQL Database Configuration --
    POSTGRESDB_URL=jdbc:postgresql://localhost:5432/collectibles
    POSTGRESDB_USER=your_postgres_user
    POSTGRESDB_PASS=your_secret_password
    ```
    > **Note:** This `config.properties` file is included in `.gitignore` and **should never** be committed to the repository.

### 3. Build and Run

1.  From your IDE, right-click the `pom.xml` file and select "Reload Project" to install all Maven dependencies.
2.  Run the application by starting the `main` method in `org.jared.trujillo.Main.java`.

The server will start at `http://localhost:4567`.

## 📖 API Endpoints

All API endpoints are prefixed with `/api/v1`.

### User Management (`/api/v1/users`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/` | Registers a new user (Sign Up). |
| `GET` | `/:id` | Gets a user's public profile by their ID. |
| `PUT` | `/:id` | Updates a user's name, username, or email. |
| `DELETE` | `/:id` | Soft-deletes a user. |

### Product Catalog (`/api/v1/items`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/` | Creates a new collectible item. |
| `GET` | `/:id` | Gets a single item by its ID. |
| `PUT` | `/:id` | Updates an existing item. |
| `DELETE` | `/:id` | Deletes an item from the catalog. |
