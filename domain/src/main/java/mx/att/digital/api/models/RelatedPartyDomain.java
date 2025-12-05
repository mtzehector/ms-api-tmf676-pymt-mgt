package mx.att.digital.api.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * The Class RelatedPartyDomain.
 */
@Data
public class RelatedPartyDomain {
	
	/** The id. */
	@NotNull
    private String id; // obligatorio

    /** The name. */
    @NotNull
    private String name; // obligatorio

    /** The at referred type. */
    @NotNull
    @Pattern(regexp = "Customer|Organization|Employee") // Regla de validación para los valores permitidos
    private String atReferredType; // obligatorio

    /** The role. */
    private String role; // opcional
}
