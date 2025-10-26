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

    @Override
    public BigDecimal calcularSalario() {
        return null;
    }

    @Override
    protected BigDecimal calcularDeducciones() {
        return null;
    }

    @Override
    public boolean validarDatosEspecificos() {
        return false;
    }

    public Contratista(String nombre, String apellido, LocalDate fechaNacimiento, String numeroDocumento,
                       BigDecimal montoPorProyecto, Integer proyectosCompletados, LocalDate fechaFinContrato) {
        super(nombre, apellido, fechaNacimiento, numeroDocumento);
        this.montoPorProyecto = montoPorProyecto;
        this.proyectosCompletados = proyectosCompletados;
        this.fechaFinContrato = fechaFinContrato;
    }

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
