package py.edu.uc.lp32025.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp32025.domain.Contratista;
import py.edu.uc.lp32025.dto.ErrorResponseDTO;
import py.edu.uc.lp32025.dto.ImpuestoResponseDTO;
import py.edu.uc.lp32025.service.ContratistaService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/contratistas")
public class ContratistaController {

    private final ContratistaService service;

    @Autowired
    public ContratistaController(ContratistaService service) {
        this.service = service;
    }

    // ==================== CRUD BÁSICO ====================

    @GetMapping
    public List<Contratista> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contratista> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/contrato-vigente")
    public List<Contratista> getByContratoVigente() {
        return service.findByContratoVigente();
    }

    // ==================== IMPUESTOS (POLIMORFISMO) ====================

    @GetMapping("/{id}/impuestos")
    public ResponseEntity<ImpuestoResponseDTO> getImpuestos(@PathVariable Long id) {
        return service.findById(id)
                .map(contratista -> {
                    BigDecimal salario = contratista.calcularSalario();
                    BigDecimal impuestoBase = contratista.calcularImpuestoBase(salario);
                    BigDecimal deducciones = contratista.calcularDeducciones();
                    BigDecimal impuestoNeto = contratista.calcularImpuestos();
                    boolean datosValidos = contratista.validarDatosEspecificos();
                    String infoCompleta = contratista.obtenerInformacionCompleta();

                    return ResponseEntity.ok(new ImpuestoResponseDTO(
                            salario, impuestoBase, deducciones, impuestoNeto,
                            datosValidos, infoCompleta
                    ));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ImpuestoResponseDTO(
                                HttpStatus.NOT_FOUND.value(),
                                "Contratista no encontrado",
                                "No se encontró un contratista con ID: " + id
                        )));
    }

    // ==================== CREAR UNO ====================

    @PostMapping
    public ResponseEntity<Contratista> create(@RequestBody Contratista contratista) {
        try {
            Contratista saved = service.save(contratista);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // ==================== BATCH ====================

    @PostMapping("/batch")
    public ResponseEntity<?> createBatch(@RequestBody List<Contratista> contratistas) {
        try {
            List<Contratista> saved = service.guardarEmpleadosEnBatch(contratistas);
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
    public ResponseEntity<Contratista> update(@PathVariable Long id, @RequestBody Contratista contratista) {
        try {
            Contratista updated = service.update(id, contratista);
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