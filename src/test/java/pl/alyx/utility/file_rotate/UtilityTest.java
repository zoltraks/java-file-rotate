package pl.alyx.utility.file_rotate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilityTest {

    @Test
    void hasWildcards() {
        String needle;
        boolean expect, result;
        needle = null;
        expect = false;
        result = Utility.hasWildcards(needle);
        assertEquals(expect, result);
        needle = "";
        expect = false;
        result = Utility.hasWildcards(needle);
        assertEquals(expect, result);
        needle = "!@#$%^&()";
        expect = false;
        result = Utility.hasWildcards(needle);
        assertEquals(expect, result);
        needle = "?";
        expect = true;
        result = Utility.hasWildcards(needle);
        assertEquals(expect, result);
        needle = "*";
        expect = true;
        result = Utility.hasWildcards(needle);
        assertEquals(expect, result);
    }

    @Test
    void isNotEmpty() {
        String needle;
        boolean expect, result;
        needle = null;
        expect = false;
        result = Utility.isNotEmpty(needle);
        assertEquals(expect, result);
        needle = "";
        expect = false;
        result = Utility.isNotEmpty(needle);
        assertEquals(expect, result);
        needle = ".";
        expect = true;
        result = Utility.isNotEmpty(needle);
        assertEquals(expect, result);
        needle = " \t \n ";
        expect = false;
        result = Utility.isNotEmpty(needle);
        assertEquals(expect, result);
        needle = " \t . \n ";
        expect = true;
        result = Utility.isNotEmpty(needle);
        assertEquals(expect, result);
    }

    @Test
    void removeTrailingDot() {
        String needle, expect, result;
        needle = null;
        expect = null;
        result = Utility.removeTrailingDot(needle);
        assertEquals(expect, result);
        needle = "";
        expect = "";
        result = Utility.removeTrailingDot(needle);
        assertEquals(expect, result);
        needle = "A.B";
        expect = "A.B";
        result = Utility.removeTrailingDot(needle);
        assertEquals(expect, result);
        needle = ".";
        expect = "";
        result = Utility.removeTrailingDot(needle);
        assertEquals(expect, result);
        needle = "...";
        expect = "..";
        result = Utility.removeTrailingDot(needle);
        assertEquals(expect, result);
    }

    @Test
    void removeAllTrailingDots() {
        String needle, expect, result;
        needle = null;
        expect = null;
        result = Utility.removeAllTrailingDots(needle);
        assertEquals(expect, result);
        needle = "";
        expect = "";
        result = Utility.removeAllTrailingDots(needle);
        assertEquals(expect, result);
        needle = "A.B";
        expect = "A.B";
        result = Utility.removeAllTrailingDots(needle);
        assertEquals(expect, result);
        needle = ".";
        expect = "";
        result = Utility.removeAllTrailingDots(needle);
        assertEquals(expect, result);
        needle = "...";
        expect = "";
        result = Utility.removeAllTrailingDots(needle);
        assertEquals(expect, result);
        needle = "...x";
        expect = "...x";
        result = Utility.removeAllTrailingDots(needle);
        assertEquals(expect, result);
    }

}