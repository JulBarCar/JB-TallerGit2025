package py.edu.uc.lp32025.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import py.edu.uc.lp32025.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MapeableFactory {

    private static final Logger log = LoggerFactory.getLogger(MapeableFactory.class);

    private MapeableFactory() {
        // Utilidad: no instanciable
    }

    public static List<Mapeable> crearElementosDemo() {
        log.info("Iniciando creación de elementos de demostración (Mapeable)...");

        List<Mapeable> elementos = new ArrayList<>();
        elementos.add(crearEmpleadoTiempoCompletoDemo());
        elementos.add(crearEmpleadoPorHoraDemo());
        elementos.add(crearContratistaDemo());
        elementos.add(crearVehiculoDemo());
        elementos.add(crearEdificioDemo());

        log.info("Creados {} elementos mapeables exitosamente.", elementos.size());
        return elementos;
    }

    public static Mapeable crearEmpleadoTiempoCompletoDemo() {
        log.debug("Creando EmpleadoTiempoCompleto de demo...");
        EmpleadoTiempoCompleto p = new EmpleadoTiempoCompleto();
        p.setNombre("Luis");
        p.setApellido("Ramírez");
        p.setNumeroDocumento("5554443");
        p.setFechaNacimiento(LocalDate.of(1992, 7, 20));
        p.setSalarioMensual(new BigDecimal("6000000"));
        p.setDepartamento("RRHH");
        p.setLatitud(-25.2916);
        p.setLongitud(-57.6471);
        p.setNick("luis_rrhh");
        p.setImagen("AVATAR_LUIS".getBytes());
        return p;
    }

    public static Mapeable crearEmpleadoPorHoraDemo() {
        log.debug("Creando EmpleadoPorHora de demo...");
        EmpleadoPorHora p = new EmpleadoPorHora();
        p.setNombre("Sofía");
        p.setApellido("Martínez");
        p.setNumeroDocumento("7778889");
        p.setFechaNacimiento(LocalDate.of(1995, 4, 12));
        p.setTarifaPorHora(new BigDecimal("120000"));
        p.setHorasTrabajadas(50);
        p.setLatitud(-25.2700);
        p.setLongitud(-57.6600);
        p.setNick("sofia_flex");
        p.setImagen("AVATAR_SOFIA".getBytes());
        return p;
    }

    public static Mapeable crearContratistaDemo() {
        log.debug("Creando Contratista de demo...");
        Contratista c = new Contratista();
        c.setNombre("Diego");
        c.setApellido("Silva");
        c.setNumeroDocumento("9991112");
        c.setFechaNacimiento(LocalDate.of(1987, 11, 5));
        c.setMontoPorProyecto(new BigDecimal("3000000"));
        c.setProyectosCompletados(4);
        c.setFechaFinContrato(LocalDate.now().plusMonths(3));
        c.setLatitud(-25.3100);
        c.setLongitud(-57.5900);
        c.setNick("diego_pro");
        c.setImagen("AVATAR_DIEGO".getBytes());
        return c;
    }

    public static Mapeable crearVehiculoDemo() {
        log.debug("Creando Vehiculo de demo...");
        Vehiculo v = new Vehiculo();
        v.setMarca("Toyota");
        v.setModelo("Corolla");
        v.setPatente("ABC-123");
        v.setLatitud(-25.3005);
        v.setLongitud(-57.5700);
        v.setNick("corolla_abc123");
        v.setImagen("VEHICULO_TOYOTA".getBytes());
        return v;
    }

    public static Mapeable crearEdificioDemo() {
        log.debug("Creando Edificio de demo...");
        Edificio e = new Edificio();
        e.setNombre("Torre Central");
        e.setDireccion("Av. Mariscal López 1000");
        e.setPisos(15);
        e.setLatitud(-25.2810);
        e.setLongitud(-57.6350);
        e.setNick("torre_central");
        e.setImagen("EDIFICIO_TORRE".getBytes());
        return e;
    }
}