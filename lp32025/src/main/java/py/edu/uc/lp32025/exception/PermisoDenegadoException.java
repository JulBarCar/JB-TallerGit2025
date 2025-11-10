package py.edu.uc.lp32025.exception;

import java.time.LocalDate;
import lombok.Getter;

/**
 * Excepción personalizada lanzada cuando una solicitud de vacaciones o permiso
 * no puede ser concedida por incumplimiento de la normativa paraguaya.
 */
@Getter
public class PermisoDenegadoException extends Exception {

    private final String motivo;
    private final LocalDate fechaSolicitada;
    private final String detalleNormativa;

    public PermisoDenegadoException(String motivo, LocalDate fechaSolicitada, String detalleNormativa) {
        super(String.format("Permiso denegado: %s (fecha: %s) - %s", motivo, fechaSolicitada, detalleNormativa));
        this.motivo = motivo;
        this.fechaSolicitada = fechaSolicitada;
        this.detalleNormativa = detalleNormativa;
    }
}