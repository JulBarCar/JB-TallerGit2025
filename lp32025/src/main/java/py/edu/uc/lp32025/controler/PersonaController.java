package py.edu.uc.lp32025.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp32025.domain.Persona;
import py.edu.uc.lp32025.dto.ReporteEmpleadoDto;
import py.edu.uc.lp32025.service.PersonaService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaService personaService;

    @Autowired
    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    // ==================== CRUD EXISTENTE ====================

    @PostMapping
    public ResponseEntity<Persona> createPersona(@RequestBody Persona persona) {
        Persona savedPersona = personaService.createPersona(persona);
        return ResponseEntity.ok(savedPersona);
    }

    @GetMapping
    public ResponseEntity<List<Persona>> getAllPersonas() {
        return ResponseEntity.ok(personaService.getAllPersonas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Persona> getPersonaById(@PathVariable Long id) {
        Persona persona = personaService.getPersonaById(id);
        return ResponseEntity.ok(persona);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Persona> updatePersona(@PathVariable Long id, @RequestBody Persona persona) {
        Persona updatedPersona = personaService.updatePersona(id, persona);
        return ResponseEntity.ok(updatedPersona);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersona(@PathVariable Long id) {
        personaService.deletePersona(id);
        return ResponseEntity.ok().build();
    }

    // ==================== MÉTODOS GLOBALES (POLIMORFISMO) ====================

    /**
     * GET /api/personas/nomina
     * → Map<TipoEmpleado, SumaSalarios>
     */
    @GetMapping("/nomina")
    public ResponseEntity<Map<String, BigDecimal>> calcularNominaTotal() {
        Map<String, BigDecimal> nomina = personaService.calcularNominaTotal();
        return ResponseEntity.ok(nomina);
    }

    /**
     * GET /api/personas/reporte
     * → List<ReporteEmpleadoDto> con info completa, impuestos y validaciones
     */
    @GetMapping("/reporte")
    public ResponseEntity<List<ReporteEmpleadoDto>> generarReporteCompleto() {
        List<ReporteEmpleadoDto> reporte = personaService.generarReporteCompleto();
        return ResponseEntity.ok(reporte);
    }

    /**
     * GET /api/personas?nombre=Juan
     * → Busca personas cuyo nombre contenga "Juan" (case-insensitive)
     */
    @GetMapping(params = "nombre")
    public ResponseEntity<List<Persona>> buscarPorNombre(@RequestParam String nombre) {
        List<Persona> personas = personaService.buscarPorNombre(nombre);
        return ResponseEntity.ok(personas);
    }
}