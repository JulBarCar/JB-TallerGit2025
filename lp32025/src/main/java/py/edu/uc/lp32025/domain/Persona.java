package py.edu.uc.lp32025.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "persona")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "numero_documento", unique = true, nullable = false)
    private String numeroDocumento;

    public Persona() {
    }

    public Persona(String nombre, String apellido, LocalDate fechaNacimiento, String numeroDocumento) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.numeroDocumento = numeroDocumento;
    }

    // Método abstracto para calcular el salario
    public abstract BigDecimal calcularSalario();

    // Método concreto que puede ser sobrescrito
    public String obtenerInformacionCompleta() {
        return "Nombre: " + nombre + " " + apellido + ", Documento: " + numeroDocumento +
                (fechaNacimiento != null ? ", Fecha de Nacimiento: " + fechaNacimiento : "");
    }

    // Método template para calcular impuestos
    public final BigDecimal calcularImpuestos() {
        BigDecimal salario = calcularSalario();
        BigDecimal impuestoBase = calcularImpuestoBase(salario);
        BigDecimal deducciones = calcularDeducciones();
        return impuestoBase.subtract(deducciones).max(BigDecimal.ZERO); // No retorna valores negativos
    }

    // Método concreto para calcular el 10% del salario
    public BigDecimal calcularImpuestoBase(BigDecimal salario) {
        return salario.multiply(new BigDecimal("0.10"));
    }

    // Método abstracto para calcular deducciones específicas
    protected abstract BigDecimal calcularDeducciones();

    // Método abstracto para validar datos específicos de la subclase
    public abstract boolean validarDatosEspecificos();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }
}