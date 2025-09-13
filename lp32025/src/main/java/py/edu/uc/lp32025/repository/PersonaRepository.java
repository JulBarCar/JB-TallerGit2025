package py.edu.uc.lp32025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.edu.uc.lp32025.domain.Persona;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
}