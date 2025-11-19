package py.edu.uc.lp32025.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import py.edu.uc.lp32025.domain.EmpleadoTiempoCompleto;
import py.edu.uc.lp32025.domain.Gerente;
import py.edu.uc.lp32025.service.EmpleadoTiempoCompletoService;
import py.edu.uc.lp32025.service.GerenteService;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Demo actualizada para probar la **jerarquía de controladores**.
 * - CRUD en /api/empleados/{id} y /api/gerentes/{id}
 * - Solicitudes en /api/empleados/{id}/...
 * - Aprobación en /api/gerentes/{gerenteId}/...
 */
@SpringBootApplication(scanBasePackages = "py.edu.uc.lp32025")
public class RestDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestDemoApplication.class, args);
    }

    @Bean
    CommandLineRunner restDemo(EmpleadoTiempoCompletoService tcService,
                               GerenteService gerenteService) {
        return args -> {
            System.out.println("\n=== DEMO JERARQUÍA DE CONTROLADORES REST ===");

            // 1. Crear empleados
            System.out.println("\n--- 1. CREANDO EMPLEADOS ---");
            EmpleadoTiempoCompleto emp = new EmpleadoTiempoCompleto(
                    "Carlos", "López", LocalDate.of(1992, 3, 15),
                    "555555", new BigDecimal("4000000"),
                    new BigDecimal("600000"), LocalDate.of(2020, 1, 1),
                    "IT"
            );
            tcService.save(emp);
            System.out.println("Empleado TC creado: ID = " + emp.getId());

            Gerente gerente = new Gerente(
                    "Laura", "Gómez", LocalDate.of(1985, 5, 10),
                    "666666", new BigDecimal("12000000"),
                    new BigDecimal("3000000"), LocalDate.of(2010, 1, 1),
                    "Dirección", 2, "IT"
            );
            gerenteService.save(gerente);
            System.out.println("Gerente creado: ID = " + gerente.getId());

            // 2. Guía de pruebas
            System.out.println("\n--- 2. PRUEBA CRUD (EMPLEADO) ---");
            System.out.println("   → GET http://localhost:8080/api/empleados/" + emp.getId());
            System.out.println("   → PUT http://localhost:8080/api/empleados/" + emp.getId() + " → cambia nombre");
            System.out.println("   → DELETE http://localhost:8080/api/empleados/" + emp.getId());

            System.out.println("\n--- 3. PRUEBA CRUD (GERENTE) ---");
            System.out.println("   → GET http://localhost:8080/api/gerentes/" + gerente.getId());

            System.out.println("\n--- 4. PRUEBA SOLICITUD VACACIONES (EMPLEADO) ---");
            System.out.println("   → POST http://localhost:8080/api/empleados/" + emp.getId() + "/vacaciones/solicitar");
            System.out.println("   Body: {\"fechaInicio\":\"2025-12-01\",\"fechaFin\":\"2025-12-10\"}");
            System.out.println("   → 201 Created");

            System.out.println("\n--- 5. PRUEBA ERROR >20 DÍAS (NO GERENTE) ---");
            System.out.println("   → POST con fechaFin: \"2025-12-31\" → 403 Forbidden");

            System.out.println("\n--- 6. PRUEBA >20 DÍAS (GERENTE) ---");
            System.out.println("   → POST http://localhost:8080/api/empleados/" + gerente.getId() + "/vacaciones/solicitar");
            System.out.println("   → 201 Created");

            System.out.println("\n--- 7. PRUEBA SOLICITUD PERMISO ---");
            System.out.println("   → POST http://localhost:8080/api/empleados/" + emp.getId() + "/permisos/solicitar");
            System.out.println("   Body: {\"fecha\":\"2025-12-15\",\"motivo\":\"MATRIMONIO\"}");

            System.out.println("\n--- 8. PRUEBA APROBACIÓN POR GERENTE ---");
            System.out.println("   → PUT http://localhost:8080/api/gerentes/" + gerente.getId() + "/aprobar-permiso/" + emp.getId() + "?aprobar=true");
            System.out.println("   Body: {\"fecha\":\"2025-12-15\",\"motivo\":\"MATRIMONIO\"}");

            System.out.println("\n--- 9. PRUEBA ID INEXISTENTE ---");
            System.out.println("   → POST http://localhost:8080/api/empleados/999/vacaciones/solicitar → 404");

            System.out.println("\n=== FIN DE DEMO. USA POSTMAN O CURL ===");
        };
    }
}