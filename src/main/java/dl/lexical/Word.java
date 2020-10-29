package dl.lexical;

import java.io.Serializable;

public class Word implements Serializable{
    private String content = "";
    private String pos = "";
    private String posSubInfo = "";

    public Word() {
    }

    public Word(String content, String pos, String posSubInfo) {
        this.content = content;
        this.pos = pos;
        this.posSubInfo = posSubInfo;
    }

    public Word(String content, String pos) {
        this.content = content;
        this.pos = pos;
    }

    public Word(String content) {
        this.content = content;
        this.pos = "";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPosSubInfo() {
        return posSubInfo;
    }

    public void setPosSubInfo(String posSubInfo) {
        this.posSubInfo = posSubInfo;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    @Override
    public String toString() {
        String str;
        if ("".equals(pos) && "".equals(posSubInfo)) {
            str = content;
        } else if ("".equals(posSubInfo)) {
            str = content + "/" + pos;
        } else {
            str = content + "/" + pos + "_" + posSubInfo;
        }
        return str;
    }
}
