package com.mao.core.p698;

import java.util.HashMap;
import java.util.Map;

/**
 * 698 对象属性获取错误
 * @author mao
 * @date 2023/8/25 13:51
 */
public class P698AttrError {
    private static final Map<Integer, String> ERROR_MAP = new HashMap<>();

    static {
        ERROR_MAP.put(1, "硬件失效");
        ERROR_MAP.put(2, "暂时失效");
        ERROR_MAP.put(3, "拒绝读写");
        ERROR_MAP.put(4, "对象未定义");
        ERROR_MAP.put(5, "对象接口类不符合");
        ERROR_MAP.put(6, "对象不存在");
        ERROR_MAP.put(7, "类型不匹配");
        ERROR_MAP.put(8, "越界");
        ERROR_MAP.put(9, "数据块不可用");
        ERROR_MAP.put(10, "分帧传输已取消");
        ERROR_MAP.put(11, "不处于分帧传输状态");
        ERROR_MAP.put(12, "块写取消");
        ERROR_MAP.put(13, "不存在块写状态");
        ERROR_MAP.put(14, "数据块序号无效");
        ERROR_MAP.put(15, "密码错/未授权");
        ERROR_MAP.put(16, "通信速率不能更改");
        ERROR_MAP.put(17, "年时区数超");
        ERROR_MAP.put(18, "日时段数超");
        ERROR_MAP.put(19, "费率数超");
        ERROR_MAP.put(20, "安全认证不匹配");
        ERROR_MAP.put(21, "重复充值");
        ERROR_MAP.put(22, "ESAM 验证失败");
        ERROR_MAP.put(23, "安全认证失败");
        ERROR_MAP.put(24, "客户编号不匹配");
        ERROR_MAP.put(25, "充值次数错误");
        ERROR_MAP.put(26, "购电超囤积");
        ERROR_MAP.put(27, "地址异常");
        ERROR_MAP.put(28, "对称解密错误");
        ERROR_MAP.put(29, "非对称解密错误");
        ERROR_MAP.put(30, "签名错误");
        ERROR_MAP.put(31, "电能表挂起");
        ERROR_MAP.put(32, "时间标签无效");
        ERROR_MAP.put(33, "请求超时");
        ERROR_MAP.put(34, "ESAM 的 P1P2 不正确");
        ERROR_MAP.put(35, "ESAM 的 LC 错误");
        ERROR_MAP.put(255, "其它");
    }

    public static String getErrorMsg(int error) {
        String errorMsg = ERROR_MAP.get(error);
        if(errorMsg == null) errorMsg = "未知错误";
        return errorMsg;
    }
}
