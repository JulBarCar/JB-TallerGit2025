# Sistema de Gestión de Empleados - Versión Final

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)
![JPA/Hibernate](https://img.shields.io/badge/JPA/Hibernate-6.2-orange)
![Lombok](https://img.shields.io/badge/Lombok-1.18.30-yellow)
![SLF4J](https://img.shields.io/badge/SLF4J-2.0.9-lightgrey)

---

## Descripción General

Este proyecto es una **aplicación Spring Boot RESTful** que implementa un sistema de gestión de empleados con **herencia JPA**, **polimorfismo**, **interfaces funcionales**, **validaciones**, **manejo de excepciones** y **batch processing**. Está diseñado para demostrar conceptos avanzados de Programación Orientada a Objetos en un contexto real, incluyendo manejo de excepciones personalizadas, utilitarios para nómina, mappers y una jerarquía de controladores.

---

## Arquitectura del Proyecto

```
src/main/java/py/edu/uc/lp32025/
├── domain/          → Entidades JPA y lógica de negocio
├── dto/             → Objetos de transferencia (respuestas API)
├── controler/       → Controladores REST (¡atención: typo en nombre!)
├── service/         → Lógica de negocio y repositorios
├── repository/      → Interfaces JPA
├── exception/       → Excepciones personalizadas
├── interfaces/      → Interfaces funcionales
├── mapper/          → Mappers para entidad-DTO y datos externos
├── demo/            → Demo de permisos y vacaciones
└── utils/           → Utilidades (NominaUtils, MapeableUtils)
```

---

## Funcionalidades Actuales

| Funcionalidad | Estado | Detalle |
|--------------|--------|-------|
| CRUD por tipo de empleado | Completed | Tiempo completo, por hora, contratista, gerente |
| Cálculo de salarios (polimorfismo) | Completed | Cada tipo tiene su fórmula |
| Impuestos (10% - deducciones) | Completed | Método común en `Persona` |
| Validaciones específicas | Completed | `validarDatosEspecificos()` por tipo |
| Vacaciones y permisos (`Permisionable`) | Completed | Según normativa paraguaya, con método exclusivo para gerentes |
| Batch con chunks de 100 | Completed | Evita `OutOfMemory` |
| Reporte de nómina total | Completed | En `PersonaService.calcularNominaTotal()` |
| Reporte completo | Completed | En `PersonaService.generarReporteCompleto()` |
| Búsqueda por nombre | Completed | En `PersonaService.buscarPorNombre()` |
| GPS + Avatar (`Mapeable`) | Completed | `Persona`, `Vehiculo`, `Edificio` |
| Utilitario `NominaUtils` | Completed | Cálculo de días y reporte JSON |
| Excepciones personalizadas | Completed | Checked (`DiasInsuficientesException`), Runtime (`EmpleadoNoEncontradoException`) |
| Jerarquía de controladores | Completed | Base + específicos para empleados y gerentes |
| Jerarquía de mappers | Completed | Base + complejos para integración externa |
| Integración con nómina externa | Completed | Simulada en `NominaExternaService` |
| Global Exception Handler | Completed | Con `@ControllerAdvice` y `ErrorResponseDTO` |

---

## Jerarquía de Clases (Dominio)

```
Persona (Abstract, Mapeable)
└── Empleado (Abstract, Permisionable)
    ├── EmpleadoTiempoCompleto
    │   └── Gerente
    ├── EmpleadoPorHora
    └── Contratista
```

- **Herencia JPA**: `InheritanceType.JOINED`
- **Polimorfismo**: `calcularSalario()`, `calcularDeducciones()`, `calcularImpuestos()`
- **Validación**: `validarDatosEspecificos()` por tipo

---

## Cálculo de Salarios

| Tipo | Fórmula |
|------|--------|
| **Tiempo Completo** | `salarioMensual + bonoAnual` |
| **Por Hora** | `(tarifa × horas) + (50% extra si >40h)` |
| **Contratista** | `montoPorProyecto × proyectosCompletados` |
| **Gerente** | Igual a Tiempo Completo, con deducción especial (3%) |

---

## Vacaciones y Permisos (`Permisionable`)

```java
int getDiasVacacionesDisponibles()
```
| Antigüedad | Días |
|-----------|------|
| 0 años | 0 |
| 1-4 años | 12 |
| 5-9 años | 18 |
| ≥10 años | 30 |

```java
boolean solicitarPermiso(LocalDate fecha, String motivo)
```
| Motivo | Días permitidos |
|--------|-----------------|
| MATRIMONIO | 3 |
| NACIMIENTO_HIJO | 2 |
| FALLECIMIENTO_FAMILIAR | 2 |

- **Exclusivo Gerentes**: `aprobarPermisoDeSubordinado(...)` – lanza `UnsupportedOperationException` en otros tipos.
- **Regla >20 días**: Solo gerentes; lanza `DiasInsuficientesException` si no se cumple.

---

## Endpoints REST

### Inicio
```
GET /
→ Redirige a /HolaMundo
GET /HolaMundo?name=Juan
→ {"status":200,"message":"¡Hola, Juan!"}
```

### Personas (Global)
```
GET    /api/personas
GET    /api/personas/{id}
POST   /api/personas
PUT    /api/personas/{id}
DELETE /api/personas/{id}

GET /api/personas/reporte/{id}
→ ReporteEmpleadoDto con polimorfismo

GET /api/personas?nombre=Carlos
→ Filtra por nombre (case-insensitive)
```

### Empleados por Tipo

#### Tiempo Completo
```
GET    /api/empleados-tiempo-completo
GET    /api/empleados-tiempo-completo/{id}
GET    /api/empleados-tiempo-completo/documento/{doc}
GET    /api/empleados-tiempo-completo/{id}/impuestos
POST   /api/empleados-tiempo-completo
POST   /api/empleados-tiempo-completo/batch
PUT    /api/empleados-tiempo-completo/{id}
DELETE /api/empleados-tiempo-completo/{id}
```

#### Por Hora
```
GET    /api/empleados-por-hora
GET    /api/empleados-por-hora/{id}
GET    /api/empleados-por-hora/horas/{horas}
GET    /api/empleados-por-hora/{id}/impuestos
POST   /api/empleados-por-hora
POST   /api/empleados-por-hora/batch
PUT    /api/empleados-por-hora/{id}
DELETE /api/empleados-por-hora/{id}
```

#### Contratistas
```
GET    /api/contratistas
GET    /api/contratistas/{id}
GET    /api/contratistas/contrato-vigente
GET    /api/contratistas/{id}/impuestos
POST   /api/contratistas
POST   /api/contratistas/batch
PUT    /api/contratistas/{id}
DELETE /api/contratistas/{id}
```

#### Gerentes
```
GET    /api/gerentes
GET    /api/gerentes/{id}
POST   /api/gerentes
POST   /api/gerentes/batch
PUT    /api/gerentes/{id}
DELETE /api/gerentes/{id}
```

### Permisos y Vacaciones
```
POST /api/empleados/{id}/vacaciones/solicitar
→ Body: {"fechaInicio":"2025-12-01","fechaFin":"2025-12-10"}

POST /api/empleados/{id}/permisos/solicitar
→ Body: {"fecha":"2025-12-15","motivo":"MATRIMONIO"}

PUT  /api/gerentes/{gerenteId}/aprobar-permiso/{empleadoId}?aprobar=true
→ Body: {"fecha":"2025-12-15","motivo":"MATRIMONIO"}
```

### Nómina
```
GET /api/nomina/empleado/{id}?periodo=2025-11-01
→ Integra datos externos con días de vacaciones/permisos

GET /api/nomina/gerente/{id}?periodo=2025-11-01
→ Similar, para gerentes
```

---

## Manejo de Errores

- `GlobalExceptionHandler` captura:
  - `IllegalArgumentException` → 400
  - `EntityNotFoundException` → 404
  - `PermisoDenegadoException` → 403
  - `DiasInsuficientesException` → 400
  - `EmpleadoNoEncontradoException` → 404
- Respuesta: `ErrorResponseDTO`

```json
{
  "status": 400,
  "error": "Invalid input",
  "userError": "El nombre no puede estar vacío.",
  "timestamp": "2025-11-14T12:00:00",
  "path": "/api/empleados"
}
```

---

---

## Batch Processing

- `guardarEmpleadosEnBatch(List<T>)`
- Valida **todos** antes de guardar
- Procesa en **chunks de 100**
- Usa `saveAll()` + `flush()`

---

## Mapeable (GPS + Avatar)

```java
interface Mapeable {
    PosicionGPS ubicarElemento();
    Avatar obtenerAvatar();
}
```

Implementado en:
- `Persona`
- `Vehiculo`
- `Edificio`

Demo: `MapeableFactory` y `MapeableUtils` para crear y mostrar elementos.

---

## Dependencias (pom.xml)

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
</dependencies>
```

---

## Ejecución

```bash
./mvnw spring-boot:run
```

Accede a:
```
http://localhost:8080/HolaMundo
```

H2 Console:
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
```

### Pruebas con Insomnia/CURL
- Importa la colección desde `docs/insomnia-collection.json` (incluida en el repo).
- Ejemplos CURL:
  - Crear empleado: `curl -X POST http://localhost:8080/api/empleados-tiempo-completo -H "Content-Type: application/json" -d '{"nombre":"Juan","apellido":"Perez","numeroDocumento":"123456"}'`
  - Solicitar vacaciones: `curl -X POST http://localhost:8080/api/empleados/1/vacaciones/solicitar -H "Content-Type: application/json" -d '{"fechaInicio":"2025-12-01","fechaFin":"2025-12-10"}'`
- Para excepciones: Intenta >20 días sin ser gerente → Espera 400/403.
- Carga datos: Ejecuta demos con `mvn spring-boot:run` y revisa logs.

---

## Instrucciones para Probar
1. Clona el repo: `git clone <url>`
2. Ejecuta: `./mvnw spring-boot:run`
3. Usa Insomnia/CURL para endpoints.
4. Prueba excepciones: Solicitudes inválidas (e.g., documento duplicado).
5. Reportes: GET `/api/personas/reporte` para ver polimorfismo en acción.
