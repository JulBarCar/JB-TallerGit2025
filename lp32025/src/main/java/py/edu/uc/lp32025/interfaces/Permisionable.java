package py.edu.uc.lp32025.interfaces;

import java.time.LocalDate;
import py.edu.uc.lp32025.exception.DiasInsuficientesException;
import py.edu.uc.lp32025.exception.PermisoDenegadoException;

/**
 * Interfaz que define el comportamiento para solicitar vacaciones o días de permiso
 * conforme a la legislación laboral de Paraguay (Código del Trabajo, Ley 729/61 y modificatorias).
 */
public interface Permisionable {

    /**
     * Solicita vacaciones para el período indicado.
     *
     * @param fechaInicio fecha de inicio de las vacaciones (inclusive)
     * @param fechaFin    fecha de fin de las vacaciones (inclusive)
     * @return true si las vacaciones fueron concedidas
     * @throws PermisoDenegadoException si no cumple con los requisitos legales
     * @throws DiasInsuficientesException si no hay suficientes días disponibles o se excede límite
     */
    boolean solicitarVacaciones(LocalDate fechaInicio, LocalDate fechaFin)
            throws PermisoDenegadoException, DiasInsuficientesException;

    /**
     * Solicita un día de permiso por motivo específico.
     *
     * @param fecha   fecha del permiso
     * @param motivo  motivo del permiso
     * @return true si el permiso fue concedido
     * @throws PermisoDenegadoException si el motivo no está contemplado o no cumple antigüedad
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

    /**
     * <strong>Método exclusivo para gerentes:</strong> Aprueba o rechaza una solicitud
     * de permiso realizada por un subordinado.
     *
     * @param empleadoId   ID del empleado que solicita el permiso
     * @param fecha        fecha del permiso solicitado
     * @param motivo       motivo del permiso
     * @param aprobar      true para aprobar, false para rechazar
     * @return true si la operación fue registrada con éxito
     * @throws PermisoDenegadoException si el gerente no tiene autoridad o el motivo no es válido
     */
    default boolean aprobarPermisoDeSubordinado(Long empleadoId, LocalDate fecha, String motivo, boolean aprobar)
            throws PermisoDenegadoException {
        throw new UnsupportedOperationException("Solo gerentes pueden aprobar permisos de subordinados.");
    }
}