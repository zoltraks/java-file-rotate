package pl.alyx.utility.file_rotate;

public class Utility {

    public static boolean hasWildcards(String text) {
        return text.indexOf('*') >= 0 || text.indexOf('?') >= 0;
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

}
