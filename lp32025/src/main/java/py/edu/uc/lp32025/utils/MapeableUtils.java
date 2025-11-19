package py.edu.uc.lp32025.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import py.edu.uc.lp32025.domain.Avatar;
import py.edu.uc.lp32025.domain.Mapeable;
import py.edu.uc.lp32025.domain.PosicionGPS;

import java.util.List;

public class MapeableUtils {

    private static final Logger log = LoggerFactory.getLogger(MapeableUtils.class);

    private MapeableUtils() {
        // Utilidad: no instanciable
    }

    public static void mostrarTodosEnMapa(List<Mapeable> elementos) {
        if (elementos == null || elementos.isEmpty()) {
            log.warn("No hay elementos mapeables para mostrar.");
            return;
        }

        log.info("{}", "=".repeat(70));
        log.info("  MOSTRANDO {} ELEMENTOS EN EL MAPA", elementos.size());
        log.info("{}", "=".repeat(70));

        for (Mapeable m : elementos) {
            mostrarEnMapa(m);
        }

        log.info("{}", "=".repeat(70));
    }

    private static void mostrarEnMapa(Mapeable m) {
        log.info("\n{}", "-".repeat(60));
        log.info("Tipo: {}", m.getClass().getSimpleName());

        PosicionGPS gps = m.ubicarElemento();
        Avatar avatar = m.obtenerAvatar();

        if (gps != null) {
            log.info("Ubicación: Lat {}, Lon {}", gps.latitud(), gps.longitud());
            log.info("  → https://maps.google.com/?q={},{}", gps.latitud(), gps.longitud());
        } else {
            log.info("Ubicación: No disponible");
        }

        if (avatar != null && avatar.getNick() != null) {
            log.info("Avatar: @{}", avatar.getNick());
            log.info("Imagen: {} bytes", avatar.getImagen() != null ? avatar.getImagen().length : 0);
        } else {
            log.info("Avatar: No disponible");
        }
        log.info("{}", "-".repeat(60));
    }
}