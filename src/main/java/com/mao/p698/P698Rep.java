package com.mao.p698;

import java.util.ArrayList;
import java.util.List;

/**
 * 698 协议响应对象
 * @author mao
 * @date 2023/8/24 16:23
 */
public class P698Rep {
    // 响应的对象属性描述
    private List<P698Attr> attrs = new ArrayList<>();

    public List<P698Attr> getAttrs() {
        return attrs;
    }

    public void addAttr(P698Attr attr) {
        attrs.add(attr);
    }

    @Override
    public String toString() {
        return "P698Rep{" +
                "attrs=" + attrs +
                '}';
    }
}
