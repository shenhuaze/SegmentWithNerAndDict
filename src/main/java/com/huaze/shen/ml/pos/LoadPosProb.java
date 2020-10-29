package com.huaze.shen.ml.pos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Huaze Shen
 * @date 2020-10-29
 *
 * 加载用于hmm词性标注的转移概率
 */
public class LoadPosProb {
    public static void loadPOSTransMatrix(String transPath,
                                          double[][] transP,
                                          double[] eachPosFreq) {
        try {
            InputStream in = LoadPosProb.class.getResourceAsStream(transPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineSplit = line.split(":");
                PosEnum posFrom = PosEnum.valueOf(lineSplit[0]);
                PosEnum posTo = PosEnum.valueOf(lineSplit[1]);
                double freq = Double.valueOf(lineSplit[2]);
                transP[posFrom.ordinal()][posTo.ordinal()] = freq;
                eachPosFreq[posFrom.ordinal()] += freq;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load(String transFile, String eachFreqFile,
                            double[][] transP, int[] eachPosFreq) {
        try {
            BufferedReader br1 = new BufferedReader(new FileReader(transFile));
            BufferedReader br2 = new BufferedReader(new FileReader(eachFreqFile));
            String line;
            line = br1.readLine();
            int index = 0;
            while ((line = br1.readLine()) != null) {
                String[] lineSplit = line.split(",");
                for (int i = 1; i < lineSplit.length; i++) {
                    transP[index][i - 1] = Float.valueOf(lineSplit[i]);
                }
                index += 1;
            }
            index = 0;
            while ((line = br2.readLine()) != null) {
                String[] lineSplit = line.split(",");
                eachPosFreq[index] = Integer.valueOf(lineSplit[1]);
                index += 1;
            }
            br1.close();
            br2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
