package py.edu.uc.lp32025.interfaces;

import java.time.LocalDate;
import py.edu.uc.lp32025.exception.PermisoDenegadoException;

/**
 * Interfaz que define el comportamiento para solicitar vacaciones o días de permiso
 * conforme a la legislación laboral de Paraguay (Código del Trabajo, Ley 729/61 y modificatorias).
 *
 * <p>Requisitos legales básicos (simplificados):</p>
 * <ul>
 *   <li>Vacaciones: 12 días hábiles continuos después de 1 año de servicio,
 *       18 días después de 5 años, 30 días después de 10 años.</li>
 *   <li>Días de permiso: justificados (matrimonio, nacimiento, fallecimiento, etc.)
 *       con tope según convenio.</li>
 * </ul>
 */
public interface Permisionable {

    /**
     * Solicita vacaciones para el período indicado.
     *
     * @param fechaInicio fecha de inicio de las vacaciones (inclusive)
     * @param fechaFin    fecha de fin de las vacaciones (inclusive)
     * @return true si las vacaciones fueron concedidas
     * @throws PermisoDenegadoException si no cumple con los requisitos legales
     *                                  o el saldo de días es insuficiente
     */
    boolean solicitarVacaciones(LocalDate fechaInicio, LocalDate fechaFin)
            throws PermisoDenegadoException;

    /**
     * Solicita un día de permiso por motivo específico.
     *
     * @param fecha   fecha del permiso
     * @param motivo  motivo del permiso (ej. "MATRIMONIO", "NACIMIENTO_HIJO", "FALLECIMIENTO_FAMILIAR")
     * @return true si el permiso fue concedido
     * @throws PermisoDenegadoException si el motivo no está contemplado,
     *                                  supera el límite anual o no cumple antigüedad
     */
    boolean solicitarPermiso(LocalDate fecha, String motivo)
            throws PermisoDenegadoException;

    /**
     * Consulta los días de vacaciones disponibles según antigüedad.
     *
     * @return cantidad de días hábiles de vacaciones pendientes
     */
    int getDiasVacacionesDisponibles();

    /**
     * Consulta los días de permiso utilizados en el año calendario actual.
     *
     * @return cantidad de días de permiso consumidos
     */
    int getDiasPermisoUtilizadosEsteAnio();
}