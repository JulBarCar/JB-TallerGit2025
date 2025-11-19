package py.edu.uc.lp32025.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import py.edu.uc.lp32025.domain.Contratista;
import py.edu.uc.lp32025.domain.EmpleadoPorHora;
import py.edu.uc.lp32025.domain.EmpleadoTiempoCompleto;
import py.edu.uc.lp32025.domain.Gerente;
import py.edu.uc.lp32025.exception.PermisoDenegadoException;
import py.edu.uc.lp32025.interfaces.Permisionable;
import py.edu.uc.lp32025.service.ContratistaService;
import py.edu.uc.lp32025.service.EmpleadoPorHorasService;
import py.edu.uc.lp32025.service.EmpleadoTiempoCompletoService;
import py.edu.uc.lp32025.service.GerenteService;
import py.edu.uc.lp32025.utils.NominaUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Aplicación demo para probar funcionalidades del sistema de empleados.
 * Incluye pruebas de:
 * - Creación y CRUD de diferentes tipos de empleados (incluyendo Gerente).
 * - Cálculos de salarios, impuestos y validaciones (polimorfismo).
 * - Solicitudes de vacaciones y permisos con excepciones.
 * - Regla de >20 días solo para gerentes.
 * - Utilitaria NominaUtils para total de días y reporte JSON.
 * - Funcionalidades previas como batch y reportes.
 */
/*@SpringBootApplication(scanBasePackages = "py.edu.uc.lp32025")
public class IntegracionDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegracionDemoApplication.class, args);
    }

    @Bean
    CommandLineRunner demo(EmpleadoTiempoCompletoService tiempoCompletoService,
                           EmpleadoPorHorasService porHoraService,
                           ContratistaService contratistaService,
                           GerenteService gerenteService) {
        return args -> {
            System.out.println("\n=== DEMO COMPLETA DEL SISTEMA DE EMPLEADOS ===");

            // 1. Crear y guardar empleados de diferentes tipos (prueba CRUD y batch)
            System.out.println("\n--- 1. CREACIÓN Y GUARDADO DE EMPLEADOS (CRUD + BATCH) ---");

            // Empleado Tiempo Completo
            EmpleadoTiempoCompleto empTC = new EmpleadoTiempoCompleto(
                    "Juan", "Pérez", LocalDate.of(1990, 1, 1),
                    "123456", new BigDecimal("3000000"),
                    new BigDecimal("500000"), LocalDate.of(2020, 1, 1),
                    "Ventas"
            );
            tiempoCompletoService.save(empTC);
            System.out.println("Empleado Tiempo Completo creado: " + empTC.obtenerInformacionCompleta());

            // Empleado Por Hora
            EmpleadoPorHora empHora = new EmpleadoPorHora(
                    "María", "Gómez", LocalDate.of(1995, 5, 5),
                    "654321", new BigDecimal("20000"), 50,
                    LocalDate.of(2022, 6, 1), "IT"
            );
            porHoraService.save(empHora);
            System.out.println("Empleado Por Hora creado: " + empHora.obtenerInformacionCompleta());

            // Contratista
            Contratista contratista = new Contratista(
                    "Pedro", "López", LocalDate.of(1985, 10, 10),
                    "789012", new BigDecimal("10000000"), 3,
                    LocalDate.now().plusMonths(6), LocalDate.of(2023, 1, 1),
                    "Consultoría"
            );
            contratistaService.save(contratista);
            System.out.println("Contratista creado: " + contratista.obtenerInformacionCompleta());

            // Gerente (nuevo)
            Gerente gerente = new Gerente(
                    "Ana", "Martínez", LocalDate.of(1980, 2, 2),
                    "345678", new BigDecimal("15000000"),
                    new BigDecimal("2000000"), LocalDate.of(2015, 1, 1),
                    "Administración", 2, "Ventas"
            );
            gerenteService.save(gerente);
            System.out.println("Gerente creado: " + gerente.obtenerInformacionCompleta());

            // Prueba batch (funcionalidad previa)
            List<EmpleadoTiempoCompleto> batchTC = Arrays.asList(
                    new EmpleadoTiempoCompleto("Batch1", "Test", LocalDate.of(1992, 3, 3),
                            "111111", new BigDecimal("4000000"), new BigDecimal("600000"),
                            LocalDate.of(2021, 2, 2), "HR"),
                    new EmpleadoTiempoCompleto("Batch2", "Test", LocalDate.of(1993, 4, 4),
                            "222222", new BigDecimal("5000000"), new BigDecimal("700000"),
                            LocalDate.of(2022, 3, 3), "Marketing")
            );
            tiempoCompletoService.guardarEmpleadosEnBatch(batchTC);
            System.out.println("Batch de Empleados Tiempo Completo guardados: " + batchTC.size());

            // 2. Cálculos de salarios e impuestos (polimorfismo, funcionalidades previas)
            System.out.println("\n--- 2. CÁLCULOS DE SALARIOS E IMPUESTOS (POLIMORFISMO) ---");
            System.out.println("Salario Empleado TC: " + empTC.calcularSalario());
            System.out.println("Impuestos Empleado TC: " + empTC.calcularImpuestos());
            System.out.println("Salario Empleado Hora: " + empHora.calcularSalario());
            System.out.println("Impuestos Empleado Hora: " + empHora.calcularImpuestos());
            System.out.println("Salario Contratista: " + contratista.calcularSalario());
            System.out.println("Impuestos Contratista: " + contratista.calcularImpuestos());
            System.out.println("Salario Gerente: " + gerente.calcularSalario());
            System.out.println("Impuestos Gerente: " + gerente.calcularImpuestos());

            // 3. Prueba de vacaciones y permisos (funcionalidades previas + nuevas excepciones y regla >20 días)
            System.out.println("\n--- 3. PRUEBA DE VACACIONES Y PERMISOS ---");

            // Prueba vacaciones normales (empleado común, <20 días)
            try {
                empTC.solicitarVacaciones(LocalDate.now().plusDays(1), LocalDate.now().plusDays(10)); // ~8 días hábiles
                System.out.println("Vacaciones aprobadas para Empleado TC.");
            } catch (PermisoDenegadoException e) {
                System.out.println("Error en vacaciones Empleado TC: " + e.getMessage());
            }

            // Prueba regla >20 días (empleado común → debe fallar)
            try {
                empTC.solicitarVacaciones(LocalDate.now().plusDays(1), LocalDate.now().plusDays(30)); // >20 días
                System.out.println("Vacaciones >20 aprobadas para Empleado TC (debería fallar).");
            } catch (PermisoDenegadoException e) {
                System.out.println("Error esperado en >20 días Empleado TC: " + e.getMessage());
            }

            // Prueba >20 días para Gerente (debe aprobarse)
            try {
                gerente.solicitarVacaciones(LocalDate.now().plusDays(1), LocalDate.now().plusDays(30)); // >20 días
                System.out.println("Vacaciones >20 aprobadas para Gerente.");
            } catch (PermisoDenegadoException e) {
                System.out.println("Error en vacaciones Gerente: " + e.getMessage());
            }

            // Prueba permiso normal
            try {
                empTC.solicitarPermiso(LocalDate.now().plusDays(5), "MATRIMONIO");
                System.out.println("Permiso aprobado para Empleado TC.");
            } catch (PermisoDenegadoException e) {
                System.out.println("Error en permiso Empleado TC: " + e.getMessage());
            }

            // Prueba aprobación de subordinado (solo Gerente)
            try {
                gerente.aprobarPermisoDeSubordinado(empTC.getId(), LocalDate.now().plusDays(10), "NACIMIENTO_HIJO", true);
                System.out.println("Aprobación de permiso por Gerente: Éxito.");
            } catch (PermisoDenegadoException e) {
                System.out.println("Error en aprobación Gerente: " + e.getMessage());
            }

            // Prueba intento de aprobación con empleado común (debe fallar)
            try {
                empTC.aprobarPermisoDeSubordinado(gerente.getId(), LocalDate.now().plusDays(15), "FALLECIMIENTO_FAMILIAR", false);
                System.out.println("Aprobación por Empleado TC (debería fallar).");
            } catch (UnsupportedOperationException | PermisoDenegadoException e) {
                System.out.println("Error esperado en aprobación Empleado TC: " + e.getMessage());
            }

            // 4. Prueba de NominaUtils (nueva utilitaria)
            System.out.println("\n--- 4. PRUEBA DE NOMINAUTILS ---");
            List<Permisionable> empleados = Arrays.asList(empTC, empHora, contratista, gerente);
            int totalDias = NominaUtils.calcularTotalDiasSolicitados(empleados);
            System.out.println("Total días solicitados: " + totalDias);

            String reporteJson = NominaUtils.generarReporteJson(10, empleados);
            System.out.println("Reporte JSON (umbral 10 días): " + reporteJson);

            // 5. Prueba de excepciones en servicios (EmpleadoNoEncontradoException)
            System.out.println("\n--- 5. PRUEBA DE EXCEPCIONES EN SERVICIOS ---");
            try {
                tiempoCompletoService.update(999L, empTC); // ID inválido → debe lanzar EmpleadoNoEncontradoException
            } catch (Exception e) {
                System.out.println("Error esperado en update con ID inválido: " + e.getMessage());
            }

            System.out.println("\n=== FIN DE DEMO ===");
        };
    }
}*/