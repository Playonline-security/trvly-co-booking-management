# ✈️ trvly.co - Sistema de Gestión de Agencia de Viajes

Proyecto en desarrollo para gestión de una agencia de viajes, creado con Spring Boot y React como parte de mi proceso de aprendizaje en desarrollo de software.
---

## 🚀 Tecnologías

### 🔹 Backend
- ☕ Java 17
- 🍃 Spring Boot 3.2.0
- 🔐 Spring Security (JWT)
- 🐘 PostgreSQL
- 🧩 JPA / Hibernate
- 🧪 JUnit 5

### 🔹 Frontend
- ⚛️ React 18
- 🎨 Tailwind CSS
- 🔄 Axios
- 🧭 React Router

---

## 📂 Estructura del Proyecto

```
trvly-co/
├── src/
│   ├── main/
│   │   ├── java/co/trvly/
│   │   │   ├── entity/          # Entidades JPA
│   │   │   ├── repository/      # Repositorios
│   │   │   ├── service/         # Lógica de negocio
│   │   │   ├── controller/      # Controladores REST
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── security/        # Configuración de seguridad
│   │   │   └── util/            # Utilidades (JWT)
│   │   └── resources/
│   │       └── application.properties
│   └── test/                    # Pruebas JUnit
├── frontend/                     # Aplicación React
├── database/                     # Scripts SQL
│   ├── schema.sql
│   └── data.sql
└── pom.xml
```

---

## ⚙️ Configuración Inicial

### 1️⃣ Base de Datos PostgreSQL

Crear la base de datos:
```sql
CREATE DATABASE trvly_co;
```

Ejecutar los scripts SQL en orden:
```bash
psql -U postgres -d trvly_co -f database/schema.sql
psql -U postgres -d trvly_co -f database/data.sql
```

---

### 2️⃣ Configuración Backend

Modificar `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/trvly_co
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

---

### 3️⃣ Ejecutar Backend

```bash
cd trvly-co
mvn clean install
mvn spring-boot:run
```

📌 Disponible en: `http://localhost:8080`

---

### 4️⃣ Ejecutar Frontend

```bash
cd frontend
npm install
npm start
```

📌 Disponible en: `http://localhost:3000`

---

## 👤 Usuario por Defecto

| Campo | Valor |
|-------|------|
| Usuario | **admin** |
| Contraseña | **admin123** |



---

## 🛂 Roles y Permisos

### 🧑‍💼 Asesor (ADVISOR)
- CRUD de clientes (sin eliminar definitivamente)
- CRUD de reservas (sin eliminar definitivamente)

### 🧑‍✈️ Supervisor (SUPERVISOR)
- Todos los permisos de Asesor
- Eliminación de clientes y reservas

### 🛡️ Administrador (ADMIN)
- Gestión total del sistema
- Usuarios y paquetes turísticos

---

## 🔗 Endpoints API

### 🔐 Autenticación
- `POST /api/auth/login`

### 👥 Clientes
- `GET /api/clients`
- `GET /api/clients/{id}`
- `POST /api/clients`
- `PUT /api/clients/{id}`
- `DELETE /api/clients/{id}`

### 📑 Reservas
- `GET /api/reservations`
- `GET /api/reservations/{id}`
- `GET /api/reservations/quotation`
- `POST /api/reservations`
- `PUT /api/reservations/{id}/status`
- `DELETE /api/reservations/{id}`

### 🧳 Paquetes (Admin)
- `GET /api/admin/packages`
- `GET /api/admin/packages/{id}`
- `POST /api/admin/packages`
- `PUT /api/admin/packages/{id}`
- `DELETE /api/admin/packages/{id}`

### 👤 Usuarios (Admin)
- `GET /api/admin/users`
- `GET /api/admin/users/{id}`
- `POST /api/admin/users`
- `PUT /api/admin/users/{id}`
- `DELETE /api/admin/users/{id}`

---

## 🧪 Pruebas Unitarias

Ejecutar:
```bash
mvn test
```

Incluye validaciones clave como:
1. Unicidad de clientes
2. Cálculo de cotizaciones
3. Persistencia de cambios
4. Control de estados
5. Creación de usuarios con validación de roles

---

## 🌟 Características Principales

- 🔒 Autenticación JWT y control por roles
- 🧍 Gestión de clientes con filtros avanzados
- 🧳 Gestión de paquetes turísticos
- 📄 Reservas con cotización automática
- 👥 Pasajeros por reserva
- 💻 UI moderna y responsiva
- ⚙️ Validaciones en backend & frontend
- 🧪 Pruebas unitarias activas

---

## 👨‍💻 Desarrollo

Compilar:
```bash
mvn clean package
```

Ejecutar pruebas:
```bash
mvn test
```

---

## 📌 Notas Importantes

- Código en inglés (nomenclatura)
- UI en español (Sentence Case)
- Generación automática de números de reserva
- Cotizaciones = precio × pasajeros

---

## 📜 Licencia

Proyecto de uso educativo. Libre para explorar, estudiar y mejorar.

