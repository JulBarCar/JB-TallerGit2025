package py.edu.uc.lp32025.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import py.edu.uc.lp32025.domain.Persona;
import py.edu.uc.lp32025.dto.ReporteEmpleadoDto;
import py.edu.uc.lp32025.repository.PersonaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;

    @Autowired
    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    /* -------------------------------------------------
       CRUD existente (mantener tal cual)
       ------------------------------------------------- */
    public Persona createPersona(Persona persona) {
        validarCamposBase(persona);
        return personaRepository.save(persona);
    }

    public List<Persona> getAllPersonas() {
        return personaRepository.findAll();
    }

    public Persona getPersonaById(Long id) {
        return personaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Persona not found with id: " + id));
    }

    public Persona updatePersona(Long id, Persona persona) {
        Persona existing = getPersonaById(id);
        validarCamposBase(persona);
        existing.setNombre(persona.getNombre());
        existing.setApellido(persona.getApellido());
        existing.setFechaNacimiento(persona.getFechaNacimiento());
        existing.setNumeroDocumento(persona.getNumeroDocumento());
        return personaRepository.save(existing);
    }

    public void deletePersona(Long id) {
        Persona p = getPersonaById(id);
        personaRepository.delete(p);
    }

    private void validarCamposBase(Persona p) {
        if (p.getNombre() == null || p.getApellido() == null || p.getNumeroDocumento() == null) {
            throw new IllegalArgumentException("Nombre, apellido y numeroDocumento son requeridos");
        }
        if (p.getFechaNacimiento() != null && p.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Fecha de nacimiento no puede ser futura");
        }
    }

    /* -------------------------------------------------
       MÉTODOS GLOBALES REQUERIDOS
       ------------------------------------------------- */

    /**
     * 5.1 calcularNominaTotal()
     * Retorna Map<TipoEmpleado, SumaSalarios>
     * Usa polimorfismo: llama a calcularSalario() de cada subclase.
     */
    public Map<String, BigDecimal> calcularNominaTotal() {
        List<Persona> todas = personaRepository.findAll();

        return todas.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getClass().getSimpleName(),                     // "EmpleadoTiempoCompleto", "EmpleadoPorHora", "Contratista"
                        Collectors.reducing(BigDecimal.ZERO,
                                Persona::calcularSalario,
                                BigDecimal::add)
                ));
    }

    /**
     * 5.2 generarReporteCompleto()
     * Recorre todas las personas, llama a métodos polimórficos
     * y devuelve una lista de DTOs con la información completa.
     */
    /*public List<ReporteEmpleadoDto> generarReporteCompleto() {
        List<Persona> todas = personaRepository.findAll();

        return todas.stream()
                .map(p -> new ReporteEmpleadoDto(
                        p.getClass().getSimpleName(),
                        p.obtenerInformacionCompleta(),
                        p.calcularSalario(),
                        p.calcularImpuestos(),
                        p.validarDatosEspecificos()
                ))
                .collect(Collectors.toList());
    }*/
    public List<ReporteEmpleadoDto> generarReporteCompleto() {
        List<Persona> todas = personaRepository.findAll();

        return todas.stream()
                .map(p -> new ReporteEmpleadoDto(
                        p.getId(),                                      // ← ahora pasamos el ID
                        p.getClass().getSimpleName(),
                        p.obtenerInformacionCompleta(),
                        p.calcularSalario(),
                        p.calcularImpuestos(),
                        p.validarDatosEspecificos()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Busca personas por nombre (contiene, insensible a mayúsculas)
     */
    public List<Persona> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        return personaRepository.findByNombreContainingIgnoreCase(nombre.trim());
    }
}