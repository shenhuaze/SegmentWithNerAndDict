package com.huaze.shen;

import ml.dict.triedict.matrix.Matrix2DDictParse;
import ml.pos.LoadPosProb;
import ml.pos.PosEnum;

/**
 * @author Huaze Shen
 * @date 2020-10-29
 *
 * 验证词性转移矩阵的二进制格式与文本格式的一致性
 */
public class DemoVerifyPosTransitionMatrix {
    public static void main(String[] args) {
        String dictFilename = "src/main/resources/data/pos/matrix2d.matrix";
        String transFile = "src/main/resources/data/pos/posTransMatrix";
        String eachFreqFile = "src/main/resources/data/pos/eachPosTotalFreq.txt";
        Matrix2DDictParse parse = new Matrix2DDictParse(dictFilename);
        int numPos = PosEnum.values().length;
        double[][] transProb = new double[numPos][numPos];
        int[] eachPosTotalFreq = new int[numPos];
        LoadPosProb.load(transFile, eachFreqFile, transProb, eachPosTotalFreq);
        // Test 2D matrix
        int valueType = parse.getValueType();
        System.out.println(valueType);
        for (int i = 0; i < numPos; i++) {
            for (int j = 0; j < numPos; j++) {
                if ((float)parse.search2DMatrix(i, j) != transProb[i][j]) {
                    System.out.println("error: " + PosEnum.values()[i] + " to " + PosEnum.values()[j]
                            + " " + (float)parse.search2DMatrix(i, j) + " - " + transProb[i][j]
                            + " = " + ((float)parse.search2DMatrix(i, j) - transProb[i][j]));
                }
            }
        }
        // Test 1D matrix
        for (int i = 0; i < numPos; i++) {
            if ((int)parse.search1DMatrix(i) != eachPosTotalFreq[i]) {
                System.out.println("error: " + PosEnum.values()[i]);
            }
        }
    }
}
