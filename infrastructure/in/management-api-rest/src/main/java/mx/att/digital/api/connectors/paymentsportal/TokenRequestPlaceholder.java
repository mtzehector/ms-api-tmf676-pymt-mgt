package mx.att.digital.api.connectors.paymentsportal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO de marcador de posición para el body del token.
 * Ignora todos los campos recibidos para evitar errores de parseo.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenRequestPlaceholder {
    // Intencionalmente vacío
}
