package py.edu.uc.lp32025.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp32025.domain.EmpleadoPorHora;
import py.edu.uc.lp32025.repository.EmpleadoPorHorasRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoPorHorasService {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoPorHorasService.class);
    private static final int BATCH_SIZE = 100;

    private final EmpleadoPorHorasRepository repository;

    @Autowired
    public EmpleadoPorHorasService(EmpleadoPorHorasRepository repository) {
        this.repository = repository;
    }

    // ========================================
    // CRUD BÁSICO
    // ========================================

    public List<EmpleadoPorHora> findAll() {
        logger.info("Buscando todos los empleados por horas.");
        List<EmpleadoPorHora> empleados = repository.findAll();
        logger.info("Encontrados {} empleados por horas.", empleados.size());
        return empleados;
    }

    public Optional<EmpleadoPorHora> findById(Long id) {
        logger.info("Buscando empleado por horas con ID: {}", id);
        if (id == null || id <= 0) {
            logger.error("ID inválido: {}", id);
            throw new IllegalArgumentException("El ID debe ser válido.");
        }
        return repository.findById(id);
    }

    public List<EmpleadoPorHora> findByHorasTrabajadasGreaterThan(Integer horas) {
        logger.info("Buscando empleados con más de {} horas trabajadas.", horas);
        if (horas == null || horas < 0) {
            throw new IllegalArgumentException("Las horas deben ser no negativas.");
        }
        return repository.findByHorasTrabajadasGreaterThan(horas);
    }

    @Transactional
    public EmpleadoPorHora save(EmpleadoPorHora empleado) {
        logger.info("Guardando empleado por horas: {} {}", empleado.getNombre(), empleado.getApellido());
        validateEmpleado(empleado);
        EmpleadoPorHora saved = repository.save(empleado);
        logger.info("Empleado por horas guardado con ID: {}", saved.getId());
        return saved;
    }

    @Transactional
    public EmpleadoPorHora update(Long id, EmpleadoPorHora empleado) {
        logger.info("Actualizando empleado por horas con ID: {}", id);
        if (!repository.existsById(id)) {
            logger.error("No existe empleado por horas con ID: {}", id);
            throw new IllegalArgumentException("Empleado no encontrado con ID: " + id);
        }
        empleado.setId(id);
        validateEmpleado(empleado);
        EmpleadoPorHora updated = repository.save(empleado);
        logger.info("Empleado por horas actualizado con ID: {}", id);
        return updated;
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Eliminando empleado por horas con ID: {}", id);
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser válido.");
        }
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Empleado no encontrado con ID: " + id);
        }
        repository.deleteById(id);
        logger.info("Empleado por horas eliminado con ID: {}", id);
    }

    // ========================================
    // BATCH CON CHUNKS DE 100
    // ========================================

    @Transactional
    public List<EmpleadoPorHora> guardarEmpleadosEnBatch(List<EmpleadoPorHora> empleados) {
        logger.info("Iniciando guardado en batch de {} empleados por horas.", empleados != null ? empleados.size() : 0);

        if (empleados == null || empleados.isEmpty()) {
            logger.error("La lista de empleados no puede estar vacía.");
            throw new IllegalArgumentException("La lista de empleados no puede estar vacía.");
        }

        // --- 1. Validar TODOS antes de guardar ---
        List<EmpleadoPorHora> validos = new ArrayList<>();
        for (int i = 0; i < empleados.size(); i++) {
            EmpleadoPorHora emp = empleados.get(i);
            try {
                validateEmpleado(emp);
                validos.add(emp);
            } catch (IllegalArgumentException e) {
                logger.error("Empleado inválido en posición {}: {} {}", i, emp.getNombre(), emp.getApellido());
                throw new IllegalArgumentException("Error en empleado posición " + i + ": " + e.getMessage());
            }
        }

        // --- 2. Guardar en chunks de 100 ---
        List<EmpleadoPorHora> guardados = new ArrayList<>();
        for (int i = 0; i < validos.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, validos.size());
            List<EmpleadoPorHora> chunk = validos.subList(i, end);

            logger.info("Guardando chunk {}-{} de {} empleados por horas.", i, end - 1, validos.size());
            List<EmpleadoPorHora> savedChunk = repository.saveAll(chunk);
            guardados.addAll(savedChunk);

            // Flush y clear para liberar memoria
            repository.flush();
            // Opcional: entityManager.clear() si usas EntityManager
        }

        logger.info("Batch completado: {} empleados por horas guardados.", guardados.size());
        return guardados;
    }

    // ========================================
    // VALIDACIÓN (polimorfismo + general)
    // ========================================

    private void validateEmpleado(EmpleadoPorHora empleado) {
        if (empleado == null) {
            logger.error("El empleado no puede ser nulo.");
            throw new IllegalArgumentException("El empleado no puede ser nulo.");
        }

        // --- Validaciones generales ---
        if (isEmpty(empleado.getNombre())) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (isEmpty(empleado.getApellido())) {
            throw new IllegalArgumentException("El apellido no puede estar vacío.");
        }
        if (isEmpty(empleado.getNumeroDocumento())) {
            throw new IllegalArgumentException("El número de documento no puede estar vacío.");
        }

        // --- Validación específica (POLIMORFISMO) ---
        if (!empleado.validarDatosEspecificos()) {
            throw new IllegalArgumentException(
                    "Datos específicos inválidos: tarifa > 0 y horas entre 1 y 80."
            );
        }

        logger.debug("Empleado por horas válido: {} {}", empleado.getNombre(), empleado.getApellido());
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}