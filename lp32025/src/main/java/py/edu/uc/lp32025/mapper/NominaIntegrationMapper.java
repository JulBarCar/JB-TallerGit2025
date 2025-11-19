package py.edu.uc.lp32025.mapper;

import py.edu.uc.lp32025.domain.Empleado;
import py.edu.uc.lp32025.dto.NominaResponseDTO;
import py.edu.uc.lp32025.dto.external.NominaExternaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NominaIntegrationMapper {

    private final EmpleadoMapper empleadoMapper;

    @Autowired
    public NominaIntegrationMapper(EmpleadoMapper empleadoMapper) {
        this.empleadoMapper = empleadoMapper;
    }

    public NominaResponseDTO toNominaResponse(Empleado empleado, NominaExternaResponseDTO externa) {
        NominaResponseDTO response = new NominaResponseDTO();
        response.setEmpleadoId(empleado.getId());
        response.setNombre(empleado.getNombre() + " " + empleado.getApellido());
        response.setDocumento(empleado.getNumeroDocumento());
        response.setMontoAPagar(externa.getSalarioNeto());
        response.setPeriodo(externa.getPeriodo());
        response.setEstado(externa.getEstado());

        if ("PAGADO".equalsIgnoreCase(externa.getEstado())) {
            response.setMensaje("Nómina procesada y pagada correctamente");
        } else if ("PENDIENTE".equalsIgnoreCase(externa.getEstado())) {
            response.setMensaje("Nómina en proceso de pago");
        } else {
            response.setMensaje("Revisar con RRHH: " + externa.getEstado());
        }

        return response;
    }
}