package py.edu.uc.lp32025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import py.edu.uc.lp32025.domain.Contratista;
import java.util.List;

public interface ContratistaRepository extends JpaRepository<Contratista, Long> {

    // --- NUEVO: Requerido por el PDF ---
    // Usa JPQL porque `contratoVigente()` es un método de instancia
    @Query("SELECT c FROM Contratista c WHERE c.fechaFinContrato > CURRENT_DATE")
    List<Contratista> findByContratoVigente();

    // Opcional: por documento
    boolean existsByNumeroDocumento(String numeroDocumento);
}