package py.edu.uc.lp32025.dto;

import java.time.LocalDate;

public class VacacionRequestDTO {

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public VacacionRequestDTO() {}

    public VacacionRequestDTO(LocalDate fechaInicio, LocalDate fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
}