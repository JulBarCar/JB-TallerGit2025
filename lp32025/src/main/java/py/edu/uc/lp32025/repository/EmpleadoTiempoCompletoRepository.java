package py.edu.uc.lp32025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.edu.uc.lp32025.domain.EmpleadoTiempoCompleto;
import java.util.List;
import java.util.Optional;

public interface EmpleadoTiempoCompletoRepository extends JpaRepository<EmpleadoTiempoCompleto, Long> {

    Optional<EmpleadoTiempoCompleto> findByNumeroDocumento(String numeroDocumento);
    boolean existsByNumeroDocumento(String numeroDocumento);

    // --- NUEVO: Requerido por el PDF ---
    List<EmpleadoTiempoCompleto> findByDepartamento(String departamento);
}