package py.edu.uc.lp32025.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import py.edu.uc.lp32025.exception.PermisoDenegadoException;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa a un contratista externo.
 * Hereda de {@link Empleado} y define cálculo de salario por proyecto.
 */
@Entity
@Table(name = "contratista")
@PrimaryKeyJoinColumn(name = "persona_id")
@Getter
@Setter
@Slf4j
public class Contratista extends Empleado {

    @Column(name = "monto_por_proyecto", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoPorProyecto;

    @Column(name = "proyectos_completados", nullable = false)
    private int proyectosCompletados;

    @Column(name = "fecha_fin_contrato")
    private LocalDate fechaFinContrato;

    @Column(name = "departamento", length = 100)
    private String departamento;

    public Contratista() {
        super();
    }

    public Contratista(String nombre, String apellido, LocalDate fechaNacimiento,
                       String numeroDocumento, BigDecimal montoPorProyecto,
                       int proyectosCompletados, LocalDate fechaFinContrato,
                       LocalDate fechaIngreso, String departamento) {
        super(nombre, apellido, fechaNacimiento, numeroDocumento, fechaIngreso);
        this.montoPorProyecto = montoPorProyecto;
        this.proyectosCompletados = proyectosCompletados;
        this.fechaFinContrato = fechaFinContrato;
        this.departamento = departamento;
    }

    @Override
    public BigDecimal calcularSalario() {
        BigDecimal salario = montoPorProyecto.multiply(new BigDecimal(proyectosCompletados));
        log.debug("Salario calculado para contratista {} {}: {}", getNombre(), getApellido(), salario);
        return salario;
    }

    @Override
    public BigDecimal calcularDeducciones() {
        BigDecimal salario = calcularSalario();
        BigDecimal deduccion = salario.multiply(new BigDecimal("0.08")); // 8% deducción
        log.debug("Deducciones 8% para contratista {} {}: {}", getNombre(), getApellido(), deduccion);
        return deduccion;
    }

    @Override
    public boolean validarDatosEspecificos() {
        boolean montoValido = montoPorProyecto != null && montoPorProyecto.compareTo(BigDecimal.ZERO) > 0;
        boolean proyectosValido = proyectosCompletados >= 0;
        boolean fechaValida = fechaFinContrato != null && !fechaFinContrato.isBefore(getFechaIngreso());
        boolean departamentoValido = departamento != null && !departamento.trim().isEmpty();

        log.debug("Validación específica (contratista): monto > 0 → {}, proyectos >= 0 → {}, fecha fin >= ingreso → {}, departamento no vacío → {}",
                montoValido, proyectosValido, fechaValida, departamentoValido);
        return montoValido && proyectosValido && fechaValida && departamentoValido;
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

        // Contratistas: máximo 15 días, no pueden >20
        if (diasSolicitados > 15) {
            throw new PermisoDenegadoException(
                    "Límite de 15 días excedido para contratista",
                    fechaInicio,
                    String.format("Solicitados: %d, Máximo permitido: 15", diasSolicitados));
        }

        if (diasSolicitados > diasDisponibles) {
            throw new PermisoDenegadoException(
                    "Días de vacaciones insuficientes",
                    fechaInicio,
                    String.format("Solicitados: %d, Disponibles: %d", diasSolicitados, diasDisponibles));
        }

        log.info("Vacaciones aprobadas para contratista {} {}: {} días hábiles (del {} al {})",
                getNombre(), getApellido(), diasSolicitados, fechaInicio, fechaFin);
        return true;
    }

    @Override
    public boolean solicitarPermiso(LocalDate fecha, String motivo)
            throws PermisoDenegadoException {
        throw new PermisoDenegadoException("Permisos no aplicables", fecha,
                "Los contratistas no tienen derecho a permisos remunerados.");
    }

    @Override
    public int getDiasVacacionesDisponibles() {
        int antiguedadAnios = java.time.Period.between(getFechaIngreso(), LocalDate.now()).getYears();
        int dias = switch (antiguedadAnios) {
            case 0 -> 0;
            case 1, 2, 3, 4 -> 8;
            default -> 15;
        };
        log.debug("Días vacaciones disponibles para contratista {} (antigüedad {} años): {}",
                getNombre() + " " + getApellido(), antiguedadAnios, dias);
        return dias;
    }

    @Override
    public int getDiasPermisoUtilizadosEsteAnio() {
        return 0; // Contratistas no tienen permisos
    }

    @Override
    public String obtenerInformacionCompleta() {
        return String.format("Contratista: %s %s, Monto por Proyecto: %s, Proyectos Completados: %d, Fin Contrato: %s, Departamento: %s",
                getNombre(), getApellido(), montoPorProyecto, proyectosCompletados, fechaFinContrato, departamento);
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