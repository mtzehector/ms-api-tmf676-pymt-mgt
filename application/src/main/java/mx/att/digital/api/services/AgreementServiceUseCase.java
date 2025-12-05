package mx.att.digital.api.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.models.AgreementDomain;
import mx.att.digital.api.models.enums.ResultCode;
import org.springframework.stereotype.Service;

/**
 * The Class AgreementServiceUseCase.
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgreementServiceUseCase {
	
	/**
	 * Creates the agreement.
	 *
	 * @param agreement the agreement
	 * @return the agreement domain
	 */
	public AgreementDomain createAgreement(AgreementDomain agreement) {
		log.info("Creating agreement: {}", agreement);
		agreement.setStatus(ResultCode.SUCCESSFUL.name());
		return agreement;
	}

}
