package py.edu.uc.lp32025.exception;

import java.time.LocalDate;

/**
 * Checked Exception lanzada cuando un empleado intenta solicitar
 * más días de vacaciones o permisos de los que tiene disponibles o permitidos.
 */
public class DiasInsuficientesException extends Exception {

    private final String motivo;
    private final LocalDate fechaReferencia;
    private final int diasSolicitados;
    private final int diasDisponibles;

    public DiasInsuficientesException(String motivo, LocalDate fechaReferencia,
                                      int diasSolicitados, int diasDisponibles) {
        super(String.format("Días insuficientes: %s (Solicitados: %d, Disponibles: %d, Fecha: %s)",
                motivo, diasSolicitados, diasDisponibles, fechaReferencia));
        this.motivo = motivo;
        this.fechaReferencia = fechaReferencia;
        this.diasSolicitados = diasSolicitados;
        this.diasDisponibles = diasDisponibles;
    }

    public String getMotivo() { return motivo; }
    public LocalDate getFechaReferencia() { return fechaReferencia; }
    public int getDiasSolicitados() { return diasSolicitados; }
    public int getDiasDisponibles() { return diasDisponibles; }
}