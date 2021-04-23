package com.huaze.shen.ml.seg;

import com.huaze.shen.dl.lexical.Word;

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
 * 在深度学习分词的基础上，添加用户自定义词典进行分词，分词原理类似于最大概率分词
 */
public class DictSeg {
    private static final Integer MAX_WORD_LENGTH = 20;
    private int totalFreq;
    private Map<String, Integer> wordFreqMap;

    public DictSeg(String dir) {
        loadDict(dir);
    }

    public void loadDict(String dir) {
        this.totalFreq = 0;
        this.wordFreqMap = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(dir + "data/CoreNatureDictionary.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineSplit = line.split("\\s+");
                String word = lineSplit[0];
                int wordFreq = 0;
                for (int i = 1; i < lineSplit.length; i = i + 2) {
                    wordFreq = Integer.parseInt(lineSplit[i + 1]);
                }
                totalFreq += wordFreq;
                wordFreqMap.put(word, wordFreq);
            }
            br.close();
        } catch (Exception e) {
            System.out.println("ERROR!!!读取分词词典失败!");
            e.printStackTrace();
        }
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
                Integer freq = 1;
                if (wordFreqMap.containsKey(word)) {
                    freq = wordFreqMap.get(word);
                }
                weight = Math.log((1.0 * freq / totalFreq));
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
                    Integer freq = 1;
                    if (wordFreqMap.containsKey(word.getContent())) {
                        freq = wordFreqMap.get(word.getContent());
                    }
                    weight = Math.log((1.0 * freq / totalFreq));
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
                    int freq;
                    double weight;
                    if (wordFreqMap.containsKey(word)) {
                        freq = wordFreqMap.get(word);
                        weight = Math.log((1.0 * freq / totalFreq));
                        dag.get(i).put(j, weight);
                    } else if (nerWords != null && nerWords.containsKey(word)) {
                        weight = Math.log((1.0 / totalFreq));
                        dag.get(i).put(j, weight);
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
}
