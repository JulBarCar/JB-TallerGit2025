package py.edu.uc.lp32025.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp32025.domain.Contratista;
import py.edu.uc.lp32025.repository.ContratistaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ContratistaService {

    private static final Logger logger = LoggerFactory.getLogger(ContratistaService.class);
    private static final int BATCH_SIZE = 100;

    private final ContratistaRepository repository;

    @Autowired
    public ContratistaService(ContratistaRepository repository) {
        this.repository = repository;
    }

    // ========================================
    // CRUD BÁSICO
    // ========================================

    public List<Contratista> findAll() {
        logger.info("Buscando todos los contratistas.");
        List<Contratista> contratistas = repository.findAll();
        logger.info("Encontrados {} contratistas.", contratistas.size());
        return contratistas;
    }

    public Optional<Contratista> findById(Long id) {
        logger.info("Buscando contratista con ID: {}", id);
        if (id == null || id <= 0) {
            logger.error("ID inválido: {}", id);
            throw new IllegalArgumentException("El ID debe ser válido.");
        }
        return repository.findById(id);
    }

    public List<Contratista> findByContratoVigente() {
        logger.info("Buscando contratistas con contratos vigentes.");
        return repository.findByContratoVigente();
    }

    @Transactional
    public Contratista save(Contratista contratista) {
        logger.info("Guardando contratista: {} {}", contratista.getNombre(), contratista.getApellido());
        validateContratista(contratista);
        Contratista saved = repository.save(contratista);
        logger.info("Contratista guardado con ID: {}", saved.getId());
        return saved;
    }

    @Transactional
    public Contratista update(Long id, Contratista contratista) {
        logger.info("Actualizando contratista con ID: {}", id);
        if (!repository.existsById(id)) {
            logger.error("No existe contratista con ID: {}", id);
            throw new IllegalArgumentException("Contratista no encontrado con ID: " + id);
        }
        contratista.setId(id);
        validateContratista(contratista);
        Contratista updated = repository.save(contratista);
        logger.info("Contratista actualizado con ID: {}", id);
        return updated;
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Eliminando contratista con ID: {}", id);
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser válido.");
        }
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Contratista no encontrado con ID: " + id);
        }
        repository.deleteById(id);
        logger.info("Contratista eliminado con ID: {}", id);
    }

    // ========================================
    // BATCH CON CHUNKS DE 100
    // ========================================

    @Transactional
    public List<Contratista> guardarEmpleadosEnBatch(List<Contratista> contratistas) {
        logger.info("Iniciando guardado en batch de {} contratistas.", contratistas != null ? contratistas.size() : 0);

        if (contratistas == null || contratistas.isEmpty()) {
            logger.error("La lista de contratistas no puede estar vacía.");
            throw new IllegalArgumentException("La lista de contratistas no puede estar vacía.");
        }

        // --- 1. Validar TODOS antes de guardar ---
        List<Contratista> validos = new ArrayList<>();
        for (int i = 0; i < contratistas.size(); i++) {
            Contratista cont = contratistas.get(i);
            try {
                validateContratista(cont);
                validos.add(cont);
            } catch (IllegalArgumentException e) {
                logger.error("Contratista inválido en posición {}: {} {}", i, cont.getNombre(), cont.getApellido());
                throw new IllegalArgumentException("Error en contratista posición " + i + ": " + e.getMessage());
            }
        }

        // --- 2. Guardar en chunks de 100 ---
        List<Contratista> guardados = new ArrayList<>();
        for (int i = 0; i < validos.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, validos.size());
            List<Contratista> chunk = validos.subList(i, end);

            logger.info("Guardando chunk {}-{} de {} contratistas.", i, end - 1, validos.size());
            List<Contratista> savedChunk = repository.saveAll(chunk);
            guardados.addAll(savedChunk);

            // Flush y clear para liberar memoria
            repository.flush();
            // Opcional: entityManager.clear() si usas EntityManager
        }

        logger.info("Batch completado: {} contratistas guardados.", guardados.size());
        return guardados;
    }

    // ========================================
    // VALIDACIÓN (polimorfismo + general)
    // ========================================

    private void validateContratista(Contratista contratista) {
        if (contratista == null) {
            logger.error("El contratista no puede ser nulo.");
            throw new IllegalArgumentException("El contratista no puede ser nulo.");
        }

        // --- Validaciones generales ---
        if (isEmpty(contratista.getNombre())) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (isEmpty(contratista.getApellido())) {
            throw new IllegalArgumentException("El apellido no puede estar vacío.");
        }
        if (isEmpty(contratista.getNumeroDocumento())) {
            throw new IllegalArgumentException("El número de documento no puede estar vacío.");
        }

        // --- Validación específica (POLIMORFISMO) ---
        if (!contratista.validarDatosEspecificos()) {
            throw new IllegalArgumentException(
                    "Datos específicos inválidos: monto > 0, proyectos >= 0 y fecha fin futura."
            );
        }

        logger.debug("Contratista válido: {} {}", contratista.getNombre(), contratista.getApellido());
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}