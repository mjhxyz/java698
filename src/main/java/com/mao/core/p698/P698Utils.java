package com.mao.core.p698;

import com.mao.common.HexUtils;
import com.mao.common.MLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * 698协议工具类
 *
 * @author : mao
 * @date 2023/8/24 11:11
 */
public class P698Utils {
    private static final byte HEADER = 0x68;
    private static final byte CLIENT_ADDRESS = 0x0b;
    private static final byte END = 0x16;

    public static class P698Msg {
        private byte[] rawData;
        private int invokeId;

        public byte[] getRawData() {
            return rawData;
        }

        public void setRawData(byte[] rawData) {
            this.rawData = rawData;
        }

        public int getInvokeId() {
            return invokeId;
        }

        public void setInvokeId(int invokeId) {
            this.invokeId = invokeId;
        }
    }

    public static class P698MsgBuilder {
        private byte[] meterAddress;
        private List<AttrEnum> attrEnums = new ArrayList<>();
        private int invokeId = 0; // 服务序号 0~63

        private synchronized int nextInvokeId() {
            invokeId = (invokeId + 1) % 64;
            return invokeId;
        }

        /**
         * 应用类型
         * ----
         * 0x05: 读取请求
         * 0x02: 读取若干个对象属性
         * ----
         */
        private byte[] appType = new byte[]{0x05, 0x02};

        public P698Msg build() {
            int curInvokeId = nextInvokeId();

            // 计算总长度
            int totalLength = 0;
            totalLength += 1; // 头部
            totalLength += 2; // 总长度
            totalLength += 1; // 控制域
            totalLength += 1; // 服务器地址标志
            totalLength += meterAddress.length; // 服务器地址
            totalLength += 1; // 客户端地址
            totalLength += 2; // HCS
            //  APDU
            totalLength += 2; // 服务类型 + 服务参数 eg: 0x05 0x02: 读取若干个对象属性
            // 服务优先级&服务序号
            totalLength += 1; // 服务优先级&服务序号
            totalLength += 1; // SeqOf长度
            for (AttrEnum attrEnum : attrEnums) {
                totalLength += 1;
                totalLength += attrEnum.getCode().length; // 特征0，属性0
                totalLength += 1;
            }
            totalLength += 1; // 无时间标记
            totalLength += 2; // FCS
            totalLength += 1; // 结束符

            byte[] data = new byte[totalLength];
            int msgLen = totalLength - 2; // 总长度不包含头部和尾部
            data[0] = HEADER;
            byte[] msgLenBytes = HexUtils.int2bytes(msgLen, 2);
            System.arraycopy(msgLenBytes, 0, data, 1, 2);
            data[3] = 0x43; // 控制域: 来自客户机;完整APDU
            data[4] = 0x05; // 服务器地址标志: 单地址;逻辑地址0;地址长度6
            System.arraycopy(meterAddress, 0, data, 5, meterAddress.length); // 服务器地址
            data[11] = CLIENT_ADDRESS; // 客户端地址
            // HCS, 帧头部分除起始字符和HCS本身之外的所有字节的校验
            byte[] bytes2HCS = HexUtils.subBytes(data, 1, 12);
            byte[] hcs = P698CS.getCrc(bytes2HCS);
            MLogger.log("进行HCS校验的数据: " + HexUtils.bytesToHexString(bytes2HCS));
            System.arraycopy(hcs, 0, data, 12, 2);
            // APDU
            System.arraycopy(appType, 0, data, 14, 2); // 服务类型
            // 序号和优先标志
            data[16] = (byte) curInvokeId; // 优先标志:0 一般, 服务序号=5, 和 response APD 中，其值与对应.request APDU 中的相等
            // SeqOf长度
            data[17] = (byte) attrEnums.size(); // 指标长度
            int index = 18;
            for (AttrEnum attrEnum : attrEnums) {
                System.arraycopy(attrEnum.getCode(), 0, data, index, attrEnum.getCode().length); // 属性0
                index += attrEnum.getCode().length;
                data[index++] = 0x02; // 属性0
                data[index++] = 0x00; // 特征0
            }
            data[index++] = 0x00; // 无时间标记
            // FCS, 帧头部分除起始字符和FCS本身之外的所有字节的校验
            byte[] bytes2FCS = HexUtils.subBytes(data, 1, index);
            MLogger.log("进行FCS校验的数据: " + HexUtils.bytesToHexString(bytes2FCS));
            byte[] fcs = P698CS.getCrc(bytes2FCS);
            System.arraycopy(fcs, 0, data, index, 2);
            data[index + 2] = END; // 结束符

            P698Msg msg = new P698Msg();
            msg.setInvokeId(curInvokeId);
            msg.setRawData(data);
            return msg;
        }

        public P698MsgBuilder setMeterAddress(String meterAddress) {
            this.meterAddress = HexUtils.hexStringToBytes(meterAddress);
            return this;
        }

        public P698MsgBuilder addAttr(AttrEnum attrEnum) {
            attrEnums.add(attrEnum);
            appType[1] = (byte) (attrEnums.size());
            return this;
        }
    }

    public static P698MsgBuilder getBuilder() {
        return new P698MsgBuilder();
    }
}
