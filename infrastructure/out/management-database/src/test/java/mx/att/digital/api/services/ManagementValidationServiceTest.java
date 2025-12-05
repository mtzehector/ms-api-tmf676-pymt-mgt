package mx.att.digital.api.services;

import mx.att.digital.api.models.AgreementDomain;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagementValidationServiceTest {

    private final ManagementValidationService service = new ManagementValidationService();

    @Test
    void validateAgreementNullThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.validateAgreement(null));
        assertTrue(ex.getMessage().contains("cannot be null"));
    }

    @Test
    void validateAgreementWithoutNameThrows() {
        AgreementDomain agreement = AgreementDomain.builder().build();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.validateAgreement(agreement));
        assertTrue(ex.getMessage().contains("name"));
    }

    @Test
    void validateAgreementOkDoesNotThrow() {
        AgreementDomain agreement = AgreementDomain.builder().name("Test Agreement").build();
        assertDoesNotThrow(() -> service.validateAgreement(agreement));
    }
}