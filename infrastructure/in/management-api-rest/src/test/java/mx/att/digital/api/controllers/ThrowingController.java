package mx.att.digital.api.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import mx.att.digital.api.error.exception.ValidationResponseException;

@RestController
@RequestMapping(path = "/__test__/throw", produces = MediaType.APPLICATION_JSON_VALUE)
public class ThrowingController {
    @GetMapping("/illegal")
    public String illegal() {
        // Mapea a 400 vía ControllerAdvice.handleValidationException(...)
        throw new ValidationResponseException("400", "Bad Request", "bad arg");
    }
    @GetMapping("/null")
    public String npe() {
        // Genérica no mapeada -> 500
        throw new NullPointerException("npe");
    }
}
