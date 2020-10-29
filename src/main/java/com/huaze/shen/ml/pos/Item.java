package com.huaze.shen.ml.pos;

import java.util.Map;

/**
 * @author Huaze Shen
 * @date 2020-10-29
 *
 * 做词性标注时，用来存储每个词所有可能的词性
 */
public class Item {
    private String word;
    private Map<String, Double> candidateNatures;

    public Item(String word) {
        this.word = word;
    }

    public Item(String word, Map<String, Double> candidateNatures) {
        this.word = word;
        this.candidateNatures = candidateNatures;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Map<String, Double> getCandidateNatures() {
        return candidateNatures;
    }

    public void setCandidateNatures(Map<String, Double> candidateNatures) {
        this.candidateNatures = candidateNatures;
    }

}
