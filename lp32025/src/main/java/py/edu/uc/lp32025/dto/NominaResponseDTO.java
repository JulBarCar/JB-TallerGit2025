package py.edu.uc.lp32025.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class NominaResponseDTO {
    private Long empleadoId;
    private String nombre;
    private String documento;
    private BigDecimal montoAPagar;
    private LocalDate periodo;
    private String estado;
    private String mensaje;
}