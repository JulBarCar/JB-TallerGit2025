package py.edu.uc.lp32025.mapper;

import py.edu.uc.lp32025.domain.Persona;
import py.edu.uc.lp32025.dto.ReporteEmpleadoDto;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class PersonaMapper extends BaseMapper<Persona, ReporteEmpleadoDto> {

    public PersonaMapper() {
        super(Persona.class, ReporteEmpleadoDto.class);
    }

    @Override
    protected void copyEntityToDto(Persona entity, ReporteEmpleadoDto dto) {}

    @Override
    protected void copyDtoToEntity(ReporteEmpleadoDto dto, Persona entity) {}

    public ReporteEmpleadoDto toReporteDto(Persona persona) {
        String info = persona.getNombre() + " " + persona.getApellido() +
                " | Doc: " + persona.getNumeroDocumento();

        return new ReporteEmpleadoDto(
                persona.getId(),
                "Persona",
                info,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                true
        );
    }
}