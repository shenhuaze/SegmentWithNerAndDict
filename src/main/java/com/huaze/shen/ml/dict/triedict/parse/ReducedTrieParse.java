package com.huaze.shen.ml.dict.triedict.parse;

import com.huaze.shen.ml.dict.triedict.common.DictCommonUtils;
import com.huaze.shen.ml.dict.triedict.triefile.ReducedTrieFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static com.huaze.shen.ml.dict.triedict.triefile.ReducedTrieFile.ReducedTrieFileFlag.VALUE_NO_EXIST;

public class ReducedTrieParse {
    // header
    private ReducedTrieFile trieFile;
    // 读取的文件内容
    public byte[] mFileBytecontent = null;
    // 文件流
    public FileInputStream in = null;
    // 词典文件名
    public String mdicFileName = null;
    // 是否完成初始化
    private boolean isInited = false;

    private int endFlag = ReducedTrieFile.ReducedTrieFileFlag.KEY_END_FLAG;
    private int delFlag = ReducedTrieFile.ReducedTrieFileFlag.DEL_FLAG;

    private ReducedTrieParse() {
        trieFile = new ReducedTrieFile();
    }

    public interface DictParseCallback {
        void dicParseMatchKeySuccess(String keyName, int startOffset);

        void dicParseMatchKeyFailed();
    }

    public ReducedTrieParse(String fileName) {
        trieFile = new ReducedTrieFile();
        load(fileName);
    }

    // 加载数据
    private boolean load(String fileName) {
        if (fileName == null || fileName.length() <= 0) {
            return false;
        }

        if (isInited) {
            return true;
        }

        return init(fileName);
    }

    /**
     * 初始化
     */
    private boolean init(String fileName) {
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
            if (!initFileHeader(mFileBytecontent)) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                in.close();
                in = null;
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            return false;
        }

        isInited = true;

        return true;
    }

    // 初始化Header
    private boolean initFileHeader(byte[] fileBytecontent) throws Exception {
        if (trieFile == null) {
            return false;
        }

        return trieFile.initFileHeader(fileBytecontent);
    }

    /**
     * 关闭流
     */
    private void closeInputstream() {
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
    private void clearResource() {
        mFileBytecontent = null;
        isInited = false;
        //trieFile.clear();
        trieFile = null;
        closeInputstream();
    }

    public boolean searchKeyIsExist(String key) {
        if (key == null || key.length() <= 0) {
            return false;
        }

        return searchByKeyCode(DictCommonUtils.string2IntArray(key, endFlag));
    }

    public byte[] searchReturnBytesValue(String key) {
        if (key == null || key.length() <= 0) {
            return null;
        }

        return searchReturnValue(DictCommonUtils.string2IntArray(key, endFlag));
    }

    public String searchReturnStringValue(String key) throws Exception {
        if (key == null || key.length() <= 0) {
            return null;
        }

        byte[] bResult = searchReturnValue(DictCommonUtils.string2IntArray(key, endFlag));
        if (bResult == null || bResult.length <= 0) {
            return null;
        }

        String result = null;

        if (bResult != null) {
            result = new String(bResult, "UTF-8");
        }

        return result;
    }

    /**
     * 查找一个词是否在Trie树结构中
     */
    private boolean searchByKeyCode(int[] keyList) {
        if (keyList == null || keyList.length <= 0) {
            return false;
        }

        if (trieFile == null || trieFile.fileContentIndex.bcLen <= 0) {
            return false;
        }

        if (!isValiedKey(keyList)) {
            return false;
        }

        int pre = 1;
        int cur = 1;

        for (int i = 0; i < keyList.length; i++) {
            int curKey = keyList[i];
            cur = getBase(pre) + curKey;
            int curBaseValue = getBase(cur);
            int curCheckValue = getCheck(cur);
            if (curCheckValue != pre) {
                return false;
            }

            if (isExistValue()) {
                if (curBaseValue < 0) {
                    return compareInTail(-curBaseValue, i + 1, keyList);
                } else if (curBaseValue > 0) {
                    pre = cur;
                } else if (curBaseValue == 0) {
                    return false;
                }
            } else {
                if (curBaseValue < 0) {
                    return compareInTail(-curBaseValue, i + 1, keyList);
                } else if (curBaseValue > 0) {
                    pre = cur;
                } else if (curBaseValue == 0) {
                    return (curKey == endFlag) ? true : false;
                }
            }
        }

        return false;
    }

    /**
     * 查找一个词是否在Trie树结构中
     */
    private byte[] searchReturnValue(int[] keyList) {
        if (keyList == null || keyList.length <= 0) {
            return null;
        }

        if (trieFile == null || trieFile.fileContentIndex.bcLen <= 0) {
            return null;
        }

        if (!isValiedKey(keyList)) {
            return null;
        }

        int pre = 1;
        int cur = 1;

        for (int i = 0; i < keyList.length; i++) {
            int curKey = keyList[i];
            cur = getBase(pre) + curKey;
            int curBaseValue = getBase(cur);
            int curCheckValue = getCheck(cur);

            if (curCheckValue != pre) {
                return null;
            }

            if (curBaseValue < 0) {
                // 剩余的key串在Tail数组中，去Tail中查询剩余的key和Value
                return getValueByCompareInTail(-curBaseValue, i + 1, keyList);
            } else if (curBaseValue > 0) {
                pre = cur;
            } else {
                return null;
            }
        }

        return null;
    }

    // 全量匹配
    public List<String> searchAllmatch(String srcString) {
        if (srcString == null || srcString.length() <= 0) {
            return null;
        }

        // 最大匹配长度,从文件中读取
        int maxMatchLength = trieFile.header.keyMaxLen;
        // 最小匹配长度，从文件中读取
        int minMatchLength = trieFile.header.keyMinLen;

        if (minMatchLength > maxMatchLength ||
                maxMatchLength <= 0 ||
                minMatchLength <= 0 ||
                minMatchLength > srcString.length()) {
            return null;
        }

        List<String> resultList = new ArrayList<String>();
        for (int i = 0;i < srcString.length();i++) {
            for (int j = i;j <= srcString.length();j++) {
                int curSubStringLen = j - i;
                if (curSubStringLen > maxMatchLength) {
                    break;
                }

                if (curSubStringLen < minMatchLength) {
                    j = i + minMatchLength;
                    if (j > srcString.length()) {
                        break;
                    }
                }

                String tmp = srcString.substring(i,j);
                if (searchKeyIsExist(tmp)) {
                    resultList.add(tmp);
                }
            }
        }

        return resultList;
    }

    // 最大长度匹配
    public List<String> searchMaxKeyMatch(String srcString) {
        final List<String> result = new ArrayList<>();
        searchMaxKeyMatch(srcString, new DictParseCallback() {
            @Override
            public void dicParseMatchKeySuccess(String keyName, int startOffset) {
                if (keyName != null && keyName.length() > 0 && result != null) {
                    result.add(keyName);
                }
            }

            @Override
            public void dicParseMatchKeyFailed() {
            }
        });

        return result;
    }

    // 最大长度匹配
    public void searchMaxKeyMatch(String srcString, DictParseCallback callback) {
        if (callback == null) {
            return;
        }

        if (srcString == null || srcString.length() <= 0) {
            callback.dicParseMatchKeyFailed();
            return;
        }

        // 最大匹配长度,从文件中读取
        int maxMatchLength = trieFile.header.keyMaxLen;
        // 最小匹配长度，从文件中读取
        int minMatchLength = trieFile.header.keyMinLen;

        if (minMatchLength > maxMatchLength) {
            callback.dicParseMatchKeyFailed();
            return;
        }

        if (maxMatchLength <= 0 || minMatchLength <= 0) {
            callback.dicParseMatchKeyFailed();
            return;
        }

        boolean isExist = false;
        for (int i = 0; i < srcString.length(); i++) {
            for (int j = maxMatchLength; j >= minMatchLength; j--) {
                if ((i + j) > srcString.length()) {
                    continue;
                }

                String tmp = srcString.substring(i, i + j);
                if (searchKeyIsExist(tmp)) {
                    callback.dicParseMatchKeySuccess(tmp, i);
                    i = i + j - 1;
                    isExist = true;
                    break;
                }
            }
        }

        if (!isExist) {
            callback.dicParseMatchKeyFailed();
        }
    }

    // 最大长度匹配
    public List<String> searchAllKeyMatch(String srcString) {
        final List<String> result = new ArrayList<>();
        searchAllKeyMatch(srcString, new DictParseCallback() {
            @Override
            public void dicParseMatchKeySuccess(String keyName, int startOffset) {
                if (keyName != null && keyName.length() > 0 && result != null) {
                    result.add(keyName);
                }
            }

            @Override
            public void dicParseMatchKeyFailed() {
            }
        });

        return result;
    }

    // 全量匹配
    public void searchAllKeyMatch(String srcString, DictParseCallback callback) {
        if (callback == null) {
            return;
        }

        if (srcString == null || srcString.length() <= 0) {
            callback.dicParseMatchKeyFailed();
            return;
        }

        // 最大匹配长度,从文件中读取
        int maxMatchLength = trieFile.header.keyMaxLen;
        // 最小匹配长度，从文件中读取
        int minMatchLength = trieFile.header.keyMinLen;

        if (minMatchLength > maxMatchLength) {
            callback.dicParseMatchKeyFailed();
            return;
        }

        if (maxMatchLength <= 0 || minMatchLength <= 0) {
            callback.dicParseMatchKeyFailed();
            return;
        }

        boolean isExist = false;
        for (int i = 0; i < srcString.length(); i++) {
            for (int j = maxMatchLength; j >= minMatchLength; j--) {
                if ((i + j) > srcString.length()) {
                    continue;
                }

                String tmp = srcString.substring(i, i + j);
                if (searchKeyIsExist(tmp)) {
                    callback.dicParseMatchKeySuccess(tmp, i);
                    isExist = true;
                    break;
                }
            }
        }

        if (!isExist) {
            callback.dicParseMatchKeyFailed();
        }
    }

    private boolean isValiedKey(int[] keyList) {
        if (keyList == null || keyList.length <= 0) {
            return false;
        }

        if (trieFile == null) {
            return false;
        }

        // 长度判断
        if ((trieFile.header.keyMaxLen + DictCommonUtils.ByteTypeByteCount) < keyList.length) {
            return false;
        }

        // 长度判断
        if ((trieFile.header.keyMinLen + DictCommonUtils.ByteTypeByteCount) > keyList.length) {
            return false;
        }

        return true;
    }

    /**
     * 比较是否在Tail数组中
     */
    private boolean compareInTail(int tailStart, int keyIndex, int[] key) {
        if (key == null || key.length <= 0) {
            return false;
        }

        // 前缀是词的查询
        if (keyIndex >= key.length) {
            //前缀是词的情况下
            if (key[keyIndex - 1] == endFlag) {
                return true;
            } else {
                return false;
            }
        }

        int i = tailStart;
        int j = keyIndex;

        while (j < key.length) {
            if (key[j] != getKeyTailValue(i)) {
                return false;
            }

            // 已经成功查找到
            if (key[j] == endFlag && getKeyTailValue(i) == endFlag) {
                return true;
            }

            i++;
            j++;
        }


        return false;
    }

    // 判断是否存在value值
    public boolean isExistValue() {
        int value = VALUE_NO_EXIST;
        if (trieFile.header.isExistValue == value) {
            return false;
        }

        return true;
    }

    /**
     * 从Tail中获取value数据
     * 格式：key:offset:len
     */
    private byte[] getValueByCompareInTail(int tailStart, int keyIndex, int[] key) {
        if (key == null || key.length <= 0) {
            return null;
        }

        if (!isExistValue()) {
            return null;
        }

        if (key.length <= keyIndex) {
            //前缀是词的情况下
            if (key[keyIndex - 1] == endFlag) {
                keyIndex = keyIndex - 1;
            } else {
                return null;
            }
        }

        int i = tailStart;
        int j = keyIndex;

        //
        boolean isSearchKeySucced = false;
        int curTailRigthRange = tailStart + key.length - j;
        while (j < key.length) {
            if (i >= curTailRigthRange) {
                return null;
            }

            if (key[j] != getKeyTailValue(i)) {
                return null;
            }

            // 已经成功查找到
            if (key[j] == endFlag && getKeyTailValue(i) == endFlag) {
                isSearchKeySucced = true;
                break;
            }

            i++;
            j++;
        }

        byte[] result = null;
        if (isSearchKeySucced) {
            i = i + 1;
            if (getKeyTailValue(i) == delFlag) {
                for (; getKeyTailValue(i) == delFlag; i++) {
                }
            }

            int curValueAddressOffset = getKeyTailValue(i);
            curValueAddressOffset += getValueStartAddress();
            result = getlValueOfKey(curValueAddressOffset);
        }

        return result;
    }

    // value的首地址
    private int getValueStartAddress() {
        int begin = trieFile.fileContentIndex.valueAddress;
        return begin;
    }

    private byte[] getlValueOfKey(int beginOffset) {
        if (!isExistValue()) {
            return null;
        }

        int len = mFileBytecontent[beginOffset] & 0xFF;

        byte[] bResult = new byte[len];

        int srcPos = beginOffset + DictCommonUtils.ByteTypeByteCount;
        System.arraycopy(mFileBytecontent, srcPos, bResult, 0, len);

        return bResult;
    }

    private int getKeyTailValue(int index) {
        int begin = trieFile.fileContentIndex.keyValueTailAddress;
        int value = DictCommonUtils.getIntValueFromByteArray(mFileBytecontent,
                begin + index * DictCommonUtils.IntFloatTypeByteCount,
                DictCommonUtils.IntFloatTypeByteCount);

        return value;
    }

    private int getBase(int index) {
        int begin = trieFile.fileContentIndex.baseAddress;

        int value = DictCommonUtils.getIntValueFromByteArray(mFileBytecontent,
                begin + index * DictCommonUtils.IntFloatTypeByteCount,
                DictCommonUtils.IntFloatTypeByteCount);

        return value;
    }

    private int getCheck(int index) {
        int begin = trieFile.fileContentIndex.checkAddress;

        int value = DictCommonUtils.getIntValueFromByteArray(mFileBytecontent,
                begin + index * DictCommonUtils.IntFloatTypeByteCount,
                DictCommonUtils.IntFloatTypeByteCount);

        return value;
    }

    public int getKeyCount() {
        if (trieFile == null) {
            return 0;
        }

        return trieFile.header.keyCount;
    }

    public int getExtendData1() {
        if (trieFile == null) {
            return 0;
        }

        return trieFile.header.extendData1;
    }
}
