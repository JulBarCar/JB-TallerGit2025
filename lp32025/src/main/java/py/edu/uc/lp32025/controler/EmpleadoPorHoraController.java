package py.edu.uc.lp32025.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp32025.domain.EmpleadoPorHora;
import py.edu.uc.lp32025.dto.ErrorResponseDTO;
import py.edu.uc.lp32025.dto.ImpuestoResponseDTO;
import py.edu.uc.lp32025.service.EmpleadoPorHorasService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/empleados-por-hora")
public class EmpleadoPorHoraController {

    private final EmpleadoPorHorasService service;

    @Autowired
    public EmpleadoPorHoraController(EmpleadoPorHorasService service) {
        this.service = service;
    }

    // ==================== CRUD BÁSICO ====================

    @GetMapping
    public List<EmpleadoPorHora> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoPorHora> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/horas/{horas}")
    public List<EmpleadoPorHora> getByHorasGreaterThan(@PathVariable Integer horas) {
        return service.findByHorasTrabajadasGreaterThan(horas);
    }

    // ==================== IMPUESTOS (POLIMORFISMO) ====================

    @GetMapping("/{id}/impuestos")
    public ResponseEntity<ImpuestoResponseDTO> getImpuestos(@PathVariable Long id) {
        return service.findById(id)
                .map(empleado -> {
                    BigDecimal salario = empleado.calcularSalario();
                    BigDecimal impuestoBase = empleado.calcularImpuestoBase(salario);
                    BigDecimal deducciones = empleado.calcularDeducciones();
                    BigDecimal impuestoNeto = empleado.calcularImpuestos();
                    boolean datosValidos = empleado.validarDatosEspecificos();
                    String infoCompleta = empleado.obtenerInformacionCompleta();

                    return ResponseEntity.ok(new ImpuestoResponseDTO(
                            salario, impuestoBase, deducciones, impuestoNeto,
                            datosValidos, infoCompleta
                    ));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ImpuestoResponseDTO(
                                HttpStatus.NOT_FOUND.value(),
                                "Empleado no encontrado",
                                "No se encontró un empleado con ID: " + id
                        )));
    }

    // ==================== CREAR UNO ====================

    @PostMapping
    public ResponseEntity<EmpleadoPorHora> create(@RequestBody EmpleadoPorHora empleado) {
        try {
            EmpleadoPorHora saved = service.save(empleado);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // ==================== BATCH ====================

    @PostMapping("/batch")
    public ResponseEntity<?> createBatch(@RequestBody List<EmpleadoPorHora> empleados) {
        try {
            List<EmpleadoPorHora> saved = service.guardarEmpleadosEnBatch(empleados);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    "Error en batch: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // ==================== ACTUALIZAR ====================

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoPorHora> update(@PathVariable Long id, @RequestBody EmpleadoPorHora empleado) {
        try {
            EmpleadoPorHora updated = service.update(id, empleado);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // ==================== ELIMINAR ====================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}