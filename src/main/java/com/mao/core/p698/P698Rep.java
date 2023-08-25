package com.mao.core.p698;

import java.util.ArrayList;
import java.util.List;

/**
 * 698 协议响应对象
 * @author mao
 * @date 2023/8/24 16:23
 */
public class P698Rep {
    // 响应的对象属性描述
    private int invokeId = 0; // 调用标识
    private List<P698Attr> attrs = new ArrayList<>();

    public List<P698Attr> getAttrs() {
        return attrs;
    }

    public void addAttr(P698Attr attr) {
        attrs.add(attr);
    }

    public int getInvokeId() {
        return invokeId;
    }

    public void setInvokeId(int invokeId) {
        this.invokeId = invokeId;
    }

    public void setAttrs(List<P698Attr> attrs) {
        this.attrs = attrs;
    }

    @Override
    public String toString() {
        return "P698Rep{" +
                "invokeId=" + invokeId +
                ", attrs=" + attrs +
                '}';
    }
}
