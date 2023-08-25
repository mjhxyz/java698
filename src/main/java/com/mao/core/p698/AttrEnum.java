package com.mao.core.p698;

import com.mao.common.HexUtils;

public enum AttrEnum {
    // 电能量
    P0000("组合有功电能", HexUtils.toB("00 00")),
    P0010("正向有功电能", HexUtils.toB("00 10")),
    P0020("反向有功电能", HexUtils.toB("00 20")),
    ;


    private final String name;
    private final byte[] code;

    public String getName() {
        return name;
    }

    public byte[] getCode() {
        return code;
    }

    AttrEnum(String name, byte[] code) {
        this.name = name;
        this.code = code;
    }
}
