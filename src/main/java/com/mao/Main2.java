package com.mao;

import com.mao.core.func.P698Service;
import com.mao.core.func.P698ServiceFactory;
import com.mao.core.p698.AttrEnum;
import com.mao.core.p698.P698Resp;

/**
 * 698 服务测试类
 * @author mao
 * @date 2023/8/25 16:20
 */
public class Main2 {
    public static void main(String[] args) throws InterruptedException {
        P698Service service = P698ServiceFactory.createService("127.0.0.1", 9876);
        String meterAddress = "39 12 19 08 37 00";
        P698Resp p698Resp = service.get(meterAddress, AttrEnum.P0010, AttrEnum.P0020, AttrEnum.P0000);
        System.out.println(p698Resp);
    }
}
