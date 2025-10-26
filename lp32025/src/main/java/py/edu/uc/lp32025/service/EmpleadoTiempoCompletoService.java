package py.edu.uc.lp32025.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import py.edu.uc.lp32025.domain.EmpleadoTiempoCompleto;
import py.edu.uc.lp32025.repository.EmpleadoTiempoCompletoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoTiempoCompletoService {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoTiempoCompletoService.class);

    private final EmpleadoTiempoCompletoRepository repository;

    @Autowired
    public EmpleadoTiempoCompletoService(EmpleadoTiempoCompletoRepository repository) {
        this.repository = repository;
    }

    public List<EmpleadoTiempoCompleto> findAll() {
        logger.info("Buscando todos los empleados a tiempo completo.");
        List<EmpleadoTiempoCompleto> empleados = repository.findAll();
        logger.info("Encontrados {} empleados a tiempo completo.", empleados.size());
        return empleados;
    }

    public Optional<EmpleadoTiempoCompleto> findById(Long id) {
        logger.info("Buscando empleado con ID: {}", id);
        if (id == null || id <= 0) {
            logger.error("El ID debe ser válido: {}", id);
            throw new IllegalArgumentException("El ID debe ser válido.");
        }
        Optional<EmpleadoTiempoCompleto> empleado = repository.findById(id);
        if (empleado.isPresent()) {
            logger.info("Empleado encontrado con ID: {}", id);
        } else {
            logger.warn("No se encontró empleado con ID: {}", id);
        }
        return empleado;
    }

    public Optional<EmpleadoTiempoCompleto> findByNumeroDocumento(String numeroDocumento) {
        logger.info("Buscando empleado con número de documento: {}", numeroDocumento);
        if (numeroDocumento == null || numeroDocumento.trim().isEmpty()) {
            logger.error("El número de documento no puede estar vacío: {}", numeroDocumento);
            throw new IllegalArgumentException("El número de documento no puede estar vacío.");
        }
        Optional<EmpleadoTiempoCompleto> empleado = repository.findByNumeroDocumento(numeroDocumento);
        if (empleado.isPresent()) {
            logger.info("Empleado encontrado con número de documento: {}", numeroDocumento);
        } else {
            logger.warn("No se encontró empleado con número de documento: {}", numeroDocumento);
        }
        return empleado;
    }

    @Transactional
    public List<EmpleadoTiempoCompleto> guardarEmpleadosEnBatch(List<EmpleadoTiempoCompleto> empleados) {
        logger.info("Iniciando guardado en lote de {} empleados a tiempo completo.", empleados != null ? empleados.size() : 0);
        if (empleados == null || empleados.isEmpty()) {
            logger.error("La lista de empleados no puede estar vacía.");
            throw new IllegalArgumentException("La lista de empleados no puede estar vacía.");
        }

        // Validar todos los empleados antes de guardar
        for (EmpleadoTiempoCompleto empleado : empleados) {
            logger.info("Validando empleado: {} {} (Documento: {})",
                    empleado.getNombre(), empleado.getApellido(), empleado.getNumeroDocumento());
            try {
                validateEmpleado(empleado);
                logger.info("Empleado validado correctamente: {} {} (Documento: {})",
                        empleado.getNombre(), empleado.getApellido(), empleado.getNumeroDocumento());
            } catch (IllegalArgumentException e) {
                logger.error("Validación fallida para empleado: {} {} (Documento: {}): {}",
                        empleado.getNombre(), empleado.getApellido(), empleado.getNumeroDocumento(), e.getMessage());
                throw e;
            }
        }

        // Procesar en chunks de 100 registros
        List<EmpleadoTiempoCompleto> savedEmpleados = new ArrayList<>();
        int batchSize = 100;
        for (int i = 0; i < empleados.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, empleados.size());
            List<EmpleadoTiempoCompleto> chunk = empleados.subList(i, endIndex);
            logger.info("Procesando chunk de {} empleados, desde índice {} hasta {}", chunk.size(), i, endIndex);

            // Guardar chunk usando saveAll para batch processing
            try {
                List<EmpleadoTiempoCompleto> savedChunk = repository.saveAll(chunk);
                savedEmpleados.addAll(savedChunk);
                logger.info("Guardado chunk de {} empleados correctamente.", chunk.size());
            } catch (Exception e) {
                logger.error("Error al guardar chunk de empleados: {}", e.getMessage());
                throw new IllegalArgumentException("Error al guardar empleados en lote: " + e.getMessage());
            }
        }

        logger.info("Finalizado guardado en lote. Guardados {} empleados.", savedEmpleados.size());
        return savedEmpleados;
    }

    public EmpleadoTiempoCompleto save(EmpleadoTiempoCompleto empleado) {
        logger.info("Guardando empleado: {} {} (Documento: {})",
                empleado.getNombre(), empleado.getApellido(), empleado.getNumeroDocumento());
        validateEmpleado(empleado);
        if (repository.existsByNumeroDocumento(empleado.getNumeroDocumento())) {
            logger.error("El número de documento ya está registrado: {}", empleado.getNumeroDocumento());
            throw new IllegalArgumentException("El número de documento ya está registrado.");
        }
        EmpleadoTiempoCompleto saved = repository.save(empleado);
        logger.info("Empleado guardado con ID: {}", saved.getId());
        return saved;
    }

    public EmpleadoTiempoCompleto update(Long id, EmpleadoTiempoCompleto empleado) {
        logger.info("Actualizando empleado con ID: {}", id);
        if (id == null || id <= 0) {
            logger.error("El ID debe ser válido: {}", id);
            throw new IllegalArgumentException("El ID debe ser válido.");
        }
        Optional<EmpleadoTiempoCompleto> existing = repository.findById(id);
        if (!existing.isPresent()) {
            logger.error("Empleado no encontrado con ID: {}", id);
            throw new IllegalArgumentException("Empleado no encontrado con ID: " + id);
        }
        EmpleadoTiempoCompleto current = existing.get();

        if (!current.getNumeroDocumento().equals(empleado.getNumeroDocumento()) &&
                repository.existsByNumeroDocumento(empleado.getNumeroDocumento())) {
            logger.error("El número de documento ya está registrado: {}", empleado.getNumeroDocumento());
            throw new IllegalArgumentException("El número de documento ya está registrado.");
        }

        current.setNombre(empleado.getNombre());
        current.setApellido(empleado.getApellido());
        current.setFechaNacimiento(empleado.getFechaNacimiento());
        current.setNumeroDocumento(empleado.getNumeroDocumento());
        current.setSalarioMensual(empleado.getSalarioMensual());
        current.setDepartamento(empleado.getDepartamento());

        logger.info("Validando empleado actualizado: {} {} (Documento: {})",
                current.getNombre(), current.getApellido(), current.getNumeroDocumento());
        validateEmpleado(current);
        EmpleadoTiempoCompleto updated = repository.save(current);
        logger.info("Empleado actualizado con ID: {}", updated.getId());
        return updated;
    }

    public void delete(Long id) {
        logger.info("Eliminando empleado con ID: {}", id);
        if (id == null || id <= 0) {
            logger.error("El ID debe ser válido: {}", id);
            throw new IllegalArgumentException("El ID debe ser válido.");
        }
        if (!repository.existsById(id)) {
            logger.error("Empleado no encontrado con ID: {}", id);
            throw new IllegalArgumentException("Empleado no encontrado con ID: " + id);
        }
        repository.deleteById(id);
        logger.info("Empleado eliminado con ID: {}", id);
    }

    private void validateEmpleado(EmpleadoTiempoCompleto empleado) {
        if (empleado == null) {
            logger.error("El empleado no puede ser nulo.");
            throw new IllegalArgumentException("El empleado no puede ser nulo.");
        }
        logger.debug("Validando nombre para empleado: {} {}", empleado.getNombre(), empleado.getApellido());
        if (empleado.getNombre() == null || empleado.getNombre().trim().isEmpty()) {
            logger.error("El nombre no puede estar vacío para empleado: {} {}", empleado.getNombre(), empleado.getApellido());
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        logger.debug("Nombre válido: {}", empleado.getNombre());

        logger.debug("Validando apellido para empleado: {} {}", empleado.getNombre(), empleado.getApellido());
        if (empleado.getApellido() == null || empleado.getApellido().trim().isEmpty()) {
            logger.error("El apellido no puede estar vacío para empleado: {} {}", empleado.getNombre(), empleado.getApellido());
            throw new IllegalArgumentException("El apellido no puede estar vacío.");
        }
        logger.debug("Apellido válido: {}", empleado.getApellido());

        logger.debug("Validando número de documento para empleado: {} {}", empleado.getNombre(), empleado.getApellido());
        if (empleado.getNumeroDocumento() == null || empleado.getNumeroDocumento().trim().isEmpty()) {
            logger.error("El número de documento no puede estar vacío para empleado: {} {}", empleado.getNombre(), empleado.getApellido());
            throw new IllegalArgumentException("El número de documento no puede estar vacío.");
        }
        logger.debug("Número de documento válido: {}", empleado.getNumeroDocumento());

        logger.debug("Validando datos específicos para empleado: {} {}", empleado.getNombre(), empleado.getApellido());
        if (!empleado.validarDatosEspecificos()) {
            logger.error("Los datos específicos del empleado no son válidos para {} {}. Salario debe ser >= 2,899,048 y departamento no puede estar vacío.",
                    empleado.getNombre(), empleado.getApellido());
            throw new IllegalArgumentException("Los datos específicos del empleado no son válidos. Salario debe ser >= 2,899,048 y departamento no puede estar vacío.");
        }
        logger.debug("Datos específicos válidos para empleado: {} {}. Salario: {}, Departamento: {}",
                empleado.getNombre(), empleado.getApellido(), empleado.getSalarioMensual(), empleado.getDepartamento());
    }
}