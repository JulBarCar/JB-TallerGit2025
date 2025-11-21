package py.edu.uc.lp32025.controler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp32025.domain.Empleado;
import py.edu.uc.lp32025.domain.Persona;
import py.edu.uc.lp32025.dto.SolicitudResponseDTO;
import py.edu.uc.lp32025.dto.VacacionRequestDTO;
import py.edu.uc.lp32025.exception.DiasInsuficientesException;
import py.edu.uc.lp32025.exception.EmpleadoNoEncontradoException;
import py.edu.uc.lp32025.exception.PermisoDenegadoException;
import py.edu.uc.lp32025.interfaces.Permisionable;
import py.edu.uc.lp32025.service.PersonaService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    // ← Este es el servicio que resuelve cualquier tipo de empleado (polimorfismo)
    private final PersonaService personaService;

    // ==================== NUEVO ENDPOINT: DÍAS DISPONIBLES ====================
    @GetMapping("/{id}/vacaciones/disponibles")
    public ResponseEntity<Map<String, Object>> getDiasVacacionesDisponibles(@PathVariable Long id) {
        Persona persona = personaService.getPersonaById(id);

        if (!(persona instanceof Permisionable empleado)) {
            throw new IllegalArgumentException("Este tipo de persona no tiene vacaciones asignadas");
        }

        int diasDisponibles = empleado.getDiasVacacionesDisponibles();

        Map<String, Object> response = new HashMap<>();
        response.put("empleadoId", id);
        response.put("nombreCompleto", persona.getNombre() + " " + persona.getApellido());
        response.put("tipoEmpleado", persona.getClass().getSimpleName());
        response.put("diasDisponibles", diasDisponibles);
        response.put("mensaje", "Días de vacaciones pendientes según antigüedad y uso actual");

        return ResponseEntity.ok(response);
    }

    // ==================== OTROS ENDPOINTS QUE YA TENÍAS (ejemplo) ====================
    // (Dejamos los que ya tenías para que no se rompa nada)
    @PostMapping("/{id}/vacaciones/solicitar")
    public ResponseEntity<SolicitudResponseDTO> solicitarVacaciones(
            @PathVariable Long id,
            @RequestBody VacacionRequestDTO request) throws PermisoDenegadoException, DiasInsuficientesException {

        Persona persona = personaService.getPersonaById(id);
        if (!(persona instanceof Permisionable empleado)) {
            throw new EmpleadoNoEncontradoException(id);
        }

        boolean aprobado = empleado.solicitarVacaciones(request.getFechaInicio(), request.getFechaFin());
        int dias = empleado.getDiasVacacionesDisponibles(); // después de la solicitud

        SolicitudResponseDTO response = new SolicitudResponseDTO(
                aprobado ? "Vacaciones aprobadas correctamente" : "Vacaciones registradas (pendiente aprobación)",
                request.getFechaInicio().until(request.getFechaFin()).getDays() + 1
        );
        return ResponseEntity.status(aprobado ? 201 : 202).body(response);
    }

    // Puedes agregar más endpoints aquí (permisos, etc.)
}