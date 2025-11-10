```markdown
# Sistema de Gestión de Empleados - Estado intemedio entre entregas

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)
![JPA/Hibernate](https://img.shields.io/badge/JPA/Hibernate-6.2-orange)
![Lombok](https://img.shields.io/badge/Lombok-1.18.30-yellow)
![SLF4J](https://img.shields.io/badge/SLF4J-2.0.9-lightgrey)

---

## Descripción General

Este proyecto es una **aplicación Spring Boot RESTful** que implementa un sistema de gestión de empleados con **herencia JPA**, **polimorfismo**, **interfaces funcionales**, **validaciones**, **manejo de excepciones** y **batch processing**. Está diseñado para demostrar conceptos avanzados de Programación Orientada a Objetos en un contexto real.

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
├── demo/            → Demo de permisos y vacaciones
└── utils/           → (Pendiente: NominaUtils)
```

---

## Funcionalidades Actuales

| Funcionalidad | Estado | Detalle |
|--------------|--------|-------|
| CRUD por tipo de empleado | Completed | Tiempo completo, por hora, contratista |
| Cálculo de salarios (polimorfismo) | Completed | Cada tipo tiene su fórmula |
| Impuestos (10% - deducciones) | Completed | Método común en `Persona` |
| Validaciones específicas | Completed | `validarDatosEspecificos()` por tipo |
| Vacaciones y permisos | Completed | Según normativa paraguaya |
| Batch con chunks de 100 | Completed | Evita `OutOfMemory` |
| Reporte de nómina total | Completed | `/api/personas/nomina` |
| Reporte completo | Completed | `/api/personas/reporte` |
| Búsqueda por nombre | Completed | `/api/personas?nombre=...` |
| GPS + Avatar (`Mapeable`) | Completed | `Persona`, `Vehiculo`, `Edificio` |

---

## Jerarquía de Clases (Dominio)

```
Persona (Abstract, Mapeable)
└── Empleado (Abstract, Permisionable)
    ├── EmpleadoTiempoCompleto
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

GET /api/personas/nomina
→ {"EmpleadoTiempoCompleto": 15000000, ...}

GET /api/personas/reporte
→ Lista de ReporteEmpleadoDto

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

---

## Manejo de Errores

- `GlobalExceptionHandler` captura:
  - `IllegalArgumentException` → 400
  - `EntityNotFoundException` → 404
- Respuesta: `ErrorResponseDTO`

```json
{
  "status": 400,
  "error": "Invalid input",
  "userError": "El nombre no puede estar vacío."
}
```

---

## Demo de Permisos

Al iniciar la app:
```java
PermisoDemoApplication → CommandLineRunner
```
Muestra:
- Vacaciones aprobadas/denegadas
- Permisos por motivo
- Casos de error (excepciones)

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


```
