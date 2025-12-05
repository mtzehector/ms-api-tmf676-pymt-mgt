package mx.att.digital.api.adapters;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.models.AgreementDomain;
import mx.att.digital.api.ports.out.AgreementRepositoryPort;
import mx.att.digital.api.services.ManagementPersistenceService;
import mx.att.digital.api.services.ManagementValidationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * The Class TopUpAdapter.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ManagementAdapter implements AgreementRepositoryPort {
	/** The validation service. */
    private final ManagementValidationService validationService;
    /** The persistence service. */
    private final ManagementPersistenceService persistenceService;
	

	/** The entity manager. */
	@PersistenceContext
	private EntityManager entityManager;

	/** The top up cancel time. */
	/*
	 * Aqui las variables de entorno que se ocupen 
	 * agregarlas como atributos de la clase
	 * configurarlas en application que dependen de secrets o config maps
	 */
	@Value("${external.topUp.cancel.time}")
	private String topUpCancelTime;

	@Override
    public AgreementDomain saveAgreement(AgreementDomain agreement) {
		validationService.validateAgreement(agreement);
        return persistenceService.persistAgreement(agreement);
    }

	/**
	 * Gets the order by update.
	 *
	 * @param id              the id
	 * @param purchaseRequest the purchase request
	 * @return the order by update
	 */
	@Override
	public AgreementDomain getAgreementByUpdate(String id, AgreementDomain agreementRequest) {
		return persistenceService.getAgreementByUpdate(id, agreementRequest);
	}

}
