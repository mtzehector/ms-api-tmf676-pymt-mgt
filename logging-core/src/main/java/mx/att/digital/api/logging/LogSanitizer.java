package mx.att.digital.api.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * The Class LogSanitizer.
 */
public class LogSanitizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogSanitizer.class);

    /** The Constant VALID_EXTRA_CHARS. */
    private static final Set<Character> VALID_EXTRA_CHARS = Set.of(
            '\\', '/', '.', '-', '_', ' ', '=', ':', '#', '\n', ')', '('
    );

    /** The Constant SENSITIVE_FIELDS. */
    private static final Set<String> SENSITIVE_FIELDS = Set.of(
    		"sensitiveData"
    );

    /**
     * Instantiates a new log sanitizer.
     */
    private LogSanitizer() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Sanitize.
     *
     * @param input the input
     * @return the string
     */
    public static String sanitize(String input) {
        return input == null ? null : cleanString(input.replaceAll("[\r\n]", "%"));
    }

    /**
     * Sanitize.
     *
     * @param input the input
     * @return the string
     */
    public static String sanitize(Object input) {
        return input == null ? null : sanitize(String.valueOf(input));
    }

    /**
     * Clean string.
     *
     * @param aString the a string
     * @return the string
     */
    public static String cleanString(String aString) {
        if (aString == null) {
            return null;
        }
        StringBuilder cleanString = new StringBuilder();
        for (int i = 0; i < aString.length(); ++i) {
            cleanString.append(cleanChar(aString.charAt(i)));
        }
        return cleanString.toString();
    }

    /**
     * Clean char.
     *
     * @param aChar the a char
     * @return the char
     */
    private static char cleanChar(char aChar) {
        if (Character.isLetterOrDigit(aChar) || VALID_EXTRA_CHARS.contains(aChar)) {
            return aChar;
        }
        return '%';
    }

    /**
     * Sanitize object.
     *
     * @param obj the obj
     * @return the object
     */
    public static Object sanitizeObject(Object obj) {
        return sanitizeObject(obj, new HashSet<>());
    }

    /**
     * Sanitize object with circular reference tracking.
     *
     * @param obj the obj
     * @param processedObjects set to track processed objects
     * @return the sanitized object
     */
    private static Object sanitizeObject(Object obj, Set<Object> processedObjects) {
        if (obj == null) return null;

        // If the object has already been processed, return it to avoid circular references
        if (processedObjects.contains(obj)) {
            return obj;
        }

        // Mark the object as processed
        processedObjects.add(obj);

        // Handle simple types
        if (obj instanceof String || obj instanceof Number || obj instanceof Boolean) {
            return sanitize(obj);
        }

        // Handle collections
        if (obj instanceof Collection<?>) {
            return ((Collection<?>) obj).stream()
                    .map(item -> sanitizeObject(item, processedObjects))
                    .toList();
        }

        // Handle maps
        if (obj instanceof Map<?, ?>) {
            return ((Map<?, ?>) obj).entrySet().stream()
                    .collect(Collectors.toMap(
                            Entry::getKey,
                            e -> sanitizeObject(e.getValue(), processedObjects)
                    ));
        }

        // Handle complex objects (POJOs) - only public fields
        Map<String, Object> sanitized = new HashMap<>();
        for (Field field : obj.getClass().getFields()) {
            Object value = null;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException ex) {
                LOGGER.warn("Failed to access field: {}", field.getName(), ex);
                continue;
            }

            if (SENSITIVE_FIELDS.contains(field.getName())) {
                sanitized.put(field.getName(), "***");
            } else {
                sanitized.put(field.getName(), sanitizeObject(value, processedObjects));
            }
        }
        return sanitized;
    }

    /**
     * Sanitize exception.
     *
     * @param ex the ex
     * @return the string
     */
    public static String sanitizeException(Throwable ex) {
        if (ex == null) return null;
        return ex.getClass().getSimpleName() + ": " + ex.getMessage();
    }
}
