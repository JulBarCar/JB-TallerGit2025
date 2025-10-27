package py.edu.uc.lp32025.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contratista")
@PrimaryKeyJoinColumn(name = "persona_id")
public class Contratista extends Persona {

    @Column(name = "monto_por_proyecto", nullable = false)
    private BigDecimal montoPorProyecto;

    @Column(name = "proyectos_completados", nullable = false)
    private Integer proyectosCompletados;

    @Column(name = "fecha_fin_contrato", nullable = false)
    private LocalDate fechaFinContrato;

    public Contratista() {
    }

    public Contratista(String nombre, String apellido, LocalDate fechaNacimiento, String numeroDocumento,
                       BigDecimal montoPorProyecto, Integer proyectosCompletados, LocalDate fechaFinContrato) {
        super(nombre, apellido, fechaNacimiento, numeroDocumento);
        this.montoPorProyecto = montoPorProyecto;
        this.proyectosCompletados = proyectosCompletados;
        this.fechaFinContrato = fechaFinContrato;
    }

    // --- CUMPLE: monto × proyectos ---
    @Override
    public BigDecimal calcularSalario() {
        return montoPorProyecto.multiply(BigDecimal.valueOf(proyectosCompletados));
    }

    // --- CUMPLE: 0% deducciones ---
    @Override
    public BigDecimal calcularDeducciones() {
        return BigDecimal.ZERO;
    }

    // --- CUMPLE: monto > 0, proyectos >= 0, fecha futura ---
    @Override
    public boolean validarDatosEspecificos() {
        return montoPorProyecto != null
                && montoPorProyecto.compareTo(BigDecimal.ZERO) > 0
                && proyectosCompletados != null
                && proyectosCompletados >= 0
                && fechaFinContrato != null
                && fechaFinContrato.isAfter(LocalDate.now());
    }

    // --- CUMPLE: Extiende base + fecha fin contrato ---
    @Override
    public String obtenerInformacionCompleta() {
        StringBuilder info = new StringBuilder(super.obtenerInformacionCompleta());
        info.append(", Monto por Proyecto: ").append(montoPorProyecto);
        info.append(", Proyectos Completados: ").append(proyectosCompletados);
        info.append(", Fecha Fin Contrato: ").append(fechaFinContrato);
        return info.toString();
    }

    // --- NUEVO MÉTODO: Verifica si el contrato está vigente ---
    public boolean contratoVigente() {
        return fechaFinContrato != null && fechaFinContrato.isAfter(LocalDate.now());
    }

    // Getters y Setters
    public BigDecimal getMontoPorProyecto() {
        return montoPorProyecto;
    }

    public void setMontoPorProyecto(BigDecimal montoPorProyecto) {
        this.montoPorProyecto = montoPorProyecto;
    }

    public Integer getProyectosCompletados() {
        return proyectosCompletados;
    }

    public void setProyectosCompletados(Integer proyectosCompletados) {
        this.proyectosCompletados = proyectosCompletados;
    }

    public LocalDate getFechaFinContrato() {
        return fechaFinContrato;
    }

    public void setFechaFinContrato(LocalDate fechaFinContrato) {
        this.fechaFinContrato = fechaFinContrato;
    }
}