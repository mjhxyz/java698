package com.mao.p698;

import com.mao.HexUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 698 协议响应解析器
 *
 * @author mao
 * @date 2023/8/24 16:23
 */
public class P698RepParser {
    private static final Byte HEAD = 0x68;
    private static final Byte TAIL = 0x16;

    public static void log(String msg) {
        System.out.println(msg);
    }

    public static P698Rep parse(byte[] bytes) {
        LinkedList<Byte> buffer = new LinkedList<>();
        for (byte b : bytes) {
            buffer.add(b);
        }
        return parse(buffer);
    }

    public static P698Rep parse(LinkedList<Byte> buffer) {
        log("开始解析================================");
        // 解析 buffer 中的数据
        if (buffer.size() < 12) { // 我的理解是 698 协议最小长度为 12
            return null;
        }
        int index = 0; // 用于记录当前解析到的位置

        // 1. 找到头部
        while (index < buffer.size()) {
            if (HEAD.equals(buffer.get(index))) { // 找到头部
                // 头部之前的数据丢弃
                for (int i = 0; i < index; i++) {
                    buffer.pollFirst();
                }
                break;
            }
            index++;
        }

        P698Rep rep = new P698Rep(); // 用于存放解析后的数据

        // 到这里后, buffer 中的数据应该是从头部开始的

        // 2. 这里需要一口气读取到头部的 FCS 字段
        if (buffer.size() < 8) { // 读到 FCS 字段至少至少需要 8 个字节
            return null;
        }
        // 2.1 读取长度
        byte[] lenBytes = new byte[2];
        lenBytes[0] = buffer.get(1);
        lenBytes[1] = buffer.get(2);
        // 2.2 读取控制域
        byte ctrl = buffer.get(3);
        // 2.3 读取服务器地址标识, 并获取服务器: 地址字节数/逻辑地址/地址类型
        byte serverAddrStatus = buffer.get(4);
        // bit0…bit3：为地址的字节数-1
        // bit4…bit5：逻辑地址
        // bit6…bit7：为服务器地址的地址类型，0 表示单地址，1 表示通配地址，2 表示组地址， 表示广播地址。
        int serverAddrLen = (serverAddrStatus & 0x0F) + 1;
        boolean isLogicAddr = (serverAddrStatus & 0x30) != 0;
        int serverAddrType = (serverAddrStatus & 0xC0) >> 6;
        log("服务器地址字节数: " + serverAddrLen + ", 是否逻辑地址: " + isLogicAddr + ", 服务器地址类型: " + serverAddrType);
        if (buffer.size() < 8 - 1 + serverAddrLen) { // 8 是假设服务地址字节数为1的情况, 现在知道了服务地址字节数, 所以需要减去 1 再加
            return null;
        }
        // 2.4 读取服务器地址
        byte[] serverAddrBytes = new byte[serverAddrLen];
        for (int i = 0; i < serverAddrLen; i++) {
            serverAddrBytes[i] = buffer.get(5 + i);
        }
        // 2.5 读取客户机地址 1B
        byte clientAddr = buffer.get(5 + serverAddrLen);
        // 2.6 读取 2B 的 HCS
        byte[] hcsBytes = new byte[2];
        hcsBytes[0] = buffer.get(6 + serverAddrLen);
        hcsBytes[1] = buffer.get(7 + serverAddrLen);
        // 2.7 检查 HCS 是否正确(除了头部的 1B, 其他的都需要参与校验)
        byte[] bytes2CS = HexUtils.subBytesList(buffer, 1, 6 + serverAddrLen);
        byte[] hcs = P698CS.getCrc(bytes2CS);
        log("HCS校验的数据: " + HexUtils.bytesToHexString(bytes2CS));
        if (!HexUtils.bytesEquals(hcs, hcsBytes)) {
            log("HCS 校验失败");
            // 校验失败后, 丢弃 HCS 字段之前的数据, HCS 也丢了
            for (int i = 0; i < 7 + serverAddrLen; i++) {
                buffer.pollFirst();
            }
            return parse(buffer); // 重新解析
        }
        log("HCS 校验成功!");
        // 2.8  HCS 正确，这个时候就能解析出数据域的长度了
        int totalDataLen = HexUtils.bytes2int(lenBytes); // 数据域的长度,和保留位
        // bit0~bit13：为数据域的长度
        totalDataLen = totalDataLen & 0x3FFF; // 数据域的长度 不包括头部和尾部
        log("数据域的长度: " + totalDataLen);

        // 3. 计算用户层数据长度, 去除了头部一直到 FCS 之前的数据, FCS 也去除
        //    7: 长度域 2B + 控制域 1B + 服务器地址标识 1B + 客户机地址 1B + HCS 2B
        int userDataLen = totalDataLen - (7 + serverAddrLen);
        if (buffer.size() < totalDataLen + 2) { // 缓冲区的数据长度不够组成一个报文
            return null;
        }

        int appOffset = 8 + serverAddrLen; // 用户数据的偏移量

        // 4. 读取链路用户数据
        // 4.1 读取链路的操作码 & 操作参数
        // eg: 0x85 0x02 读取若干个属性
        //     0x85 0x01 读取单个属性
        byte opCode = buffer.get(appOffset++);
        byte opParam = buffer.get(appOffset++);
        // 4.2 获取优先级 & ACD & 服务序号
        // bit7: 服务优先级
        // bit6: （请求访问 ACD）——0：不请求，1：请求。
        // bit0~bit5: 服务序号
        byte respCtrl = buffer.get(appOffset++);
        int priority = (respCtrl & 0x80) >> 7;
        int acd = (respCtrl & 0x40) >> 6;
        int invokeId = respCtrl & 0x3F;
        rep.setInvokeId(invokeId);

        byte attrNum = 0; // 读取属性的个数

        // 4.3 如果是获取若干个属性，则需要先获取个数
        if (opParam == 0x02) {
            attrNum = buffer.get(appOffset++);
            log("读取属性的个数:" + attrNum);
        } else if (opParam == 0x01) {
            attrNum = 1;
        }

        // 5. 循环获取属性
        for (byte i = 0; i < attrNum; ++i) {
            P698Attr attrObj = new P698Attr();

            // 5.4 OAD 对象属性描述符 4B
            byte[] oi = new byte[2];
            oi[0] = buffer.get(appOffset++);
            oi[1] = buffer.get(appOffset++);
            attrObj.setOI(oi);

            // bit0…bit4 编码表示对象属性编号，取值 0…31，其中 0 表示整个对象属性，即对象的所有属性；
            // bit5…bit7 编码表示属性特征，属性特征是对象同一个属性 在不同快照环境下取值模式，取值 0…7，特征含义在具体类 属性中描述。
            byte attr = buffer.get(appOffset++);
            int attrId = attr & 0x1F;
            int attrType = (attr & 0xE0) >> 5;
            attrObj.setAttrId(attrId);

            // 属性内元素索引——00H 表示整个属性全部内容。如果属性是一个 结构或数组，01H 指向对象属性的第一个元素；如果属性是一个记 录型的存储区，非零值 n 表示最近第 n 次的记录。
            byte attrIndex = buffer.get(appOffset++);

            // 5.5 结果类型 1(数据)
            byte resultType = buffer.get(appOffset++);
            if(resultType == 0x00) { // 0:(错误)
                byte darType = buffer.get(appOffset++); // 这个就是 错误码
                attrObj.setErrorCode(darType);
                rep.addAttr(attrObj);
                continue;
            }

            // 5.6 类型
            byte type = buffer.get(appOffset++); // 类型:1 数组
            // 5.7 数组元素个数
            byte arrayNum = buffer.get(appOffset++);
            List<Object> tempCurAttrData = new ArrayList<>();

            for (byte j = 0; j < arrayNum; j++) {
                // 获取数据的基本类型
                byte typeId = buffer.get(appOffset++);
                // 假设是浮点数, 那就占用 4B
                if (typeId == 0x06) {
                    // double-long-unsigned 无符号
                    byte[] valueBytes = new byte[4];
                    valueBytes[0] = buffer.get(appOffset++);
                    valueBytes[1] = buffer.get(appOffset++);
                    valueBytes[2] = buffer.get(appOffset++);
                    valueBytes[3] = buffer.get(appOffset++);
                    // 读取数据
                    double value = HexUtils.bytes2float(valueBytes);
                    tempCurAttrData.add(value);
                    log("获取 double-long-unsigned 数据: " + value);
                } else if (typeId == 0x05) {
                    // double-long 有符号
                    byte[] valueBytes = new byte[4];
                    valueBytes[0] = buffer.get(appOffset++);
                    valueBytes[1] = buffer.get(appOffset++);
                    valueBytes[2] = buffer.get(appOffset++);
                    valueBytes[3] = buffer.get(appOffset++);
                    // 读取数据
                    double value = HexUtils.bytes2float(valueBytes);
                    tempCurAttrData.add(value);
                    log("获取 double-long 数据: " + value);
                }
                // TODO 可能还涉及到 换算-倍数因子的指数
            }

            // 5.8 将数据添加到属性对象中
            attrObj.setData(tempCurAttrData);
            // 5.9 将属性对象添加到报文对象中
            rep.addAttr(attrObj);
        }

        // 6. 获取跟随上报信息域
        byte followInfo = buffer.get(appOffset++);
        // 7. 时间标签
        byte timeTag = buffer.get(appOffset++);
        // 8. FCS 对整帧除起始字符、结束字符和FCS本身之外的所有字节的校验
        byte[] fcsBytes = new byte[2];
        fcsBytes[0] = buffer.get(appOffset++);
        fcsBytes[1] = buffer.get(appOffset++);
        byte[] bytes2fcs = P698CS.getCrc(HexUtils.subBytesList(buffer, 1, buffer.size() - 3));
        if (!HexUtils.bytesEquals(fcsBytes, bytes2fcs)) {
            log("FCS 校验失败");
            // 校验失败后, 丢弃 FCS 字段之前的数据, FCS 也丢了
            for (int i = 0; i < buffer.size() - 2; i++) {
                buffer.pollFirst();
            }
            return parse(buffer); // 重新解析
        }
        // 9. 最后一个字节是结束符
        byte end = buffer.get(appOffset++);
        if (!TAIL.equals(end)) {
            log("结束符校验失败");
            // 校验失败后, 丢弃结束符之前的数据, 结束符也丢了
            for (int i = 0; i < buffer.size() - 1; i++) {
                buffer.pollFirst();
            }
            return parse(buffer); // 重新解析
        }
        log("解析成功!!!");
        return rep;
    }
}
