package mx.att.digital.api.services;

import mx.att.digital.api.models.AgreementDomain;
import mx.att.digital.api.models.enums.ResultCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AgreementServiceUseCaseTest {

    @Test
    void createAgreement_setsSuccessfulStatus() {
        AgreementServiceUseCase svc = new AgreementServiceUseCase();
        // Usar builder sin campos específicos para evitar métodos inexistentes como id(...)
        AgreementDomain input = AgreementDomain.builder().build();

        AgreementDomain out = svc.createAgreement(input);

        assertNotNull(out);
        assertEquals(ResultCode.SUCCESSFUL.name(), out.getStatus());
    }
}
