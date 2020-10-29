package ml.pos;

/**
 * @author Huaze Shen
 * @date 2020-10-29
 *
 * 词性信息
 */
public class PosInfo {
    private String pos;
    private String subInfo;

    public PosInfo(String pos, String subInfo) {
        this.pos = pos;
        this.subInfo = subInfo;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getSubInfo() {
        return subInfo;
    }

    public void setSubInfo(String subInfo) {
        this.subInfo = subInfo;
    }
}
