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
├── dto/             → DTOs (incluye ReporteEmpleadoDto con ID)
├── controler/       → Controladores REST
├── service/         → Lógica de negocio
├── repository/      → Repositorios JPA
├── exception/       → Excepciones personalizadas
├── interfaces/      → Permisionable, Mapeable
├── mapper/          → Mappers con herencia y polimorfismo
├── utils/           → Utilidades (NominaUtils, MapeableUtils)
└── RestDemoApplication.java → Clase principal (¡ejecutar desde aquí!)
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

---

## Ejecución

```bash
./mvnw spring-boot:run
```

**Clase principal de la aplicación:**
```java
@SpringBootApplication
public class RestDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestDemoApplication.class, args);
    }
}
```

> **Importante:** El proyecto se ejecuta desde `RestDemoApplication.java`

Accede a:
```
http://localhost:8080/HolaMundo
```

H2 Console:
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
```

### Colección de pruebas incluida

El proyecto incluye un archivo de **Insomnia** con toda la colección de peticiones lista para importar:

```
pruebas_JulianBareiro.yaml
```

→ Solo abre Insomnia → Import → selecciona el archivo → y tendrás **todas las peticiones organizadas** (CRUD, batch, permisos, reportes, errores, etc.)

---
##cUrls
# --- Crear Empleado Tiempo Completo ---
curl -X POST http://localhost:8080/api/empleados-tiempo-completo \
-H "Content-Type: application/json" \
-d '{
"nombre": "Ana",
"apellido": "Gómez",
"numeroDocumento": "1234567",
"fechaNacimiento": "1985-03-15",
"fechaIngreso": "2020-01-10",
"salarioMensual": 5000000,
"bonoAnual": 800000,
"departamento": "Ventas"
}'

# --- Crear Gerente ---
curl -X POST http://localhost:8080/api/gerentes \
-H "Content-Type: application/json" \
-d '{
"nombre": "Carlos",
"apellido": "López",
"numeroDocumento": "9876543",
"fechaNacimiento": "1978-06-20",
"fechaIngreso": "2015-05-01",
"salarioMensual": 12000000,
"bonoAnual": 3000000,
"departamento": "Dirección",
"nivelJerarquico": 2,
"departamentoGestionado": "Tecnología"
}'

# --- Crear Contratista ---
curl -X POST http://localhost:8080/api/contratistas \
-H "Content-Type: application/json" \
-d '{
"nombre": "María",
"apellido": "Rojas",
"numeroDocumento": "5555555",
"fechaNacimiento": "1990-09-10",
"fechaIngreso": "2024-03-01",
"montoPorProyecto": 2000000,
"proyectosCompletados": 8,
"fechaFinContrato": "2025-12-31",
"departamento": "Consultoría"
}'

# --- Crear Empleado por Hora ---
curl -X POST http://localhost:8080/api/empleados-por-hora \
-H "Content-Type: application/json" \
-d '{
"nombre": "Pedro",
"apellido": "Martínez",
"numeroDocumento": "4444444",
"fechaNacimiento": "1995-11-05",
"fechaIngreso": "2023-07-15",
"tarifaPorHora": 85000,
"horasTrabajadas": 180,
"departamento": "Soporte"
}'

# =============================================
# 4. CONSULTAS ESPECÍFICAS
# =============================================

# Impuestos de un contratista (ID = 3, cambiar según tu BD)
curl http://localhost:8080/api/contratistas/3/impuestos

# Impuestos de un empleado por hora
curl http://localhost:8080/api/empleados-por-hora/5/impuestos

# Días de vacaciones disponibles (cualquier tipo de empleado)
curl http://localhost:8080/api/empleados/1/vacaciones/disponibles

# Reporte individual con DTO bonito
curl http://localhost:8080/api/personas/reporte/1

# =============================================
# 5. SOLICITUD DE VACACIONES Y PERMISOS
# =============================================

# Solicitar vacaciones (empleado ID=1)
curl -X POST http://localhost:8080/api/permisos/empleados/1/vacaciones/solicitar \
-H "Content-Type: application/json" \
-d '{
"fechaInicio": "2025-12-20",
"fechaFin": "2025-12-30"
}'

# Solicitar permiso por matrimonio (3 días)
curl -X POST http://localhost:8080/api/permisos/empleados/1/permisos/solicitar \
-H "Content-Type: application/json" \
-d '{
"fecha": "2025-08-15",
"motivo": "MATRIMONIO"
}'

# Gerente aprueba/rechaza permiso de subordinado
curl -X PUT "http://localhost:8080/api/gerentes/2/aprobar-permiso/5?aprobar=true" \
-H "Content-Type: application/json" \
-d '{
"fecha": "2025-09-10",
"motivo": "NACIMIENTO_HIJO"
}'

# =============================================
# 6. LISTADOS
# =============================================

curl http://localhost:8080/api/gerentes
curl http://localhost:8080/api/empleados-tiempo-completo
curl http://localhost:8080/api/empleados-por-hora
curl http://localhost:8080/api/contratistas
curl http://localhost:8080/api/contratistas/contrato-vigente

# =============================================
# 7. PRUEBA RÁPIDA DE POLIMORFISMO (lo que más te gusta mostrar)
# =============================================

# Este endpoint es una joyita: llama a calcularSalario() y calcularImpuestos()
# en cada subclase sin saber cuál es → polimorfismo puro
curl http://localhost:8080/api/reportes/empleados/completo | jq

```markdown
## Instrucciones para Probar
1. Clona el repo: `git clone <url>`
2. Abre el proyecto en tu IDE (IntelliJ, Eclipse, VS Code, etc.)
3. **Ejecuta la clase principal RestDemoApplication**

o desde terminal:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.main-class=py.edu.uc.lp32025.RestDemoApplication
   ```
4. Una vez levantada la app (puerto 8080), abre **Insomnia** e importa:
   ```
   pruebas_JulianBareiro.yaml
   ```
   → Tendrás todas las peticiones listas (CRUD, batch, vacaciones, impuestos, errores, etc.)
5. O usa directamente los `curl` de este README
6. Prueba excepciones intencionalmente:
    - Vacaciones >20 días siendo no gerente → 400
    - Permiso con motivo inválido → 400
    - Horas trabajadas >300 en batch → 400
7. Reportes clave para demostrar polimorfismo:
   ```bash
   GET http://localhost:8080/api/reportes/empleados/completo
   GET http://localhost:8080/api/personas/reporte/{id}
   ```
```
