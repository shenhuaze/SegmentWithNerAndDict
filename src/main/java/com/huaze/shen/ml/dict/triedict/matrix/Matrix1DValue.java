package com.huaze.shen.ml.dict.triedict.matrix;

import java.util.ArrayList;

public class Matrix1DValue<T> {
    // 二维数组
    private ArrayList<T> matrix1D = null;
    private int matrixLen = 0;

    private Matrix1DValue() {
    }

    public Matrix1DValue(int matrixLenValue) {
        matrixLen = matrixLenValue;
        matrix1D = new ArrayList(matrixLen);
    }

    public void insert(T value) {
        if (matrix1D == null) {
            return;
        }

        matrix1D.add(value);
    }

    /**
     * 从矩阵中根据行列查找
     */
    public T search(int index) {
        int rowColMax = matrixLen;
        if (index >= rowColMax) {
            return null;
        }

        T matrixValue = (T) matrix1D.get(index);

        return matrixValue;
    }
}
