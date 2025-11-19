package py.edu.uc.lp32025.mapper;

import py.edu.uc.lp32025.domain.*;
import py.edu.uc.lp32025.dto.ReporteEmpleadoDto;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class EmpleadoMapper extends BaseMapper<Empleado, ReporteEmpleadoDto> {

    public EmpleadoMapper() {
        super(Empleado.class, ReporteEmpleadoDto.class);
    }

    @Override
    protected void copyEntityToDto(Empleado entity, ReporteEmpleadoDto dto) {}

    @Override
    protected void copyDtoToEntity(ReporteEmpleadoDto dto, Empleado entity) {}

    public ReporteEmpleadoDto toReporteDto(Empleado empleado) {
        if (empleado == null) return null;

        String tipo = empleado.getClass().getSimpleName();
        String departamento = obtenerDepartamento(empleado);
        BigDecimal salario = obtenerSalario(empleado);

        String info = empleado.getNombre() + " " + empleado.getApellido() +
                " | Doc: " + empleado.getNumeroDocumento() +
                (departamento != null ? " | Depto: " + departamento : "");

        return new ReporteEmpleadoDto(tipo, info, salario, BigDecimal.ZERO, true);
    }

    private String obtenerDepartamento(Empleado e) {
        return switch (e) {
            case Gerente g -> g.getDepartamento();
            case EmpleadoTiempoCompleto etc -> etc.getDepartamento();
            case EmpleadoPorHora eph -> eph.getDepartamento();
            case Contratista c -> c.getDepartamento();
            default -> null;
        };
    }

    private BigDecimal obtenerSalario(Empleado e) {
        return switch (e) {
            case Gerente g -> g.getSalarioMensual();
            case EmpleadoTiempoCompleto etc -> etc.getSalarioMensual();
            case EmpleadoPorHora eph -> eph.getTarifaPorHora().multiply(BigDecimal.valueOf(eph.getHorasTrabajadas()));
            case Contratista c -> c.getMontoPorProyecto();
            default -> BigDecimal.ZERO;
        };
    }
}