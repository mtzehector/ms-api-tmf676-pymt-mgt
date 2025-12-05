package mx.att.digital.api.handlers;

import mx.att.digital.api.error.exception.ExternalSystemError;
import mx.att.digital.api.error.exception.ValidationResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * The Class ExceptionThrowingTestController.
 */
@RestController
class ExceptionThrowingTestController {

    /**
     * Throw external system error.
     */
    @GetMapping("/test/external-system-error")
    public void throwExternalSystemError() {
        throw new ExternalSystemError("ExternalError");
    }

    /**
     * Throw custom validation exception.
     */
    @GetMapping("/test/custom-validation-exception")
    public void throwCustomValidationException() {
        throw new ValidationResponseException("30", "Validation error", "field must not be null");
    }
}
