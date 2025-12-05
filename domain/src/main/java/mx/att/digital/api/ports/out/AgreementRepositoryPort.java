package mx.att.digital.api.ports.out;

import mx.att.digital.api.models.AgreementDomain;

public interface AgreementRepositoryPort {
	AgreementDomain saveAgreement(AgreementDomain agreement);
	AgreementDomain getAgreementByUpdate(String id, AgreementDomain agreementRequest);
}
