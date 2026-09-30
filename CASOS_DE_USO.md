# EduManage — Especificación de Casos de Uso

Especificación funcional de los casos de uso del sistema EduManage (SaaS para colegios rurales que acerca a los padres al proceso académico de sus hijos).

> Documento complementario: la descripción técnica, el modelo de dominio y el stack se detallan en [DOCUMENTACION.md](DOCUMENTACION.md).
>
> **Convención de estados**: ✅ implementado en código · 🔵 modelo de datos listo, flujo propuesto · ⚪ pendiente.
>
> Los flujos marcados como *propuestos* corresponden a diseño futuro: se especifican para guiar el desarrollo y no reflejan comportamiento actual del sistema.

---

## 1. Actores del sistema

| Actor | Descripción |
| :--- | :--- |
| **ADMIN** | Personal administrativo del colegio. Gestiona cuentas de usuario, estudiantes, docentes, tutores y toda la estructura académica (grados, materias, cursos, horarios). |
| **TEACHER** | Docente. Registra calificaciones y asistencia diaria; consulta sus cursos, estudiantes y horario. |
| **STUDENT** | Estudiante. Consulta sus propias notas, asistencia y horario. |
| **PARENT** | Padre/tutor. Actor central del negocio: supervisa en todo momento el rendimiento académico de sus hijos vinculados (notas, asistencia, horario) y recibe alertas. |

---

## 2. Requisitos Funcionales

Cada caso de uso referencia su requisito funcional asociado (`Ref. ID RS`). La numeración es 1:1: `CU-XX` ↔ `RF-XX(X)`.

| RF | Requisito Funcional | CU |
| :--- | :--- | :--- |
| RF-001 | El sistema debe permitir gestionar cuentas de usuario: creación con email único y rol existente en BD, actualización parcial y desactivación lógica (estado `INACTIVE`). | CU-01 |
| RF-002 | El sistema debe permitir registrar estudiantes creando en cascada su cuenta (`User`), datos personales (`Person`) y asignación a un grado válido. | CU-02 |
| RF-003 | El sistema debe permitir registrar docentes con especialidad. | CU-03 |
| RF-004 | El sistema debe permitir registrar padres/tutores y vincularlos con uno o más estudiantes (relación M–N vía `student_parents`). | CU-04 |
| RF-005 | El sistema debe permitir gestionar grados escolares con nombre y cupo máximo. | CU-05 |
| RF-006 | El sistema debe permitir gestionar materias/asignaturas. | CU-06 |
| RF-007 | El sistema debe permitir crear cursos asociando un docente, una materia, un grado y un periodo académico. | CU-07 |
| RF-008 | El sistema debe permitir definir horarios por curso (día de la semana, hora inicio, hora fin). | CU-08 |
| RF-009 | El sistema debe permitir al docente registrar calificaciones por estudiante y curso, con fecha de registro automática. | CU-09 |
| RF-010 | El sistema debe permitir modificar o anular calificaciones registradas. | CU-10 |
| RF-011 | El sistema debe permitir al docente registrar asistencia diaria por curso y estudiante (PRESENT / ABSENT / EXCUSED / TARDY). | CU-11 |
| RF-012 | El sistema debe permitir editar registros de asistencia posteriores para justificar ausencias. | CU-12 |
| RF-013 | El sistema debe permitir al docente consultar sus cursos, los estudiantes inscritos y su horario propio. | CU-13 |
| RF-014 | El sistema debe permitir al padre consultar las notas de cada hijo vinculado, filtrando por curso y periodo. | CU-14 |
| RF-015 | El sistema debe permitir al padre consultar el historial de asistencia de cada hijo y su porcentaje de inasistencia. | CU-15 |
| RF-016 | El sistema debe permitir al padre consultar el horario de clases de cada hijo. | CU-16 |
| RF-017 | El sistema debe generar alertas al padre ante inasistencias reiteradas, calificaciones bajas y comunicados institucionales. | CU-17 |
| RF-018 | El sistema debe permitir al padre consultar los datos de contacto de los docentes de sus hijos. | CU-18 |
| RF-019 | El sistema debe permitir al estudiante consultar sus propias notas, asistencia y horario. | CU-19 |
| RF-020 | El sistema debe permitir al estudiante consultar su horario de clases. | CU-20 |
| RF-021 | El sistema debe autenticar usuarios (login/logout) y autorizar el acceso según rol (ADMIN / TEACHER / STUDENT / PARENT). | CU-21 |
| RF-022 | El sistema debe permitir cambiar la contraseña validando la contraseña actual y exigiendo que la nueva sea distinta. | CU-22 |

---

## 3. Índice de casos de uso

| ID | Caso de uso | Iniciador | Estado |
| :--- | :--- | :--- | :--- |
| CU-01 | Gestionar cuentas de usuario | ADMIN | ✅ `UserService`, `UserController` |
| CU-02 | Registrar estudiante | ADMIN | ✅ `StudentService`, `StudentController` |
| CU-03 | Registrar docente | ADMIN | ✅ `TeacherService`, `TeacherController` |
| CU-04 | Registrar padre/tutor y vincular hijos | ADMIN | ✅ `ParentService`, `ParentController` |
| CU-05 | Gestionar grados | ADMIN | ✅ `GradeService`, `GradeController` |
| CU-06 | Gestionar materias | ADMIN | ✅ `SubjectService`, `SubjectController` |
| CU-07 | Crear cursos | ADMIN | ✅ `CourseService`, `CourseController` |
| CU-08 | Definir horarios de cursos | ADMIN | ✅ `ScheduleService`, `ScheduleController` |
| CU-09 | Registrar calificación | TEACHER | ✅ `MarkService`, `MarkController` |
| CU-10 | Modificar/anular calificación | TEACHER | ✅ `MarkService`, `MarkController` |
| CU-11 | Tomar asistencia | TEACHER | ✅ `AttendanceService`, `AttendanceController` |
| CU-12 | Justificar ausencias | TEACHER | ✅ `AttendanceService`, `AttendanceController` |
| CU-13 | Consultar cursos/estudiantes/horario propios | TEACHER | ✅ `TeacherAcademicService`, `TeacherController` |
| CU-14 | Consultar notas de los hijos | PARENT | ✅ `ParentAcademicService`, `ParentController` |
| CU-15 | Consultar asistencia de los hijos | PARENT | ✅ `ParentAcademicService`, `ParentController` |
| CU-16 | Consultar horario de los hijos | PARENT | ✅ `ParentAcademicService`, `ParentController` |
| CU-17 | Recibir alertas | PARENT | ⚪ Pendiente (Fase 4) |
| CU-18 | Consultar contacto de docentes | PARENT | ⚪ Opcional/futuro |
| CU-19 | Consultar propias notas y asistencia | STUDENT | ✅ `StudentAcademicService`, `StudentController` |
| CU-20 | Consultar horario propio | STUDENT | ✅ `StudentAcademicService`, `StudentController` |
| CU-21 | Iniciar sesión / cerrar sesión | Todos | ⚪ Sin seguridad aún (Fase 2) |
| CU-22 | Cambiar contraseña | Todos (autenticados) | ✅ `UserService.changePassword`, `UserController` |

**Precondiciones transversales**: los roles deben existir previamente en la tabla `Roles`; todos los borrados son lógicos (cambio de estado a `INACTIVE`, nunca eliminación física).

---

## 4. Especificaciones detalladas (casos de uso prioritarios)

### CU-01 — Gestionar cuentas de usuario

| Campo | Detalle |
| :--- | :--- |
| **ID del caso de uso** | `CU-01` |
| **Ref. ID RS** | `RF-001` |
| **Nombre del caso de uso** | Gestionar cuentas de usuario |
| **Iniciador** | ADMIN |
| **Otros actores** | — |
| **Precondición** | Los roles (ADMIN, TEACHER, STUDENT, PARENT) deben estar configurados en la tabla `Roles`. Para actualizar/desactivar: la cuenta objetivo debe existir. |

#### Flujo básico (creación)

| Actor | Sistema |
| :--- | :--- |
| 1. El administrador solicita crear una cuenta e ingresa username, email, contraseña y rol | |
| | 2. Valida que el email no esté registrado (`existsByEmail`) |
| | 3. Resuelve el rol indicado contra la base de datos (`findByRol`) |
| | 4. Crea la cuenta con estado inicial `ACTIVE` y confirma la operación |

#### Postcondiciones y Excepciones

| Campo | Detalle |
| :--- | :--- |
| **Camino alternativo 1** | Email ya registrado → excepción "Email already exists"; no se crea la cuenta. |
| **Camino alternativo 2** | Rol inválido o no configurado → "Invalid role provided: X" / "Role not found in database". |
| **Camino alternativo 3** | Actualización parcial: solo se modifican los campos no nulos recibidos (email, rol, estado). |
| **Camino alternativo 4** | Desactivación (soft delete): no se elimina el registro; el estado cambia a `INACTIVE`. Si la cuenta no existe → "User Not Found". |
| **Puntos de extensión** | CU-02 (la creación de un estudiante genera su cuenta automáticamente); CU-21 (autenticación futura sobre estas cuentas). |
| **Postcondición** | Cuenta persistida con el rol y estado solicitados; las respuestas nunca exponen la contraseña. |

---

### CU-02 — Registrar estudiante

| Campo | Detalle |
| :--- | :--- |
| **ID del caso de uso** | `CU-02` |
| **Ref. ID RS** | `RF-002` |
| **Nombre del caso de uso** | Registrar estudiante |
| **Iniciador** | ADMIN |
| **Otros actores** | — |
| **Precondición** | Rol STUDENT configurado en la tabla `Roles`; el grado (`gradeId`) indicado debe existir; el email no debe estar registrado. |

#### Flujo básico

| Actor | Sistema |
| :--- | :--- |
| 1. El administrador ingresa datos personales (nombres, apellido, DNI, fecha de nacimiento, teléfono, dirección), credenciales (username, email, contraseña) y el grado asignado | |
| | 2. Valida que el email no esté registrado |
| | 3. Busca el rol STUDENT en la base de datos |
| | 4. Crea el `User` (rol STUDENT, estado `ACTIVE`) y la `Person` vinculada |
| | 5. Verifica la existencia del grado indicado |
| | 6. Persiste el estudiante (con cascada hacia User/Person) y devuelve el DTO sin datos sensibles |

#### Postcondiciones y Excepciones

| Campo | Detalle |
| :--- | :--- |
| **Camino alternativo 1** | Email duplicado → "Email already exist"; no se registra el estudiante. |
| **Camino alternativo 2** | Rol STUDENT ausente en BD → "STUDENT role is not configured in the database". |
| **Camino alternativo 3** | Grado inexistente → "Grade not found with ID: X". |
| **Camino alternativo 4** | Actualización posterior (parcial): solo campos no nulos del DTO; permite cambio de grado validando su existencia. |
| **Camino alternativo 5** | Eliminación (soft delete): se desactiva la cuenta `User` asociada, conservando el historial académico. |
| **Puntos de extensión** | CU-04 (vinculación posterior del estudiante con padres/tutores); CU-09/CU-11 (el estudiante registrado pasa a ser elegible para notas y asistencia). |
| **Postcondición** | Estudiante activo con su cuenta, persona y grado persistidos; consultas devuelven únicamente datos no sensibles (nunca la contraseña). |

---

### CU-04 — Registrar padre/tutor y vincular hijos

> 🔵 Flujo propuesto — módulo no implementado aún.

| Campo | Detalle |
| :--- | :--- |
| **ID del caso de uso** | `CU-04` |
| **Ref. ID RS** | `RF-004` |
| **Nombre del caso de uso** | Registrar padre/tutor y vincular hijos |
| **Iniciador** | ADMIN |
| **Otros actores** | PARENT (recibe las credenciales de acceso) |
| **Precondición** | Rol PARENT configurado en la tabla `Roles`; los estudiantes a vincular deben estar registrados (CU-02); email del tutor no registrado. |

#### Flujo básico

| Actor | Sistema |
| :--- | :--- |
| 1. El administrador ingresa los datos personales y credenciales del padre/tutor | |
| | 2. Valida email único y crea la `Person` con su `User` (rol PARENT, estado `ACTIVE`) |
| 3. El administrador selecciona uno o más estudiantes hijos del tutor | |
| | 4. Verifica que los estudiantes existan |
| | 5. Registra las relaciones en la tabla intermedia `student_parents` (M–N) y confirma |

#### Postcondiciones y Excepciones

| Campo | Detalle |
| :--- | :--- |
| **Camino alternativo 1** | Email duplicado → error de unicidad; no se crea el tutor. |
| **Camino alternativo 2** | Estudiante inexistente al vincular → error y no se registra esa relación. |
| **Camino alternativo 3** | Desvinculación posterior: se elimina solo la relación padre–hijo, no los registros. |
| **Puntos de extensión** | CU-14/CU-15/CU-16 dependen de esta vinculación: el padre solo podrá consultar hijos con relación vigente en `student_parents`. |
| **Postcondición** | Tutor persistido y vinculado a sus hijos; base habilitada para el portal de consulta parental. |

---

### CU-09 — Registrar calificación

> 🔵 Flujo propuesto — modelo `Mark` definido (fecha automática vía `@PrePersist`), lógica pendiente.

| Campo | Detalle |
| :--- | :--- |
| **ID del caso de uso** | `CU-09` |
| **Ref. ID RS** | `RF-009` |
| **Nombre del caso de uso** | Registrar calificación |
| **Iniciador** | TEACHER |
| **Otros actores** | — |
| **Precondición** | Docente autenticado (CU-21); curso impartido por el docente; estudiante inscrito en el curso. |

#### Flujo básico

| Actor | Sistema |
| :--- | :--- |
| 1. El docente selecciona uno de sus cursos | |
| | 2. Lista los estudiantes inscriptos en ese curso |
| 3. El docente selecciona un estudiante e ingresa el valor de la nota | |
| | 4. Valida formato/rango según la escala definida |
| | 5. Persiste la `Mark` asociando estudiante y curso; asigna `assignedAt` automáticamente |
| | 6. Confirma el registro |

#### Postcondiciones y Excepciones

| Campo | Detalle |
| :--- | :--- |
| **Camino alternativo 1** | Estudiante no inscripto en el curso → rechazo del registro. |
| **Camino alternativo 2** | Nota fuera de rango o formato inválido → error de validación (escala por definir). |
| **Camino alternativo 3** | Docente intenta registrar en un curso que no imparte → denegación por autorización (futuro, requiere CU-21). |
| **Puntos de extensión** | CU-10 (modificar/anular nota); CU-14 (visible para el padre); CU-17 (alerta por nota baja, futuro). |
| **Postcondición** | Calificación persistida con fecha automática y disponible para las consultas de padres y estudiantes. |

---

### CU-11 — Tomar asistencia

> 🔵 Flujo propuesto — modelo `Attendance` definido (4 estados), lógica pendiente.

| Campo | Detalle |
| :--- | :--- |
| **ID del caso de uso** | `CU-11` |
| **Ref. ID RS** | `RF-011` |
| **Nombre del caso de uso** | Tomar asistencia |
| **Iniciador** | TEACHER |
| **Otros actores** | — |
| **Precondición** | Docente autenticado (CU-21); curso con horario vigente; estudiantes inscriptos. |

#### Flujo básico

| Actor | Sistema |
| :--- | :--- |
| 1. El docente abre su curso en la fecha de clase | |
| | 2. Muestra la lista de estudiantes inscriptos |
| 3. El docente marca el estado de cada estudiante (PRESENT / ABSENT / EXCUSED / TARDY) | |
| | 4. Persiste los registros de `Attendance` con la fecha actual |
| | 5. Confirma el pase de lista |

#### Postcondiciones y Excepciones

| Campo | Detalle |
| :--- | :--- |
| **Camino alternativo 1** | Estudiante sin marca explícita → definir valor por defecto (regla pendiente). |
| **Camino alternativo 2** | Registro duplicado para misma fecha/curso → actualizar en lugar de duplicar (regla pendiente de implementar). |
| **Puntos de extensión** | CU-12 (justificar ausencias después del registro); CU-15 (% de asistencia para padres); CU-17 (alerta por inasistencias reiteradas). |
| **Postcondición** | Registro diario de asistencia disponible como historial consultable por los tutores. |

---

### CU-14 — Consultar notas de los hijos

> ⚪ Flujo propuesto — módulo no implementado. Caso de uso central de la propuesta de valor para colegios rurales.

| Campo | Detalle |
| :--- | :--- |
| **ID del caso de uso** | `CU-14` |
| **Ref. ID RS** | `RF-014` |
| **Nombre del caso de uso** | Consultar notas de los hijos |
| **Iniciador** | PARENT |
| **Otros actores** | — |
| **Precondición** | Padre autenticado (CU-21) y vinculado a al menos un estudiante (CU-04); existen calificaciones registradas (CU-09). |

#### Flujo básico

| Actor | Sistema |
| :--- | :--- |
| 1. El padre accede a la sección de rendimiento académico | |
| | 2. Lista los hijos vinculados según `student_parents` |
| 3. El padre selecciona un hijo y filtra por periodo/curso | |
| | 4. Verifica el vínculo padre–estudiante antes de entregar datos |
| | 5. Recupera las `Marks` del estudiante filtradas por curso/periodo |
| | 6. Muestra las notas con su curso y fecha de registro |

#### Postcondiciones y Excepciones

| Campo | Detalle |
| :--- | :--- |
| **Camino alternativo 1** | Padre sin hijos vinculados → mensaje informativo sin listado. |
| **Camino alternativo 2** | Hijo sin calificaciones registradas → vista vacía informativa. |
| **Camino alternativo 3** | Intento de acceso a un estudiante sin vínculo → denegación (protección de datos menores). |
| **Puntos de extensión** | CU-15/CU-16 (misma navegación por hijo); exportación/PDF de boletín (futuro); promedios por curso (futuro). |
| **Postcondición** | Información mostrada en modo lectura; ningún dato modificado. |

---

### CU-15 — Consultar asistencia de los hijos

> ⚪ Flujo propuesto — módulo no implementado.

| Campo | Detalle |
| :--- | :--- |
| **ID del caso de uso** | `CU-15` |
| **Ref. ID RS** | `RF-015` |
| **Nombre del caso de uso** | Consultar asistencia de los hijos |
| **Iniciador** | PARENT |
| **Otros actores** | — |
| **Precondición** | Padre autenticado (CU-21) y vinculado a al menos un estudiante (CU-04); existen registros de asistencia (CU-11). |

#### Flujo básico

| Actor | Sistema |
| :--- | :--- |
| 1. El padre accede a la sección de asistencia | |
| | 2. Lista los hijos vinculados según `student_parents` |
| 3. El padre selecciona un hijo y un rango de fechas/periodo | |
| | 4. Verifica el vínculo padre–estudiante |
| | 5. Calcula y recupera el historial de asistencias con su resumen (% presente, ausente, excusado, tardío) |
| | 6. Muestra el detalle por fecha, curso y estado |

#### Postcondiciones y Excepciones

| Campo | Detalle |
| :--- | :--- |
| **Camino alternativo 1** | Sin registros de asistencia en el rango → vista vacía informativa. |
| **Camino alternativo 2** | Definir si las ausencias EXCUSED cuentan o se excluyen del % de inasistencia (regla de negocio pendiente). |
| **Puntos de extensión** | CU-17 (alerta cuando el % de inasistencia supere un umbral); descarga de reporte (futuro). |
| **Postcondición** | Historial y métricas mostrados en modo lectura; ningún dato modificado. |

---

### CU-21 — Iniciar sesión / cerrar sesión

> ⚪ Flujo propuesto — actualmente el proyecto no incluye Spring Security ni JWT (fase 2 del roadmap).

| Campo | Detalle |
| :--- | :--- |
| **ID del caso de uso** | `CU-21` |
| **Ref. ID RS** | `RF-021` |
| **Nombre del caso de uso** | Iniciar sesión / cerrar sesión |
| **Iniciador** | Cualquier actor con cuenta (ADMIN, TEACHER, STUDENT, PARENT) |
| **Otros actores** | — |
| **Precondición** | Cuenta existente en la tabla `Users` con estado `ACTIVE`; mecanismo de seguridad implementado (pendiente). |

#### Flujo básico

| Actor | Sistema |
| :--- | :--- |
| 1. El usuario ingresa email/username y contraseña | |
| | 2. Verifica las credenciales y que el estado sea `ACTIVE` |
| | 3. Emite un token de sesión (JWT previsto) que incluye el rol del usuario |
| 4. El usuario navega las funciones permitidas según su rol | |
| 5. El usuario cierra sesión | |
| | 6. Invalida el token y finaliza la sesión |

#### Postcondiciones y Excepciones

| Campo | Detalle |
| :--- | :--- |
| **Camino alternativo 1** | Credenciales inválidas → error genérico (sin revelar si existe el usuario). |
| **Camino alternativo 2** | Cuenta con estado `INACTIVE` (borrado lógico) → acceso rechazado. |
| **Camino alternativo 3** | Token expirado durante la navegación → re-autenticación requerida. |
| **Puntos de extensión** | CU-22 (cambio de contraseña); recuperación de contraseña (futuro); autorización por rol en todos los demás casos de uso. |
| **Postcondición** | Sesión activa con identidad y rol verificados, o sesión cerrada sin dejar token válido. |

---

### CU-22 — Cambiar contraseña

| Campo | Detalle |
| :--- | :--- |
| **ID del caso de uso** | `CU-22` |
| **Ref. ID RS** | `RF-022` |
| **Nombre del caso de uso** | Cambiar contraseña |
| **Iniciador** | Usuario autenticado (cualquier rol) |
| **Otros actores** | — |
| **Precondición** | La cuenta objetivo debe existir en la tabla `Users`. |

#### Flujo básico

| Actor | Sistema |
| :--- | :--- |
| 1. El usuario indica su cuenta, la contraseña actual y la nueva contraseña | |
| | 2. Localiza el usuario por identificador |
| | 3. Compara la contraseña actual con la almacenada |
| | 4. Verifica que la nueva contraseña sea distinta de la actual |
| | 5. Persiste la nueva contraseña y confirma el cambio |

#### Postcondiciones y Excepciones

| Campo | Detalle |
| :--- | :--- |
| **Camino alternativo 1** | Contraseña actual incorrecta → "Incorrect current password"; no hay cambio. |
| **Camino alternativo 2** | Nueva contraseña igual a la actual → "New password must be different."; no hay cambio. |
| **Camino alternativo 3** | Usuario inexistente → "User Not Found". |
| **Puntos de extensión** | CU-21 (tras el cambio, la próxima autenticación usa la nueva contraseña). Con la fase de seguridad se migrará la comparación/almacenamiento a hash BCrypt (hoy es texto plano). |
| **Postcondición** | Contraseña actualizada en la base de datos; la anterior queda invalidada. |

---

*Documento generado para el proyecto EduManage — última actualización: agosto de 2026.*
