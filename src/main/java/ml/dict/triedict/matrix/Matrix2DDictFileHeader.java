package ml.dict.triedict.matrix;

import ml.dict.triedict.common.DictCommonUtils;

class Matrix2DDictFileHeader {
    // 基本配置
    public static String kHeaderCharsetName = "UTF-8";
    public static final String Matrix2DfileType = "Matrix2DDict";
    public static final int Matrix2DFileTypeLen = getFileTypeLen();

    // 偏移地址
    public static final int fileTypeOffset = 0;
    public static final int fileTypeLenOffset = fileTypeOffset + Matrix2DFileTypeLen;

    public static final int colCountOffset = fileTypeLenOffset + DictCommonUtils.IntFloatTypeByteCount;
    public static final int rowCountOffset = colCountOffset + DictCommonUtils.IntFloatTypeByteCount;

    public static final int valueTypeOffset = rowCountOffset + DictCommonUtils.IntFloatTypeByteCount;

    public static final int valueDataLenOffset = valueTypeOffset + DictCommonUtils.IntFloatTypeByteCount;
    public static final int valueBaseAdrOffset = valueDataLenOffset + DictCommonUtils.IntFloatTypeByteCount;

    public static final int extendValueBaseAdrOffset  = valueBaseAdrOffset + DictCommonUtils.IntFloatTypeByteCount;

    public static final int header_size = extendValueBaseAdrOffset + DictCommonUtils.IntFloatTypeByteCount;

    // 写入到文件的数据或者从文件中读取的数据
    public String fileType = Matrix2DfileType;
    public int fileTypeLen = Matrix2DFileTypeLen;

    public int colCount = 0;
    public int rowCount = 0;
    public int valueType = 0;
    public int valueDataLen = 0;
    // value存储位置的的地址
    public int valueBaseAdr = header_size;
    // extendvalue存储位置的的地址：header_size + value长度
    public int extendValueBaseAdr = 0;

    private static int getFileTypeLen() {
        return DictCommonUtils.getBytesLen(Matrix2DfileType, kHeaderCharsetName);
    }

    // 从byte初始化TrieFile
    public boolean initFileHeader(byte[] fileBytecontent) throws Exception {
        if (fileBytecontent == null) {
            return false;
        }

        // 文件Header区
        fileType = getHeaderType(fileBytecontent);
        if (!isValidHeaderType()) {
            return false;
        }

        fileTypeLen = getHeaderTypeLen(fileBytecontent);
        colCount = getHeaderColCount(fileBytecontent);
        rowCount = getHeaderRowCount(fileBytecontent);
        valueType = getHeaderValueType(fileBytecontent);
        valueDataLen = getHeaderValueDataLen(fileBytecontent);
        valueBaseAdr = getHeaderValueBaseAdr(fileBytecontent);
        extendValueBaseAdr = getHeaderExtendValueBaseAdr(fileBytecontent);

        return true;
    }

    public boolean isValidHeaderType() {
        if (fileType == null || fileType.length() <= 0) {
            return false;
        }

        if (fileType.equals(Matrix2DfileType)) {
            return true;
        }

        return false;
    }

    private String getHeaderType(byte[] fileBytecontent) throws Exception {
        String headerType = new String(fileBytecontent,
                fileTypeOffset,
                fileTypeLen,
                kHeaderCharsetName);

        return headerType;
    }

    private int getHeaderTypeLen(byte[] fileBytecontent) throws Exception {
        int len = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                fileTypeLenOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return len;
    }

    private int getHeaderColCount(byte[] fileBytecontent) throws Exception {
        int len = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                colCountOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return len;
    }

    private int getHeaderRowCount(byte[] fileBytecontent) throws Exception {
        int len = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                rowCountOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return len;
    }

    private int getHeaderValueType(byte[] fileBytecontent) throws Exception {
        int len = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                valueTypeOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return len;
    }

    private int getHeaderValueDataLen(byte[] fileBytecontent) throws Exception {
        int len = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                valueDataLenOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return len;
    }

    private int getHeaderValueBaseAdr(byte[] fileBytecontent) throws Exception {
        int len = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                valueBaseAdrOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return len;
    }

    private int getHeaderExtendValueBaseAdr(byte[] fileBytecontent) throws Exception {
        int len = DictCommonUtils.getIntValueFromByteArray(fileBytecontent,
                extendValueBaseAdrOffset,
                DictCommonUtils.IntFloatTypeByteCount);

        return len;
    }


}
