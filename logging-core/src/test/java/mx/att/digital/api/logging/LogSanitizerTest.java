package mx.att.digital.api.logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LogSanitizerTest {

    @AfterEach
    void resetProcessedObjects() throws ReflectiveOperationException {
        // Limpia el Set estático processedObjects entre tests para evitar interferencias
        Field field = LogSanitizer.class.getDeclaredField("processedObjects");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Set<Object> processed = (Set<Object>) field.get(null);
        processed.clear();
    }

    @Test
    void sanitize_null_returnsNull() {
        assertNull(LogSanitizer.sanitize((String) null));
    }

    @Test
    void sanitize_replacesLineBreaksAndIllegalChars() {
        String input = "Hola\nMundo\r\t!";
        String sanitized = LogSanitizer.sanitize(input);
        // \n y \r se cambian primero a '%', luego cleanString procesa cada char
        // '\t' y '!' no están permitidos -> se convierten a '%'
        assertFalse(sanitized.contains("\n"));
        assertFalse(sanitized.contains("\r"));
        assertFalse(sanitized.contains("\t"));
        assertFalse(sanitized.contains("!"));
        assertTrue(sanitized.contains("%"));
    }

    @Test
    void sanitizeObject_simpleTypes() {
        assertEquals(LogSanitizer.sanitize("abc"), LogSanitizer.sanitizeObject("abc"));
        assertEquals(LogSanitizer.sanitize(123), LogSanitizer.sanitizeObject(123));
        assertEquals(LogSanitizer.sanitize(true), LogSanitizer.sanitizeObject(true));
    }

    @Test
    void sanitizeObject_nullReturnsNull() {
        assertNull(LogSanitizer.sanitizeObject(null));
    }

    @Test
    void sanitizeObject_collection() {
        List<String> list = Arrays.asList("a\nb", "c");
        Object result = LogSanitizer.sanitizeObject(list);

        assertTrue(result instanceof List<?>);
        @SuppressWarnings("unchecked")
        List<Object> sanitizedList = (List<Object>) result;
        assertEquals(2, sanitizedList.size());
        assertFalse(sanitizedList.get(0).toString().contains("\n"));
    }

    @Test
    void sanitizeObject_map() {
        Map<String, Object> map = new HashMap<>();
        map.put("key", "value\nwithBreak");
        Object result = LogSanitizer.sanitizeObject(map);

        assertTrue(result instanceof Map<?, ?>);
        @SuppressWarnings("unchecked")
        Map<String, Object> sanitized = (Map<String, Object>) result;
        assertTrue(sanitized.containsKey("key"));
        assertFalse(String.valueOf(sanitized.get("key")).contains("\n"));
    }

    static class SamplePojo {
        public String name;
        public String sensitiveData;
        // Campo privateField eliminado porque no aporta valor al test y Sonar lo marcaba como unused
    }

    @Test
    void sanitizeObject_pojoMasksSensitiveField() {
        SamplePojo pojo = new SamplePojo();
        pojo.name = "John";
        pojo.sensitiveData = "123456";

        Object result = LogSanitizer.sanitizeObject(pojo);

        assertTrue(result instanceof Map<?, ?>);
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) result;

        // Campo sensible debe estar enmascarado
        assertEquals("***", map.get("sensitiveData"));
        // Campo público no sensible sanitizado como String normal
        assertEquals(LogSanitizer.sanitize("John"), map.get("name"));
    }

    @Test
    void sanitizeException_returnsClassAndMessage() {
        RuntimeException ex = new RuntimeException("boom");
        String sanitized = LogSanitizer.sanitizeException(ex);
        assertTrue(sanitized.contains("RuntimeException"));
        assertTrue(sanitized.contains("boom"));
    }

    @Test
    void sanitizeException_null_returnsNull() {
        assertNull(LogSanitizer.sanitizeException(null));
    }
}