package mx.att.digital.api.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

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
     * @throws Throwable the throwable
     */
    @Test
    void testLogController_proceedsAndLogs() throws Throwable {
        // Simula el método interceptado
        when(signature.toShortString()).thenReturn("SampleController.sampleMethod()");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.proceed()).thenReturn("response");
        // Simula los argumentos del método interceptado
        when(joinPoint.getArgs()).thenReturn(new Object[] { "arg1", 42 });

        Object result = loggingAspect.logController(joinPoint);

        assertEquals("response", result);
        verify(joinPoint, times(1)).proceed();
    }
    
    @Test
    void testLogController_whenSanitizingFlagIsTrue() throws Throwable {
        // Establecer el flag isSanitizing a true para simular que el aspecto ya está en proceso de sanitización
        setIsSanitizing(true);

        // Crear un ProceedingJoinPoint simulado
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.proceed()).thenReturn("response");

        // Crear una instancia del aspecto
        LoggingAspect loggingAspect = new LoggingAspect();

        // Invocar el método logController
        Object result = loggingAspect.logController(joinPoint);

        // Verificar que el método proceed() fue llamado
        verify(joinPoint).proceed();

        // Verificar que el resultado es el esperado
        assertEquals("response", result);

        // Restablecer el flag isSanitizing a false después de la prueba
        setIsSanitizing(false);
    }

    private void setIsSanitizing(boolean value) throws NoSuchFieldException, IllegalAccessException {
        Field field = LoggingAspect.class.getDeclaredField("isSanitizing");
        field.setAccessible(true);
        field.set(null, value);
    }

}
