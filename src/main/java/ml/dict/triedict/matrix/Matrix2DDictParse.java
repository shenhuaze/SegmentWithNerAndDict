package ml.dict.triedict.matrix;

import ml.dict.triedict.common.DictCommonUtils;

import java.io.File;
import java.io.FileInputStream;

import static ml.dict.triedict.matrix.MatrixValueType.ValueType_Double;
import static ml.dict.triedict.matrix.MatrixValueType.ValueType_Float;
import static ml.dict.triedict.matrix.MatrixValueType.ValueType_Int;

/**
 * 二维矩阵
 */
public class Matrix2DDictParse<T> {

    // Header
    private Matrix2DDictFileHeader dictFileHeader;
    // 二维数组
    private Matrix2DValue matrix2DValue = null;
    private Matrix1DValue matrix1DValue = null;

    // 读取的文件内容
    public byte[] mFileBytecontent = null;
    // 文件流
    public FileInputStream in = null;
    // 词典文件名
    public String mdicFileName = null;
    // 是否完成初始化
    private boolean isInited = false;

    private Matrix2DDictParse() {
    }

    public Matrix2DDictParse(String fileName) {
        dictFileHeader = new Matrix2DDictFileHeader();
        load(fileName);
    }

    // 加载数据
    public boolean load(String fileName) {
        if (fileName == null || fileName.length() <= 0) {
            return false;
        }

        if (isInited) {
            return true;
        }

        return init(fileName);
    }

    public int getValueType() {
        if (dictFileHeader == null) {
            return 0;
        }

        return dictFileHeader.valueType;
    }

    public int getRowCount() {
        if (dictFileHeader == null) {
            return 0;
        }

        return dictFileHeader.rowCount;
    }

    /**
     * 从矩阵中根据行列查找
     */
    public T search2DMatrix(int row, int col) {
        int rowColMax = dictFileHeader.rowCount;
        if (row >= rowColMax || col >= rowColMax) {
            return null;
        }

        return (T) matrix2DValue.search(row, col);
    }

    /**
     * 从矩阵中根据行列查找
     */
    public T search1DMatrix(int row) {
        int rowColMax = dictFileHeader.rowCount;
        if (row >= rowColMax) {
            return null;
        }

        if (matrix1DValue == null) {
            return null;
        }

        return (T) matrix1DValue.search(row);
    }

    /**
     * 初始化
     */
    private boolean init(String fileName) {
        if (fileName == null || fileName.length() <= 0) {
            return false;
        }

        if (isInited) {
            return true;
        }

        mdicFileName = fileName;

        File file = new File(fileName);
        if (!file.exists()) {
            return false;
        }

        if (mFileBytecontent != null) {
            return true;
        }

        Long filelen = file.length();
        byte[] filebContent = new byte[filelen.intValue()];

        try {
            in = new FileInputStream(file);
            in.read(filebContent);

            mFileBytecontent = filebContent;
            if (!initFileHeader(mFileBytecontent)) {
                return false;
            }

            if (!initMatrix2DValue(mFileBytecontent)) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                in.close();
                in = null;
                dictFileHeader = null;
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            return false;
        }

        isInited = true;

        return true;
    }

    private boolean initFileHeader(byte[] fileBytecontent) throws Exception {
        if (fileBytecontent == null) {
            return false;
        }

        return dictFileHeader.initFileHeader(fileBytecontent);
    }

    private boolean initMatrix2DValue(byte[] fileBytecontent) throws Exception {
        if (fileBytecontent == null) {
            return false;
        }

        if (dictFileHeader == null) {
            return false;
        }

        int colCount = dictFileHeader.colCount;
        int rowCount = dictFileHeader.rowCount;
        int valueType = dictFileHeader.valueType;

        if (matrix2DValue == null) {
            matrix2DValue = new Matrix2DValue<T>(rowCount);
        }

        if (matrix1DValue == null) {
            matrix1DValue = new Matrix1DValue<Integer>(rowCount);
        }

        int index = 0;
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                index = i * rowCount + j;
                if (valueType == ValueType_Int) {
                    int value = getMatrix2DIntValue(index);
                    matrix2DValue.insert(i, j, value);
                } else if (valueType == ValueType_Float) {
                    float value = getMatrix2DFloatValue(index);
                    matrix2DValue.insert(i, j, value);
                } else if (valueType == ValueType_Double) {
                    double value = getMatrix2DDoubleValue(index);
                    matrix2DValue.insert(i, j, value);
                }
            }
        }

        for (int i = 0; i < rowCount; i++) {
            int value = getMatrix1DIntValue(i);
            matrix1DValue.insert(value);
        }

        return true;
    }

    private int getMatrix2DIntValue(int index) {
        int begin = dictFileHeader.valueBaseAdr;

        int value = DictCommonUtils.getIntValueFromByteArray(mFileBytecontent,
                begin + index * DictCommonUtils.IntFloatTypeByteCount,
                DictCommonUtils.IntFloatTypeByteCount);

        return value;
    }

    private float getMatrix2DFloatValue(int index) {
        int begin = dictFileHeader.valueBaseAdr;

        float value = DictCommonUtils.getFloatValueFromByteArray(mFileBytecontent,
                begin + index * DictCommonUtils.IntFloatTypeByteCount,
                DictCommonUtils.IntFloatTypeByteCount);

        return value;
    }

    private double getMatrix2DDoubleValue(int index) {
        int begin = dictFileHeader.valueBaseAdr;
        double value = 0;

//        double value = DictCommonUtils.getDoubleValueFromByteArray(mFileBytecontent,
//                begin + index * DictCommonUtils.DoubleTypeByteCount,
//                DictCommonUtils.DoubleTypeByteCount);

        return value;
    }

    private int getMatrix1DIntValue(int index) {
        int begin = dictFileHeader.extendValueBaseAdr;

        int value = DictCommonUtils.getIntValueFromByteArray(mFileBytecontent,
                begin + index * DictCommonUtils.IntFloatTypeByteCount,
                DictCommonUtils.IntFloatTypeByteCount);

        return value;
    }

}
