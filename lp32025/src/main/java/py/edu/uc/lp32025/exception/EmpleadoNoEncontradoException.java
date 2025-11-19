package py.edu.uc.lp32025.exception;

/**
 * Runtime Exception lanzada cuando no se encuentra un empleado
 * en operaciones críticas (búsqueda por ID, documento, etc.).
 */
public class EmpleadoNoEncontradoException extends RuntimeException {

    private final Long id;
    private final String criterio;

    public EmpleadoNoEncontradoException(Long id) {
        super("Empleado no encontrado con ID: " + id);
        this.id = id;
        this.criterio = "ID";
    }

    public EmpleadoNoEncontradoException(String documento) {
        super("Empleado no encontrado con documento: " + documento);
        this.id = null;
        this.criterio = "documento";
    }

    public Long getId() { return id; }
    public String getCriterio() { return criterio; }
}