package com.mao.core;

import com.mao.core.func.server.P698ServerService;
import com.mao.core.func.server.P698ServerServiceFactory;
import com.mao.core.p698.AttrEnum;
import com.mao.core.p698.P698Resp;

import java.util.List;

/**
 * 启动类
 * @author mao
 * @date 2023/8/28 14:25
 */
public class Main {
    public static void main(String[] args) throws Exception {
        P698ServerService serverService = P698ServerServiceFactory.createService(
                "0.0.0.0", 50000
        );

        while (true) {
            P698Resp p698Resp = serverService.get("39 12 19 08 37 00", AttrEnum.P0020, AttrEnum.P0010);
            System.out.println(p698Resp);
            Thread.sleep(1000);
            System.out.println("结束");
        }
    }
}
