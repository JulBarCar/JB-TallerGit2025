package py.edu.uc.lp32025.domain;

public class Avatar {
    private byte[] imagen;
    private String nick;

    public Avatar() {}

    public Avatar(byte[] imagen, String nick) {
        this.imagen = imagen;
        this.nick = nick;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}