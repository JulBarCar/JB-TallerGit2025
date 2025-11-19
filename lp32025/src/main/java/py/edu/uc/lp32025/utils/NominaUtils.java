package py.edu.uc.lp32025.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import py.edu.uc.lp32025.interfaces.Permisionable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase utilitaria con métodos estáticos para cálculos de nómina relacionados
 * con días de vacaciones y permisos.
 */
public class NominaUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    private NominaUtils() {
        // Clase utilitaria: no instanciable
    }

    /**
     * Calcula el total de días solicitados (vacaciones + permisos) por todos los empleados.
     * Simulación: días solicitados = (máximo teórico - disponibles) + permisos usados.
     *
     * @param empleados Lista de objetos que implementan Permisionable
     * @return Total de días solicitados
     */
    public static int calcularTotalDiasSolicitados(List<Permisionable> empleados) {
        if (empleados == null || empleados.isEmpty()) {
            return 0;
        }

        int total = 0;
        for (Permisionable emp : empleados) {
            int diasVacacionesDisponibles = emp.getDiasVacacionesDisponibles();
            int diasPermisoUsados = emp.getDiasPermisoUtilizadosEsteAnio();

            // Simulación: asumimos un máximo de 45 días de vacaciones para gerentes, 30 para otros
            int maxVacaciones = emp instanceof py.edu.uc.lp32025.domain.Gerente ? 45 : 30;
            int diasVacacionesSolicitados = maxVacaciones - diasVacacionesDisponibles;

            total += diasVacacionesSolicitados + diasPermisoUsados;
        }
        return total;
    }

    /**
     * Genera un reporte JSON de empleados que han solicitado más de un umbral de días.
     *
     * @param umbral    Número mínimo de días para incluir en el reporte
     * @param empleados Lista de empleados
     * @return JSON String con el reporte
     * @throws RuntimeException si falla la serialización JSON
     */
    public static String generarReporteJson(int umbral, List<Permisionable> empleados) {
        if (empleados == null || empleados.isEmpty()) {
            return "[]";
        }

        List<Map<String, Object>> reporte = new ArrayList<>();

        for (Permisionable emp : empleados) {
            String tipo = emp.getClass().getSimpleName();
            String nombreCompleto = getNombreCompleto(emp);

            int diasVacacionesDisponibles = emp.getDiasVacacionesDisponibles();
            int diasPermisoUsados = emp.getDiasPermisoUtilizadosEsteAnio();
            int maxVacaciones = emp instanceof py.edu.uc.lp32025.domain.Gerente ? 45 : 30;
            int diasVacacionesSolicitados = maxVacaciones - diasVacacionesDisponibles;
            int totalSolicitados = diasVacacionesSolicitados + diasPermisoUsados;

            if (totalSolicitados > umbral) {
                Map<String, Object> entrada = new HashMap<>();
                entrada.put("id", getId(emp));
                entrada.put("nombreCompleto", nombreCompleto);
                entrada.put("tipo", tipo);
                entrada.put("diasSolicitados", totalSolicitados);
                reporte.add(entrada);
            }
        }

        try {
            return mapper.writeValueAsString(reporte);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al generar JSON del reporte", e);
        }
    }

    // === MÉTODOS AUXILIARES ===

    private static String getNombreCompleto(Permisionable emp) {
        if (emp instanceof py.edu.uc.lp32025.domain.Persona p) {
            return p.getNombre() + " " + p.getApellido();
        }
        return "Desconocido";
    }

    private static Long getId(Permisionable emp) {
        if (emp instanceof py.edu.uc.lp32025.domain.Persona p) {
            return p.getId();
        }
        return null;
    }
}