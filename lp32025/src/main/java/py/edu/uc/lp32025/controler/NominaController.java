package py.edu.uc.lp32025.controler;

import py.edu.uc.lp32025.domain.Empleado;
import py.edu.uc.lp32025.dto.NominaResponseDTO;
import py.edu.uc.lp32025.mapper.NominaIntegrationMapper;
import py.edu.uc.lp32025.service.NominaExternaService;
import py.edu.uc.lp32025.service.PersonaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/nomina")
public class NominaController {

    private final PersonaService personaService;
    private final NominaExternaService nominaExternaService;
    private final NominaIntegrationMapper nominaMapper;

    public NominaController(PersonaService personaService,
                            NominaExternaService nominaExternaService,
                            NominaIntegrationMapper nominaMapper) {
        this.personaService = personaService;
        this.nominaExternaService = nominaExternaService;
        this.nominaMapper = nominaMapper;
    }

    @GetMapping("/empleado/{id}")
    public ResponseEntity<NominaResponseDTO> consultarNominaEmpleado(
            @PathVariable Long id,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().withDayOfMonth(1)}") LocalDate periodo) {

        Empleado empleado = (Empleado) personaService.getPersonaById(id);
        var externa = nominaExternaService.consultarNomina(empleado.getNumeroDocumento(), periodo);
        NominaResponseDTO response = nominaMapper.toNominaResponse(empleado, externa);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/gerente/{id}")
    public ResponseEntity<NominaResponseDTO> consultarNominaGerente(
            @PathVariable Long id,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().withDayOfMonth(1)}") LocalDate periodo) {

        Empleado gerente = (Empleado) personaService.getPersonaById(id);
        var externa = nominaExternaService.consultarNomina(gerente.getNumeroDocumento(), periodo);
        NominaResponseDTO response = nominaMapper.toNominaResponse(gerente, externa);

        return ResponseEntity.ok(response);
    }
}