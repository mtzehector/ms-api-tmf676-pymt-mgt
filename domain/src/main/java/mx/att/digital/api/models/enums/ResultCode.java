package mx.att.digital.api.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * The Enum ResultCode.
 */
@AllArgsConstructor
@Getter
public enum ResultCode {
    
    /** The successful. */
    SUCCESSFUL(0, "Exitoso"),
    
    /** The others. */
    OTHERS(5, "Otros"),
    
    /** The invalid amount. */
    INVALID_AMOUNT(13, "Monto Inválido"),
    
    /** The invalid format. */
    INVALID_FORMAT(30, "Formato Inválido"),
    
    /** The contact telephone company. */
    CONTACT_TELEPHONE_COMPANY(60, "Comunicarse con la Telefonica"),
    
    /** The invalid phone. */
    INVALID_PHONE(83, "Teléfono Inválido"),
    
    /** The fallen authorizer. */
    FALLEN_AUTHORIZER(89, "Autorizador Abajo"),
    
    /** The timeout. */
    TIMEOUT(92, "Timeout"),
    
    /** The repeated transaction. */
    REPEATED_TRANSACTION(94, "Transacción Repetida");

    /** The code. */
    private final int code;
    
    /** The message. */
    private final String message;

    /**
     * From code.
     *
     * @param code the code
     * @return the result code
     */
    public static ResultCode fromCode(int code) {
        for (ResultCode value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }

    /**
     * From message.
     *
     * @param message the message
     * @return the result code
     */
    public static ResultCode fromMessage(String message) {
        for (ResultCode value : values()) {
            if (value.message.equalsIgnoreCase(message)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown message: " + message);
    }

}