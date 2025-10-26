package py.edu.uc.lp32025.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "empleado_tiempo_completo")
@PrimaryKeyJoinColumn(name = "persona_id")
public class EmpleadoTiempoCompleto extends Persona {

    @Column(name = "salario_mensual", nullable = false)
    private BigDecimal salarioMensual;

    @Column(nullable = false)
    private String departamento;

    public EmpleadoTiempoCompleto() {
    }

    public EmpleadoTiempoCompleto(String nombre, String apellido, LocalDate fechaNacimiento, String numeroDocumento,
                                  BigDecimal salarioMensual, String departamento) {
        super(nombre, apellido, fechaNacimiento, numeroDocumento);
        this.salarioMensual = salarioMensual;
        this.departamento = departamento;
    }

    @Override
    public BigDecimal calcularSalario() {
        // Aplica un descuento del 9% al salario mensual
        return salarioMensual.multiply(new BigDecimal("0.91"));
    }

    @Override
    public String obtenerInformacionCompleta() {
        StringBuilder info = new StringBuilder(super.obtenerInformacionCompleta());
        info.append(", Salario Mensual: ").append(salarioMensual);
        info.append(", Departamento: ").append(departamento);
        return info.toString();
    }

    @Override
    public BigDecimal calcularDeducciones() {
        // Deducción del 5% del salario mensual
        return salarioMensual.multiply(new BigDecimal("0.05"));
    }

    @Override
    public boolean validarDatosEspecificos() {
        return salarioMensual != null &&
                salarioMensual.compareTo(new BigDecimal("2899048")) >= 0 &&
                departamento != null && !departamento.trim().isEmpty();
    }

    public BigDecimal getSalarioMensual() {
        return salarioMensual;
    }

    public void setSalarioMensual(BigDecimal salarioMensual) {
        this.salarioMensual = salarioMensual;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
}