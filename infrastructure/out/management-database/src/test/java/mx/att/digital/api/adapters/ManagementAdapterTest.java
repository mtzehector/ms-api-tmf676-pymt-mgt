package mx.att.digital.api.adapters;

import mx.att.digital.api.models.AgreementDomain;
import mx.att.digital.api.services.ManagementPersistenceService;
import mx.att.digital.api.services.ManagementValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManagementAdapterTest {

    @Mock
    private ManagementValidationService validationService;
    @Mock
    private ManagementPersistenceService persistenceService;

    @InjectMocks
    private ManagementAdapter adapter;

    @Test
    void saveAgreementCallsValidateAndPersist() {
        AgreementDomain in = AgreementDomain.builder().name("Agg").build();
        AgreementDomain expected = AgreementDomain.builder().name("Agg").build();

        when(persistenceService.persistAgreement(in)).thenReturn(expected);

        AgreementDomain out = adapter.saveAgreement(in);

        verify(validationService).validateAgreement(in);
        verify(persistenceService).persistAgreement(in);
        assertSame(expected, out);
    }

    @Test
    void getAgreementByUpdateDelegatesToPersistence() {
        AgreementDomain req = AgreementDomain.builder().name("Req").build();
        AgreementDomain resp = AgreementDomain.builder().name("Resp").build();

        when(persistenceService.getAgreementByUpdate("ID-1", req)).thenReturn(resp);

        AgreementDomain out = adapter.getAgreementByUpdate("ID-1", req);

        verify(persistenceService).getAgreementByUpdate("ID-1", req);
        assertSame(resp, out);
    }
}