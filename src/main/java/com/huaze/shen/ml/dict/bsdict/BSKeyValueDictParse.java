package com.huaze.shen.ml.dict.bsdict;

import java.util.ArrayList;
import java.util.List;

public class BSKeyValueDictParse extends BSKeyValueDictParseBase {
    public interface BSKeyValueDictParseCallback {
        void dicParseMatchItemSuccess(String keyName, String value, int startOffset);
        void dicParseMatchItemFailed();
    }

    public BSKeyValueDictParse(String fileName) {
        init(fileName);
    }

    private boolean isExistValue() {
        return dictHeader.isExistValue(mFileBytecontent);
    }

    @Override
    public String getHeaderType() throws Exception {
        String headerType = new String(mFileBytecontent,
                0,
                BSKeyValueDictData.BSDictHeader.headerTypeEndOffset,
                BSKeyValueDictData.BSDictHeader.kBSDictHeaderCharsetName);

        return headerType;
    }

    @Override
    public int getKeyCharSetNameType() {
        int multyColCount = BSKeyValueDictUtil.getIntValueFromByteArray(mFileBytecontent,
                BSKeyValueDictData.BSDictHeader.keyCharSetNameTypeStartOffset,
                BSKeyValueDictUtil.intFloatTypeByte);

        return multyColCount;
    }

    @Override
    public int getvalueCharSetNameType() {
        int multyColCount = BSKeyValueDictUtil.getIntValueFromByteArray(mFileBytecontent,
                BSKeyValueDictData.BSDictHeader.valueCharSetNameTypeStartOffset,
                BSKeyValueDictUtil.intFloatTypeByte);

        return multyColCount;
    }

    @Override
    public int getIsMultyColData() {
        int multyColCount = BSKeyValueDictUtil.getIntValueFromByteArray(mFileBytecontent,
                BSKeyValueDictData.BSDictHeader.isMultyColDataStartOffset,
                BSKeyValueDictUtil.intFloatTypeByte);

        return multyColCount;
    }

    @Override
    public int getBlockSize() {
        int blockSize = BSKeyValueDictUtil.getIntValueFromByteArray(mFileBytecontent,
                BSKeyValueDictData.BSDictHeader.blockSizeStartOffset,
                BSKeyValueDictUtil.intFloatTypeByte);

        return blockSize;

    }

    public int getDataBlockCount(byte[] filebContent) {
        if (filebContent == null || filebContent.length <= 0) {
            return 0;
        }

        int blockCount = BSKeyValueDictUtil.getIntValueFromByteArray(filebContent,
                BSKeyValueDictData.BSDictHeader.dataBlockCountStartOffset,
                BSKeyValueDictUtil.intFloatTypeByte);

        return blockCount;
    }

    @Override
    public int getDataBlockCount() {
        if (mFileBytecontent == null || mFileBytecontent.length <= 0) {
            return 0;
        }
        //
        int blockCount = getDataBlockCount(mFileBytecontent);

        return blockCount;
    }

    public int getNameMaxLen() {
        if (mFileBytecontent == null || mFileBytecontent.length <= 0) {
            return 0;
        }
        //
        int len = BSKeyValueDictUtil.getIntValueFromByteArray(mFileBytecontent,
                BSKeyValueDictData.BSDictHeader.nameMaxLenStartOffset,
                BSKeyValueDictUtil.intFloatTypeByte);

        return len;
    }

    public int getNameMinLen() {
        if (mFileBytecontent == null || mFileBytecontent.length <= 0) {
            return 0;
        }
        //
        int len = BSKeyValueDictUtil.getIntValueFromByteArray(mFileBytecontent,
                BSKeyValueDictData.BSDictHeader.nameMinLenStartOffset,
                BSKeyValueDictUtil.intFloatTypeByte);

        return len;
    }

    private BSKeyValueDictData makeData(byte[] filebContent, int curIndex) {
        BSKeyValueDictData bsKeyValueDictData = null;
        try {
            int nameLen = filebContent[dictHeader.blockItemSize * curIndex + BSKeyValueDictData.nameByteLenOffset];


            int namedataAddr = BSKeyValueDictUtil.getIntValueFromByteArray(filebContent,
                    dictHeader.blockItemSize * curIndex + BSKeyValueDictData.nameAddressOffset,
                    BSKeyValueDictUtil.intFloatTypeByte);

            // name
            String strName = new String(filebContent, namedataAddr, nameLen, BSKeyValueDictData.BSDictHeader.keyNameCharsetName);

            // hashCode
            int hashCode = BSKeyValueDictUtil.getIntValueFromByteArray(filebContent,
                    dictHeader.blockItemSize * curIndex + BSKeyValueDictData.hashCodeValueOffset,
                    BSKeyValueDictUtil.intFloatTypeByte);

            // 创建数据
            bsKeyValueDictData = new BSKeyValueDictData(isMultyColData);
            bsKeyValueDictData.name = strName;
            bsKeyValueDictData.nameByteLen = bsKeyValueDictData.getNameByteLen(strName);
            bsKeyValueDictData.hashCode = hashCode;

            if (isMultyColData) {
                int secondColValueDataByteLen = filebContent[dictHeader.blockItemSize * curIndex + BSKeyValueDictData.valueDataByteLenStartOffset];

                int secondColValueDataAddress = BSKeyValueDictUtil.getIntValueFromByteArray(filebContent,
                        dictHeader.blockItemSize * curIndex + BSKeyValueDictData.valueDataAddressStartOffset,
                        BSKeyValueDictUtil.intFloatTypeByte);

                String secondColValueStr = new String(filebContent, secondColValueDataAddress, secondColValueDataByteLen, BSKeyValueDictData.BSDictHeader.valueNameCharsetName);

                bsKeyValueDictData.secondColValue = secondColValueStr;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return bsKeyValueDictData;
    }

    /**
     * 查找在hashcode相同的情况查看是否key值也相同-hashcode冲突时通过key进行确认
     */
    private BSKeyValueDictData searchData(byte[] filebContent, String keyName, int searchHashcode, int index, int end) {
        if (keyName == null || keyName.length() <= 0) {
            return null;
        }
        BSKeyValueDictData bsKeyValueDictData = null;

        int left = index;
        int right = index + 1;

        // left
        try {
            while (left >= 0) {
                int nameLen = filebContent[dictHeader.blockItemSize * left + BSKeyValueDictData.nameByteLenOffset];

                int namedataAddr = BSKeyValueDictUtil.getIntValueFromByteArray(filebContent,
                        dictHeader.blockItemSize * left + BSKeyValueDictData.nameAddressOffset,
                        BSKeyValueDictUtil.intFloatTypeByte);

                // name
                String strName = new String(filebContent, namedataAddr, nameLen, BSKeyValueDictData.BSDictHeader.keyNameCharsetName);

                int curHashCode = BSKeyValueDictUtil.getIntValueFromByteArray(filebContent,
                        dictHeader.blockItemSize * left + BSKeyValueDictData.hashCodeValueOffset,
                        BSKeyValueDictUtil.intFloatTypeByte);

                if (curHashCode == searchHashcode) {
                    if (strName.equals(keyName)) {
                        bsKeyValueDictData = makeData(filebContent, left);
                        bsKeyValueDictData.name = strName;
                        return bsKeyValueDictData;
                    }
                } else {
                    break;
                }

                left--;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // right
        try {
            while (right <= end) {
                int nameLen = filebContent[dictHeader.blockItemSize * right + BSKeyValueDictData.nameByteLenOffset];

                int namedataAddr = BSKeyValueDictUtil.getIntValueFromByteArray(filebContent,
                        dictHeader.blockItemSize * right + BSKeyValueDictData.nameAddressOffset,
                        BSKeyValueDictUtil.intFloatTypeByte);

                // name
                String strName = new String(filebContent, namedataAddr, nameLen, BSKeyValueDictData.BSDictHeader.keyNameCharsetName);

                int curHashCode = BSKeyValueDictUtil.getIntValueFromByteArray(filebContent,
                        dictHeader.blockItemSize * right + BSKeyValueDictData.hashCodeValueOffset,
                        BSKeyValueDictUtil.intFloatTypeByte);

                if (curHashCode == searchHashcode) {
                    if (strName.equals(keyName)) {
                        bsKeyValueDictData = makeData(filebContent, right);
                        bsKeyValueDictData.name = strName;
                        return bsKeyValueDictData;
                    }
                } else {
                    break;
                }

                right++;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return bsKeyValueDictData;
    }

    public BSKeyValueDictData search(String searchNameKey) {
        if (mFileBytecontent == null) {
            return null;
        }

        if (searchNameKey == null || searchNameKey.length() <= 0) {
            return null;
        }

        BSKeyValueDictData bsKeyValueDictData = null;
        int hashCodeKey = BSKeyValueDictUtil.getHashcode(searchNameKey);
        byte[] filebContent = mFileBytecontent;

        try {
            // 通过hashcode 二分查找
            int left = 0;
            int right = dictHeader.dataBlockCount;
            int end = right;

            if (left > right) {
                return null;
            }

            while (left <= right) {
                int middle = left + ((right - left) >> 1);
                int curHashcode = BSKeyValueDictUtil.getIntValueFromByteArray(filebContent,
                        dictHeader.blockItemSize * middle + BSKeyValueDictData.hashCodeValueOffset,
                        BSKeyValueDictUtil.intFloatTypeByte);

                if (hashCodeKey < curHashcode) {
                    right = middle - 1;
                } else if (hashCodeKey > curHashcode) {
                    left = middle + 1;
                } else {
                    bsKeyValueDictData = searchData(filebContent, searchNameKey, hashCodeKey, middle, end);
                    break;
                }
            }
            return bsKeyValueDictData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询是否某个可以是否在库中存在---通过二分查找的方式
     *
     * @param searchNameKey
     * @return
     */
    public boolean searchIsExist(String searchNameKey) {
        BSKeyValueDictData dataLenUnFixed = search(searchNameKey);
        if (dataLenUnFixed != null) {
            return true;
        }
        return false;
    }

    // 全量匹配
    public List<String> searchAllmatch(String srcString) {
        if (srcString == null || srcString.length() <= 0) {
            return null;
        }

        // 最大匹配长度,从文件中读取
        int maxMatchLength =  dictHeader.keyNameMaxLen;
        // 最小匹配长度，从文件中读取
        int minMatchLength = dictHeader.keyNameMinLen;

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
                BSKeyValueDictData bsKeyValueDictData = search(tmp);
                if (bsKeyValueDictData != null) {
                    resultList.add(tmp);
                }
            }
        }

        return resultList;
    }

    // 最大匹配
    public List<String> searchMaxmatch(String srcString) {
        final List<String> result = new ArrayList<>();
        searchMaxmatch(srcString, new BSKeyValueDictParseCallback() {
            @Override
            public void dicParseMatchItemSuccess(String keyName, String value, int startOffset) {
                if (keyName != null && keyName.length() > 0 && result != null) {
                    result.add(keyName);
                }
            }
            @Override
            public void dicParseMatchItemFailed() {
            }
        });

        return result;
    }
    /**
     * @param srcString 原始字符串，支持多模式字符串匹配
     * @param callback  回调函数，每次碰撞查询到某个字符串后逐个返回
     * @return 返回匹配成功的字符串
     */
    public void searchMaxmatch(String srcString, BSKeyValueDictParseCallback callback) {
        if (callback == null) {
            return;
        }

        if (srcString == null || srcString.length() <= 0) {
            callback.dicParseMatchItemFailed();
            return;
        }

        // 最大匹配长度,从文件中读取
        int maxMatchLength =  dictHeader.keyNameMaxLen;
        // 最小匹配长度，从文件中读取
        int minMatchLength = dictHeader.keyNameMinLen;

        if (minMatchLength > maxMatchLength) {
            callback.dicParseMatchItemFailed();
            return;
        }

        if (maxMatchLength <= 0 || minMatchLength <= 0) {
            callback.dicParseMatchItemFailed();
            return;
        }

        boolean isExist = false;
        for (int i = 0; i < srcString.length(); i++) {
            for (int j = maxMatchLength; j >= minMatchLength; j--) {
                if ((i + j) > srcString.length()) {
                    continue;
                }
                String tmp = srcString.substring(i, i + j);
                BSKeyValueDictData dataLenUnFixed = search(tmp);
                if (dataLenUnFixed != null) {
                    callback.dicParseMatchItemSuccess(tmp, dataLenUnFixed.secondColValue, i);
                    i = i + j - 1;
                    isExist = true;
                    break;
                }
            }
        }

        if (!isExist) {
            callback.dicParseMatchItemFailed();
        }
    }
}
