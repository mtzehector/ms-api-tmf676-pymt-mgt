package mx.att.digital.api.services;

import mx.att.digital.api.models.AgreementDomain;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagementValidationServiceTest {

    private final ManagementValidationService service = new ManagementValidationService();

    @Test
    void validateAgreement_null_throws() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.validateAgreement(null));
        assertTrue(ex.getMessage().contains("cannot be null"));
    }

    @Test
    void validateAgreement_withoutName_throws() {
        AgreementDomain agreement = AgreementDomain.builder().build();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.validateAgreement(agreement));
        assertTrue(ex.getMessage().contains("name"));
    }

    @Test
    void validateAgreement_ok_doesNotThrow() {
        AgreementDomain agreement = AgreementDomain.builder().name("Test Agreement").build();
        assertDoesNotThrow(() -> service.validateAgreement(agreement));
    }
}
