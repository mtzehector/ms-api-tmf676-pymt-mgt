package mx.att.digital.api.services;

import lombok.RequiredArgsConstructor;
import mx.att.digital.api.models.AgreementDomain;
import org.springframework.stereotype.Service;

/**
 * The Class ManagementValidationService.
 */
@Service
@RequiredArgsConstructor
public class ManagementValidationService {
	/**
	 * Validate agreement.
	 *
	 * @param agreement the agreement
	 */
	public void validateAgreement(AgreementDomain agreement) {
		// Implement validation logic here
		if (agreement == null) {
			throw new IllegalArgumentException("Agreement cannot be null");
		}
		if (agreement.getName() == null || agreement.getName().isEmpty()) {
			throw new IllegalArgumentException("Agreement name cannot be null or empty");
		}
		// Add more validation rules as needed ...
	}

}
