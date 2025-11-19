package py.edu.uc.lp32025.service;

import py.edu.uc.lp32025.dto.external.NominaExternaResponseDTO;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class NominaExternaService {

    public NominaExternaResponseDTO consultarNomina(String documento, LocalDate periodo) {
        // Simulación de llamada a API externa
        NominaExternaResponseDTO response = new NominaExternaResponseDTO();
        response.setDocumento(documento);
        response.setNombreCompleto("Simulación Externa");
        response.setSalarioBruto(new BigDecimal("5000000"));
        response.setDeducciones(new BigDecimal("1200000"));
        response.setSalarioNeto(new BigDecimal("3800000"));
        response.setPeriodo(periodo);
        response.setEstado(Math.random() > 0.3 ? "PAGADO" : "PENDIENTE");
        return response;
    }
}