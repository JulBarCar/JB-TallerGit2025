package py.edu.uc.lp32025;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import py.edu.uc.lp32025.utils.MapeableFactory;
import py.edu.uc.lp32025.utils.MapeableUtils;

@SpringBootApplication
public class DemoMapeableApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoMapeableApplication.class, args);
    }

    @Bean
    CommandLineRunner demoMapeable() {
        return args -> {
            // Ya no usamos System.out → todo va por log
            var elementos = MapeableFactory.crearElementosDemo();
            MapeableUtils.mostrarTodosEnMapa(elementos);
        };
    }
}