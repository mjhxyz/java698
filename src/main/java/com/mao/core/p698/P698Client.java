package com.mao.core.p698;

import com.mao.core.conn.ITCPClient;


public class P698Client {
    private ITCPClient tcpClient;

    private final byte[] CLIENT_ADDRESS = new byte[]{0x00};

    public P698Client(ITCPClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    /**
     * 获取指定电表的属性值
     *
     * @param meterAddress 电表地址
     * @param attrEnum     属性枚举
     * @return 属性值
     */
    public Object getAttr(String meterAddress, AttrEnum attrEnum) {
        return null;
    }
}
