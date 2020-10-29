package com.huaze.shen.dl.lexical;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ModelLoader {
    private static TFPredictor createPredictor(String name, int classNum) throws IOException {
        InputStream inputStream = new FileInputStream(name);
        byte[] bytes = IOUtils.toByteArray(inputStream);
        return new TFPredictor(bytes, classNum);
    }

    public static LexicalAnalyzer getLSTMLexicalAnalyzer() {
        LexicalAnalyzer lexicalAnalyzer = null;
        String resourcesDir = "src/main/resources/";
        try {
            Vocab vocab = new Vocab(resourcesDir + "tfmodels/all_map.json");
            TFPredictor segModel = createPredictor(resourcesDir + "tfmodels/seg.pb", vocab.getSegLabelNum());
            TFPredictor posModel = null;
            TFPredictor nerModel = createPredictor(resourcesDir + "tfmodels/ner.pb", vocab.getNerLabelNum());
            lexicalAnalyzer = new LSTMLexicalAnalyzer(vocab, segModel, posModel, nerModel);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return lexicalAnalyzer;
    }
}
