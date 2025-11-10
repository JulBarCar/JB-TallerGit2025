package py.edu.uc.lp32025.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "empleado_tiempo_completo")
@PrimaryKeyJoinColumn(name = "persona_id")
@Getter
@Setter
@Slf4j
public class EmpleadoTiempoCompleto extends Empleado {

    @Column(name = "salario_mensual", nullable = false)
    private BigDecimal salarioMensual;

    @Column(name = "bono_anual", nullable = false)
    private BigDecimal bonoAnual;

    @Column(name = "departamento")
    private String departamento;

    public EmpleadoTiempoCompleto() {
    }

    public EmpleadoTiempoCompleto(String nombre, String apellido, LocalDate fechaNacimiento,
                                  String numeroDocumento, BigDecimal salarioMensual,
                                  BigDecimal bonoAnual, LocalDate fechaIngreso) {
        super(nombre, apellido, fechaNacimiento, numeroDocumento, fechaIngreso);
        this.salarioMensual = salarioMensual;
        this.bonoAnual = bonoAnual;
    }

    public EmpleadoTiempoCompleto(String nombre, String apellido, LocalDate fechaNacimiento,
                                  String numeroDocumento, BigDecimal salarioMensual,
                                  BigDecimal bonoAnual, LocalDate fechaIngreso, String departamento) {
        super(nombre, apellido, fechaNacimiento, numeroDocumento, fechaIngreso);
        this.salarioMensual = salarioMensual;
        this.bonoAnual = bonoAnual;
        this.departamento = departamento;
    }

    @Override
    public BigDecimal calcularSalario() {
        log.debug("Calculando salario para {} {} (tiempo completo, depto: {})", getNombre(), getApellido(), departamento);
        return salarioMensual.add(bonoAnual);
    }

    @Override
    public BigDecimal calcularDeducciones() {
        BigDecimal salario = calcularSalario();
        log.debug("Deducciones 5% sobre salario {} = {}", salario, salario.multiply(new BigDecimal("0.05")));
        return salario.multiply(new BigDecimal("0.05"));
    }

    @Override
    public boolean validarDatosEspecificos() {
        boolean valido = salarioMensual != null && salarioMensual.compareTo(BigDecimal.ZERO) > 0
                && bonoAnual != null && bonoAnual.compareTo(BigDecimal.ZERO) >= 0
                && getFechaIngreso() != null && !getFechaIngreso().isAfter(LocalDate.now());
        log.debug("Validación específica (tiempo completo): {}", valido ? "OK" : "FALLIDA");
        return valido;
    }
}