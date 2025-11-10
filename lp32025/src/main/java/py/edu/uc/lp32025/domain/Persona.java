package py.edu.uc.lp32025.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "persona")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Persona implements Mapeable {

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

    // === NUEVOS CAMPOS PARA MAPEABLE ===
    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    @Lob
    @Column(name = "imagen")
    private byte[] imagen;

    @Column(name = "nick")
    private String nick;

    public Persona() {
    }

    public Persona(String nombre, String apellido, LocalDate fechaNacimiento, String numeroDocumento) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.numeroDocumento = numeroDocumento;
    }

    public abstract BigDecimal calcularSalario();

    public String obtenerInformacionCompleta() {
        return "Nombre: " + nombre + " " + apellido + ", Documento: " + numeroDocumento +
                (fechaNacimiento != null ? ", Fecha de Nacimiento: " + fechaNacimiento : "");
    }

    public final BigDecimal calcularImpuestos() {
        BigDecimal salario = calcularSalario();
        BigDecimal impuestoBase = calcularImpuestoBase(salario);
        BigDecimal deducciones = calcularDeducciones();
        return impuestoBase.subtract(deducciones).max(BigDecimal.ZERO);
    }

    public BigDecimal calcularImpuestoBase(BigDecimal salario) {
        return salario.multiply(new BigDecimal("0.10"));
    }

    protected abstract BigDecimal calcularDeducciones();

    public abstract boolean validarDatosEspecificos();

    // === IMPLEMENTACIÓN DE Mapeable ===
    @Override
    public PosicionGPS ubicarElemento() {
        if (latitud == null || longitud == null) {
            return null;
        }
        return new PosicionGPS(latitud, longitud);
    }

    @Override
    public Avatar obtenerAvatar() {
        if (imagen == null && nick == null) {
            return null;
        }
        return new Avatar(imagen, nick);
    }

    // === GETTERS Y SETTERS (incluyendo nuevos) ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public byte[] getImagen() { return imagen; }
    public void setImagen(byte[] imagen) { this.imagen = imagen; }

    public String getNick() { return nick; }
    public void setNick(String nick) { this.nick = nick; }
}