package org.tmf676.security.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;
import java.util.regex.Pattern;

public class SafeLoggingTurboFilter extends TurboFilter {
    private static final Pattern CONTROL = Pattern.compile("[\r\n\t\f\u0000-\u001F\u007F]+");

    private static String s(String in) {
        if (in == null) return null;
        return CONTROL.matcher(in).replaceAll(" ");
    }

    @Override
    public FilterReply decide(Marker marker,
                              ch.qos.logback.classic.Logger logger,
                              ch.qos.logback.classic.Level level,
                              String format, Object[] params, Throwable t) {

        if (format != null) {
            format = s(format);
        }
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                Object p = params[i];
                if (p instanceof String) {
                    params[i] = s((String) p);
                }
            }
        }
        return FilterReply.NEUTRAL;
    }
}
