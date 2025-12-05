package mx.att.digital.api.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * The Class LoggingAspectTest.
 */
class LoggingAspectTest {

    /** The logging aspect. */
    private LoggingAspect loggingAspect;

    /** The join point. */
    private ProceedingJoinPoint joinPoint;

    /** The signature. */
    private Signature signature;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        loggingAspect = new LoggingAspect();
        joinPoint = mock(ProceedingJoinPoint.class);
        signature = mock(Signature.class);
    }

    /**
     * Test log controller proceeds and logs.
     *
     * Se verifica que el aspecto llama a proceed() y devuelve el resultado.
     */
    @Test
    void testLogController_proceedsAndLogs() throws Throwable {
        // Simula el método interceptado
        when(signature.toShortString()).thenReturn("SampleController.sampleMethod()");
        when(joinPoint.getSignature()).thenReturn(signature);
        // Stub de proceed sin invocarlo directamente para evitar manejar Throwable
        doReturn("response").when(joinPoint).proceed();
        // Simula los argumentos del método interceptado
        when(joinPoint.getArgs()).thenReturn(new Object[] { "arg1", 42 });

        Object result = loggingAspect.logController(joinPoint);

        assertEquals("response", result);
        verify(joinPoint, times(1)).proceed();
    }

    /**
     * Cuando el flag isSanitizing está en true, el aspecto debe delegar directamente en proceed()
     * sin volver a sanitizar ni cambiar el flag.
     */
    @Test
    void testLogController_whenSanitizingFlagIsTrue() throws Throwable {
        // Establecer el flag isSanitizing a true para simular que el aspecto ya está en proceso de sanitización
        setIsSanitizing(true);

        // Crear un ProceedingJoinPoint simulado
        ProceedingJoinPoint mockJoinPoint = mock(ProceedingJoinPoint.class);
        doReturn("response").when(mockJoinPoint).proceed();

        // Crear una instancia del aspecto
        LoggingAspect localLoggingAspect = new LoggingAspect();

        // Invocar el método logController
        Object result = localLoggingAspect.logController(mockJoinPoint);

        // Verificar que el método proceed() fue llamado
        verify(mockJoinPoint).proceed();

        // Verificar que el resultado es el esperado
        assertEquals("response", result);

        // Restablecer el flag isSanitizing a false después de la prueba
        setIsSanitizing(false);
    }

    /**
     * Verifica que si proceed() lanza excepción, el aspecto:
     * - Propaga la excepción
     * - Ejecuta el bloque finally (limpia el ThreadLocal isSanitizing)
     */
    @Test
    void testLogController_whenProceedThrowsException_cleansFlagAndPropagates() throws Throwable {
        when(signature.toShortString()).thenReturn("SampleController.failingMethod()");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[] { "arg1" });

        RuntimeException expected = new RuntimeException("boom");
        // Stub de proceed para que lance la excepción sin invocarlo directamente
        doThrow(expected).when(joinPoint).proceed();

        // isSanitizing empieza en false (valor por defecto del ThreadLocal)

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> loggingAspect.logController(joinPoint));
        assertSame(expected, thrown);

        // Comprobamos que el flag se limpió (ThreadLocal.remove() en el finally).
        // Para ello, accedemos al ThreadLocal por reflexión y verificamos que su valor es null/false.
        Field field = LoggingAspect.class.getDeclaredField("isSanitizing");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        ThreadLocal<Boolean> threadLocal = (ThreadLocal<Boolean>) field.get(null);
        // Después de remove(), get() devuelve el valor inicial (false)
        assertNotEquals(Boolean.TRUE, threadLocal.get());
    }

    /**
     * Helper para manipular el ThreadLocal isSanitizing por reflexión en los tests.
     */
    private void setIsSanitizing(boolean value) throws ReflectiveOperationException {
        Field field = LoggingAspect.class.getDeclaredField("isSanitizing");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        ThreadLocal<Boolean> threadLocal = (ThreadLocal<Boolean>) field.get(null);
        threadLocal.set(value);
    }
}