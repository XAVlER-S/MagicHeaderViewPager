package com.culiu.mhvp.core.util;

/**
 * 整形变量内。可直接改变里面的值，而不用修改对象引用。
 * （With an Integer inside. Can change value without changing reference.）
 */
public final class IntegerVariable {

    private int mValue;

    public IntegerVariable(int value) {
        mValue = value;
    }

    public IntegerVariable() {
    }

    public final int getValue() {
        return mValue;
    }

    public final void setValue(int value) {
        mValue = value;
    }

    @Override
    public boolean equals(Object o) {
        // 地址
        if(this == o) {
            return true;
        }

        // 同类比较值
        if(o instanceof IntegerVariable) {
            return mValue == ((IntegerVariable)o).getValue();
        }

        // 异类比较值
        if(o instanceof Integer) {
            return mValue == ((Integer)o).intValue();
        }

        // 无法处理的，扔给父类
        return super.equals(o);
    }

    @Override
    public String toString() {
        return String.valueOf(mValue);
    }
}
