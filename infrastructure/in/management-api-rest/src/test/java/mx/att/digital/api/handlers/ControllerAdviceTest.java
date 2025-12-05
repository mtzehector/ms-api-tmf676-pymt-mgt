package mx.att.digital.api.handlers;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.RestClientException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class ControllerAdviceTest {

    private final ControllerAdvice advice = new ControllerAdvice();

    @Test
    void handleValidationException_returns400WithBody() throws ReflectiveOperationException {
        Class<?> vexClass = findClass("mx.att.digital.api.util.exception.ValidationResponseException",
                                      "mx.att.digital.api.error.exception.ValidationResponseException");
    
        if (vexClass == null) {
            // Si no existe en ninguno de los dos paquetes, fallamos el test explícitamente
            fail("ValidationResponseException not found");
        }
    
        Object vexInstance = tryBuildVex(vexClass, "400", "Bad Request", "detail-xyz");

        Method target = findMethodAccepting(vexClass);
        assertNotNull(target, "Handler for ValidationResponseException not found");
    
        @SuppressWarnings("unchecked")
        ResponseEntity<Map<String, Object>> response =
                (ResponseEntity<Map<String, Object>>) target.invoke(advice, vexInstance);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("400", body.get("code"));
    }

    @Test
    void handleMethodArgumentNotValidException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        FieldError fieldError = new FieldError("objectName", "fieldName", "must not be null");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Map<String, Object>> response = advice.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("30", body.get("code"));
    
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) body.get("details");
        assertNotNull(details);
        assertEquals("must not be null", details.get("fieldName"));
    }

    @Test
    void handleBalancePlatformConnectionErrorException() throws ReflectiveOperationException {
        Class<?> exClass = findClass("mx.att.digital.api.util.exception.BalancePlatformConnectionErrorException",
                                     "mx.att.digital.api.error.exception.BalancePlatformConnectionErrorException");
    
        // Si la clase no está presente en el classpath, se marca el test como "skipped" en lugar de hacer System.out/return
        assumeTrue(exClass != null, "Skipping handleBalancePlatformConnectionErrorException: Class not found");

        // Esta excepción necesita un RestClientException en el constructor
        Object exInstance = tryBuildExceptionWithCode(exClass, "503", "Service Unavailable", "Connection failed");
    
        Method target = findMethodAccepting(exClass);
    
        if (target != null) {
            @SuppressWarnings("unchecked")
            ResponseEntity<?> response = (ResponseEntity<?>) target.invoke(advice, exInstance);
            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        }
    }

    @Test
    void handleExternalSystemError() throws ReflectiveOperationException {
        Class<?> exClass = findClass("mx.att.digital.api.util.exception.ExternalSystemError",
                                     "mx.att.digital.api.error.exception.ExternalSystemError");
    
        // De nuevo, si la clase no existe se marca el test como "skipped"
        assumeTrue(exClass != null, "Skipping handleExternalSystemError: Class not found");

        Object exInstance = tryBuildExceptionWithCode(exClass, "500", "Internal Error", "External system fail");
        Method target = findMethodAccepting(exClass);
    
        if (target != null) {
            @SuppressWarnings("unchecked")
            ResponseEntity<?> response = (ResponseEntity<?>) target.invoke(advice, exInstance);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }
    }

    // --- Helpers ---

    /**
     * Intenta cargar la primera clase existente entre los nombres proporcionados.
     * Si no se encuentra ninguna, devuelve null.
     */
    private Class<?> findClass(String... classNames) {
        for (String name : classNames) {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException ignored) {
                // Intentionally ignored: probamos con el siguiente nombre de clase
            }
        }
        return null;
    }

    private Method findMethodAccepting(Class<?> paramType) {
        for (Method m : ControllerAdvice.class.getDeclaredMethods()) {
            Class<?>[] types = m.getParameterTypes();
            if (types.length == 1 && types[0].isAssignableFrom(paramType)) {
                return m;
            }
        }
        return null;
    }

    private static Object tryBuildVex(Class<?> vexClass, String code, String reason, String details)
            throws ReflectiveOperationException {
        // Try 3-string constructor first
        for (Constructor<?> c : vexClass.getDeclaredConstructors()) {
            if (c.getParameterCount() == 3) {
                c.setAccessible(true);
                return c.newInstance(code, reason, details);
            }
        }
        // Fallback
        Object inst = vexClass.getDeclaredConstructor().newInstance();
        setIfPresent(inst, "code", code);
        setIfPresent(inst, "reason", reason);
        setIfPresent(inst, "details", details);
        return inst;
    }

    private static Object tryBuildExceptionWithCode(Class<?> exClass, String code, String reason, String message)
            throws ReflectiveOperationException {
        Object instance = null;
    
        // 1. Try standard constructor(String message)
        try {
            instance = exClass.getConstructor(String.class).newInstance(message);
        } catch (ReflectiveOperationException e) {
            // 2. Try constructor(RestClientException) - specific for BalancePlatformConnectionErrorException
            try {
                Constructor<?> c = exClass.getConstructor(RestClientException.class);
                instance = c.newInstance(new RestClientException(message));
            } catch (ReflectiveOperationException e2) {
                 // 3. Try no-arg
                 try {
                    instance = exClass.getDeclaredConstructor().newInstance();
                 } catch (ReflectiveOperationException e3) {
                     // 4. Try (code, reason, message) - custom
                     for (Constructor<?> c : exClass.getConstructors()) {
                         if (c.getParameterCount() == 3) {
                             return c.newInstance(code, reason, message);
                         }
                     }
                 }
            }
        }

        if (instance != null) {
            // Best-effort: si los campos existen, se intentan setear para las pruebas
            setIfPresent(instance, "code", code);
            setIfPresent(instance, "reason", reason);
        } else {
             throw new IllegalStateException("Could not instantiate " + exClass.getName());
        }
        return instance;
    }

    private static void setIfPresent(Object target, String fieldName, Object value) {
        Field f = findField(target.getClass(), fieldName);
        if (f == null) {
            return;
        }
        try {
            f.setAccessible(true);
            f.set(target, value);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            // Intentionally ignored: si no se puede setear, no es crítico para el test
        }
    }

    private static Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}
