package mx.att.digital.api.models.filtercriteria;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * The Class TopUpOrderFilterCriteria.
 */
@Data

/**
 * The Class TopUpOrderFilterCriteriaBuilder.
 */
@Builder
public class TopUpOrderFilterCriteria {
	
	/** The external id. */
	private String externalId;
	
	/** The id. */
	private Long id;
	
	/** The amount. */
	private Float amount;
	
	/** The currency. */
	private String currency;
	
	/** The confirmation id. */
	private String confirmationId;
	
	/** The code. */
	private Integer code;
	
	/** The reason. */
	private String reason;
	
	/** The logical resource type. */
	private String logicalResourceType;
	
	/** The logical resource id. */
	private Long logicalResourceId;
	
	/** The logical resource value. */
	private String logicalResourceValue;
	
	/** The channel id. */
	private Long channelId;
	
	/** The channel external id. */
	private String channelExternalId;
	
	/** The requestor id. */
	private Long requestorId;
	
	/** The requestor external id. */
	private String requestorExternalId;
	
	/** The requestor role. */
	private String requestorRole;
	
	/** The status. */
	private String status;
	
	/** The created at. */
	private OffsetDateTime createdAt;
	
	/** The modified at. */
	private OffsetDateTime modifiedAt;
	
	/** The created by. */
	private String createdBy;
	
	/** The modified by. */
	private String modifiedBy;

}
