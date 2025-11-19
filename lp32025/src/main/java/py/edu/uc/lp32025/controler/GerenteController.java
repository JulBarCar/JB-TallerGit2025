package py.edu.uc.lp32025.controler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import py.edu.uc.lp32025.domain.Gerente;
import py.edu.uc.lp32025.dto.PermisoRequestDTO;
import py.edu.uc.lp32025.dto.ReporteEmpleadoDto;
import py.edu.uc.lp32025.dto.SolicitudResponseDTO;
import py.edu.uc.lp32025.exception.PermisoDenegadoException;
import py.edu.uc.lp32025.mapper.GerenteMapper;
import py.edu.uc.lp32025.service.GerenteService;
import py.edu.uc.lp32025.service.PersonaService;

import java.util.List;

@RestController
@RequestMapping("/api/gerentes")
public class GerenteController extends BaseEmpleadoController<Gerente> {

    private final GerenteMapper gerenteMapper;
    private final GerenteService gerenteService;

    // Constructor con inyección correcta
    public GerenteController(PersonaService personaService,
                             GerenteMapper gerenteMapper,
                             GerenteService gerenteService) {
        super(personaService);
        this.gerenteMapper = gerenteMapper;
        this.gerenteService = gerenteService;
    }

    // GET: Listar todos los gerentes
    @GetMapping
    public ResponseEntity<List<ReporteEmpleadoDto>> getAll() {
        List<ReporteEmpleadoDto> dtos = gerenteService.findAll()
                .stream()
                .map(gerenteMapper::toReporteDto)  // Aquí usa el método específico para Gerente
                .toList();

        return ResponseEntity.ok(dtos);
    }

    // POST: Crear un gerente
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Gerente gerente) {
        try {
            Gerente saved = gerenteService.save(gerente);
            ReporteEmpleadoDto dto = gerenteMapper.toReporteDto(saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST: Crear varios gerentes en lote
    @PostMapping("/batch")
    public ResponseEntity<?> createBatch(@RequestBody List<Gerente> gerentes) {
        try {
            List<Gerente> saved = gerenteService.guardarGerentesEnBatch(gerentes);
            List<ReporteEmpleadoDto> dtos = saved.stream()
                    .map(gerenteMapper::toReporteDto)
                    .toList();
            return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET by ID (override para devolver DTO específico)
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ReporteEmpleadoDto> getById(@PathVariable Long id) {
        Gerente gerente = (Gerente) personaService.getPersonaById(id);
        ReporteEmpleadoDto dto = gerenteMapper.toReporteDto(gerente);
        return ResponseEntity.ok(dto);
    }

    // PUT: Actualizar gerente
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ReporteEmpleadoDto> update(@PathVariable Long id,
                                                     @RequestBody Gerente gerente) {
        gerente.setId(id);
        Gerente updated = (Gerente) personaService.updatePersona(id, gerente);
        ReporteEmpleadoDto dto = gerenteMapper.toReporteDto(updated);
        return ResponseEntity.ok(dto);
    }

    // APROBAR / RECHAZAR permiso de subordinado
    @PutMapping("/{gerenteId}/aprobar-permiso/{empleadoId}")
    public ResponseEntity<SolicitudResponseDTO> aprobarPermiso(
            @PathVariable Long gerenteId,
            @PathVariable Long empleadoId,
            @RequestBody PermisoRequestDTO request,
            @RequestParam boolean aprobar) throws PermisoDenegadoException {

        Gerente gerente = (Gerente) personaService.getPersonaById(gerenteId);

        boolean procesado = gerente.aprobarPermisoDeSubordinado(
                empleadoId,
                request.getFecha(),
                request.getMotivo(),
                aprobar
        );

        String mensaje = aprobar
                ? "Permiso aprobado exitosamente por el gerente"
                : "Permiso rechazado por el gerente";

        SolicitudResponseDTO response = new SolicitudResponseDTO(mensaje, 1);
        return ResponseEntity.ok(response);
    }
}