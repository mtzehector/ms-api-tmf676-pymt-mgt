package mx.att.digital.api.connectors.paymentsportal;

/**
 * Contexto de hilo para propagar el body de la petición /token
 * hacia el cliente de payments-portal sin cambiar la firma pública.
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
