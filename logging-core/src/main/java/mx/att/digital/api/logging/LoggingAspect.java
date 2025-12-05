package mx.att.digital.api.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * The Class LoggingAspect.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    // Uso de ThreadLocal para evitar condiciones de carrera y manejar el estado por hilo
    private static final ThreadLocal<Boolean> isSanitizing = ThreadLocal.withInitial(() -> false);

    /**
     * Logs the method execution details for methods within classes annotated
     * with @RestController.
     *
     * @param jp the join point representing the method execution
     * @return the result of the method execution
     * @throws Throwable if any error occurs during method execution
     */
    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logController(ProceedingJoinPoint jp) throws Throwable {
        if (Boolean.TRUE.equals(isSanitizing.get())) {
            return jp.proceed();
        }

        isSanitizing.set(true);
        try {
            String rawMethod = jp.getSignature().toShortString();
            String method = LogSanitizer.sanitize(rawMethod);

            Object[] args = jp.getArgs();
            Object[] sanitizedArgs = Arrays.stream(args).map(LogSanitizer::sanitizeObject).toArray();

            long start = System.currentTimeMillis();
            log.info("Start {} with args {}", method, sanitizedArgs);

            Object result = jp.proceed();

            Object sanitizedResult = LogSanitizer.sanitizeObject(result);
            log.info("End {} took {} ms, result: {}", method, (System.currentTimeMillis() - start), sanitizedResult);

            return result;
        } finally {
            isSanitizing.remove();
        }
    }
}