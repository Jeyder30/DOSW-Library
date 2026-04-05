# DOSW-Library

Proyecto de clase para administrar una biblioteca por medio de una API REST construida con Spring Boot. La aplicacion permite autenticar usuarios, registrar libros, consultar inventario y gestionar prestamos con control de roles.

## 1. Resumen

| Tema | Detalle |
| --- | --- |
| Nombre del proyecto | DOSW-Library |
| Tipo de aplicacion | API REST |
| Framework principal | Spring Boot 3.3.4 |
| Lenguaje | Java 21 |
| Persistencia principal | PostgreSQL con Spring Data JPA |
| Seguridad | JWT stateless con Spring Security |
| Documentacion | Swagger / OpenAPI |
| Perfil de pruebas | test con H2 en memoria |

## 2. Funcionalidades principales

| Modulo | Que hace |
| --- | --- |
| Autenticacion | Valida credenciales y entrega un token JWT |
| Usuarios | Permite crear usuarios y consultar informacion del usuario autenticado o del listado completo |
| Libros | Registra libros, actualiza datos del inventario y consulta disponibilidad |
| Prestamos | Crea prestamos, registra devoluciones y consulta prestamos activos |
| Seguridad | Restringe acceso segun el rol del usuario |

## 3. Tecnologias usadas

| Tecnologia | Uso dentro del proyecto |
| --- | --- |
| Spring Boot | Configuracion general y arranque de la aplicacion |
| Spring Web | Exposicion de endpoints REST |
| Spring Security | Autenticacion y autorizacion |
| JWT | Generacion y validacion de tokens |
| Spring Data JPA | Acceso a datos relacional |
| PostgreSQL | Base de datos principal |
| H2 | Base de datos en memoria para pruebas |
| MongoDB | Soporte adicional por perfil mongo |
| MapStruct | Conversion entre entidades, DTOs y modelos |
| Lombok | Reduccion de codigo repetitivo |
| JaCoCo | Reporte de cobertura |
| PMD | Analisis estatico |

## 4. Modelo del dominio

El dominio esta compuesto por tres entidades principales: usuarios, libros y prestamos. Un usuario puede tener varios prestamos y cada prestamo hace referencia a un libro.

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

### Reglas de negocio

- Un libro no se puede registrar con availableCopies mayor que totalCopies.
- Un usuario no puede tomar un libro si no hay existencias disponibles.
- Un usuario solo puede tener hasta 3 prestamos activos al mismo tiempo.
- Cuando se crea un prestamo, el inventario disponible disminuye en 1.
- Cuando se registra una devolucion, el inventario disponible aumenta en 1.
- Solo el bibliotecario puede crear usuarios, registrar libros y consultar todos los prestamos.

## 5. Estructura del proyecto

| Ruta | Contenido |
| --- | --- |
| controller | Controladores REST, DTOs y mappers de entrada/salida |
| core | Modelos del dominio, servicios, validadores y reglas de negocio |
| persistence/relational | Entidades JPA, repositorios y adapters para PostgreSQL |
| persistence/nonrelational | Adapters y mappers para el perfil Mongo |
| security | Login, JWT, filtro de autenticacion y configuracion de seguridad |
| test/java | Pruebas de integracion y pruebas de servicios |

## 6. Seguridad y roles

La API usa autenticacion por token. Despues del login, el cliente debe enviar el JWT en el encabezado:
Authorization: Bearer <token>


### Usuario bootstrap

Al arrancar la aplicacion se crea un bibliotecario por defecto si todavia no existe.

| Campo | Valor |
| --- | --- |
| Username | admin |
| Password | Admin123* |
| Rol | LIBRARIAN |

### Roles disponibles

| Rol | Acceso |
| --- | --- |
| LIBRARIAN | Crear usuarios, crear/actualizar libros, consultar usuarios y ver todos los prestamos |
| USER | Consultar libros, pedir prestamos, devolver libros y consultar sus propios prestamos |

## 7. Endpoints principales

| Metodo | Ruta | Rol requerido | Descripcion |
| --- | --- | --- | --- |
| POST | /auth/login | Publico | Inicia sesion y genera el JWT |
| POST | /users | LIBRARIAN | Crea un usuario |
| GET | /users | LIBRARIAN | Lista todos los usuarios |
| GET | /users/{id} | LIBRARIAN | Consulta un usuario por id |
| GET | /users/me | LIBRARIAN, USER | Consulta el usuario autenticado |
| POST | /books | LIBRARIAN | Registra un libro |
| PUT | /books/{id} | LIBRARIAN | Actualiza un libro |
| GET | /books | LIBRARIAN, USER | Lista todos los libros |
| GET | /books/available | LIBRARIAN, USER | Lista solo libros con disponibilidad |
| GET | /books/{id} | LIBRARIAN, USER | Consulta un libro por id |
| POST | /loans | LIBRARIAN, USER | Crea un prestamo |
| PUT | /loans/{id}/return | LIBRARIAN, USER | Registra la devolucion de un prestamo |
| GET | /loans | LIBRARIAN | Lista todos los prestamos |
| GET | /loans/{id} | LIBRARIAN | Consulta un prestamo por id |
| GET | /loans/me | LIBRARIAN, USER | Lista los prestamos del usuario autenticado |

## 8. Ejecucion del proyecto

### Requisitos

| Requisito | Version recomendada |
| --- | --- |
| Java | 21 |
| Maven Wrapper | Incluido en el proyecto |
| PostgreSQL | 15 o superior |
| MongoDB | Solo si se desea usar el perfil mongo |

### Variables de entorno

| Variable | Uso | Valor por defecto |
| --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | Perfil activo | `jpa` |
| `DB_URL` | URL JDBC para PostgreSQL | `jdbc:postgresql://localhost:5433/dosw_library` |
| `DB_USERNAME` | Usuario de base de datos | `postgres` |
| `DB_PASSWORD` | Contrasena de base de datos | `postgres` |
| `MONGODB_URI` | Conexion MongoDB | `mongodb+srv://jeyderleonl_db_user:Je.30le14@cluster0.gfyayxk.mongodb.net/` |
| `JWT_SECRET` | Clave para firmar JWT | valor definido en `application.yaml` |
| `JWT_EXPIRATION_MS` | Duracion del token | `3600000` |
| `CORS_ALLOWED_ORIGINS` | Origenes permitidos | `http://localhost:3000,http://localhost:5173` |
| `SERVER_PORT` | Puerto HTTP | `8080` |

## Ejecucion con PostgreSQL

## Descripción

En esta parte del proyecto se realizó la validación del sistema utilizando PostgreSQL como base de datos y el perfil JPA en Spring Boot.

El objetivo de esta sección fue comprobar que la aplicación pudiera ejecutarse correctamente y que el flujo principal del sistema funcionara de manera adecuada a través de Swagger UI.

Las evidencias incluidas muestran el proceso de conexión, ejecución y validación del comportamiento esperado del sistema.

---

## Base de datos en PostgreSQL

Como parte de la ejecución del sistema, se utilizó una base de datos llamada `dosw_library` en PostgreSQL.

En la siguiente evidencia se puede observar la base de datos creada y disponible dentro de pgAdmin.

### Evidencia

![Base de datos en pgAdmin](docs/imagenes/base_de_datos_pgAdmin.png)

---

## Ejecución del proyecto

Una vez configurado el entorno, el proyecto fue ejecutado desde IntelliJ utilizando la configuración correspondiente para trabajar con PostgreSQL.

En la siguiente imagen se muestra la aplicación en ejecución dentro del entorno de desarrollo.

### Evidencia

![Aplicación ejecutándose en IntelliJ](docs/imagenes/aplicacion_intelliJ.png)

---

## Acceso a Swagger UI

Con la aplicación en ejecución, se accedió a la documentación interactiva de la API mediante Swagger UI.

Desde esta interfaz fue posible interactuar con los endpoints disponibles y validar el funcionamiento del sistema.

### Evidencia

![Swagger UI](docs/imagenes/swagger.png)
![Swagger UI](docs/imagenes/swagger1.png)

---

## Autenticación del sistema

Una de las primeras validaciones realizadas fue el inicio de sesión dentro del sistema.

En la siguiente evidencia se observa la autenticación exitosa, lo que permitió acceder a las operaciones protegidas de la API.

### Evidencia


![Login exitoso](docs/imagenes/login.png)
![Login exitoso](docs/imagenes/login1.png)
![Login exitoso](docs/imagenes/login2.png)

---

## Registro de un libro

Posteriormente, se realizó el registro de un libro dentro del sistema.

En la siguiente imagen se puede observar la solicitud realizada y la respuesta obtenida al crear correctamente el recurso.

### Evidencia


![Creación de libro](docs/imagenes/registro_libro.png)

---

## Registro de un préstamo

Después de crear el libro, se realizó el registro de un préstamo dentro del sistema.

La siguiente evidencia muestra el momento en el que se registra correctamente esta operación.

### Evidencia

![Préstamo exitoso](docs/imagenes/prestamo.png)

---

## Validación del estado final del libro

Finalmente, se realizó la consulta del libro registrado para verificar su estado después del préstamo.

En la siguiente imagen se evidencia que la cantidad de copias disponibles fue actualizada correctamente, lo cual confirma el comportamiento esperado del sistema.

### Evidencia

![Validación de stock](docs/imagenes/validacion_stock.png)

---

## Conclusión

A partir de las evidencias presentadas, se puede observar que el sistema fue ejecutado correctamente con PostgreSQL y que el flujo principal validado respondió de manera satisfactoria.

Las imágenes permiten comprobar el funcionamiento general de la aplicación, desde su ejecución hasta la validación de operaciones principales relacionadas con autenticación, gestión de libros y registro de préstamos.

---
### MONGO
![Mongo](docs/imagenes/Mongo.png)
![Mongo](docs/imagenes/Mongo2.png)
![Mongo](docs/imagenes/Mongo1.png)
![Mongo](docs/imagenes/Mongo3.png)
---
#### Cobertura y analisis estatico

<img width="1463" height="588" alt="image" src="https://github.com/user-attachments/assets/885b5958-abc8-46e7-9e7d-9f5348694bb9" />
<img width="1494" height="758" alt="image" src="https://github.com/user-attachments/assets/c7cbda9c-7be9-44c7-8a49-5d363d5bae03" />
<img width="1484" height="439" alt="image" src="https://github.com/user-attachments/assets/e16d2439-5294-41ac-992d-61b38aa46d12" />
<img width="1466" height="413" alt="image" src="https://github.com/user-attachments/assets/5152a247-9b5e-49e9-a95a-128826f624ef" />



