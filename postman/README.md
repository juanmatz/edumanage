# Pruebas de API con Postman — EduManage

Colección de **8 pruebas** sobre el recurso `Grades` (grados): CRUD, validación y casos negativos.

No necesita roles ni datos previos. Solo la aplicación levantada y la base de datos creada.

## Requisitos

- Java 21
- PostgreSQL
- Postman (o Node.js si quieres ejecutarlas por consola con Newman)

## 1. Traer el proyecto

Primera vez:

```bash
git clone https://github.com/juanmatz/edumanage.git
cd edumanage
git checkout develop
git pull origin develop
```

Si ya lo tienes clonado:

```bash
cd edumanage
git checkout develop
git pull origin develop
```

## 2. Crear la base de datos (solo la primera vez)

```bash
psql -U postgres -c "CREATE DATABASE edumanage;"
```

Si la base ya existe, el mensaje de error es inofensivo.

La conexión por defecto está en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/edumanage
spring.datasource.username=postgres
spring.datasource.password=admin
```

Si en tu PC la contraseña es distinta, cambia ese archivo.

## 3. Levantar la aplicación

**Windows (PowerShell):**

```powershell
.\gradlew.bat bootRun
```

**Mac/Linux:**

```bash
chmod +x gradlew
./gradlew bootRun
```

Espera hasta ver `Started ...` y deja esa terminal abierta.

La API queda en `http://localhost:8080`.

## 4. Importar en Postman

1. Abre Postman.
2. **Import** → selecciona:
   - `postman/EduManage-8-Pruebas.postman_collection.json`
   - `postman/EduManage-Local.postman_environment.json`
3. Arriba a la derecha elige el environment **EduManage Local**.

## 5. Ejecutar las pruebas

En Postman:

1. Clic derecho sobre **EduManage - 8 Pruebas (Entrega)**.
2. **Run collection**.
3. **Run**.
4. Revisa **Test Results**: deben salir 8 requests y 21 assertions en verde.

Por consola (requiere Node.js):

```bash
npx newman run postman/EduManage-8-Pruebas.postman_collection.json -e postman/EduManage-Local.postman_environment.json
```

## 6. Si tu API usa otro puerto

En Postman: **Environments** → **EduManage Local** → cambia `baseUrl` por `http://localhost:TU_PUERTO`.

O por consola:

```bash
npx newman run postman/EduManage-8-Pruebas.postman_collection.json -e postman/EduManage-Local.postman_environment.json --env-var baseUrl=http://localhost:8083
```

## Qué prueba cada request

| # | Request | Qué verifica |
|---|---|---|
| 1 | `POST /api/grades` | Crea un grado (201), devuelve ID y guarda los datos. |
| 2 | `GET /api/grades` | Lista 200, es un array y contiene el grado creado. |
| 3 | `GET /api/grades/{id}` | Consulta 200 y el ID coincide. |
| 4 | `PUT /api/grades/{id}` | Actualiza 200 nombre y capacidad. |
| 5 | `POST /api/grades` inválido | 400 con `fieldErrors` de `gradeName` y `maxCapacity`. |
| 6 | `GET /api/grades/999999` | 404 para un grado inexistente. |
| 7 | `DELETE /api/grades/{id}` | Elimina con 204 y sin cuerpo. |
| 8 | `GET /api/grades/{id}` | 404 después de eliminar. |

Cada request agrupa varias aserciones; en total son **21 assertions**.

## Problemas comunes

| Síntoma | Solución |
|---|---|
| `ECONNREFUSED` | La app no está corriendo o `baseUrl` está mal. |
| `psql no se reconoce` | Usa pgAdmin o agrega `C:\Program Files\PostgreSQL\...\bin` al PATH. |
| `npx no se reconoce` | Instala Node.js LTS. |
| Contraseña de PostgreSQL distinta | Edita `src/main/resources/application.properties`. |
| `POST /api/students` responde 500 | Haz `git pull` en `develop`: el fix de `StudentService` ya está incluido. |

## Notas

- La colección está diseñada para ejecutarse **en orden**, del request 1 al 8.
- El request 1 guarda el ID en la variable de entorno `gradeId`; los siguientes lo reutilizan.
- El request 7 elimina el grado creado, así que la base queda limpia.
- Esta colección **no requiere** ejecutar `roles-seed.sql`.
