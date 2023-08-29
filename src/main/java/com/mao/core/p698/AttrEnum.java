package com.mao.core.p698;

import com.mao.common.HexUtils;

import java.util.Arrays;

public enum AttrEnum {
    // 电能量
    P0000("组合有功电能", HexUtils.toB("00 00")),
    P0010("正向有功电能", HexUtils.toB("00 10")),
    P0020("反向有功电能", HexUtils.toB("00 20")),
    P2000("电压", HexUtils.toB("20 00")),
    P2001("电流", HexUtils.toB("20 01")),
    ;


    private final String name;
    private final byte[] code;

    public String getName() {
        return name;
    }

    public byte[] getCode() {
        return code;
    }

    public static AttrEnum getAttrEnum(byte[] code) {
        for (AttrEnum attrEnum : AttrEnum.values()) {
            if (Arrays.equals(attrEnum.getCode(), code)) {
                return attrEnum;
            }
        }
        return null;
    }

    AttrEnum(String name, byte[] code) {
        this.name = name;
        this.code = code;
    }
}
