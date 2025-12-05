package mx.att.digital.api.model.enums;

import mx.att.digital.api.models.enums.ResultCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The Class ResultCodeTest.
 */
class ResultCodeTest {

	/**
	 * Test from code valid codes should return enum.
	 */
	@Test
	void testFromCode_validCodes_shouldReturnEnum() {
		assertEquals(ResultCode.SUCCESSFUL, ResultCode.fromCode(0));
		assertEquals(ResultCode.OTHERS, ResultCode.fromCode(5));
		assertEquals(ResultCode.INVALID_AMOUNT, ResultCode.fromCode(13));
		assertEquals(ResultCode.INVALID_FORMAT, ResultCode.fromCode(30));
		assertEquals(ResultCode.CONTACT_TELEPHONE_COMPANY, ResultCode.fromCode(60));
		assertEquals(ResultCode.INVALID_PHONE, ResultCode.fromCode(83));
		assertEquals(ResultCode.FALLEN_AUTHORIZER, ResultCode.fromCode(89));
		assertEquals(ResultCode.TIMEOUT, ResultCode.fromCode(92));
		assertEquals(ResultCode.REPEATED_TRANSACTION, ResultCode.fromCode(94));
	}

	/**
	 * Test from code invalid code should throw exception.
	 */
	@Test
	void testFromCode_invalidCode_shouldThrowException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			ResultCode.fromCode(9999);
		});
		assertEquals("Unknown code: 9999", exception.getMessage());
	}

	/**
	 * Test from message valid messages should return enum.
	 */
	@Test
	void testFromMessage_validMessages_shouldReturnEnum() {
		assertEquals(ResultCode.SUCCESSFUL, ResultCode.fromMessage("Exitoso"));
		assertEquals(ResultCode.OTHERS, ResultCode.fromMessage("Otros"));
		assertEquals(ResultCode.INVALID_AMOUNT, ResultCode.fromMessage("Monto Inválido"));
		assertEquals(ResultCode.INVALID_FORMAT, ResultCode.fromMessage("Formato Inválido"));
		assertEquals(ResultCode.CONTACT_TELEPHONE_COMPANY, ResultCode.fromMessage("Comunicarse con la Telefonica"));
		assertEquals(ResultCode.INVALID_PHONE, ResultCode.fromMessage("Teléfono Inválido"));
		assertEquals(ResultCode.FALLEN_AUTHORIZER, ResultCode.fromMessage("Autorizador Abajo"));
		assertEquals(ResultCode.TIMEOUT, ResultCode.fromMessage("Timeout"));
		assertEquals(ResultCode.REPEATED_TRANSACTION, ResultCode.fromMessage("Transacción Repetida"));
	}

	/**
	 * Test from message invalid message should throw exception.
	 */
	@Test
	void testFromMessage_invalidMessage_shouldThrowException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			ResultCode.fromMessage("Desconocido");
		});
		assertEquals("Unknown message: Desconocido", exception.getMessage());
	}

	/**
	 * Test getters should return expected values.
	 */
	@Test
	void testGetters_shouldReturnExpectedValues() {
		ResultCode codeEnum = ResultCode.SUCCESSFUL;
		assertEquals(0, codeEnum.getCode());
		assertEquals("Exitoso", codeEnum.getMessage());
	}

	/**
	 * Test from message case insensitive should return enum.
	 */
	@Test
	void testFromMessage_caseInsensitive_shouldReturnEnum() {
		// Validar branch de equalsIgnoreCase
		assertEquals(ResultCode.SUCCESSFUL, ResultCode.fromMessage("eXiToSo"));
	}

	/**
	 * Test from code all enum values checked.
	 */
	@Test
	void testFromCode_allEnumValuesChecked() {
		for (ResultCode value : ResultCode.values()) {
			assertEquals(value, ResultCode.fromCode(value.getCode()));
		}
	}

	/**
	 * Test from message all enum values checked.
	 */
	@Test
	void testFromMessage_allEnumValuesChecked() {
		for (ResultCode value : ResultCode.values()) {
			assertEquals(value, ResultCode.fromMessage(value.getMessage()));
		}
	}
}