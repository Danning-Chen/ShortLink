package application.service;

public class Base62 {
    private static final char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
            .toCharArray();

    public static String encode(long value) {
        if (value == 0)
            return "0";
        StringBuilder sb = new StringBuilder(11);
        long v = value;
        while (v > 0) {
            int rem = (int) (v % 62);
            sb.append(ALPHABET[rem]);
            v /= 62;
        }
        return sb.reverse().toString();
    }
}
