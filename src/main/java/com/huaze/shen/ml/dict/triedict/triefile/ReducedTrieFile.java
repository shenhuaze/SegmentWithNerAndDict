package com.huaze.shen.ml.dict.triedict.triefile;

import com.huaze.shen.ml.dict.triedict.common.DictCommonUtils;

import static com.huaze.shen.ml.dict.triedict.triefile.ReducedTrieFile.ValueCharSetNameType.ValueCharSetName_UTF16BE;
import static com.huaze.shen.ml.dict.triedict.triefile.ReducedTrieFile.ValueCharSetNameType.ValueCharSetName_UTF16LE;
import static com.huaze.shen.ml.dict.triedict.triefile.ReducedTrieFile.ValueCharSetNameType.ValueCharSetName_UTF8;

public class ReducedTrieFile {
    public static final String UTF8CharSetName = "UTF-8";
    public static final String UTF16BECharSetName = "UTF-16BE";
    public static final String UTF16LECharSetName = "UTF-16LE";

    public FileHeader header;
    public FileContentIndex fileContentIndex;

    public ReducedTrieFile() {
        header = new FileHeader();
        fileContentIndex = new FileContentIndex();
    }

    // 从byte初始化TrieFile
    public boolean initFileHeader(byte[] fileBytecontent) throws Exception {
        if (fileBytecontent == null) {
            return false;
        }

        // 文件Header区
        header.fileType = getHeaderType(fileBytecontent);
        if (!header.isValidHeaderType()) {
            return false;
        }
        header.fileTypeLen = getHeaderTypeLen(fileBytecontent);
        header.intFileTypeCharSetName = getHeaderIntFileTypeCharSetName(fileBytecontent);

        header.keyMaxLen = getHeaderBCMaxLen(fileBytecontent);
        header.keyMinLen = getHeaderBCMinLen(fileBytecontent);
        header.keyCount = getHeaderKeyCount(fileBytecontent);

        header.extendData1 = getHeaderExtendData1(fileBytecontent);
        header.extendData2 = getHeaderExtendData2(fileBytecontent);
        header.extendData3 = getHeaderExtendData3(fileBytecontent);

        header.isExistValue = getHeaderKeyIsExistValue(fileBytecontent);
        header.keyValueColCount = getHeaderKeyValueColCount(fileBytecontent);

        header.intValueCharSetNameType = getHeaderIntValueCharSetNameType(fileBytecontent);

        // 文件索引区
        fileContentIndex.baseAddress = getHeaderBaseAddress(fileBytecontent);
        fileContentIndex.checkAddress = getHeaderCheckAddress(fileBytecontent);
        fileContentIndex.bcLen = getHeaderBCSize(fileBytecontent);

        fileContentIndex.keyValueTailAddress = getHeaderKeyValueTailAddress(fileBytecontent);
        fileContentIndex.keyValueTailDataLen = getHeaderKeyValueTailAddressLen(fileBytecontent);

        fileContentIndex.valueAddress = getHeaderValueAddress(fileBytecontent);
        fileContentIndex.valueDataLen = getHeaderValueAddressLen(fileBytecontent);

        // 其他辅助信息
        if (header.intFileTypeCharSetName == ValueCharSetName_UTF8) {
            header.fileTypeCharSetName = ReducedTrieFile.UTF8CharSetName;
        } else if (header.intFileTypeCharSetName == ValueCharSetName_UTF16LE) {
            header.fileTypeCharSetName = ReducedTrieFile.UTF16LECharSetName;
        } else if (header.intFileTypeCharSetName == ValueCharSetName_UTF16BE) {
            header.fileTypeCharSetName = ReducedTrieFile.UTF16BECharSetName;
        }

        //
        if (header.intValueCharSetNameType == ValueCharSetName_UTF8) {
            header.valueCharSetNameType = ReducedTrieFile.UTF8CharSetName;
        } else if (header.intValueCharSetNameType == ValueCharSetName_UTF16LE) {
            header.valueCharSetNameType = ReducedTrieFile.UTF16LECharSetName;
        } else if (header.intValueCharSetNameType == ValueCharSetName_UTF16BE) {
            header.valueCharSetNameType = ReducedTrieFile.UTF16BECharSetName;
        }

        return true;
    }

    private int getHeaderBCSize(byte[] fileBytecontent) {
        int size = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                ReducedTrieFile.FileContentIndex.bcLenOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return size;
    }

    private int getHeaderBCMaxLen(byte[] fileBytecontent) {
        int size = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                ReducedTrieFile.FileHeader.keyMaxLenOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return size;
    }

    private int getHeaderBCMinLen(byte[] fileBytecontent) {
        int size = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                ReducedTrieFile.FileHeader.keyMinLenOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return size;
    }

    private int getHeaderKeyCount(byte[] fileBytecontent) {
        int size = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                FileHeader.keyCountOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return size;
    }

    private int getHeaderKeyValueColCount(byte[] fileBytecontent) {
        int colCount = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                ReducedTrieFile.FileHeader.keyValueColCountOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return colCount;
    }

    private int getValueCharSetNameType(byte[] fileBytecontent) {
        int type = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                ReducedTrieFile.FileHeader.fileTypeOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return type;
    }

    private String getHeaderType(byte[] fileBytecontent) throws Exception {
        String headerType = new String(fileBytecontent,
                ReducedTrieFile.FileHeader.fileTypeOffset,
                ReducedTrieFile.FileHeader.TrieFileTypeLen,
                ReducedTrieFile.FileHeader.kHeaderCharsetName);

        return headerType;
    }

    private int getHeaderTypeLen(byte[] fileBytecontent) throws Exception {
        return ReducedTrieFile.FileHeader.TrieFileTypeLen;
//        int len = ReducedTrieUtils.getIntValueFromByteArray(mFileBytecontent,
//                ReducedTrieFile.FileHeader.fileTypeLenOffset,
//                ReducedTrieUtils.IntFloatTypeByteCount);
//
//        return len;
    }

    private int getHeaderKeyIsExistValue(byte[] fileBytecontent) {
        int value = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                ReducedTrieFile.FileHeader.isExistValueOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return value;
    }

    private int getHeaderBaseAddress(byte[] fileBytecontent) throws Exception {
        int baseStartAddress = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                ReducedTrieFile.FileContentIndex.baseAddressOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return baseStartAddress;
    }

    private int getHeaderCheckAddress(byte[] fileBytecontent) throws Exception {
        int checkStartAddress = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                ReducedTrieFile.FileContentIndex.checkAddressOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return checkStartAddress;
    }

    private int getHeaderKeyValueTailAddress(byte[] fileBytecontent) throws Exception {
        int keyValueTailStartAddress = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                ReducedTrieFile.FileContentIndex.keyValueTailAddressOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return keyValueTailStartAddress;
    }

    private int getHeaderKeyValueTailAddressLen(byte[] fileBytecontent) throws Exception {
        int len = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                ReducedTrieFile.FileContentIndex.keyValueTailAddressLenOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return len;
    }

    private int getHeaderValueAddress(byte[] fileBytecontent) throws Exception {
        int valueStartAddress = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                ReducedTrieFile.FileContentIndex.valueAddressOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return valueStartAddress;
    }

    private int getHeaderValueAddressLen(byte[] fileBytecontent) throws Exception {
        int len = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                ReducedTrieFile.FileContentIndex.valueAddressLenOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return len;
    }

    private int getHeaderIntFileTypeCharSetName(byte[] fileBytecontent) throws Exception {
        int len = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                ReducedTrieFile.FileHeader.intValueCharSetNameTypeOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return len;
    }

    private int getHeaderIntValueCharSetNameType(byte[] fileBytecontent) throws Exception {
        int len = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                ReducedTrieFile.FileHeader.intValueCharSetNameTypeOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return len;
    }

    private int getHeaderExtendData1(byte[] fileBytecontent) throws Exception {
        int len = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                FileHeader.extendData1Offset,
                DictCommonUtils.IntFloatTypeByteCount);

        return len;
    }

    private int getHeaderExtendData2(byte[] fileBytecontent) throws Exception {
        int len = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                FileHeader.extendData2Offset,
                DictCommonUtils.IntFloatTypeByteCount);

        return len;
    }

    private int getHeaderExtendData3(byte[] fileBytecontent) throws Exception {
        int len = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                ReducedTrieFile.FileHeader.extendData3Offset,
                DictCommonUtils.IntFloatTypeByteCount);

        return len;
    }

    static public class FileHeader {
        // 基本配置
        public static String kHeaderCharsetName = UTF8CharSetName;
        public static final String TrieFileType = "reducedTre";
        public static final int TrieFileTypeLen = getFileTypeLen();

        // 偏移设置
        public static final int fileTypeOffset = 0;
        public static final int fileTypeLenOffset = fileTypeOffset + TrieFileTypeLen;
        public static final int fileTypeCharSetNameOffset = fileTypeLenOffset + DictCommonUtils.IntFloatTypeByteCount;

        public static final int keyMaxLenOffset = fileTypeCharSetNameOffset + DictCommonUtils.IntFloatTypeByteCount;
        public static final int keyMinLenOffset = keyMaxLenOffset + DictCommonUtils.IntFloatTypeByteCount;
        public static final int keyCountOffset = keyMinLenOffset + DictCommonUtils.IntFloatTypeByteCount;

        public static final int extendData1Offset = keyCountOffset + DictCommonUtils.IntFloatTypeByteCount;
        public static final int extendData2Offset = extendData1Offset + DictCommonUtils.IntFloatTypeByteCount;
        public static final int extendData3Offset = extendData2Offset + DictCommonUtils.IntFloatTypeByteCount;

        public static final int isExistValueOffset = extendData3Offset + DictCommonUtils.IntFloatTypeByteCount;
        public static final int keyValueColCountOffset = isExistValueOffset + DictCommonUtils.IntFloatTypeByteCount;
        public static final int intValueCharSetNameTypeOffset = keyValueColCountOffset + DictCommonUtils.IntFloatTypeByteCount;

        public static final int OBJ_SIZE = intValueCharSetNameTypeOffset + DictCommonUtils.IntFloatTypeByteCount;

        //
        public String fileType = TrieFileType;
        public int fileTypeLen = TrieFileTypeLen;
        public int intFileTypeCharSetName = ValueCharSetName_UTF8;

        // key的最大产长度
        public int keyMaxLen;
        // key的最小长度
        public int keyMinLen;
        // key的数目
        public int keyCount;

        // 扩展数据
        public int extendData1 = 0;
        public int extendData2 = 0;
        public int extendData3 = 0;

        // 是否存在多列地址
        public int isExistValue;
        // 是否存在value
        public int keyValueColCount = KeyValueColCount.KeyValueColCount_Single;
        // value编码类型
        public int intValueCharSetNameType = ValueCharSetName_UTF8;

        // 辅助变量-不写入文件中
        public String fileTypeCharSetName = "";
        public String valueCharSetNameType = "";

        public FileHeader() {
        }

        private static int getFileTypeLen() {
            return DictCommonUtils.getBytesLen(TrieFileType, kHeaderCharsetName);
        }

        public boolean isValidHeaderType() {
            if (fileType == null || fileType.length() <= 0) {
                return false;
            }

            if (fileType.equals(TrieFileType)) {
                return true;
            }

            return false;
        }

        public static int objSize() {
            return OBJ_SIZE;
        }

        public void clear() {

        }
    }

    /**
     * 文件内容索引数据
     */
    static public class FileContentIndex {
        public static final int baseAddressOffset = FileHeader.intValueCharSetNameTypeOffset + DictCommonUtils.IntFloatTypeByteCount;
        public static final int checkAddressOffset = baseAddressOffset + DictCommonUtils.IntFloatTypeByteCount;
        public static final int bcLenOffset = checkAddressOffset + DictCommonUtils.IntFloatTypeByteCount;

        public static final int keyValueTailAddressOffset = bcLenOffset + DictCommonUtils.IntFloatTypeByteCount;
        public static final int keyValueTailAddressLenOffset = keyValueTailAddressOffset + DictCommonUtils.IntFloatTypeByteCount;
        public static final int valueAddressOffset = keyValueTailAddressLenOffset + DictCommonUtils.IntFloatTypeByteCount;
        public static final int valueAddressLenOffset = valueAddressOffset + DictCommonUtils.IntFloatTypeByteCount;

        // 该OFFSET的占用字节数
        public static final int OBJ_SIZE = valueAddressLenOffset + DictCommonUtils.IntFloatTypeByteCount;

        // base开始地址offset
        public int baseAddress;
        // check开始地址offset
        public int checkAddress;
        // bc 数组与Tail数组的长度
        public int bcLen;

        // keyValueTail开始地址offset
        public int keyValueTailAddress;
        // keyValueTail长度
        public int keyValueTailDataLen;

        // Value开始地址offset
        public int valueAddress;
        // Value长度
        public int valueDataLen;

        public static int objSize() {
            return OBJ_SIZE;
        }
    }

    static public class ReducedTrieFileFlag {
        //
        public static final int DEL_FLAG = 1;
        // Key字符串的结束标记
        public static final int KEY_END_FLAG = 2;

        public static final int VALUE_EXIST = 3;

        public static final int VALUE_NO_EXIST = 4;
    }

    // 编码类型
    public interface ValueCharSetNameType {
        // key-value编码类型
        int ValueCharSetName_UTF8 = 1;
        int ValueCharSetName_UTF16LE = 2;
        int ValueCharSetName_UTF16BE = 3;
    }

    // 词典的数据列数配置
    public interface KeyValueColCount {
        // 是否存在多列数据
        int KeyValueColCount_Single = 1;
        int KeyValueColCount_Two = 2;
        int KeyValueColCount_Three = 3;
        int KeyValueColCount_MORE = 4;
    }
}

