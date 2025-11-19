package py.edu.uc.lp32025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.edu.uc.lp32025.domain.Gerente;
import java.util.List;

public interface GerenteRepository extends JpaRepository<Gerente, Long> {

    List<Gerente> findByNivelJerarquicoGreaterThanEqual(Integer nivel);

    boolean existsByNumeroDocumento(String numeroDocumento);
}