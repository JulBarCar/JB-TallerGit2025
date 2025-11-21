package py.edu.uc.lp32025.controler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp32025.dto.ReporteEmpleadoDto;
import py.edu.uc.lp32025.service.PersonaService;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteControler {

    private final PersonaService personaService;

    /**
     * Reporte completo de TODOS los empleados del sistema
     * Usa polimorfismo total: cada empleado calcula su propio salario e impuestos
     */
    @GetMapping("/empleados/completo")
    public ResponseEntity<List<ReporteEmpleadoDto>> reporteCompletoEmpleados() {
        List<ReporteEmpleadoDto> reporte = personaService.generarReporteCompleto();
        return ResponseEntity.ok(reporte);
    }

    /**
     * Bonus: nómina total por tipo de empleado (otro endpoint brutal para demo)
     */
    @GetMapping("/nomina/total-por-tipo")
    public ResponseEntity<java.util.Map<String, java.math.BigDecimal>> nominaTotalPorTipo() {
        return ResponseEntity.ok(personaService.calcularNominaTotal());
    }
}