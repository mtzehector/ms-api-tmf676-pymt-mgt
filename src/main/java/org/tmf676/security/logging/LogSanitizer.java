package org.tmf676.security.logging;

public final class LogSanitizer {
    private LogSanitizer(){}
    public static String s(String in){
        if(in==null) return null;
        return in.replaceAll("[\r\n\t\f\u0000-\u001F\u007F]+"," ");
    }
}
