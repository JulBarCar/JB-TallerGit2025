package py.edu.uc.lp32025.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import py.edu.uc.lp32025.exception.PermisoDenegadoException;
import py.edu.uc.lp32025.interfaces.Permisionable;

import java.time.LocalDate;

/**
 * Clase abstracta base para todos los tipos de empleados.
 */
@MappedSuperclass
@Getter
@Setter
@Slf4j
public abstract class Empleado extends Persona implements Permisionable {

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    public Empleado() {
        super();
    }

    public Empleado(String nombre, String apellido, LocalDate fechaNacimiento,
                    String numeroDocumento, LocalDate fechaIngreso) {
        super(nombre, apellido, fechaNacimiento, numeroDocumento);
        this.fechaIngreso = fechaIngreso;
    }

    // ====================== Permisionable (métodos comunes) ======================

    @Override
    public boolean solicitarVacaciones(LocalDate fechaInicio, LocalDate fechaFin)
            throws PermisoDenegadoException {
        return solicitarVacacionesInterno(fechaInicio, fechaFin);
    }

    protected abstract boolean solicitarVacacionesInterno(LocalDate fechaInicio, LocalDate fechaFin)
            throws PermisoDenegadoException;

    @Override
    public boolean solicitarPermiso(LocalDate fecha, String motivo)
            throws PermisoDenegadoException {
        if (fecha == null || motivo == null || motivo.trim().isEmpty()) {
            throw new PermisoDenegadoException("Datos incompletos", fecha,
                    "Fecha y motivo son requeridos.");
        }

        String motivoUpper = motivo.toUpperCase();
        if (!motivoUpper.equals("MATRIMONIO") && !motivoUpper.equals("NACIMIENTO_HIJO")
                && !motivoUpper.equals("FALLECIMIENTO_FAMILIAR")) {
            throw new PermisoDenegadoException("Motivo no contemplado", fecha,
                    "Motivos válidos: MATRIMONIO (3 días), NACIMIENTO_HIJO (2 días), FALLECIMIENTO_FAMILIAR (2 días)");
        }

        int diasPermitidos = switch (motivoUpper) {
            case "MATRIMONIO" -> 3;
            case "NACIMIENTO_HIJO" -> 2;
            case "FALLECIMIENTO_FAMILIAR" -> 2;
            default -> 0;
        };

        log.info("Permiso aprobado para {} {}: {} días por {}", getNombre(), getApellido(), diasPermitidos, motivo);
        return true;
    }

    @Override
    public int getDiasVacacionesDisponibles() {
        int antiguedadAnios = java.time.Period.between(getFechaIngreso(), LocalDate.now()).getYears();
        int dias = switch (antiguedadAnios) {
            case 0 -> 0;
            case 1, 2, 3, 4 -> 12;
            case 5, 6, 7, 8, 9 -> 18;
            default -> 30;
        };
        log.debug("Días vacaciones base para {} (antigüedad {} años): {}",
                getNombre() + " " + getApellido(), antiguedadAnios, dias);
        return dias;
    }

    @Override
    public int getDiasPermisoUtilizadosEsteAnio() {
        return 0;
    }

    @Override
    public String obtenerInformacionCompleta() {
        return String.format("Empleado: %s %s, Ingreso: %s", getNombre(), getApellido(), fechaIngreso);
    }

    /**
     * Cuenta días hábiles (lunes a viernes) entre dos fechas.
     * Disponible para todas las subclases.
     */
    public int contarDiasHabiles(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null || inicio.isAfter(fin)) {
            return 0;
        }
        int count = 0;
        LocalDate current = inicio;
        while (!current.isAfter(fin)) {
            if (current.getDayOfWeek().getValue() <= 5) {
                count++;
            }
            current = current.plusDays(1);
        }
        return count;
    }
}