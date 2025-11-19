package py.edu.uc.lp32025.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import py.edu.uc.lp32025.exception.DiasInsuficientesException;
import py.edu.uc.lp32025.exception.PermisoDenegadoException;
import py.edu.uc.lp32025.interfaces.Permisionable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

/**
 * Representa a un gerente dentro de la empresa.
 * Hereda de {@link EmpleadoTiempoCompleto} para reutilizar salario + bono,
 * e implementa {@link Permisionable} con funcionalidad adicional de aprobación.
 */
@Entity
@Table(name = "gerente")
@PrimaryKeyJoinColumn(name = "persona_id")
@Getter
@Setter
@Slf4j
public class Gerente extends EmpleadoTiempoCompleto {

    @Column(name = "nivel_jerarquico", nullable = false)
    private Integer nivelJerarquico; // 1 = Gerente de área, 2 = Gerente general, etc.

    @Column(name = "departamento_gestionado")
    private String departamentoGestionado;

    public Gerente() {
        super();
    }

    public Gerente(String nombre, String apellido, LocalDate fechaNacimiento,
                   String numeroDocumento, BigDecimal salarioMensual,
                   BigDecimal bonoAnual, LocalDate fechaIngreso,
                   String departamento, Integer nivelJerarquico,
                   String departamentoGestionado) {
        super(nombre, apellido, fechaNacimiento, numeroDocumento,
                salarioMensual, bonoAnual, fechaIngreso, departamento);
        this.nivelJerarquico = nivelJerarquico;
        this.departamentoGestionado = departamentoGestionado;
    }

    // ====================== REUTILIZACIÓN DE EmpleadoTiempoCompleto ======================

    @Override
    public BigDecimal calcularSalario() {
        // Mismo cálculo que tiempo completo: salario + bono
        return super.calcularSalario();
    }

    @Override
    public BigDecimal calcularDeducciones() {
        // Deducción especial para gerentes: 3%
        BigDecimal salario = calcularSalario();
        log.debug("Deducciones 3% para gerente {} {}: {}", getNombre(), getApellido(),
                salario.multiply(new BigDecimal("0.03")));
        return salario.multiply(new BigDecimal("0.03"));
    }

    @Override
    public boolean validarDatosEspecificos() {
        boolean baseValido = super.validarDatosEspecificos();
        boolean gerenteValido = nivelJerarquico != null && nivelJerarquico >= 1
                && departamentoGestionado != null && !departamentoGestionado.trim().isEmpty();

        log.debug("Validación específica (gerente): {}", baseValido && gerenteValido ? "OK" : "FALLIDA");
        return baseValido && gerenteValido;
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
        int diasDisponibles = getDiasVacacionesDisponibles(); // 15/25/45 según antigüedad

        if (diasSolicitados > diasDisponibles) {
            throw new PermisoDenegadoException("Días insuficientes para gerente", fechaInicio,
                    String.format("Solicitados: %d, Disponibles: %d", diasSolicitados, diasDisponibles));
        }

        log.info("Vacaciones aprobadas para gerente {} {}: {} días hábiles (del {} al {})",
                getNombre(), getApellido(), diasSolicitados, fechaInicio, fechaFin);
        return true;
    }

    @Override
    public boolean solicitarPermiso(LocalDate fecha, String motivo)
            throws PermisoDenegadoException {
        // Gerentes tienen permisos ilimitados por motivo justificado
        if (fecha == null || motivo == null || motivo.trim().isEmpty()) {
            throw new PermisoDenegadoException("Datos incompletos", fecha,
                    "Fecha y motivo son requeridos.");
        }

        log.info("Permiso aprobado para gerente {} {}: {}", getNombre(), getApellido(), motivo);
        return true;
    }

    @Override
    public int getDiasVacacionesDisponibles() {
        int antiguedadAnios = Period.between(getFechaIngreso(), LocalDate.now()).getYears();
        int dias = switch (antiguedadAnios) {
            case 0 -> 0;
            case 1, 2, 3, 4 -> 15;      // +3 vs empleado común
            case 5, 6, 7, 8, 9 -> 25;   // +7
            default -> 45;              // +15
        };
        log.debug("Días vacaciones disponibles para gerente {} (antigüedad {} años): {}",
                getNombre() + " " + getApellido(), antiguedadAnios, dias);
        return dias;
    }

    @Override
    public int getDiasPermisoUtilizadosEsteAnio() {
        // Implementación real: consulta a tabla de permisos
        int usados = 0;
        log.debug("Días permiso usados este año para gerente {} {}: {}", getNombre(), getApellido(), usados);
        return usados;
    }

    @Override
    public boolean aprobarPermisoDeSubordinado(Long empleadoId, LocalDate fecha, String motivo, boolean aprobar)
            throws PermisoDenegadoException {
        if (empleadoId == null || fecha == null || motivo == null || motivo.trim().isEmpty()) {
            throw new PermisoDenegadoException("Datos incompletos", fecha,
                    "ID empleado, fecha y motivo son requeridos.");
        }

        if (!tieneAutoridadSobreEmpleado(empleadoId)) {
            throw new PermisoDenegadoException("Sin autoridad", fecha,
                    "El gerente no gestiona al empleado ID: " + empleadoId);
        }

        String accion = aprobar ? "APROBADO" : "RECHAZADO";
        log.info("Gerente {} {} {} permiso para empleado ID {}: {} - {}",
                getNombre(), getApellido(), accion, empleadoId, fecha, motivo);
        // Aquí se registraría la aprobación/rechazo en BD
        return true;
    }

    /**
     * Verifica si el gerente tiene autoridad sobre el empleado.
     * En producción: consulta a tabla de organigrama.
     */
    private boolean tieneAutoridadSobreEmpleado(Long empleadoId) {
        // Simulación: si el departamento coincide, tiene autoridad
        // En implementación real: join con tabla de subordinados
        return true;
    }

    // ====================== UTILIDADES ======================

    @Override
    public String obtenerInformacionCompleta() {
        String base = super.obtenerInformacionCompleta();
        return base + String.format(", Nivel Jerárquico: %d, Departamento Gestionado: %s",
                nivelJerarquico, departamentoGestionado);
    }

    /**
     * Cuenta días hábiles (lunes a viernes) entre dos fechas.
     */
    public int contarDiasHabiles(LocalDate inicio, LocalDate fin) {
        int count = 0;
        LocalDate current = inicio;
        while (!current.isAfter(fin)) {
            if (current.getDayOfWeek().getValue() <= 5) { // Lunes=1, Viernes=5
                count++;
            }
            current = current.plusDays(1);
        }
        return count;
    }
}