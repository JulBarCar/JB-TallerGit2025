package py.edu.uc.lp32025.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import py.edu.uc.lp32025.exception.PermisoDenegadoException;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa a un empleado contratado por horas.
 * Hereda de {@link Empleado} y define cálculo de salario por horas trabajadas.
 */
@Entity
@Table(name = "empleado_por_hora")
@PrimaryKeyJoinColumn(name = "persona_id")
@Getter
@Setter
@Slf4j
public class EmpleadoPorHora extends Empleado {

    @Column(name = "tarifa_por_hora", nullable = false, precision = 15, scale = 2)
    private BigDecimal tarifaPorHora;

    @Column(name = "horas_trabajadas", nullable = false)
    private int horasTrabajadas;

    @Column(name = "departamento", length = 100)
    private String departamento;

    public EmpleadoPorHora() {
        super();
    }

    public EmpleadoPorHora(String nombre, String apellido, LocalDate fechaNacimiento,
                           String numeroDocumento, BigDecimal tarifaPorHora,
                           int horasTrabajadas, LocalDate fechaIngreso,
                           String departamento) {
        super(nombre, apellido, fechaNacimiento, numeroDocumento, fechaIngreso);
        this.tarifaPorHora = tarifaPorHora;
        this.horasTrabajadas = horasTrabajadas;
        this.departamento = departamento;
    }

    @Override
    public BigDecimal calcularSalario() {
        BigDecimal salarioBase = tarifaPorHora.multiply(new BigDecimal(horasTrabajadas));
        BigDecimal extra = BigDecimal.ZERO;
        if (horasTrabajadas > 40) {
            int horasExtra = horasTrabajadas - 40;
            extra = tarifaPorHora.multiply(new BigDecimal("1.5")).multiply(new BigDecimal(horasExtra));
        }
        BigDecimal total = salarioBase.add(extra);
        log.debug("Salario calculado para {} {}: base {} + extra {} = {}", getNombre(), getApellido(), salarioBase, extra, total);
        return total;
    }

    @Override
    public BigDecimal calcularDeducciones() {
        BigDecimal salario = calcularSalario();
        BigDecimal deduccion = salario.multiply(new BigDecimal("0.06")); // 6% deducción
        log.debug("Deducciones 6% para {} {}: {}", getNombre(), getApellido(), deduccion);
        return deduccion;
    }

    @Override
    public boolean validarDatosEspecificos() {
        boolean tarifaValida = tarifaPorHora != null && tarifaPorHora.compareTo(new BigDecimal("15000")) >= 0;
        boolean horasValida = horasTrabajadas >= 0 && horasTrabajadas <= 60;
        boolean departamentoValido = departamento != null && !departamento.trim().isEmpty();

        log.debug("Validación específica (por hora): tarifa >= 15,000 → {}, horas 0-60 → {}, departamento no vacío → {}",
                tarifaValida, horasValida, departamentoValido);
        return tarifaValida && horasValida && departamentoValido;
    }

    // ====================== Permisionable ======================

    @Override
    protected boolean solicitarVacacionesInterno(LocalDate fechaInicio, LocalDate fechaFin)
            throws PermisoDenegadoException {
        if (fechaInicio == null || fechaFin == null || fechaInicio.isAfter(fechaFin)) {
            throw new PermisoDenegadoException("Fechas inválidas", fechaInicio,
                    "La fecha de inicio debe ser anterior o igual a la fecha de fin.");
        }

        int diasSolicitados = contarDiasHabiles(fechaInicio, fechaFin);
        int diasDisponibles = getDiasVacacionesDisponibles();

        // Empleados por hora: máximo 18 días, no pueden >20
        if (diasSolicitados > 18) {
            throw new PermisoDenegadoException(
                    "Límite de 18 días excedido para empleado por hora",
                    fechaInicio,
                    String.format("Solicitados: %d, Máximo permitido: 18", diasSolicitados));
        }

        if (diasSolicitados > diasDisponibles) {
            throw new PermisoDenegadoException(
                    "Días de vacaciones insuficientes",
                    fechaInicio,
                    String.format("Solicitados: %d, Disponibles: %d", diasSolicitados, diasDisponibles));
        }

        log.info("Vacaciones aprobadas para empleado por hora {} {}: {} días hábiles (del {} al {})",
                getNombre(), getApellido(), diasSolicitados, fechaInicio, fechaFin);
        return true;
    }

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
            case 1, 2, 3, 4 -> 10;
            case 5, 6, 7, 8, 9 -> 15;
            default -> 18;
        };
        log.debug("Días vacaciones disponibles para empleado por hora {} (antigüedad {} años): {}",
                getNombre() + " " + getApellido(), antiguedadAnios, dias);
        return dias;
    }

    @Override
    public int getDiasPermisoUtilizadosEsteAnio() {
        // Implementación real: consulta a tabla de permisos
        int usados = 0;
        log.debug("Días permiso usados este año para {} {}: {}", getNombre(), getApellido(), usados);
        return usados;
    }

    @Override
    public String obtenerInformacionCompleta() {
        return String.format("Empleado Por Hora: %s %s, Tarifa: %s, Horas: %d, Departamento: %s, Antigüedad: %d años",
                getNombre(), getApellido(), tarifaPorHora, horasTrabajadas, departamento,
                java.time.Period.between(getFechaIngreso(), LocalDate.now()).getYears());
    }

    /**
     * Cuenta días hábiles (lunes a viernes) entre dos fechas.
     */
    public int contarDiasHabiles(LocalDate inicio, LocalDate fin) {
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