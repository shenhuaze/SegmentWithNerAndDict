package com.huaze.shen;

import com.huaze.shen.dl.lexical.Word;
import com.huaze.shen.ml.seg.DictSeg;

import java.util.List;

public class DemoDictSeg {
    public static void main(String[] args) {
        String resourcesDir = "src/main/resources/";
        DictSeg dictSeg = new DictSeg(resourcesDir);
        String text = "今天天气不错";
        List<Word> words = dictSeg.segOnlyDict(text);
        System.out.println(words);
    }
}
