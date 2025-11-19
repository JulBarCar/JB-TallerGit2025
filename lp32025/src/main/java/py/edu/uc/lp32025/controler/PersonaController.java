package py.edu.uc.lp32025.controler;

import py.edu.uc.lp32025.domain.Empleado;
import py.edu.uc.lp32025.domain.Persona;
import py.edu.uc.lp32025.dto.ReporteEmpleadoDto;
import py.edu.uc.lp32025.mapper.EmpleadoMapper;
import py.edu.uc.lp32025.service.PersonaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaService personaService;
    private final EmpleadoMapper empleadoMapper; // ← ESTE ES EL QUE FALTABA

    public PersonaController(PersonaService personaService, EmpleadoMapper empleadoMapper) {
        this.personaService = personaService;
        this.empleadoMapper = empleadoMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Persona persona = personaService.getPersonaById(id);
        return ResponseEntity.ok(persona);
    }

    // NUEVO ENDPOINT CON MAPPER
    @GetMapping("/reporte/{id}")
    public ResponseEntity<ReporteEmpleadoDto> getReporte(@PathVariable Long id) {
        Persona persona = personaService.getPersonaById(id);

        if (persona instanceof Empleado empleado) {
            return ResponseEntity.ok(empleadoMapper.toReporteDto(empleado));
        }

        // Si es Persona genérica (no empleado)
        String info = persona.getNombre() + " " + persona.getApellido() +
                " | Doc: " + persona.getNumeroDocumento();

        ReporteEmpleadoDto dto = new ReporteEmpleadoDto(
                "Persona",
                info,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                true
        );
        return ResponseEntity.ok(dto);
    }

    // Tus otros endpoints existentes (si tenés más)
    // @PostMapping, @PutMapping, etc.
}