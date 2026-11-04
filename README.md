# backendjeremy

Para compilar e iniciar el backend en Windows, ejecuta `desplegar.bat` con doble
clic o desde una terminal. Necesitas un JDK 17 o superior y Maven en el `PATH`
(o el wrapper `mvnw.cmd` en la carpeta del proyecto). La primera compilación
necesita acceso a Internet para descargar las dependencias que falten.

```bat
desplegar.bat
```

El script ejecuta las pruebas, genera `target\serf-1.0.0.jar` e inicia Spring Boot
con el perfil `postgres`. Spring Boot carga automáticamente el archivo `.env`
ubicado junto al `.bat`. La conexión existente ya se trasladó a ese archivo;
edita allí `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME` y `DB_PASSWORD`.
`PORT` define el puerto HTTP del backend y `CORS_ORIGINS` los orígenes permitidos
del frontend. Las variables del sistema tienen prioridad sobre `.env`.

El archivo `.env` usa formato Properties: `CLAVE=valor`, sin comillas ni `export`;
las barras invertidas se escriben como `\\`. Está excluido de Git. En otra máquina,
crea `.env` con la conexión y las variables de login descritas debajo. El backend queda en primer
plano: conserva la ventana abierta y usa `Ctrl+C` para detenerlo.

La conexión PostgreSQL usa el protocolo 3. Si defines `DB_URL`, no uses
`protocolVersion=2`: el driver no admite ese protocolo. El driver actualizado
también evita enviar `extra_float_digits` en el paquete de inicio, que el proxy
del hosting rechaza. El registro del backend se guarda en `logs\backend.log`.

Swagger estará disponible al terminar el arranque en
<http://localhost:8080/swagger-ui.html>. Para cambiar el puerto, edita `.env`
y vuelve a ejecutar el `.bat`:

```properties
PORT=8081
```

`DB_DDL_AUTO=update` conserva la creación o actualización automática de las tablas
faltantes. El script SQL inicial agrega las filiales solo cuando no existen.

Para probar sin PostgreSQL, usa el perfil `demo`, que guarda datos temporales en
H2 y los descarta al detener el backend:

```bat
desplegar.bat demo
```

Para ejecutar las pruebas y generar únicamente el JAR:

```bat
desplegar.bat compilar
```

Este script compila e inicia el backend en el equipo Windows donde se ejecuta.
Para consultar sus opciones, ejecuta `desplegar.bat --ayuda`.

La configuración activa es **`.env` en la raíz del proyecto**, junto a
`desplegar.bat`. `application-local.properties` referencia esas variables y no
contiene credenciales. Spring Boot no carga archivos llamados `.env.example`.

El login de la aplicación usa estas variables del mismo `.env`:

```properties
ADMIN_USERNAME=admin
ADMIN_PASSWORD=tu_clave_inicial_de_al_menos_8_caracteres
JWT_SECRET=tu_clave_aleatoria_de_firma_de_al_menos_32_bytes
JWT_TTL_SECONDS=3600
```

En el `.env` local ya se generaron claves aleatorias. Consulta `ADMIN_USERNAME`
y `ADMIN_PASSWORD` allí para iniciar sesión. Son independientes de `DB_USERNAME`
y `DB_PASSWORD`, que sirven para conectar PostgreSQL. Al primer arranque se crea
el usuario en la tabla `usuarios` con su contraseña en BCrypt; si ya existe, no
se sobrescribe. Cambiar `ADMIN_PASSWORD` después no cambia una contraseña ya
guardada en la base de datos.

Envía el login con `Content-Type: application/json`:

```http
POST /api/auth/login

{"username":"admin","password":"valor de ADMIN_PASSWORD en tu .env"}
```

La respuesta contiene `token`, `tokenType`, `expiresIn` y `usuario` (id, username,
rol). Envía `Authorization: Bearer TU_TOKEN` en las siguientes peticiones.
`GET /api/auth/me` devuelve el usuario autenticado. Las operaciones de inventario,
ventas, configuración y reportes requieren token; sin él devuelven HTTP 401.
Swagger y el login son públicos. En Swagger, inicia sesión y pega el token en
**Authorize**. Para cerrar la sesión del cliente, descarta el token; los tokens
emitidos vencen al cumplirse `JWT_TTL_SECONDS`.

El perfil predeterminado `postgres` guarda usuarios, filiales, productos y ventas
en PostgreSQL mediante repositorios JPA. `demo` y las pruebas usan H2 en memoria.
Los reportes consultan esos repositorios a través de `ReporteFacade` y usan estos
patrones en el flujo real:

| Patrón | Implementación y uso |
| --- | --- |
| Singleton | `ConfiguracionGlobal`: moneda y tasas compartidas por ventas y reportes. |
| Builder | `ReporteBuilder`: ensambla plantilla, resumen, tablas y secciones. |
| Prototype | `CatalogoPlantillas` y `PlantillaReporte.clonar()`: cada reporte recibe una copia independiente. |
| Composite | `SeccionCompuesta` y `SeccionSimple`: jerarquía y sumas de las secciones. |
| Decorator | `MarcaAguaDecorator` y `FirmaDigitalDecorator`: añaden funciones al reporte. |
| Facade | `ReporteFacade`: coordina consultas y patrones desde `ReporteController`. |

Las pruebas comprueban la generación de reportes, la independencia de los clones,
las sumas compuestas, los decoradores y la autenticación con contraseñas y tokens.
# Backendv2---Patrones-de-Dise-o-de-Software-
