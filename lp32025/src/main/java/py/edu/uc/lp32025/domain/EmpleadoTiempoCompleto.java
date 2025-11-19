package py.edu.uc.lp32025.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import py.edu.uc.lp32025.exception.DiasInsuficientesException;
import py.edu.uc.lp32025.exception.PermisoDenegadoException;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa a un empleado contratado a tiempo completo.
 */
@Entity
@Table(name = "empleado_tiempo_completo")
@PrimaryKeyJoinColumn(name = "persona_id")
@Getter
@Setter
@Slf4j
public class EmpleadoTiempoCompleto extends Empleado {

    @Column(name = "salario_mensual", nullable = false, precision = 15, scale = 2)
    private BigDecimal salarioMensual;

    @Column(name = "bono_anual", precision = 15, scale = 2)
    private BigDecimal bonoAnual;

    @Column(name = "departamento", length = 100)
    private String departamento;

    public EmpleadoTiempoCompleto() {
        super();
    }

    public EmpleadoTiempoCompleto(String nombre, String apellido, LocalDate fechaNacimiento,
                                  String numeroDocumento, BigDecimal salarioMensual,
                                  BigDecimal bonoAnual, LocalDate fechaIngreso,
                                  String departamento) {
        super(nombre, apellido, fechaNacimiento, numeroDocumento, fechaIngreso);
        this.salarioMensual = salarioMensual;
        this.bonoAnual = bonoAnual != null ? bonoAnual : BigDecimal.ZERO;
        this.departamento = departamento;
    }

    @Override
    public BigDecimal calcularSalario() {
        BigDecimal total = salarioMensual.add(bonoAnual != null ? bonoAnual : BigDecimal.ZERO);
        log.debug("Salario calculado para {} {}: {}", getNombre(), getApellido(), total);
        return total;
    }

    @Override
    public BigDecimal calcularDeducciones() {
        BigDecimal salario = calcularSalario();
        BigDecimal deduccion = salario.multiply(new BigDecimal("0.05"));
        log.debug("Deducciones 5% para {} {}: {}", getNombre(), getApellido(), deduccion);
        return deduccion;
    }

    @Override
    public boolean validarDatosEspecificos() {
        boolean salarioValido = salarioMensual != null && salarioMensual.compareTo(new BigDecimal("2899048")) >= 0;
        boolean departamentoValido = departamento != null && !departamento.trim().isEmpty();
        log.debug("Validación específica: salario >= 2,899,048 → {}, departamento no vacío → {}", salarioValido, departamentoValido);
        return salarioValido && departamentoValido;
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

        try {
            // Regla: solo gerentes >20 días
            if (diasSolicitados > 20 && !(this instanceof Gerente)) {
                throw new DiasInsuficientesException(
                        "Límite de 20 días excedido (solo gerentes pueden solicitar más)",
                        fechaInicio, diasSolicitados, diasDisponibles);
            }

            if (diasSolicitados > diasDisponibles) {
                throw new DiasInsuficientesException(
                        "Días de vacaciones insuficientes",
                        fechaInicio, diasSolicitados, diasDisponibles);
            }
        } catch (DiasInsuficientesException e) {
            // Convertir a PermisoDenegadoException para cumplir con throws
            throw new PermisoDenegadoException(e.getMotivo(), e.getFechaReferencia(),
                    String.format("Solicitados: %d, Disponibles: %d", e.getDiasSolicitados(), e.getDiasDisponibles()));
        }

        log.info("Vacaciones aprobadas para {} {}: {} días hábiles (del {} al {})",
                getNombre(), getApellido(), diasSolicitados, fechaInicio, fechaFin);
        return true;
    }

    @Override
    public int getDiasVacacionesDisponibles() {
        return super.getDiasVacacionesDisponibles(); // Usa lógica base
    }

    @Override
    public String obtenerInformacionCompleta() {
        return String.format("Empleado Tiempo Completo: %s %s, Salario: %s, Bono: %s, Departamento: %s, Antigüedad: %d años",
                getNombre(), getApellido(), salarioMensual, bonoAnual, departamento,
                java.time.Period.between(getFechaIngreso(), LocalDate.now()).getYears());
    }

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