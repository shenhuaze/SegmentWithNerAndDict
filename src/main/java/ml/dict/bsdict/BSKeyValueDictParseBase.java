package ml.dict.bsdict;

import java.io.File;
import java.io.FileInputStream;

public class BSKeyValueDictParseBase {
    // 读取的文件内容
    public byte[] mFileBytecontent = null;
    // 文件流
    public FileInputStream in = null;
    // 词典文件名
    public String mdicFileName = null;
    // 是否完成初始化
    private boolean isInited = false;

    /**
     * 词典中存放的相关统计数据
     */
    public boolean isMultyColData = false;
    BSKeyValueDictData.BSDictHeader dictHeader = new BSKeyValueDictData.BSDictHeader();

    /**
     * 判断是否为合法的数据，由子类实现判断,可以大幅减少无效的搜索
     */
    public boolean isValidDicData(String input) {
        return true;
    }

    /**
     * 初始化
     */
    public boolean init(String fileName) {
        if (fileName == null || fileName.length() <= 0) {
            return false;
        }

        if (isInited) {
            return true;
        }

        mdicFileName = fileName;

        File file = new File(fileName);
        if (!file.exists()) {
            return false;
        }

        if (mFileBytecontent != null) {
            return true;
        }

        Long filelen = file.length();
        byte[] filebContent = new byte[filelen.intValue()];

        try {
            in = new FileInputStream(file);
            in.read(filebContent);
            mFileBytecontent = filebContent;

            dictHeader.headerType = getHeaderType();
            if (!dictHeader.isValidHeaderType()) {
                return false;
            }
            dictHeader.keyCharSetNameType = getKeyCharSetNameType();
            dictHeader.valueCharSetNameType = getvalueCharSetNameType();
            isMultyColData = dictHeader.isExistValue(mFileBytecontent);
            dictHeader.isMultyColData = getIsMultyColData();
            dictHeader.blockItemSize = getBlockSize();
            dictHeader.dataBlockCount = getDataBlockCount();
            dictHeader.keyNameMaxLen = getNameMaxLen();
            dictHeader.keyNameMinLen = getNameMinLen();

            if (dictHeader.keyCharSetNameType == BSKeyValueDictData.BSDictHeaderConfig.KeyCharSetNameType.KeyCharKSetName_UTF16BE) {
                BSKeyValueDictData.BSDictHeader.keyNameCharsetName = BSKeyValueDictData.UTF16CharSetName;
            } else if (dictHeader.keyCharSetNameType == BSKeyValueDictData.BSDictHeaderConfig.KeyCharSetNameType.KeyCharKSetName_UTF8){
                BSKeyValueDictData.BSDictHeader.keyNameCharsetName = BSKeyValueDictData.UTF8CharSetName;
            }

            if (dictHeader.valueCharSetNameType == BSKeyValueDictData.BSDictHeaderConfig.ValueCharSetNameType.ValueCharSetName_UTF16BE) {
                BSKeyValueDictData.BSDictHeader.valueNameCharsetName = BSKeyValueDictData.UTF16CharSetName;
            } else if (dictHeader.valueCharSetNameType == BSKeyValueDictData.BSDictHeaderConfig.ValueCharSetNameType.ValueCharSetName_UTF8){
                BSKeyValueDictData.BSDictHeader.valueNameCharsetName = BSKeyValueDictData.UTF8CharSetName;
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                in.close();
                in = null;

                dictHeader.clear();

            } catch (Exception e1) {
                e1.printStackTrace();
            }

            return false;
        }

        isInited = true;

        return true;
    }

    /**
     * 关闭流
     */
    public void closeInputstream() {
        if (in == null) {
            return;
        }

        try {
            in.close();
            in = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清理资源
     */
    public void clearResource() {
        mFileBytecontent = null;
        isInited = false;
        isMultyColData = false;
        dictHeader.clear();
        closeInputstream();
    }

    public int getMaxSearchId() {
        return 0;
    }

    public int getBlockSize() {
        return 0;
    }

    public int getDataBlockCount() {
        return 0;
    }

    public int getNameMaxLen() {
        return 0;
    }

    public int getNameMinLen() {
        return 0;
    }

    public int getIsMultyColData() {
        return 0;
    }

    public int getKeyCharSetNameType() {
        return 0;
    }

    public int getvalueCharSetNameType() {
        return 0;
    }

    public String getHeaderType() throws Exception {
        return "";
    }
}
