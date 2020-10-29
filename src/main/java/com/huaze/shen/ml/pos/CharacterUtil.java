package com.huaze.shen.ml.pos;

public class CharacterUtil {
    public static boolean isNumberSeq(String str) {
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (!isDigit(ch)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isDigit(char ch) {
        if (ch >= 0x0030 && ch <= 0x0039) {
            return true;
        }
        return false;
    }
}
