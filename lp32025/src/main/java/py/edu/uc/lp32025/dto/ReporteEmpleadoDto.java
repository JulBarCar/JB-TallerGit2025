package py.edu.uc.lp32025.dto;

import java.math.BigDecimal;

public record ReporteEmpleadoDto(
        String tipoEmpleado,
        String informacionCompleta,
        BigDecimal salario,
        BigDecimal impuestoNeto,
        boolean datosValidos
) {}