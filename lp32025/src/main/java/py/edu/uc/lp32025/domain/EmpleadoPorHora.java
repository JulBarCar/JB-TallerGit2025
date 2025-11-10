package py.edu.uc.lp32025.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "empleado_por_hora")
@PrimaryKeyJoinColumn(name = "persona_id")
@Getter
@Setter
@Slf4j
public class EmpleadoPorHora extends Empleado {

    @Column(name = "tarifa_por_hora", nullable = false)
    private BigDecimal tarifaPorHora;

    @Column(name = "horas_trabajadas", nullable = false)
    private Integer horasTrabajadas;

    @Column(name = "departamento")
    private String departamento;

    public EmpleadoPorHora() {
    }

    public EmpleadoPorHora(String nombre, String apellido, LocalDate fechaNacimiento, String numeroDocumento,
                           BigDecimal tarifaPorHora, Integer horasTrabajadas, LocalDate fechaIngreso) {
        super(nombre, apellido, fechaNacimiento, numeroDocumento, fechaIngreso);
        this.tarifaPorHora = tarifaPorHora;
        this.horasTrabajadas = horasTrabajadas;
    }

    public EmpleadoPorHora(String nombre, String apellido, LocalDate fechaNacimiento, String numeroDocumento,
                           BigDecimal tarifaPorHora, Integer horasTrabajadas, LocalDate fechaIngreso, String departamento) {
        super(nombre, apellido, fechaNacimiento, numeroDocumento, fechaIngreso);
        this.tarifaPorHora = tarifaPorHora;
        this.horasTrabajadas = horasTrabajadas;
        this.departamento = departamento;
    }

    @Override
    public BigDecimal calcularSalario() {
        BigDecimal salarioBase = tarifaPorHora.multiply(BigDecimal.valueOf(horasTrabajadas));
        int horasExtra = Math.max(horasTrabajadas - 40, 0);
        BigDecimal bonus = tarifaPorHora.multiply(new BigDecimal("0.50")).multiply(BigDecimal.valueOf(horasExtra));
        BigDecimal total = salarioBase.add(bonus);
        log.debug("Salario por hora para {} {} (depto: {}): base {} + bonus {} = {}", getNombre(), getApellido(), departamento, salarioBase, bonus, total);
        return total;
    }

    @Override
    public BigDecimal calcularDeducciones() {
        BigDecimal deduccion = calcularSalario().multiply(new BigDecimal("0.02"));
        log.debug("Deducciones 2% para {} {}: {}", getNombre(), getApellido(), deduccion);
        return deduccion;
    }

    @Override
    public boolean validarDatosEspecificos() {
        boolean valido = tarifaPorHora != null && tarifaPorHora.compareTo(BigDecimal.ZERO) > 0
                && horasTrabajadas != null && horasTrabajadas >= 1 && horasTrabajadas <= 80
                && getFechaIngreso() != null && !getFechaIngreso().isAfter(LocalDate.now());
        log.debug("Validación específica (por hora): {}", valido ? "OK" : "FALLIDA");
        return valido;
    }
}