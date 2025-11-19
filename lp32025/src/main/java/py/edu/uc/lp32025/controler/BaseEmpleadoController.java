package py.edu.uc.lp32025.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp32025.domain.Empleado;
import py.edu.uc.lp32025.service.PersonaService;

public abstract class BaseEmpleadoController<T extends Empleado> {

    protected final PersonaService personaService;

    @Autowired
    public BaseEmpleadoController(PersonaService personaService) {
        this.personaService = personaService;
    }

    // Cambiamos Object → ahora acepta cualquier tipo de respuesta
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        T empleado = (T) personaService.getPersonaById(id);
        return ResponseEntity.ok(empleado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody T empleado) {
        empleado.setId(id);
        T updated = (T) personaService.updatePersona(id, empleado);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        personaService.deletePersona(id);
        return ResponseEntity.noContent().build();
    }
}