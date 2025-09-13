package py.edu.uc.lp32025.controler;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import py.edu.uc.lp32025.dto.GreetingDTO;

@RestController
public class IndexControler {

    @GetMapping("/")
    public RedirectView redirectToHolaMundo(@RequestParam(value = "name", required = false) String name) {
        String redirectUrl = "/HolaMundo";
        if (name != null && !name.isEmpty()) {
            redirectUrl += "?name=" + name;
        }
        return new RedirectView(redirectUrl);
    }

    @GetMapping("/HolaMundo")
    public GreetingDTO holaMundo(@RequestParam(value = "name", defaultValue = "Mundo") String name) {
        return new GreetingDTO("¡Hola, " + name + "!");
    }
}