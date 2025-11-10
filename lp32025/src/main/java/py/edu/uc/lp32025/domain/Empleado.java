package py.edu.uc.lp32025.domain;

import jakarta.persistence.*;
import py.edu.uc.lp32025.interfaces.Permisionable;
import py.edu.uc.lp32025.exception.PermisoDenegadoException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase intermedia abstracta que representa a cualquier empleado.
 * Hereda de {@link Persona} y aplica la interfaz {@link Permisionable}
 * con lógica común para vacaciones y permisos según normativa paraguaya.
 */
@MappedSuperclass
@Getter
@Setter
@Slf4j
public abstract class Empleado extends Persona implements Permisionable {

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    public Empleado() {
    }

    public Empleado(String nombre, String apellido, LocalDate fechaNacimiento,
                    String numeroDocumento, LocalDate fechaIngreso) {
        super(nombre, apellido, fechaNacimiento, numeroDocumento);
        this.fechaIngreso = fechaIngreso;
    }

    // ====================== IMPLEMENTACIÓN DE Permisionable ======================

    @Override
    public boolean solicitarVacaciones(LocalDate fechaInicio, LocalDate fechaFin)
            throws PermisoDenegadoException {
        log.info("Empleado {} solicita vacaciones: {} → {}", getNombre() + " " + getApellido(), fechaInicio, fechaFin);

        if (fechaInicio == null || fechaFin == null || fechaFin.isBefore(fechaInicio)) {
            throw new PermisoDenegadoException("Rango inválido", fechaInicio,
                    "La fecha de fin debe ser posterior a la de inicio.");
        }

        int diasSolicitados = contarDiasHabiles(fechaInicio, fechaFin);
        int diasDisponibles = getDiasVacacionesDisponibles();

        if (diasSolicitados > diasDisponibles) {
            log.warn("Vacaciones denegadas para {}: {} días solicitados > {} disponibles",
                    getNombre() + " " + getApellido(), diasSolicitados, diasDisponibles);
            throw new PermisoDenegadoException("Saldo insuficiente", fechaInicio,
                    String.format("Días solicitados: %d, disponibles: %d.", diasSolicitados, diasDisponibles));
        }

        log.info("Vacaciones aprobadas para {}: {} días", getNombre() + " " + getApellido(), diasSolicitados);
        // Aquí se registraría en base de datos
        return true;
    }

    @Override
    public boolean solicitarPermiso(LocalDate fecha, String motivo)
            throws PermisoDenegadoException {
        log.info("Empleado {} solicita permiso: {} - Motivo: {}", getNombre() + " " + getApellido(), fecha, motivo);

        if (fecha == null || motivo == null || motivo.trim().isEmpty()) {
            throw new PermisoDenegadoException("Datos incompletos", fecha,
                    "Debe indicar fecha y motivo válido.");
        }

        int diasUsados = getDiasPermisoUtilizadosEsteAnio();
        int limiteAnual = switch (motivo.toUpperCase()) {
            case "MATRIMONIO" -> 3;
            case "NACIMIENTO_HIJO" -> 2;
            case "FALLECIMIENTO_FAMILIAR" -> 2;
            default -> throw new PermisoDenegadoException("Motivo no contemplado", fecha,
                    "Motivo '" + motivo + "' no está regulado por la ley.");
        };

        if (diasUsados >= limiteAnual) {
            log.warn("Permiso denegado para {}: límite anual excedido para {}", getNombre() + " " + getApellido(), motivo);
            throw new PermisoDenegadoException("Límite anual excedido", fecha,
                    String.format("Máximo %d días por %s al año.", limiteAnual, motivo));
        }

        log.info("Permiso aprobado para {}: {}", getNombre() + " " + getApellido(), motivo);
        // Registro de permiso aprobado
        return true;
    }

    @Override
    public int getDiasVacacionesDisponibles() {
        int antiguedadAnios = Period.between(fechaIngreso, LocalDate.now()).getYears();
        int dias = switch (antiguedadAnios) {
            case 0 -> 0;
            case 1, 2, 3, 4 -> 12;
            case 5, 6, 7, 8, 9 -> 18;
            default -> 30;
        };
        log.debug("Días vacaciones disponibles para {} (antigüedad {} años): {}", getNombre() + " " + getApellido(), antiguedadAnios, dias);
        return dias;
    }

    @Override
    public int getDiasPermisoUtilizadosEsteAnio() {
        // Implementación real: consulta a tabla de permisos
        int usados = 0;
        log.debug("Días permiso usados este año para {}: {}", getNombre() + " " + getApellido(), usados);
        return usados;
    }

    /**
     * Cuenta días hábiles (lunes a viernes) entre dos fechas.
     */
    protected int contarDiasHabiles(LocalDate inicio, LocalDate fin) {
        int dias = 0;
        LocalDate actual = inicio;
        while (!actual.isAfter(fin)) {
            if (actual.getDayOfWeek().getValue() <= 5) {
                dias++;
            }
            actual = actual.plusDays(1);
        }
        return dias;
    }
}