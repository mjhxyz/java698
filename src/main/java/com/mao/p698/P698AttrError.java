package com.mao.p698;

import java.util.HashMap;
import java.util.Map;

/**
 * 698 对象属性获取错误
 * @author mao
 * @date 2023/8/25 13:51
 */
public class P698AttrError {
    private static final Map<Byte, String> ERROR_MAP = new HashMap<>();

    static {
        ERROR_MAP.put(b(1), "硬件失效");
        ERROR_MAP.put(b(2), "暂时失效");
        ERROR_MAP.put(b(3), "拒绝读写");
        ERROR_MAP.put(b(4), "对象未定义");
        ERROR_MAP.put(b(5), "对象接口类不符合");
        ERROR_MAP.put(b(6), "对象不存在");
        ERROR_MAP.put(b(7), "类型不匹配");
        ERROR_MAP.put(b(8), "越界");
        ERROR_MAP.put(b(9), "数据块不可用");
        ERROR_MAP.put(b(10), "分帧传输已取消");
        ERROR_MAP.put(b(11), "不处于分帧传输状态");
        ERROR_MAP.put(b(12), "块写取消");
        ERROR_MAP.put(b(13), "不存在块写状态");
        ERROR_MAP.put(b(14), "数据块序号无效");
        ERROR_MAP.put(b(15), "密码错/未授权");
        ERROR_MAP.put(b(16), "通信速率不能更改");
        ERROR_MAP.put(b(17), "年时区数超");
        ERROR_MAP.put(b(18), "日时段数超");
        ERROR_MAP.put(b(19), "费率数超");
        ERROR_MAP.put(b(20), "安全认证不匹配");
        ERROR_MAP.put(b(21), "重复充值");
        ERROR_MAP.put(b(22), "ESAM 验证失败");
        ERROR_MAP.put(b(23), "安全认证失败");
        ERROR_MAP.put(b(24), "客户编号不匹配");
        ERROR_MAP.put(b(25), "充值次数错误");
        ERROR_MAP.put(b(26), "购电超囤积");
        ERROR_MAP.put(b(27), "地址异常");
        ERROR_MAP.put(b(28), "对称解密错误");
        ERROR_MAP.put(b(29), "非对称解密错误");
        ERROR_MAP.put(b(30), "签名错误");
        ERROR_MAP.put(b(31), "电能表挂起");
        ERROR_MAP.put(b(32), "时间标签无效");
        ERROR_MAP.put(b(33), "请求超时");
        ERROR_MAP.put(b(34), "ESAM 的 P1P2 不正确");
        ERROR_MAP.put(b(35), "ESAM 的 LC 错误");
        ERROR_MAP.put(b(255), "其它");
    }

    private static Byte b(int val) {
        return (byte)val;
    }

    public static String getErrorMsg(byte error) {
        return ERROR_MAP.get(error);
    }
}
