package py.edu.uc.lp32025.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import py.edu.uc.lp32025.domain.*;
import py.edu.uc.lp32025.exception.PermisoDenegadoException;
import py.edu.uc.lp32025.interfaces.Permisionable;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Aplicación demo para probar las excepciones de Permisionable.
 * Ejecuta al iniciar la app y muestra ejemplos de vacaciones/permisos aprobados y denegados.

@SpringBootApplication(scanBasePackages = "py.edu.uc.lp32025")
public class PermisoDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PermisoDemoApplication.class, args);
    }

    @Bean
    CommandLineRunner demo() {
        return args -> {
            System.out.println("\n=== DEMO DE EXCEPCIONES - PERMISIONABLE ===\n");

            // Crear un empleado de prueba
            LocalDate fechaIngreso = LocalDate.of(2020, 1, 15);
            EmpleadoTiempoCompleto empleado = new EmpleadoTiempoCompleto(
                    "Carlos", "Gómez", LocalDate.of(1985, 5, 20),
                    "1234567", new BigDecimal("5000000"), new BigDecimal("10000000"), fechaIngreso
            );

            probarVacaciones(empleado);
            probarPermiso(empleado);
            probarCasosDenegados(empleado);
        };
    }

    private void probarVacaciones(Permisionable emp) {
        System.out.println("--- PRUEBA DE VACACIONES ---");
        try {
            LocalDate inicio = LocalDate.now().plusDays(7);
            LocalDate fin = inicio.plusDays(10); // 11 días → ~8 hábiles
            boolean aprobado = emp.solicitarVacaciones(inicio, fin);
            System.out.printf("Vacaciones del %s al %s: %s%n", inicio, fin, aprobado ? "APROBADO" : "DENEGADO");
        } catch (PermisoDenegadoException e) {
            manejarExcepcion(e, "vacaciones");
        }
    }

    private void probarPermiso(Permisionable emp) {
        System.out.println("\n--- PRUEBA DE PERMISO ---");
        try {
            boolean aprobado = emp.solicitarPermiso(LocalDate.now().plusDays(3), "MATRIMONIO");
            System.out.printf("Permiso por MATRIMONIO: %s%n", aprobado ? "APROBADO" : "DENEGADO");
        } catch (PermisoDenegadoException e) {
            manejarExcepcion(e, "permiso");
        }
    }

    private void probarCasosDenegados(Permisionable emp) {
        System.out.println("\n--- CASOS QUE DEBEN FALLAR ---");

        // 1. Vacaciones insuficientes
        try {
            emp.solicitarVacaciones(LocalDate.now(), LocalDate.now().plusDays(40));
        } catch (PermisoDenegadoException e) {
            manejarExcepcion(e, "vacaciones largas");
        }

        // 2. Motivo inválido
        try {
            emp.solicitarPermiso(LocalDate.now(), "VIAJE TURÍSTICO");
        } catch (PermisoDenegadoException e) {
            manejarExcepcion(e, "motivo inválido");
        }

        // 3. Fecha inválida (fin antes que inicio)
        try {
            emp.solicitarVacaciones(LocalDate.now().plusDays(5), LocalDate.now());
        } catch (PermisoDenegadoException e) {
            manejarExcepcion(e, "rango inválido");
        }
    }

    private void manejarExcepcion(PermisoDenegadoException e, String tipo) {
        System.out.printf("❌ %s DENEGADO%n", tipo.toUpperCase());
        System.out.println("   Motivo: " + e.getMotivo());
        System.out.println("   Fecha: " + e.getFechaSolicitada());
        System.out.println("   Detalle: " + e.getDetalleNormativa());
        System.out.println();
    }
} */