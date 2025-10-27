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

    // --- CUMPLE: Tarifa × horas + bonus 50% por horas extra (>40) ---
    @Override
    public BigDecimal calcularSalario() {
        BigDecimal salarioBase = tarifaPorHora.multiply(BigDecimal.valueOf(horasTrabajadas));
        int horasExtra = Math.max(horasTrabajadas - 40, 0);
        BigDecimal bonus = tarifaPorHora.multiply(new BigDecimal("0.50")).multiply(BigDecimal.valueOf(horasExtra));
        return salarioBase.add(bonus);
    }

    // --- CUMPLE: 2% del salario total ---
    @Override
    public BigDecimal calcularDeducciones() {
        return calcularSalario().multiply(new BigDecimal("0.02"));
    }

    // --- CUMPLE: Tarifa > 0, horas entre 1 y 80 ---
    @Override
    public boolean validarDatosEspecificos() {
        return tarifaPorHora != null
                && tarifaPorHora.compareTo(BigDecimal.ZERO) > 0
                && horasTrabajadas != null
                && horasTrabajadas >= 1
                && horasTrabajadas <= 80;
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