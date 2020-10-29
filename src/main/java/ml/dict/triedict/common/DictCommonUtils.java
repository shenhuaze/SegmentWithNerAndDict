package ml.dict.triedict.common;

public class DictCommonUtils {
    public static final int IntFloatTypeByteCount = 4;
    public static final int DoubleTypeByteCount = 8;
    public static final int ByteTypeByteCount = 1;

    public static byte[] getBytesValue(String text, String chatSetName) {
        if (text == null || text.length() <= 0) {
            return null;
        }

        if (chatSetName == null || chatSetName.length() <= 0) {
            return null;
        }

        byte[] bytesValue = null;
        try {
            bytesValue = text.getBytes(chatSetName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bytesValue;
    }

    //
    public static byte getBytesLen(String text, String chatSetName) {
        if (text == null || text.length() <= 0) {
            return 0;
        }

        if (chatSetName == null || chatSetName.length() <= 0) {
            return 0;
        }

        byte len = 0;

        try {
            len = (byte) text.getBytes(chatSetName).length;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return len;
    }

    public static int[] string2IntArray(String key, int endFlag) {
        if (key == null || key.length() <= 0) {
            return null;
        }

        int[] result = new int[key.length() + 1];
        for (int i = 0; i < key.length(); i++) {
            result[i] = key.charAt(i);
        }

        result[key.length()] = endFlag;

        return result;
    }

    public static int[] string2IntArray(String key) {
        if (key == null || key.length() <= 0) {
            return null;
        }

        int[] result = new int[key.length()];
        for (int i = 0; i < key.length(); i++) {
            result[i] = key.charAt(i);
        }

        return result;
    }

    /**
     * byte数组转换为int类型
     */
    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        // 由高位到低位
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;// 往高位游
        }
        return value;
    }

    public static int byteArrayToInt(byte[] bytes, int beginOffset) {
        int value = 0;
        // 由高位到低位
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (bytes[i + beginOffset] & 0x000000FF) << shift;// 往高位游
        }
        return value;
    }

    /**
     * byte数组转换为int类型
     */
    public static int getIntValueFromByteArray(byte[] src, int begin, int count) {
        if (src == null) {
            return 0;
        }

        int value = byteArrayToInt(src, begin);
        return value;
    }

    /**
     * byte数组转换为float类型
     */
    public static float getFloatValueFromByteArray(byte[] src, int begin, int count) {
        if (src == null) {
            return 0;
        }

        int value = byteArrayToInt(src, begin);
        float floatValure = Float.intBitsToFloat(value);
        return floatValure;
    }

    /**
     * String转换为int类型
     */
    public static double stringToDouble(String string) {
        if (string == null || string.length() <= 0) {
            return 0;
        }

        double value = Double.parseDouble(string);

        return value;
    }

    /**
     * String转换为float类型
     */
    public static float stringToFloat(String string) {
        if (string == null || string.length() <= 0) {
            return 0;
        }

        float value = Float.parseFloat(string);
        return value;
    }

    /**
     * String转换为int类型
     */
    public static int stringToInt(String string) {
        if (string == null || string.length() <= 0) {
            return 0;
        }

        int value = Integer.parseInt(string);

        return value;
    }

    /**
     * 从byte数组转为String
     */
    public static String getStringFromByteArrat(byte[] src, int startOffset, int count, String charsetName) {
        if (src == null || src.length <= 0) {
            return null;
        }

        String str = null;

        try {
            str = new String(src, startOffset, count, charsetName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return str;
    }
}
