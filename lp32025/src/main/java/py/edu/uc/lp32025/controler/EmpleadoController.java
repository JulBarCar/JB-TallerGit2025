package py.edu.uc.lp32025.controler;

import py.edu.uc.lp32025.domain.Empleado;
import py.edu.uc.lp32025.dto.*;
import py.edu.uc.lp32025.exception.PermisoDenegadoException;
import py.edu.uc.lp32025.mapper.EmpleadoMapper;
import py.edu.uc.lp32025.service.PersonaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController extends BaseEmpleadoController<Empleado> {

    private final EmpleadoMapper empleadoMapper;

    public EmpleadoController(PersonaService personaService, EmpleadoMapper empleadoMapper) {
        super(personaService);
        this.empleadoMapper = empleadoMapper;
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ReporteEmpleadoDto> getById(@PathVariable Long id) {
        Empleado empleado = (Empleado) personaService.getPersonaById(id);
        ReporteEmpleadoDto dto = empleadoMapper.toReporteDto(empleado);
        return ResponseEntity.ok(dto);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ReporteEmpleadoDto> update(@PathVariable Long id, @RequestBody Empleado empleado) {
        empleado.setId(id);
        Empleado updated = (Empleado) personaService.updatePersona(id, empleado);
        ReporteEmpleadoDto dto = empleadoMapper.toReporteDto(updated);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<ReporteEmpleadoDto>> getAll() {
        List<Empleado> empleados = personaService.getAllPersonas()
                .stream()
                .filter(p -> p instanceof Empleado)
                .map(p -> (Empleado) p)
                .toList();

        List<ReporteEmpleadoDto> dtos = empleados.stream()
                .map(empleadoMapper::toReporteDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{id}/vacaciones/solicitar")
    public ResponseEntity<SolicitudResponseDTO> solicitarVacaciones(
            @PathVariable Long id,
            @RequestBody VacacionRequestDTO request) throws PermisoDenegadoException {

        Empleado empleado = (Empleado) personaService.getPersonaById(id);
        boolean aprobado = empleado.solicitarVacaciones(request.getFechaInicio(), request.getFechaFin());
        int dias = empleado.contarDiasHabiles(request.getFechaInicio(), request.getFechaFin());

        SolicitudResponseDTO response = new SolicitudResponseDTO("Vacaciones aprobadas", dias);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/permisos/solicitar")
    public ResponseEntity<SolicitudResponseDTO> solicitarPermiso(
            @PathVariable Long id,
            @RequestBody PermisoRequestDTO request) throws PermisoDenegadoException {

        Empleado empleado = (Empleado) personaService.getPersonaById(id);
        boolean aprobado = empleado.solicitarPermiso(request.getFecha(), request.getMotivo());

        SolicitudResponseDTO response = new SolicitudResponseDTO("Permiso aprobado", 1);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}