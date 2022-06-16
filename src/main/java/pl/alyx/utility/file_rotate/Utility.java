package pl.alyx.utility.file_rotate;

public class Utility {
    public static boolean hasWildcards(String text) {
        return text.indexOf('*') >= 0 || text.indexOf('?') >= 0;
    }
}
