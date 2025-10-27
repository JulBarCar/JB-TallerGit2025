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

    // --- CUMPLE: Retorna el salario mensual (sin descuento) ---
    @Override
    public BigDecimal calcularSalario() {
        return salarioMensual;
    }

    // --- CUMPLE: Extiende información base con salario y departamento ---
    @Override
    public String obtenerInformacionCompleta() {
        StringBuilder info = new StringBuilder(super.obtenerInformacionCompleta());
        info.append(", Salario Mensual: ").append(salarioMensual);
        info.append(", Departamento: ").append(departamento);
        return info.toString();
    }

    // --- CUMPLE: 5% si es "IT", 3% para otros ---
    @Override
    public BigDecimal calcularDeducciones() {
        if (departamento != null && "IT".equalsIgnoreCase(departamento.trim())) {
            return salarioMensual.multiply(new BigDecimal("0.05"));
        } else {
            return salarioMensual.multiply(new BigDecimal("0.03"));
        }
    }

    // --- CUMPLE: salario > 0 y departamento no vacío ---
    @Override
    public boolean validarDatosEspecificos() {
        return salarioMensual != null
                && salarioMensual.compareTo(BigDecimal.ZERO) > 0
                && departamento != null
                && !departamento.trim().isEmpty();
    }

    // Getters y Setters
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