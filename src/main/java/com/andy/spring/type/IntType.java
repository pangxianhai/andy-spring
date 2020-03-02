package com.andy.spring.type;

import java.io.Serializable;

/**
 * int类型对应对象类型 主要针对db里的int对应到IntType的一个子类的对象
 * 目的子类继承IntType 定义一些常量或方法
 * 比如某个类型按位表示 可能是1(001)表示A类型 2(010)表示B类型 4(100)表示C类型
 * 实际对象可能符合A B C 或者ABC之间的任意组合
 * 判读3是否符合A或B或C就可以在子类中实现 并且ABC常量也可以在子类中定义
 * 考虑到这些操作与值定义在不同类中 难以关联所以定义这样的类型  DB中的vlue可以直接映射到该类的子类类型上去
 *
 * @author 庞先海 2019-11-14
 */
public class IntType implements Serializable {

    private static final long serialVersionUID = 8292360832973152022L;

    private Integer value;

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public IntType() {}
}
