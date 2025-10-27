package py.edu.uc.lp32025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.edu.uc.lp32025.domain.Persona;
import java.util.List;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

    // --- NUEVO: Filtro por nombre (insensible a mayúsculas) ---
    List<Persona> findByNombreContainingIgnoreCase(String nombre);
}