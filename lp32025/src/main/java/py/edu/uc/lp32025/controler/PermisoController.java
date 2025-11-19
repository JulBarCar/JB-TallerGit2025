package py.edu.uc.lp32025.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp32025.domain.Empleado;
import py.edu.uc.lp32025.domain.Gerente;
import py.edu.uc.lp32025.dto.PermisoRequestDTO;
import py.edu.uc.lp32025.dto.SolicitudResponseDTO;
import py.edu.uc.lp32025.dto.VacacionRequestDTO;
import py.edu.uc.lp32025.exception.EmpleadoNoEncontradoException;
import py.edu.uc.lp32025.exception.PermisoDenegadoException;
import py.edu.uc.lp32025.service.PersonaService;

@RestController
@RequestMapping("/api/permisos")
public class PermisoController {

    private final PersonaService personaService;

    @Autowired
    public PermisoController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @PostMapping("/empleados/{id}/vacaciones/solicitar")
    public ResponseEntity<SolicitudResponseDTO> solicitarVacaciones(
            @PathVariable Long id, @RequestBody VacacionRequestDTO request)
            throws PermisoDenegadoException {
        Empleado empleado = (Empleado) personaService.getPersonaById(id);
        boolean aprobado = empleado.solicitarVacaciones(request.getFechaInicio(), request.getFechaFin());
        int dias = empleado.contarDiasHabiles(request.getFechaInicio(), request.getFechaFin());
        SolicitudResponseDTO response = new SolicitudResponseDTO("Vacaciones aprobadas", dias);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/empleados/{id}/permisos/solicitar")
    public ResponseEntity<SolicitudResponseDTO> solicitarPermiso(
            @PathVariable Long id, @RequestBody PermisoRequestDTO request)
            throws PermisoDenegadoException {
        Empleado empleado = (Empleado) personaService.getPersonaById(id);
        boolean aprobado = empleado.solicitarPermiso(request.getFecha(), request.getMotivo());
        SolicitudResponseDTO response = new SolicitudResponseDTO("Permiso aprobado", 1);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/gerentes/{gerenteId}/aprobar-permiso/{empleadoId}")
    public ResponseEntity<SolicitudResponseDTO> aprobarPermiso(
            @PathVariable Long gerenteId, @PathVariable Long empleadoId,
            @RequestBody PermisoRequestDTO request, @RequestParam boolean aprobar)
            throws PermisoDenegadoException {
        Gerente gerente = (Gerente) personaService.getPersonaById(gerenteId);
        boolean procesado = gerente.aprobarPermisoDeSubordinado(empleadoId, request.getFecha(), request.getMotivo(), aprobar);
        String msg = aprobar ? "Permiso aprobado" : "Permiso rechazado";
        SolicitudResponseDTO response = new SolicitudResponseDTO(msg, 1);
        return ResponseEntity.ok(response);
    }
}