package mx.att.digital.api.controllers;

import mx.att.digital.api.connectors.paymentsportal.TokenRequestPlaceholder;

/**
 * Contexto de hilo para propagar el body de la petición /token
 * hacia el cliente de payments-portal sin necesidad de importar
 * explícitamente la clase en el controlador.
 *
 * Se declara en el mismo package que PaymentController para que
 * las referencias a TokenRequestContext.* compilen sin cambios
 * adicionales.
 */
public final class TokenRequestContext {

    private static final ThreadLocal<TokenRequestPlaceholder> HOLDER = new ThreadLocal<>();

    private TokenRequestContext() {
        // utility
    }

    public static void set(TokenRequestPlaceholder request) {
        HOLDER.set(request);
    }

    public static TokenRequestPlaceholder get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
