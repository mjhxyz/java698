package com.mao;

import com.mao.core.func.P698Service;
import com.mao.core.func.P698ServiceFactory;

import java.util.List;

/**
 * 698 服务测试类
 * @author mao
 * @date 2023/8/26 9:13
 */
public class Main3 {
    public static void main(String[] args) throws InterruptedException {
        P698Service service = P698ServiceFactory.createService("127.0.0.1", 9876);
        String meterAddress = "39 12 19 08 37 00";
        List<Double> papR = service.getPapR(meterAddress);
        System.out.println(papR);
    }
}
