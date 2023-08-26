package com.mao.core.p698;

import com.mao.common.HexUtils;

/**
 * 698 协议的对象属性描述
 *
 * @author mao
 * @date 2023/8/25 11:13
 */
public class P698Attr {
    private byte[] OI = new byte[2]; // 对象标志
    private int attrId = 0; // 属性 eg: 组合有功电能+0x02 总及费率电能量数组
    private Object data = null; // 数据
    private byte errorCode = 0; // 错误码, 0: 无错误

    public byte[] getOI() {
        return OI;
    }

    public void setOI(byte[] OI) {
        this.OI = OI;
    }

    public int getAttrId() {
        return attrId;
    }

    public void setAttrId(int attrId) {
        this.attrId = attrId;
    }

    public byte getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(byte errorCode) {
        this.errorCode = errorCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public P698Attr() {
    }

    public boolean isError() { // 是否有错误
        return errorCode != 0;
    }

    public String getError() { // 获取错误信息
        if(!isError()) return "";
        return P698AttrError.getErrorMsg(errorCode);
    }

    @Override
    public String toString() {
        if(isError()) {
            return "P698Attr{" +
                    "OI=[" + HexUtils.bytes2HexString(OI) + "]" +
                    ", attrId=" + attrId +
                    ", errorCode=" + errorCode +
                    ", error='" + getError() + '\'' +
                    '}';
        }
        return "P698Attr{" +
                "OI=[" + HexUtils.bytes2HexString(OI) + "]" +
                ", attrId=" + attrId +
                ", data=" + data +
                '}';
    }
}
