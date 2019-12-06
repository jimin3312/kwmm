package com.kwmm0.Restaurant;

import android.support.annotation.NonNull;

public class MenuChildVO extends MenuVO{
    private String name;
    private String rate;
    private String price;
    private int menuId;

    public MenuChildVO(String name, String rate, String price, int menuId, int type) {
        super(type);
        this.name = name;
        this.rate = rate;
        this.price = price;
        this.menuId = menuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRate() {
        return rate;
    }

    public String getPrice() {
        return price;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setRate(String rate) { this.rate = rate; }

    @Override
    public int compareTo(@NonNull MenuVO o) {
        if(Double.parseDouble(((MenuChildVO)o).getRate()) > Double.parseDouble(this.getRate()))
            return 1;
        else if (Double.parseDouble(((MenuChildVO)o).getRate()) < Double.parseDouble(this.getRate()))
            return -1;
        else
            return 0;
    }
}