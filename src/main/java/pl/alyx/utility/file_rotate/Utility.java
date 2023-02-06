package pl.alyx.utility.file_rotate;

public class Utility {

    public static boolean hasWildcards(String text) {
        if (text == null || text.length() == 0) {
            return false;
        } else {
            return text.indexOf('*') >= 0 || text.indexOf('?') >= 0;
        }
    }

    public static String wildToExpression(String text) {
        StringBuilder sb = new StringBuilder();
        sb.append('^');
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (c == '*') {
                sb.append(".*");
            } else if (c == '?') {
                sb.append('.');
            } else if ("\\.[]{}()+-^$|".indexOf(c) >= 0) {
                sb.append('\\');
                sb.append(c);
            } else {
                sb.append(c);
            }
        }
        sb.append('$');
        return sb.toString();
    }

    public static boolean isNotEmpty(String text) {
        boolean result = text != null && text.trim().length() > 0;
        return result;
    }

    public static String removeTrailingDot(String text) {
        if (text == null || text.length() == 0) {
            return text;
        }
        if (text.endsWith(".")) {
            return text.substring(0, text.length() - 1);
        } else {
            return text;
        }
    }

    public static String removeAllTrailingDots(String text) {
        if (text == null || text.length() == 0) {
            return text;
        } else {
            return text.replace("\\.+$", "");
        }
    }

}
