package py.edu.uc.lp32025.dto;

public class SolicitudResponseDTO extends AbstractResponseDTO {

    private String mensaje;
    private int diasSolicitados;

    public SolicitudResponseDTO() {
        super(200, null, null);
    }

    public SolicitudResponseDTO(String mensaje, int diasSolicitados) {
        super(200, null, null);
        this.mensaje = mensaje;
        this.diasSolicitados = diasSolicitados;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getDiasSolicitados() {
        return diasSolicitados;
    }

    public void setDiasSolicitados(int diasSolicitados) {
        this.diasSolicitados = diasSolicitados;
    }
}