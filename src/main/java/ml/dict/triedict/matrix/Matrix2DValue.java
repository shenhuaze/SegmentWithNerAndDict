package ml.dict.triedict.matrix;

import java.util.ArrayList;

public class Matrix2DValue<T> {
    // 二维数组
    private ArrayList<ArrayList<T>> matrix2D = null;
    private int matrixColRowCount = 0;

    private Matrix2DValue() {
    }

    public Matrix2DValue(int colRowCount) {
        matrixColRowCount = colRowCount;
        matrix2D = new ArrayList(colRowCount);
        for (int i = 0; i < colRowCount; i++) {
            ArrayList<T> newmatrix2D = new ArrayList<T>(colRowCount);
            matrix2D.add(newmatrix2D);
        }
    }

    public void insert(int row, int col, T value) {
        if (matrix2D == null) {
            return;
        }

        ArrayList<T> matrix1D = (ArrayList<T>) matrix2D.get(row);
        if (matrix1D == null) {
            return;
        }

        matrix1D.add(col, value);
    }

    /**
     * 从矩阵中根据行列查找
     */
    public T search(int row, int col) {
        int rowColMax = matrixColRowCount;
        if (row >= rowColMax || col >= rowColMax) {
            return null;
        }

        ArrayList<T> matrixRow = (ArrayList<T>) matrix2D.get(row);

        return matrixRow.get(col);
    }

}
