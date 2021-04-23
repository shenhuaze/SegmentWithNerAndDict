package com.huaze.shen;

import com.huaze.shen.dl.lexical.Entity;
import com.huaze.shen.dl.lexical.ModelLoader;
import com.huaze.shen.dl.lexical.LexicalAnalyzer;
import com.huaze.shen.dl.lexical.Word;
import com.huaze.shen.ml.pos.PosTag;
import com.huaze.shen.ml.seg.DictSeg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Huaze Shen
 * @date 2020-10-29
 *
 * 测试深度学习分词 + 深度学习NER + 自定义词典分词 + 词性标注
 */
public class DemoCombinedSeg {
    public static void main(String[] args){
        String resourcesDir = "src/main/resources/";
        long t1 = System.currentTimeMillis();

        // 初始化
        LexicalAnalyzer lexicalAnalyzer = ModelLoader.getLSTMLexicalAnalyzer(); // 深度学习分词
        DictSeg dictSeg = new DictSeg(resourcesDir); // 词典分词
        PosTag posTag = new PosTag(resourcesDir); // 词性标注

        //long t1 = System.currentTimeMillis();

        // 加载待处理文本
        List<String> docs = new ArrayList<>();
        docs.add("我在北京晒太阳你在非洲看雪");
        docs.add("我不喜欢日本和服。");
        docs.add("雷猴回归人间。");
        docs.add("老何周的家在北京市西城区郑汴路麦当劳餐厅的后面11栋2321号zero房间");
        docs.add("东海县迅捷贸易有限责任公司");
        docs.add("教授正在教授自然语言处理");
        docs.add("我在广百新翼大厦吃饭");
        docs.add("小张和小李从此老死不相往来");
        docs.add("邓颖超生前杜绝超生");
        docs.add("刘琦超生罚款一百");
        docs.add("我们在大望路站上");
        docs.add("张伟13019283847");
        docs.add("2018年1月2日，我们出去玩");
        docs.add("国际奥委会公布奥运会申办城市");
        docs.add("我在上海林原科技有限公司工作");
        docs.add("陈生记黑鱼馆，巴依老爷新疆美食，到啦精品主题酒店，7天优品酒店五棵松店");

        // 深度学习分词
        List<List<Word>> dlSegResults = lexicalAnalyzer.cut(docs);

        // 深度学习进行NER识别，找出每个文本中可能出现的实体新词集合
        List<List<Entity>> entities = lexicalAnalyzer.ner(docs);
        List<Map<String, Map<String, String>>> nerWordsList = new ArrayList<>();
        System.out.println("Deep learning NER results:");
        for (int i = 0; i < docs.size(); i++) {
            Map<String, Map<String, String>> nerWords = new HashMap<>();
            nerWordsList.add(nerWords);
            System.out.println(entities.get(i));
            for (Entity entity: entities.get(i)) {
                String recognizedWord = entity.getContent();
                String pos;
                String entityType;
                switch (entity.getLabel()) {
                    case "person":
                        pos = "nr";
                        entityType = "person";
                        break;
                    case "location":
                        pos = "ns";
                        entityType = "location";
                        break;
                    case "org":
                        pos = "nt";
                        entityType = "organization";
                        break;
                    case "company":
                        pos = "nt";
                        entityType = "company";
                        break;
                    case "phone":
                        pos = "m";
                        entityType = "phone";
                        break;
                    case "time":
                        pos = "t";
                        entityType = "time";
                        break;
                    default:
                        pos = "nz";
                        entityType = entity.getLabel();
                        break;
                }
                if (!nerWords.containsKey(recognizedWord)) {
                    nerWords.put(recognizedWord, new HashMap<>());
                    nerWords.get(recognizedWord).put(pos, entityType);
                } else {
                    nerWords.get(recognizedWord).put(pos, entityType);
                }
            }
            //for (String word: nerWords.keySet()) {
            //    for (String pos: nerWords.get(word).keySet()) {
            //        System.out.print(word + ":" + pos + ":" + nerWords.get(word).get(pos));
            //    }
            //    System.out.print("\n");
            //}
            //System.out.println();
        }
        System.out.println();

        // 在深度学习分词的基础上，结合NER和词典进行分词
        System.out.println("Deep learning segment results:");
        for (int i = 0; i < docs.size(); i++) {
            System.out.println(dlSegResults.get(i));
        }
        System.out.println();

        List<List<Word>> finalSegResults = new ArrayList<>();
        System.out.println("Combined segment results:");
        for (int i = 0; i < docs.size(); i++) {
            finalSegResults.add(dictSeg.segCombinedDL(docs.get(i), dlSegResults.get(i), nerWordsList.get(i)));
            //finalSegResults.add(dictSeg.segOnlyDict(docs.get(i)));
            System.out.println(finalSegResults.get(i));
        }
        System.out.println();

        // 词性标注
        System.out.println("Pos-tagging results:");
        for (int i = 0; i < docs.size(); i++) {
            posTag.posTaggingCombinedDL(finalSegResults.get(i), nerWordsList.get(i));
            //posTag.posTaggingOnlyDict(finalSegResults.get(i));
            System.out.println(finalSegResults.get(i));
        }

        long t2 = System.currentTimeMillis();
        System.out.println("time cost: " + (t2 - t1));
    }
}