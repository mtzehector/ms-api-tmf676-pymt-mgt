package mx.att.digital.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * The Class AgreementDomain.
 */
@Data

/**
 * The Class AgreementDomainBuilder.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementDomain {

	/** The agreement type. */
	@NotNull
    private String agreementType;
	
	/** The name. */
	@NotNull
    private String name;
	
	/** The agreement item. */
	@Valid
    @NotNull
    private List<AgreementItemDomain> agreementItem;
	
	/** The engaged party. */
	@Valid
    @NotNull
    private List<RelatedPartyDomain> engagedParty;
	
	/** The status. */
	private @Nullable String status;
}
