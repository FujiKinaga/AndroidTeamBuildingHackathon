package team_ky.androidteambuildinghackathon.encode;

/**
 * Created by kinagafuji on 15/11/22.
 */
public class PercentEscaper extends UnicodeEscaper {
    public static final String SAFECHARS_URLENCODER = "-_.*";

    public static final String SAFEPATHCHARS_URLENCODER = "-_.!~*'()@:$&,;=";

    public static final String SAFEQUERYSTRINGCHARS_URLENCODER
            = "-_.!~*'()@:$,;/?:";

    // In some uri escapers spaces are escaped to '+'
    private static final char[] URI_ESCAPED_SPACE = {'+'};

    private static final char[] UPPER_HEX_DIGITS =
            "0123456789ABCDEF".toCharArray();

    private final boolean plusForSpace;

    private final boolean[] safeOctets;

    public PercentEscaper(String safeChars, boolean plusForSpace) {
        // Avoid any misunderstandings about the behavior of this escaper
        if (safeChars.matches(".*[0-9A-Za-z].*")) {
            throw new IllegalArgumentException(
                    "Alphanumeric characters are always 'safe' and should not be " +
                            "explicitly specified");
        }
        // Avoid ambiguous parameters. Safe characters are never modified so if
        // space is a safe character then setting plusForSpace is meaningless.
        if (plusForSpace && safeChars.contains(" ")) {
            throw new IllegalArgumentException(
                    "plusForSpace cannot be specified when space is a 'safe' character");
        }
        if (safeChars.contains("%")) {
            throw new IllegalArgumentException(
                    "The '%' character cannot be specified as 'safe'");
        }
        this.plusForSpace = plusForSpace;
        this.safeOctets = createSafeOctets(safeChars);
    }

    private static boolean[] createSafeOctets(String safeChars) {
        int maxChar = 'z';
        char[] safeCharArray = safeChars.toCharArray();
        for (char c : safeCharArray) {
            maxChar = Math.max(c, maxChar);
        }
        boolean[] octets = new boolean[maxChar + 1];
        for (int c = '0'; c <= '9'; c++) {
            octets[c] = true;
        }
        for (int c = 'A'; c <= 'Z'; c++) {
            octets[c] = true;
        }
        for (int c = 'a'; c <= 'z'; c++) {
            octets[c] = true;
        }
        for (char c : safeCharArray) {
            octets[c] = true;
        }
        return octets;
    }

    @Override
    protected int nextEscapeIndex(CharSequence csq, int index, int end) {
        for (; index < end; index++) {
            char c = csq.charAt(index);
            if (c >= safeOctets.length || !safeOctets[c]) {
                break;
            }
        }
        return index;
    }

    @Override
    public String escape(String s) {
        int slen = s.length();
        for (int index = 0; index < slen; index++) {
            char c = s.charAt(index);
            if (c >= safeOctets.length || !safeOctets[c]) {
                return escapeSlow(s, index);
            }
        }
        return s;
    }

    @Override
    protected char[] escape(int cp) {
        // We should never get negative values here but if we do it will throw an
        // IndexOutOfBoundsException, so at least it will get spotted.
        if (cp < safeOctets.length && safeOctets[cp]) {
            return null;
        } else if (cp == ' ' && plusForSpace) {
            return URI_ESCAPED_SPACE;
        } else if (cp <= 0x7F) {
            // Single byte UTF-8 characters
            // Start with "%--" and fill in the blanks
            char[] dest = new char[3];
            dest[0] = '%';
            dest[2] = UPPER_HEX_DIGITS[cp & 0xF];
            dest[1] = UPPER_HEX_DIGITS[cp >>> 4];
            return dest;
        } else if (cp <= 0x7ff) {
            // Two byte UTF-8 characters [cp >= 0x80 && cp <= 0x7ff]
            // Start with "%--%--" and fill in the blanks
            char[] dest = new char[6];
            dest[0] = '%';
            dest[3] = '%';
            dest[5] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[4] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
            cp >>>= 2;
            dest[2] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[1] = UPPER_HEX_DIGITS[0xC | cp];
            return dest;
        } else if (cp <= 0xffff) {
            // Three byte UTF-8 characters [cp >= 0x800 && cp <= 0xffff]
            // Start with "%E-%--%--" and fill in the blanks
            char[] dest = new char[9];
            dest[0] = '%';
            dest[1] = 'E';
            dest[3] = '%';
            dest[6] = '%';
            dest[8] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[7] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
            cp >>>= 2;
            dest[5] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[4] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
            cp >>>= 2;
            dest[2] = UPPER_HEX_DIGITS[cp];
            return dest;
        } else if (cp <= 0x10ffff) {
            char[] dest = new char[12];
            // Four byte UTF-8 characters [cp >= 0xffff && cp <= 0x10ffff]
            // Start with "%F-%--%--%--" and fill in the blanks
            dest[0] = '%';
            dest[1] = 'F';
            dest[3] = '%';
            dest[6] = '%';
            dest[9] = '%';
            dest[11] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[10] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
            cp >>>= 2;
            dest[8] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[7] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
            cp >>>= 2;
            dest[5] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[4] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
            cp >>>= 2;
            dest[2] = UPPER_HEX_DIGITS[cp & 0x7];
            return dest;
        } else {
            // If this ever happens it is due to bug in UnicodeEscaper, not bad input.
            throw new IllegalArgumentException(
                    "Invalid unicode character value " + cp);
        }
    }
}
