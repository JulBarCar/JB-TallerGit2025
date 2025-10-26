package py.edu.uc.lp32025.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "empleado_por_hora")
@PrimaryKeyJoinColumn(name = "persona_id")
public class EmpleadoPorHora extends Persona {

    @Column(name = "tarifa_por_hora", nullable = false)
    private BigDecimal tarifaPorHora;

    @Column(name = "horas_trabajadas", nullable = false)
    private Integer horasTrabajadas;

    public EmpleadoPorHora() {
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

    public EmpleadoPorHora(String nombre, String apellido, LocalDate fechaNacimiento, String numeroDocumento,
                           BigDecimal tarifaPorHora, Integer horasTrabajadas) {
        super(nombre, apellido, fechaNacimiento, numeroDocumento);
        this.tarifaPorHora = tarifaPorHora;
        this.horasTrabajadas = horasTrabajadas;
    }

    public BigDecimal getTarifaPorHora() {
        return tarifaPorHora;
    }

    public void setTarifaPorHora(BigDecimal tarifaPorHora) {
        this.tarifaPorHora = tarifaPorHora;
    }

    public Integer getHorasTrabajadas() {
        return horasTrabajadas;
    }

    public void setHorasTrabajadas(Integer horasTrabajadas) {
        this.horasTrabajadas = horasTrabajadas;
    }
}