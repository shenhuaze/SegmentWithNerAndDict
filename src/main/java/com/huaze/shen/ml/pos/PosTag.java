package com.huaze.shen.ml.pos;

import com.huaze.shen.dl.lexical.Word;
import com.huaze.shen.ml.dict.triedict.matrix.Matrix2DDictParse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Huaze Shen
 * @date 2020-10-29
 *
 * 词性标注
 */
public class PosTag {
    private static final String MATRIX_FILE = "data/pos/matrix2d.matrix";
    private Matrix2DDictParse matrixParse;
    private Map<String, Map<String, Integer>> wordPosFreqMap;
    private Map<String, Map<String, Double>> wordPosProbMap;

    public PosTag(String dir) {
        matrixParse = new Matrix2DDictParse(dir + MATRIX_FILE);
        loadDict(dir);
    }

    public void loadDict(String dir) {
        this.wordPosFreqMap = new HashMap<>();
        this.wordPosProbMap = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(dir + "data/CoreNatureDictionary.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineSplit = line.split("\\s+");
                String word = lineSplit[0];
                Map<String, Integer> posFreq = new HashMap<>();
                Map<String, Double> posProb = new HashMap<>();
                for (int i = 1; i < lineSplit.length; i = i + 2) {
                    String pos = lineSplit[i];
                    int freq = Integer.parseInt(lineSplit[i + 1]);
                    posFreq.put(pos, freq);
                    posProb.put(pos, 1.0);
                }
                wordPosFreqMap.put(word, posFreq);
                wordPosProbMap.put(word, posProb);
            }
            br.close();
        } catch (Exception e) {
            System.out.println("ERROR!!!读取分词词典失败!");
            e.printStackTrace();
        }
    }

    /**
     * 结合深度学习NER，用HMM和Viterbi进行词性标注
     */
    public void posTaggingCombinedDL(List<Word> wordList, Map<String, Map<String, String>> nerWords) {
        // 设定观测序列中每个观测状态的所有可能隐藏状态
        List<Item> itemList = new ArrayList<>();
        setItemList(itemList, wordList, nerWords);

        // 往itemList中添加start和end节点
        itemList.add(0, new Item("", new HashMap<>()));
        double defaultProb = Math.log(1.0 / PosEnum.values().length);
        itemList.get(0).getCandidateNatures().put("start", defaultProb);
        itemList.add(new Item("", new HashMap<>()));
        itemList.get(itemList.size() - 1).getCandidateNatures().put("end", defaultProb);

        // 添加start和end节点之后的观测序列长度
        int numItems = itemList.size();
        // 词性总数
        int numStates = PosEnum.values().length;

        // 用二维数组记录观测序列每个时刻的所有可能隐状态的最佳log概率，
        // 数组的行数为numItems，列数为numStates
        // 所有log概率初始化为负无穷大(对应的真实概率为零)
        double[][] probs = new double[numItems][numStates];
        for (int i = 0; i < numItems; i++) {
            for (int j = 0; j < numStates; j++) {
                probs[i][j] = Double.NEGATIVE_INFINITY;
            }
        }

        // 用二维数组记录每个时刻的隐状态的最佳前驱隐状态
        // 数组的行数为numItems，列数为numStates
        PosEnum[][] bestPrev = new PosEnum[numItems][numStates];

        // 设定零时刻(也就是起始位置)为"start"词性的log概率为0(对应的真实概率为1)，
        probs[0][PosEnum.start.ordinal()] = 0;

        // 用Viterbi算法计算最佳前驱
        for (int i = 1; i < numItems; i++) {
            Item currItem = itemList.get(i);
            Item prevItem = itemList.get(i - 1);
            Map<String, Double> currNatures = currItem.getCandidateNatures();
            Map<String, Double> prevNatures = prevItem.getCandidateNatures();
            for (String currNature: currNatures.keySet()) {
                int currIndex = PosEnum.valueOf(currNature).ordinal();
                double emit_p;
                // 设定发射概率emit_p
                emit_p = currNatures.get(currNature);
                for (String prevNature: prevNatures.keySet()) {
                    // 如果前一个词性是"unknown"，则当前词各个词性对应的累积概率设定为正比于各个词性的出现次数
                    if (prevNatures.size() == 1 && prevNature.equals("unknown")) {
                        probs[i][currIndex] = currNatures.get(currNature);
                        bestPrev[i][currIndex] = PosEnum.valueOf(prevNature);
                        break;
                    }
                    int prevIndex = PosEnum.valueOf(prevNature).ordinal();
                    double prev_p = probs[i - 1][prevIndex];
                    double prev_pos_freq = (int)matrixParse.search1DMatrix(prevIndex);
                    // 设定转移概率trans_p
                    double trans_p = (float)matrixParse.search2DMatrix(prevIndex, currIndex);
                    double total_p = emit_p + trans_p + prev_p;
                    if (total_p > probs[i][currIndex]) {
                        probs[i][currIndex] = total_p;
                        bestPrev[i][currIndex] = PosEnum.valueOf(prevNature);
                    }
                }
            }
        }

        // 回溯求最佳路径
        PosEnum buffPos = PosEnum.end;
        PosEnum[] bestRoute = new PosEnum[numItems];
        for (int i = numItems - 1; i > 1; i--) {
            bestRoute[i - 1] = bestPrev[i][buffPos.ordinal()];
            buffPos = bestRoute[i - 1];
        }

        // 设定每个词最终的词性
        for (int i = 1; i < numItems - 1; i++) {
            String pos = bestRoute[i].toString();
            String wordText = wordList.get(i - 1).getContent();
            try {
                if (!wordPosProbMap.containsKey(wordText) && nerWords.containsKey(wordText)
                        && nerWords.get(wordText).containsKey(pos)) {
                    wordList.get(i - 1).setPos(pos);
                    wordList.get(i - 1).setPosSubInfo(nerWords.get(wordText).get(pos));
                } else {
                    wordList.get(i - 1).setPos(pos);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 只基于词典进行词性标注
     */
    public void posTaggingOnlyDict(List<Word> wordList) {
        Map<String, Map<String, String>> nerWords = new HashMap<>();
        posTaggingCombinedDL(wordList, nerWords);
    }

    private void setItemList(List<Item> itemList, List<Word> wordList, Map<String, Map<String, String>> nerWords) {
        for (Word word: wordList) {
            String textWord = word.getContent();
            Item item = new Item(textWord);
            try {
                //String returnLine = wordPosFreqMap.get(textWord);
                String returnLine = "";
                if (returnLine != null && nerWords !=null && nerWords.containsKey(textWord)) {
                    item.setCandidateNatures(getPosProb(returnLine, textWord));
                    itemList.add(item);
                    continue;
                }
                if (returnLine != null) {
                    item.setCandidateNatures(getPosProb(returnLine, textWord));
                    itemList.add(item);
                    continue;
                }
                if (nerWords != null && nerWords.containsKey(textWord)) {
                    Map<String, Double> posProb = new HashMap<>();
                    for (String pos: nerWords.get(textWord).keySet()) {
                        int posIndex = PosEnum.valueOf(pos).ordinal();
                        double prob = Math.log(1.0 /
                                        ((int)matrixParse.search1DMatrix(posIndex) + PosEnum.values().length));
                        posProb.put(pos, prob);
                    }
                    item.setCandidateNatures(posProb);
                    itemList.add(item);
                    continue;
                }
                if (CharacterUtil.isNumberSeq(textWord)) {
                    item.setCandidateNatures(new HashMap<>());
                    double prob = Math.log(1.0 / PosEnum.values().length);
                    item.getCandidateNatures().put("m", prob);
                    itemList.add(item);
                    continue;
                }
                item.setCandidateNatures(new HashMap<>());
                double prob = Math.log(1.0 / PosEnum.values().length);
                item.getCandidateNatures().put("unknown", prob);
            } catch (Exception e) {
                e.printStackTrace();
            }
            itemList.add(item);
        }
    }

    private Map<String, Double> getPosProb(String returnLine, String word) {
        Map<String, Double> candidateNatures = new HashMap<>();
        String[] lineSplit = returnLine.split("#")[1].split(":");
        for (int i = 0; i < lineSplit.length; i += 2) {
            candidateNatures.put(lineSplit[i], Double.valueOf(lineSplit[i + 1]));
        }
        return candidateNatures;
    }
}
