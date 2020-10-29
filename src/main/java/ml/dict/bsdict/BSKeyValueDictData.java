package ml.dict.bsdict;

public class BSKeyValueDictData {
    public static final String UTF8CharSetName = "UTF-8";
    public static final String UTF16CharSetName = "UTF-16BE";
    /**
     * Header config
     */
    public static class BSDictHeaderConfig {
        // 词典的数据列数配置
        public interface KeyValueColCount {
            // 是否存在多列数据
            byte KeyValueColCount_Single = 1;
            byte KeyValueColCount_MORE = 2;
        }

        // Key的编码类型配置
        public interface KeyCharSetNameType {
            // key-value编码类型
            byte KeyCharKSetName_UTF8 = 1;
            byte KeyCharKSetName_UTF16LE = 2;
            byte KeyCharKSetName_UTF16BE = 3;
        }

        // valuKe的编码类型配置
        public interface ValueCharSetNameType {
            // key-value编码类型
            byte ValueCharSetName_UTF8 = 1;
            byte ValueCharSetName_UTF16LE = 2;
            byte ValueCharSetName_UTF16BE = 3;
        }
    }

    /**
     * Header数据存储区
     */
    public static class BSDictHeader {
        public static final String kBSDictHeader = "bsdict";

        public static String kBSDictHeaderCharsetName = UTF8CharSetName;
        public static String keyNameCharsetName = UTF16CharSetName;
        public static String valueNameCharsetName = UTF16CharSetName;

        public static final int headerTypeStartOffset = 0;
        public static final int headerTypeEndOffset = getBytesLen(kBSDictHeader);
        public static final int keyCharSetNameTypeStartOffset = headerTypeEndOffset;
        public static final int valueCharSetNameTypeStartOffset = keyCharSetNameTypeStartOffset + BSKeyValueDictUtil.intFloatTypeByte;
        public static final int isMultyColDataStartOffset = valueCharSetNameTypeStartOffset + BSKeyValueDictUtil.intFloatTypeByte;
        public static final int blockSizeStartOffset = isMultyColDataStartOffset + BSKeyValueDictUtil.intFloatTypeByte;
        public static final int dataBlockCountStartOffset = blockSizeStartOffset + BSKeyValueDictUtil.intFloatTypeByte;
        public static final int dataBlockCountEndOffset = dataBlockCountStartOffset + BSKeyValueDictUtil.intFloatTypeByte;
        public static final int nameMaxLenStartOffset = dataBlockCountStartOffset + BSKeyValueDictUtil.intFloatTypeByte;
        public static final int nameMinLenStartOffset = nameMaxLenStartOffset + BSKeyValueDictUtil.intFloatTypeByte;

        public String headerType = "";
        // keyvalue 采用的编码类型
        public int keyCharSetNameType = 0;
        public int valueCharSetNameType = 0;

        // 是否存在第二列数据
        public int isMultyColData = 0;
        // 每个block Item的字节数大小
        public int blockItemSize = 0;
        // 数据块个数
        public int dataBlockCount = 0;
        // keyName的最大字节长度
        public int keyNameMaxLen = 0;
        // keyName的最小字节长度
        public int keyNameMinLen = 0;

        public void BSDictHeader() {
            clear();
        }

        public boolean isValidHeaderType() {
            if (headerType == null || headerType.length() <= 0) {
                return false;
            }

            if (headerType.equals(BSDictHeader.kBSDictHeader)) {
                return true;
            }

            return false;
        }
        public static byte getBytesLen(String nameStr) {
            if (nameStr == null || nameStr.length() <= 0) {
                return 0;
            }

            byte len = 0;

            try {
                len = (byte) nameStr.getBytes(kBSDictHeaderCharsetName).length;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return len;
        }

        public boolean isExistValue(byte[] fileBytecontent) {
            if (fileBytecontent == null) {
                return false;
            }

            int multyColCount = BSKeyValueDictUtil.getIntValueFromByteArray(fileBytecontent,
                    BSDictHeader.isMultyColDataStartOffset,
                    BSKeyValueDictUtil.intFloatTypeByte);

            boolean isMultyColData = false;
            if (multyColCount == BSDictHeaderConfig.KeyValueColCount.KeyValueColCount_Single) {
                isMultyColData = false;
            } else if (multyColCount == BSDictHeaderConfig.KeyValueColCount.KeyValueColCount_MORE) {
                isMultyColData = true;
            }

            return isMultyColData;
        }

        public void clear() {
            keyCharSetNameType = 0;
            valueCharSetNameType = 0;
            isMultyColData = 0;
            blockItemSize = 0;
            dataBlockCount = 0;
            keyNameMaxLen = 0;
            keyNameMinLen = 0;
        }
    }

    /**
     * block Offset区
     */
    public static final int nameByteLenOffset = BSDictHeader.nameMinLenStartOffset + BSKeyValueDictUtil.intFloatTypeByte;
    public static final int nameAddressOffset = nameByteLenOffset + BSKeyValueDictUtil.ByteTypeByteCount;
    public static final int hashCodeValueOffset = nameAddressOffset + BSKeyValueDictUtil.intFloatTypeByte;

    public static final int valueDataByteLenStartOffset = hashCodeValueOffset + BSKeyValueDictUtil.intFloatTypeByte;
    public static final int valueDataAddressStartOffset = valueDataByteLenStartOffset + BSKeyValueDictUtil.ByteTypeByteCount;

    /**
     * Header 数据存储区
     */
    public BSDictHeader dictHeader = new BSDictHeader();

    /**
     * block数据存储区
     */
    // name有效字符串的长度
    public byte nameByteLen = 0;                            // 1
    // 字符串
    public int nameAddress = 0;                            // 2
    public String name;
    // hashCode
    public int hashCode;                                   // 3
    // 第二列数据Content
    public byte secondColValueDataByteLen = 0;
    public int secondColValueDataAddress = 0;             // 4
    public String secondColValue;

    /**
     * function
     */
    private BSKeyValueDictData() {
    }

    public BSKeyValueDictData(boolean isMultyColDicData) {
        if (isMultyColDicData) {
            dictHeader.isMultyColData = BSDictHeaderConfig.KeyValueColCount.KeyValueColCount_MORE;
        } else {
            dictHeader.isMultyColData = BSDictHeaderConfig.KeyValueColCount.KeyValueColCount_Single;
        }

        dictHeader.blockItemSize = getBlockByteCount();
    }

    public byte getNameByteLen(String nameStr) {
        if (nameStr == null || nameStr.length() <= 0) {
            return 0;
        }

        byte len = 0;

        try {
            len = (byte) nameStr.getBytes(BSDictHeader.keyNameCharsetName).length;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return len;
    }

    public byte getValueByteLen(String nameStr) {
        if (nameStr == null || nameStr.length() <= 0) {
            return 0;
        }

        byte len = 0;

        try {
            len = (byte) nameStr.getBytes(BSDictHeader.valueNameCharsetName).length;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return len;
    }

    public static int getNameAddrStartOffset(int blockSize, int dataBlockCount) {
        int nameAddrStartOffset = dataBlockCount * blockSize + nameByteLenOffset;
        return nameAddrStartOffset;
    }

    // 获取int字节数
    public int getBlockByteCount() {
        int value = 4 * 2 + 1;
        if (dictHeader.isMultyColData == BSDictHeaderConfig.KeyValueColCount.KeyValueColCount_MORE) {
            value += 5;
        }

        return value;
    }
}
