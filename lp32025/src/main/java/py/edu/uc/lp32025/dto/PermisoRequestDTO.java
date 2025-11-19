package py.edu.uc.lp32025.dto;

import java.time.LocalDate;

public class PermisoRequestDTO {

    private LocalDate fecha;
    private String motivo;

    public PermisoRequestDTO() {}

    public PermisoRequestDTO(LocalDate fecha, String motivo) {
        this.fecha = fecha;
        this.motivo = motivo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}