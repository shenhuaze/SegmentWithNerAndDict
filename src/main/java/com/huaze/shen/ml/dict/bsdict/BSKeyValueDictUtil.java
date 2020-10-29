
package com.huaze.shen.ml.dict.bsdict;

public class BSKeyValueDictUtil {
    // int float类型占用了4个字节即32位
    public static final int intFloatTypeByte = 4;
    public static final int ByteTypeByteCount = 1;
    public static final int utf16ByteLen = 2;

    /**
     * 获取子byte
     */
    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        try {
            System.arraycopy(src, begin, bs, 0, count);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bs;
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

    public static int byteArrayToInt(byte[] bytes,int beginOffset) {
        int value = 0;
        // 由高位到低位
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (bytes[i+beginOffset] & 0x000000FF) << shift;// 往高位游
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

        int value = byteArrayToInt(src,begin);
        return value;
    }

    /**
     * byte数组转换为float类型
     */
    public static float getFloatValueFromByteArray(byte[] src, int begin, int count) {
        if (src == null) {
            return 0;
        }

        int value = byteArrayToInt(src,begin);
        float floatValure = Float.intBitsToFloat(value);
        return floatValure;
    }

    /**
     * String转换为float类型
     */
    public static float stringToFloat(String string) {
        if (string == null || string.length() <= 0) {
            return 0;
        }

        float value = Float.parseFloat(string);;
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
     * 获取字符串的hash-确保不存在负数-BKDRHash算法
     */
    public static int getHashcode(String strName) {
        int value = strName.hashCode() & 0x7fffffff;
        return value;
    }

    /**
     * 从byte数组转为String
     */
    public static String getStringFromByteArrat(byte[] src, int startOffset, int count,String charsetName) {
          if (src == null || src.length <= 0) {
              return null;
          }

          String str = null;

          try {
              str = new String(src,startOffset,count,charsetName);
          } catch (Exception e) {
              e.printStackTrace();
              return null;
          }

          return str;
    }

}
