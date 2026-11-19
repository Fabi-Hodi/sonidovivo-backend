# Sonido Vivo - Backend

Backend de Sonido Vivo, tienda online de instrumentos musicales. DSY1104 Desarrollo Fullstack II.

- **Integrantes:** Fabián Muñoz, Jonathan Pozo
- **Sección:** (completar)
- **Frontend:** (pegar enlace)
- **Swagger:** http://3.87.120.220:8080/swagger-ui.html (la IP cambia al reiniciar el laboratorio de AWS)
- **Diagramas (modelo de datos y flujo de login):** (pegar enlace de Drive)

## Tecnologías

- Java 21 y Spring Boot 3.5 (generado con Spring Initializr)
- Spring Security + JWT
- PostgreSQL en Amazon RDS, con Spring Data JPA
- Swagger para documentar y probar la API
- EC2 con Ubuntu 24.04

La base está en RDS para que los datos no dependan del servidor: si se reinicia o se cambia la EC2, los datos siguen ahí. La base no tiene acceso público; solo la EC2 se conecta a ella.

## Base de datos

Por ahora hay dos tablas:

- `rol`: Administrador, Vendedor y Cliente.
- `usuario`: nombre, correo (único), contraseña como hash, fecha de nacimiento, dirección, estado activo y su rol.

Cada usuario tiene un rol. Al eliminar un usuario se desactiva en vez de borrarse. El SQL está en `db/01_modelo_referencia.sql`.

## Estructura

```
src/main/java/cl/duoc/sonidovivo/
├── controller   endpoints
├── service      lógica
├── repository   acceso a la base de datos
├── model        tablas
├── dto          datos de entrada y salida
├── security     JWT y errores 401/403
├── config       seguridad, Swagger y datos iniciales
└── exception    manejo de errores
```

## Endpoints

| Método | Ruta | Permiso | Pantalla |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Público | Crear cuenta |
| POST | `/api/v1/auth/login` | Público | Login |
| GET | `/api/v1/auth/me` | Con sesión | Perfil |
| GET | `/api/v1/usuarios` | Administrador | Administrar usuarios |
| GET | `/api/v1/usuarios/{id}` | Administrador o el mismo usuario | Perfil |
| POST | `/api/v1/usuarios` | Administrador | Administrar usuarios |
| PUT | `/api/v1/usuarios/{id}` | Administrador o el mismo usuario | Perfil |
| PATCH | `/api/v1/usuarios/{id}/rol` | Administrador | Administrar usuarios |
| DELETE | `/api/v1/usuarios/{id}` | Administrador | Administrar usuarios |
| PATCH | `/api/v1/usuarios/{id}/reactivar` | Administrador | Administrar usuarios |

Para probar en Swagger: hacer login, copiar el `accessToken`, presionar **Authorize** y pegarlo. En `postman/` hay una colección con las pruebas.

## Seguridad

- **Contraseñas:** se guardan con BCrypt, que es lento a propósito y agrega una sal distinta a cada contraseña. Es lo que recomienda OWASP.
- **Login:** si los datos están mal, siempre responde "Correo o contraseña incorrectos". Después de 5 intentos fallidos, el correo se bloquea 5 minutos.
- **JWT:** lo genera el backend al hacer login, firmado con una clave que está en el servidor (no en el código). Dura 60 minutos. En cada petición se revisa que sea válido y que la cuenta siga activa.
- **Permisos:** el registro siempre crea clientes. Solo el Administrador lista usuarios, cambia roles y desactiva cuentas. Un cliente solo puede ver y editar su propia cuenta.
- **Errores:** 401 si no hay sesión o el token no es válido; 403 si no tiene permiso.

## Cómo ejecutarlo

En la EC2:

```bash
git clone URL_DEL_REPOSITORIO
cd sonidovivo-backend
cp .env.example .env      # completar con los datos del RDS
./mvnw clean package -DskipTests
set -a; source .env; set +a
java -jar target/sonidovivo-backend-0.1.0.jar
```

El `.env` tiene las claves y no se sube a GitHub. Al iniciar se crean las tablas y estas cuentas de prueba (contraseña: la de `DEMO_PASSWORD`):

- `fabian@sonidovivo.cl` y `jonathan@sonidovivo.cl`: Administrador
- `vendedor@sonidovivo.cl`: Vendedor
- `cliente@sonidovivo.cl`: Cliente

## Fuentes

- OWASP Password Storage Cheat Sheet: https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html
- Spring Security: https://docs.spring.io/spring-security/reference/
- JWT (RFC 7519): https://datatracker.ietf.org/doc/html/rfc7519

## Estado

| Funcionalidad | Estado | Probado |
|---|---|---|
| Tabla de usuarios en PostgreSQL | Listo | 18-11-2026 |
| Inicio de sesión con JWT | Listo | 18-11-2026 |
| Registro | Listo | 18-11-2026 |
| Modificar usuario | Listo | 18-11-2026 |
| Listar usuarios | Listo | 18-11-2026 |
| Eliminar (desactivar) usuario | Listo | 18-11-2026 |
| Cambio de rol | En avance | Falta probar |
| Rutas protegidas (401/403) | En avance | 403 probado, falta 401 sin token |
