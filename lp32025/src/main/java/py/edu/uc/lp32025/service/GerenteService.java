package py.edu.uc.lp32025.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp32025.domain.Gerente;
import py.edu.uc.lp32025.repository.GerenteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GerenteService {

    private static final Logger logger = LoggerFactory.getLogger(GerenteService.class);
    private static final int BATCH_SIZE = 100;

    private final GerenteRepository repository;

    @Autowired
    public GerenteService(GerenteRepository repository) {
        this.repository = repository;
    }

    public List<Gerente> findAll() {
        logger.info("Buscando todos los gerentes.");
        return repository.findAll();
    }

    public Optional<Gerente> findById(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("ID inválido");
        return repository.findById(id);
    }

    @Transactional
    public Gerente save(Gerente gerente) {
        validateGerente(gerente);
        if (repository.existsByNumeroDocumento(gerente.getNumeroDocumento())) {
            throw new IllegalArgumentException("Número de documento ya registrado");
        }
        return repository.save(gerente);
    }

    @Transactional
    public List<Gerente> guardarGerentesEnBatch(List<Gerente> gerentes) {
        if (gerentes == null || gerentes.isEmpty()) {
            throw new IllegalArgumentException("Lista vacía");
        }

        List<Gerente> validos = new ArrayList<>();
        for (int i = 0; i < gerentes.size(); i++) {
            try {
                validateGerente(gerentes.get(i));
                validos.add(gerentes.get(i));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Error en posición " + i + ": " + e.getMessage());
            }
        }

        List<Gerente> guardados = new ArrayList<>();
        for (int i = 0; i < validos.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, validos.size());
            List<Gerente> chunk = validos.subList(i, end);
            guardados.addAll(repository.saveAll(chunk));
            repository.flush();
        }
        return guardados;
    }

    private void validateGerente(Gerente g) {
        if (g == null) throw new IllegalArgumentException("Gerente nulo");
        if (isEmpty(g.getNombre())) throw new IllegalArgumentException("Nombre vacío");
        if (isEmpty(g.getApellido())) throw new IllegalArgumentException("Apellido vacío");
        if (isEmpty(g.getNumeroDocumento())) throw new IllegalArgumentException("Documento vacío");
        if (!g.validarDatosEspecificos()) {
            throw new IllegalArgumentException("Datos específicos inválidos para gerente");
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}