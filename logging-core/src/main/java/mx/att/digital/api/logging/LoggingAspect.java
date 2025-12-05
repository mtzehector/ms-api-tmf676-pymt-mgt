package mx.att.digital.api.logging;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The Class LoggingAspect.
 */
@Aspect
@Component
public class LoggingAspect {

	private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
	private static boolean isSanitizing = false;

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
		if (isSanitizing) {
			return jp.proceed();
		}

		isSanitizing = true;
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
			isSanitizing = false;
		}
	}
}