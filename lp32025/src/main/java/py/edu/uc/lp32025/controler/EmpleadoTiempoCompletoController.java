package py.edu.uc.lp32025.controler;

import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp32025.domain.EmpleadoTiempoCompleto;
import py.edu.uc.lp32025.service.EmpleadoTiempoCompletoService;
import py.edu.uc.lp32025.dto.ErrorResponseDTO;
import py.edu.uc.lp32025.dto.ImpuestoResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/empleados-tiempo-completo")
public class EmpleadoTiempoCompletoController {

    private final EmpleadoTiempoCompletoService service;

    @Autowired
    public EmpleadoTiempoCompletoController(EmpleadoTiempoCompletoService service) {
        this.service = service;
    }

    @GetMapping
    public List<EmpleadoTiempoCompleto> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoTiempoCompleto> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(empleado -> ResponseEntity.ok(empleado))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/documento/{numeroDocumento}")
    public ResponseEntity<EmpleadoTiempoCompleto> getByNumeroDocumento(@PathVariable String numeroDocumento) {
        return service.findByNumeroDocumento(numeroDocumento)
                .map(empleado -> ResponseEntity.ok(empleado))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{id}/impuestos")
    public ResponseEntity<ImpuestoResponseDTO> getImpuestos(@PathVariable Long id) {
        return service.findById(id)
                .map(empleado -> {
                    BigDecimal salario = empleado.calcularSalario();
                    BigDecimal impuestoBase = empleado.calcularImpuestoBase(salario);
                    BigDecimal deducciones = empleado.calcularDeducciones();
                    BigDecimal impuestoNeto = empleado.calcularImpuestos();
                    boolean datosValidos = empleado.validarDatosEspecificos();
                    String informacionCompleta = empleado.obtenerInformacionCompleta();
                    return ResponseEntity.ok(new ImpuestoResponseDTO(salario, impuestoBase, deducciones,
                            impuestoNeto, datosValidos, informacionCompleta));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ImpuestoResponseDTO(HttpStatus.NOT_FOUND.value(),
                                "Empleado no encontrado",
                                "No se encontró un empleado con ID: " + id)));
    }

    @PostMapping
    public ResponseEntity<EmpleadoTiempoCompleto> create(@RequestBody EmpleadoTiempoCompleto empleado) {
        try {
            EmpleadoTiempoCompleto saved = service.save(empleado);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<?> createBatch(@RequestBody List<EmpleadoTiempoCompleto> empleados) {
        try {
            List<EmpleadoTiempoCompleto> saved = service.guardarEmpleadosEnBatch(empleados);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    "Error al crear empleados en lote: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoTiempoCompleto> update(@PathVariable Long id, @RequestBody EmpleadoTiempoCompleto empleado) {
        try {
            EmpleadoTiempoCompleto updated = service.update(id, empleado);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    
}