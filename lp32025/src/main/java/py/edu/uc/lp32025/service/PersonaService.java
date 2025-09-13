package py.edu.uc.lp32025.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import py.edu.uc.lp32025.domain.Persona;
import py.edu.uc.lp32025.repository.PersonaRepository;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

@Service
public class PersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    public Persona createPersona(Persona persona) {
        if (persona.getNombre() == null || persona.getApellido() == null || persona.getNumeroDocumento() == null) {
            throw new IllegalArgumentException("Nombre, apellido, and numeroDocumento are required");
        }
        if (persona.getFechaNacimiento() != null && persona.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Fecha de nacimiento cannot be in the future");
        }
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
        Persona existingPersona = personaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Persona not found with id: " + id));
        if (persona.getNombre() == null || persona.getApellido() == null || persona.getNumeroDocumento() == null) {
            throw new IllegalArgumentException("Nombre, apellido, and numeroDocumento are required");
        }
        if (persona.getFechaNacimiento() != null && persona.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Fecha de nacimiento cannot be in the future");
        }
        existingPersona.setNombre(persona.getNombre());
        existingPersona.setApellido(persona.getApellido());
        existingPersona.setFechaNacimiento(persona.getFechaNacimiento());
        existingPersona.setNumeroDocumento(persona.getNumeroDocumento());
        return personaRepository.save(existingPersona);
    }

    public void deletePersona(Long id) {
        Persona persona = personaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Persona not found with id: " + id));
        personaRepository.delete(persona);
    }
}