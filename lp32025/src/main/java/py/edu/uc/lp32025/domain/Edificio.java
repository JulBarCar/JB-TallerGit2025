package py.edu.uc.lp32025.domain;

public class Edificio implements Mapeable {
    private String nombre;
    private String direccion;
    private Integer pisos;
    private Double latitud;
    private Double longitud;
    private byte[] imagen;
    private String nick;

    // Constructor vacío
    public Edificio() {}

    // Constructor completo
    public Edificio(String nombre, String direccion, Integer pisos, Double latitud, Double longitud, String nick, byte[] imagen) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.pisos = pisos;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nick = nick;
        this.imagen = imagen;
    }

    // === IMPLEMENTACIÓN DE Mapeable ===
    @Override
    public PosicionGPS ubicarElemento() {
        if (latitud == null || longitud == null) return null;
        return new PosicionGPS(latitud, longitud);
    }

    @Override
    public Avatar obtenerAvatar() {
        if (nick == null && imagen == null) return null;
        return new Avatar(imagen, nick);
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Integer getPisos() { return pisos; }
    public void setPisos(Integer pisos) { this.pisos = pisos; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public byte[] getImagen() { return imagen; }
    public void setImagen(byte[] imagen) { this.imagen = imagen; }

    public String getNick() { return nick; }
    public void setNick(String nick) { this.nick = nick; }

    @Override
    public String toString() {
        return "Edificio{" +
                "nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", pisos=" + pisos +
                '}';
    }
}