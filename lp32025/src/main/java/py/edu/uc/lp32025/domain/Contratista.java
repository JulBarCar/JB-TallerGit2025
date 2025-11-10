package py.edu.uc.lp32025.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "contratista")
@PrimaryKeyJoinColumn(name = "persona_id")
@Getter
@Setter
@Slf4j
public class Contratista extends Empleado {

    @Column(name = "monto_por_proyecto", nullable = false)
    private BigDecimal montoPorProyecto;

    @Column(name = "proyectos_completados", nullable = false)
    private Integer proyectosCompletados;

    @Column(name = "fecha_fin_contrato", nullable = false)
    private LocalDate fechaFinContrato;

    @Column(name = "departamento")
    private String departamento;

    public Contratista() {
    }

    public Contratista(String nombre, String apellido, LocalDate fechaNacimiento, String numeroDocumento,
                       BigDecimal montoPorProyecto, Integer proyectosCompletados,
                       LocalDate fechaFinContrato, LocalDate fechaIngreso) {
        super(nombre, apellido, fechaNacimiento, numeroDocumento, fechaIngreso);
        this.montoPorProyecto = montoPorProyecto;
        this.proyectosCompletados = proyectosCompletados;
        this.fechaFinContrato = fechaFinContrato;
    }

    public Contratista(String nombre, String apellido, LocalDate fechaNacimiento, String numeroDocumento,
                       BigDecimal montoPorProyecto, Integer proyectosCompletados,
                       LocalDate fechaFinContrato, LocalDate fechaIngreso, String departamento) {
        super(nombre, apellido, fechaNacimiento, numeroDocumento, fechaIngreso);
        this.montoPorProyecto = montoPorProyecto;
        this.proyectosCompletados = proyectosCompletados;
        this.fechaFinContrato = fechaFinContrato;
        this.departamento = departamento;
    }

    @Override
    public BigDecimal calcularSalario() {
        BigDecimal total = montoPorProyecto.multiply(BigDecimal.valueOf(proyectosCompletados));
        log.debug("Salario contratista {} {} (depto: {}): {} × {} = {}", getNombre(), getApellido(), departamento, montoPorProyecto, proyectosCompletados, total);
        return total;
    }

    @Override
    public BigDecimal calcularDeducciones() {
        log.debug("Deducciones para contratista {} {}: 0%", getNombre(), getApellido());
        return BigDecimal.ZERO;
    }

    @Override
    public boolean validarDatosEspecificos() {
        boolean valido = montoPorProyecto != null && montoPorProyecto.compareTo(BigDecimal.ZERO) > 0
                && proyectosCompletados != null && proyectosCompletados >= 0
                && fechaFinContrato != null && fechaFinContrato.isAfter(LocalDate.now())
                && getFechaIngreso() != null && !getFechaIngreso().isAfter(LocalDate.now());
        log.debug("Validación específica (contratista): {}", valido ? "OK" : "FALLIDA");
        return valido;
    }

    @Override
    public String obtenerInformacionCompleta() {
        StringBuilder info = new StringBuilder(super.obtenerInformacionCompleta());
        info.append(", Monto por Proyecto: ").append(montoPorProyecto);
        info.append(", Proyectos Completados: ").append(proyectosCompletados);
        info.append(", Fecha Fin Contrato: ").append(fechaFinContrato);
        info.append(", Departamento: ").append(departamento);
        return info.toString();
    }

    public boolean contratoVigente() {
        boolean vigente = fechaFinContrato != null && fechaFinContrato.isAfter(LocalDate.now());
        log.debug("Contrato vigente para {} {}: {}", getNombre(), getApellido(), vigente);
        return vigente;
    }
}