# DOSW-Library

API REST para gestion de biblioteca con:

- Persistencia relacional con Spring Data JPA
- PostgreSQL como base principal
- JWT stateless con Spring Security
- Roles `LIBRARIAN` y `USER`
- Inventario de libros por existencias totales y disponibles
- Swagger/OpenAPI
- Pruebas funcionales con H2

## Arquitectura

- `controller`: endpoints REST, DTOs y manejo HTTP
- `core`: dominio, reglas de negocio, validadores y servicios
- `persistence`: entidades JPA, repositorios y mappers MapStruct
- `security`: autenticacion JWT, filtro, autorizacion y CORS

## Modelo ER

```mermaid
erDiagram
    USERS ||--o{ LOANS : has
    BOOKS ||--o{ LOANS : is_loaned_in

    USERS {
        uuid id PK
        string name
        string username UK
        string password
        string role
    }

    BOOKS {
        uuid id PK
        string title
        string author
        string isbn UK
        int total_copies
        int available_copies
    }

    LOANS {
        uuid id PK
        uuid user_id FK
        uuid book_id FK
        string status
        date loan_date
        date return_date
    }
```

## Configuracion

La aplicacion toma sus valores desde `src/main/resources/application.yaml`.

Variables principales:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_MS`
- `CORS_ALLOWED_ORIGINS`
- `SSL_ENABLED`
- `SSL_KEY_STORE`
- `SSL_KEY_STORE_PASSWORD`
- `SSL_KEY_STORE_TYPE`
- `SSL_KEY_ALIAS`

Ejemplo para PostgreSQL local:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/dosw_library"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"
./mvnw spring-boot:run
```

## Usuario bootstrap

Al iniciar la aplicacion se crea un bibliotecario por defecto si no existe:

- `username`: `admin`
- `password`: `Admin123*`

## Seguridad

- Login: `POST /auth/login`
- Header requerido para endpoints protegidos:

```text
Authorization: Bearer <token>
```

- `LIBRARIAN`: gestiona libros, usuarios y consulta todos los prestamos
- `USER`: consulta libros disponibles, solicita prestamos, devuelve libros y consulta solo sus prestamos

## Swagger

- UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## HTTPS

La aplicacion soporta SSL/TLS por propiedades. Para activarlo configure:

- `SSL_ENABLED=true`
- `SSL_KEY_STORE`
- `SSL_KEY_STORE_PASSWORD`
- `SSL_KEY_STORE_TYPE`
- `SSL_KEY_ALIAS`

## Pruebas

```powershell
./mvnw test
./mvnw pmd:pmd
```

Las pruebas usan H2 con el perfil `test` y validan:

- autenticacion JWT
- rechazo sin token
- rechazo con token invalido
- rechazo por rol incorrecto
- persistencia de usuarios, libros y prestamos
- actualizacion de inventario al prestar y devolver

## La ejecucion de las funcionalidades de su API
<img width="1092" height="691" alt="image" src="https://github.com/user-attachments/assets/bc6e77ba-cc7c-4e7f-b14f-130cff9a73b0" />
<img width="1100" height="794" alt="image" src="https://github.com/user-attachments/assets/a0350b81-b657-4936-9c95-332438528924" />

## La ejecucion de las pruebas de los servicios 
<img width="333" height="299" alt="image" src="https://github.com/user-attachments/assets/471d91f1-b93c-40bd-9478-2dfae6093169" />

## la cobertura y analisis estatico
<img width="1143" height="836" alt="image" src="https://github.com/user-attachments/assets/ba27e33f-3597-4e33-9aa2-885dda94210d" />


