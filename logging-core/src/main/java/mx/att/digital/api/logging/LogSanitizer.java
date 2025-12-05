package mx.att.digital.api.logging;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class LogSanitizer.
 */
public class LogSanitizer {

    /** The Constant VALID_EXTRA_CHARS. */
    private static final Set<Character> VALID_EXTRA_CHARS = Set.of(
            '\\', '/', '.', '-', '_', ' ', '=', ':', '#', '\n', ')', '('
    );
    
    /** The Constant SENSITIVE_FIELDS. */
    private static final Set<String> SENSITIVE_FIELDS = Set.of(
    		"sensitiveData"
    );

    /** Set to track processed objects and avoid circular references. */
    private static final Set<Object> processedObjects = new HashSet<>();

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
                    .map(LogSanitizer::sanitizeObject)
                    .collect(Collectors.toList());
        }

        // Handle maps
        if (obj instanceof Map<?, ?>) {
            return ((Map<?, ?>) obj).entrySet().stream()
                    .collect(Collectors.toMap(
                            Entry::getKey,
                            e -> sanitizeObject(e.getValue())
                    ));
        }

        // Handle complex objects (POJOs)
        Logger log = LoggerFactory.getLogger(LogSanitizer.class);
        Map<String, Object> sanitized = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            //Public complex objects field.setAccessible(false);
            Object value = null;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException ignored) {
				log.warn("Failed to access field: {}", field.getName(), ignored);
            }

            if (SENSITIVE_FIELDS.contains(field.getName())) {
                sanitized.put(field.getName(), "***");
            } else {
                sanitized.put(field.getName(), sanitizeObject(value));
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
