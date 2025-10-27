package py.edu.uc.lp32025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.edu.uc.lp32025.domain.EmpleadoPorHora;
import java.util.List;

public interface EmpleadoPorHorasRepository extends JpaRepository<EmpleadoPorHora, Long> {

    // --- NUEVO: Requerido por el PDF ---
    List<EmpleadoPorHora> findByHorasTrabajadasGreaterThan(Integer horas);

    // Opcional: por documento (para consistencia con otros)
    boolean existsByNumeroDocumento(String numeroDocumento);
}