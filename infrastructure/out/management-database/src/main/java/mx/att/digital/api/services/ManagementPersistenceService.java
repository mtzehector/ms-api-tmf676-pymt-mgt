package mx.att.digital.api.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.models.AgreementDomain;

import org.springframework.stereotype.Service;

/**
 * The Class ManagementPersistenceService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ManagementPersistenceService {
	/**
	 * Persist agreement.
	 *
	 * @param agreement the agreement
	 * @return the agreement domain
	 */
	public AgreementDomain persistAgreement(AgreementDomain agreement) {
		return null;
	}
	/**
	 * Gets the agreement by update.
	 *
	 * @param id               the id
	 * @param agreementRequest the agreement request
	 * @return the agreement by update
	 */
	public AgreementDomain getAgreementByUpdate(String id, AgreementDomain agreementRequest) {
		return null;
	}

}