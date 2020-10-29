package com.huaze.shen;

import com.huaze.shen.dl.lexical.Entity;
import com.huaze.shen.dl.lexical.ModelLoader;
import com.huaze.shen.dl.lexical.LexicalAnalyzer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Huaze Shen
 * @date 2020-10-29
 *
 * 测试深度学习NER
 */
public class DemoDlNer {
    public static void main(String[] args){

        long t1 = System.currentTimeMillis();

        // 初始化
        LexicalAnalyzer lexicalAnalyzer = ModelLoader.getLSTMLexicalAnalyzer(); // 深度学习分词

        //long t1 = System.currentTimeMillis();

        // 加载待处理文本
        List<String> docs = new ArrayList<>();
        docs.add("今天天气不错");

        // 深度学习进行NER识别，找出每个文本中可能出现的实体新词集合
        List<List<Entity>> entities = lexicalAnalyzer.ner(docs);
        System.out.println(entities);

        long t2 = System.currentTimeMillis();
        System.out.println("time cost: " + (t2 - t1));
    }
}
