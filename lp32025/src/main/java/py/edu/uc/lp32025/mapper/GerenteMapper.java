package py.edu.uc.lp32025.mapper;

import org.springframework.stereotype.Component;
import py.edu.uc.lp32025.domain.Gerente;
import py.edu.uc.lp32025.dto.ReporteEmpleadoDto;

@Component
public class GerenteMapper extends EmpleadoMapper {

    // NO usamos @Override porque NO es un override real
    // Es un método NUEVO específico para Gerente
    public ReporteEmpleadoDto toReporteDto(Gerente gerente) {
        ReporteEmpleadoDto base = super.toReporteDto(gerente); // aquí el cast implícito es seguro

        String infoCompleta = base.informacionCompleta() +
                " | Nivel: " + gerente.getNivelJerarquico() +
                " | Gestiona: " + gerente.getDepartamentoGestionado();

        return new ReporteEmpleadoDto(
                "Gerente",
                infoCompleta,
                base.salario(),
                base.impuestoNeto(),
                base.datosValidos()
        );
    }
}