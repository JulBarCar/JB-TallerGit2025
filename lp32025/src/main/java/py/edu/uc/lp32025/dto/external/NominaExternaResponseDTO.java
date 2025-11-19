package py.edu.uc.lp32025.dto.external;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class NominaExternaResponseDTO {
    private String documento;
    private String nombreCompleto;
    private BigDecimal salarioBruto;
    private BigDecimal deducciones;
    private BigDecimal salarioNeto;
    private LocalDate periodo;
    private String estado; // "PENDIENTE", "PAGADO", "RECHAZADO"
}