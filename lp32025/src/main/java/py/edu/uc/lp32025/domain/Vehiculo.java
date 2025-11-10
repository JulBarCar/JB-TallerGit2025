package py.edu.uc.lp32025.domain;

public class Vehiculo implements Mapeable {
    private String marca;
    private String modelo;
    private String patente;
    private Double latitud;
    private Double longitud;
    private byte[] imagen;
    private String nick;

    // Constructor vacío
    public Vehiculo() {}

    // Constructor completo
    public Vehiculo(String marca, String modelo, String patente, Double latitud, Double longitud, String nick, byte[] imagen) {
        this.marca = marca;
        this.modelo = modelo;
        this.patente = patente;
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
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getPatente() { return patente; }
    public void setPatente(String patente) { this.patente = patente; }

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
        return "Vehiculo{" +
                "marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", patente='" + patente + '\'' +
                '}';
    }
}