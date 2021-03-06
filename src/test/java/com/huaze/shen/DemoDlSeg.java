package com.huaze.shen;

import com.huaze.shen.dl.lexical.ModelLoader;
import com.huaze.shen.dl.lexical.Entity;
import com.huaze.shen.dl.lexical.LexicalAnalyzer;
import com.huaze.shen.dl.lexical.Word;

import java.util.List;

/**
 * @author Huaze Shen
 * @date 2020-10-29
 *
 * 测试深度学习分词
 */
public class DemoDlSeg {
    public static void main(String[] args) {
        LexicalAnalyzer lexicalAnalyzer = ModelLoader.getLSTMLexicalAnalyzer();
        String text = "今天天气不错";
        List<List<Word>> wordLists = lexicalAnalyzer.cut(text);
        List<List<Entity>> entityLists = lexicalAnalyzer.ner(text);
        for (List<Word> wordList: wordLists) {
            System.out.println(wordList);
        }
        for (List<Entity> entityList: entityLists) {
            System.out.println(entityList);
        }
    }
}
