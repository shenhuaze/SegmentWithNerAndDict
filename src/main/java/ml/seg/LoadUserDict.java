package ml.seg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Huaze Shen
 * @date 2020-10-29
 *
 * 加载用户自定义词典
 */
public class LoadUserDict {
    //private static final String DICT_PATH = "/nmodels/user_dict.txt";
    //private static final String DICT_PATH = "/nmodels/test_dict.txt";
    private static final String DICT_PATH = "src/main/resources/ml/seg/seg_dict.txt";

    public Map<String, Map<String, Integer>> dict;
    public Map<String, Integer> wordFreq;
    public int totalFreq;
    public int vocabSize;
    public Map<String, String> wordStringMap;

    public LoadUserDict() {
        init();
    }

    public void init() {
        this.wordStringMap = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(DICT_PATH));
            String line;
            line = br.readLine();
            this.totalFreq = Integer.parseInt(line.split("\t")[1]);
            line = br.readLine();
            this.vocabSize = Integer.parseInt(line.split("\t")[1]);
            while ((line = br.readLine()) != null) {
                String[] lineSplit = line.split("\t");
                wordStringMap.put(lineSplit[0], lineSplit[1]);
            }
            br.close();
        } catch (Exception e) {
            System.out.println("ERROR!!!读取分词词典失败!");
            e.printStackTrace();
        }
    }

    public int getTotalFreq() {
        return this.totalFreq;
    }

    public int getVocabSize() {
        return this.vocabSize;
    }

    public Map<String, String> getReturnString() {
        return this.wordStringMap;
    }

    public void loadDict() {
        dict = new HashMap<>();
        wordFreq = new HashMap<>();
        this.totalFreq = 0;
        try {
            InputStream in = LoadUserDict.class.getResourceAsStream(DICT_PATH);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineSplit = line.split("[\t ]");
                String word = lineSplit[0];
                dict.put(word, new HashMap<>());
                wordFreq.put(word, 0);
                for (int i = 1; i < lineSplit.length; i += 2) {
                    String pos = lineSplit[i];
                    if (lineSplit[i + 1].equals("ns")) {
                        System.out.println(line);
                    }
                    int freq = Integer.valueOf(lineSplit[i + 1]);
                    dict.get(word).put(pos, freq);
                    wordFreq.put(word, wordFreq.get(word) + freq);
                    totalFreq += freq;
                }
            }
            vocabSize = wordFreq.size();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
