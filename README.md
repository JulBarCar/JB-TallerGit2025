# Sistema de Gestión de Empleados y Contratistas

Este proyecto es una aplicación Spring Boot para la gestión de personas (empleados a tiempo completo, empleados por hora y contratistas), con funcionalidades para calcular salarios, impuestos, reportes y operaciones CRUD. Utiliza herencia polimórfica para manejar diferentes tipos de empleados.

## Instrucciones de ejecución

1. **Requisitos previos:**
   - Java 17 o superior.
   - Maven 3.6+ (para compilar y ejecutar).
   - Base de datos: La aplicación usa Spring Data JPA. Por defecto, puedes usar H2 (en memoria) para pruebas. Para producción, configura PostgreSQL o MySQL en `application.properties`.
     Ejemplo de configuración en `src/main/resources/application.properties`:
     ```
     spring.datasource.url=jdbc:h2:mem:testdb
     spring.datasource.driverClassName=org.h2.Driver
     spring.datasource.username=sa
     spring.datasource.password=
     spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
     spring.h2.console.enabled=true
     ```

2. **Compilación y ejecución:**
   - Clona el repositorio: `git clone <url-repositorio>`.
   - Navega al directorio del proyecto: `cd lp32025`.
   - Compila el proyecto: `mvn clean install`.
   - Ejecuta la aplicación: `mvn spring-boot:run`.
   - Alternativa: Genera el JAR ejecutable con `mvn package` y ejecútalo con `java -jar target/lp32025-0.0.1-SNAPSHOT.jar`.
   - La aplicación se inicia en `http://localhost:8080`.

3. **Pruebas:**
   - Usa herramientas como Postman o cURL para probar los endpoints.
   - Accede a la consola H2 (si está habilitada): `http://localhost:8080/h2-console`.

## Descripción de la arquitectura implementada

La arquitectura sigue el patrón MVC (Model-View-Controller) de Spring Boot, con énfasis en principios OOP como herencia y polimorfismo. Se divide en capas:

- **Capa de Dominio (Model):**
  - Clase abstracta `Persona` como base para herencia (estrategia JOINED en JPA).
  - Subclases: `EmpleadoTiempoCompleto`, `EmpleadoPorHora`, `Contratista`.
  - Métodos polimórficos: `calcularSalario()`, `calcularImpuestos()`, `validarDatosEspecificos()`, `obtenerInformacionCompleta()`.
  - Validaciones específicas por tipo (ej: horas trabajadas para empleados por hora).

- **Capa de Persistencia (Repository):**
  - Interfaces JpaRepository para cada subclase: `EmpleadoTiempoCompletoRepository`, `EmpleadoPorHorasRepository`, `ContratistaRepository`, `PersonaRepository`.
  - Consultas personalizadas: por departamento, horas trabajadas, contratos vigentes, nombre (insensible a mayúsculas).

- **Capa de Negocio (Service):**
  - Servicios por tipo: `EmpleadoTiempoCompletoService`, `EmpleadoPorHorasService`, `ContratistaService`, `PersonaService`.
  - Lógica de validación, transacciones (@Transactional), batch processing (guardado en lotes de 100 para eficiencia).
  - Métodos globales en `PersonaService`: `calcularNominaTotal()` (suma salarios por tipo), `generarReporteCompleto()` (lista DTOs con info polimórfica), búsqueda por nombre.

- **Capa de Presentación (Controller):**
  - Controladores REST: `IndexController` (saludo), `PersonaController` (global), y por tipo (`EmpleadoTiempoCompletoController`, etc.).
  - Endpoints para CRUD, cálculos de impuestos (DTO `ImpuestoResponseDTO`), reportes.
  - Manejo de errores: `GlobalExceptionHandler` para excepciones como IllegalArgumentException y EntityNotFoundException, usando `ErrorResponseDTO`.

- **DTOs:**
  - Para respuestas: `GreetingDTO`, `ImpuestoResponseDTO`, `ReporteEmpleadoDto`, `ErrorResponseDTO`.
  - Herencia en DTOs: `AbstractResponseDTO` y `BaseResponseDTO`.

- **Otras características:**
  - Logging con SLF4J.
  - Excepciones personalizadas y validaciones en servicios.
  - Polimorfismo para cálculos unificados (salarios, impuestos, validaciones).
  - Batch processing para inserciones masivas.
  - Redirección en raíz (`/`) a `/HolaMundo`.

## Ejemplos de comandos cURL

Asume que la aplicación corre en `http://localhost:8080`. Usa JSON para cuerpos de request.

1. **Saludo básico:**
   ```
   curl -X GET "http://localhost:8080/HolaMundo?name=Juan"
   ```
   Respuesta esperada: `{"status":200,"error":null,"userError":null,"message":"¡Hola, Juan!"}`

2. **Crear un empleado a tiempo completo (POST):**
   ```
   curl -X POST "http://localhost:8080/api/empleados-tiempo-completo" \
   -H "Content-Type: application/json" \
   -d '{
     "nombre": "Juan",
     "apellido": "Perez",
     "fechaNacimiento": "1990-01-01",
     "numeroDocumento": "1234567",
     "salarioMensual": 5000000,
     "departamento": "IT"
   }'
   ```

3. **Obtener todos los empleados a tiempo completo (GET):**
   ```
   curl -X GET "http://localhost:8080/api/empleados-tiempo-completo"
   ```

4. **Calcular impuestos para un empleado (GET):**
   ```
   curl -X GET "http://localhost:8080/api/empleados-tiempo-completo/1/impuestos"
   ```

5. **Actualizar un contratista (PUT):**
   ```
   curl -X PUT "http://localhost:8080/api/contratistas/1" \
   -H "Content-Type: application/json" \
   -d '{
     "nombre": "Ana",
     "apellido": "Lopez",
     "fechaNacimiento": "1985-05-05",
     "numeroDocumento": "7654321",
     "montoPorProyecto": 10000000,
     "proyectosCompletados": 3,
     "fechaFinContrato": "2026-12-31"
   }'
   ```

6. **Eliminar un empleado por hora (DELETE):**
   ```
   curl -X DELETE "http://localhost:8080/api/empleados-por-hora/1"
   ```

7. **Reporte completo global (GET):**
   ```
   curl -X GET "http://localhost:8080/api/personas/reporte"
   ```

8. **Nómina total por tipo (GET):**
   ```
   curl -X GET "http://localhost:8080/api/personas/nomina"
   ```

9. **Buscar personas por nombre (GET):**
   ```
   curl -X GET "http://localhost:8080/api/personas?nombre=Juan"
   ```

10. **Crear en batch (POST, ejemplo para empleados por hora):**
    ```
    curl -X POST "http://localhost:8080/api/empleados-por-hora/batch" \
    -H "Content-Type: application/json" \
    -d '[
      {
        "nombre": "Pedro",
        "apellido": "Gomez",
        "numeroDocumento": "1111111",
        "tarifaPorHora": 50000,
        "horasTrabajadas": 50
      },
      {
        "nombre": "Maria",
        "apellido": "Rodriguez",
        "numeroDocumento": "2222222",
        "tarifaPorHora": 60000,
        "horasTrabajadas": 30
      }
    ]'
    ```
