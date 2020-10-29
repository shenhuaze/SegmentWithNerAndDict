package com.huaze.shen.ml.seg;

import com.huaze.shen.dl.lexical.Word;
import com.huaze.shen.ml.dict.triedict.parse.ReducedTrieParse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Huaze Shen
 * @date 2020-10-29
 *
 * 在深度学习分词的基础上，添加用户自定义词典进行分词，分词原理类似于最大概率分词，不过这里每个词的权重不仅与词频有关，还与词长有关
 */
public class DictSeg {
    public static final Integer MAX_WORD_LENGTH = 20;
    public static ReducedTrieParse reducedTrieParse;
    public static Map<String, String> wordStringMap;
    private int totalFreq;
    private int vocabSize;

    public DictSeg(String path) {
        LoadUserDict userDict = new LoadUserDict();
        wordStringMap = userDict.getReturnString();
        totalFreq = userDict.totalFreq;
        vocabSize = userDict.vocabSize;
    }

    // 结合深度学习分词 + 深度学习NER + 自定义词典，进行分词
    public List<Word> segCombinedDL(String str, List<Word> dlSegResult, Map<String, Map<String, String>> nerWords) {
        int n = str.length();
        Map<Integer, Map<Integer, Double>> dag = createDAG(str, dlSegResult, nerWords);
        Map<Integer, Pair> route = calcRoute(str, dag);
        List<Word> optimumRoute = new ArrayList<>();
        int i = 0;
        while (i < n) {
            int j = route.get(i).getNextNode() + 1;
            String word = str.substring(i, j);
            optimumRoute.add(new Word(word));
            i = j;
        }
        return optimumRoute;
    }

    // 只基于自定义词典进行分词
    public List<Word> segOnlyDict(String str) {
        List<Word> dlSegResult = new ArrayList<>();
        Map<String, Map<String, String>> nerWords = new HashMap<>();
        return segCombinedDL(str, dlSegResult, nerWords);
    }

    /**
     * 构建有向无环图(Directed Acyclic Graph, DAG)
     */
    private Map<Integer, Map<Integer, Double>> createDAG(String str, List<Word> dlSegResult, Map<String, Map<String, String>> nerWords) {
        Map<Integer, Map<Integer, Double>> dag = new HashMap<>();
        for (int i = 0; i < str.length(); i++) {
            dag.put(i, new HashMap<>());
            String word = str.substring(i, i + 1);
            double weight;
            try {
                //String returnLine = reducedTrieParse.searchReturnStringValue(word);
                String returnLine = wordStringMap.get(word);
                if (returnLine != null) {
                    weight = getWordWeight(returnLine);
                } else {
                    weight = Math.log((1.0 * word.length() / (totalFreq + vocabSize)));
                }
                dag.get(i).put(i, weight);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int index = 0;
        if (dlSegResult != null) {
            for (Word word: dlSegResult) {
                double weight;
                try {
                    //String returnLine = reducedTrieParse.searchReturnStringValue(word.getContent());
                    String returnLine = wordStringMap.get(word.getContent());
                    if (returnLine != null) {
                        weight = getWordWeight(returnLine);
                    } else {
                        weight = Math.log(1.0 * word.getContent().length() / (totalFreq + vocabSize));
                    }
                    dag.get(index).put(index + word.getContent().length() - 1, weight);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                index += word.getContent().length();
            }
        }
        for (int i = 0; i < str.length(); i++) {
            for (int j = i + 1; j < i + MAX_WORD_LENGTH; j++) {
                if (j < str.length()) {
                    String word = str.substring(i, j + 1);
                    double weight;
                    try {
                        //String returnLine = reducedTrieParse.searchReturnStringValue(word);
                        String returnLine = wordStringMap.get(word);
                        if (returnLine != null) {
                            weight = getWordWeight(returnLine);
                            dag.get(i).put(j, weight);
                        } else if (nerWords != null && nerWords.containsKey(word)) {
                            weight = Math.log(1.0 * word.length() / (totalFreq + vocabSize));
                            dag.get(i).put(j, weight);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return dag;
    }

    /**
     * 动态规划计算最佳路径
     */
    private Map<Integer, Pair> calcRoute(String str, Map<Integer, Map<Integer, Double>> dag) {
        Map<Integer, Pair> route = new HashMap<>();
        int n = str.length();
        route.put(n, new Pair(0, 0.0));
        for (int i = n - 1; i >= 0; i--) {
            Pair candidate = null;
            for (int j: dag.get(i).keySet()) {
                double eachWeight = dag.get(i).get(j);
                double totalWeight = eachWeight + route.get(j + 1).getWeight();
                if (candidate == null) {
                    candidate = new Pair(j, totalWeight);
                } else if (totalWeight > candidate.getWeight()) {
                    candidate = new Pair(j, totalWeight);
                }
            }
            route.put(i, candidate);
        }
        return route;
    }

    private double getWordWeight(String returnLine) {
        double weight = Double.NEGATIVE_INFINITY;
        try {
            String[] lineSplit = returnLine.split("#");
            weight = Double.parseDouble(lineSplit[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weight;
    }
}
