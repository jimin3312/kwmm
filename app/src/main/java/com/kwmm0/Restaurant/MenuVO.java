package com.kwmm0.Restaurant;

public abstract class MenuVO implements Comparable<MenuVO>{
    public static final int HEAD = 1;
    public final static int CHILD = 0;
    private int type;

    public int getType() {
        return type;
    }

    public MenuVO(int type) {
        this.type = type;
    }

}