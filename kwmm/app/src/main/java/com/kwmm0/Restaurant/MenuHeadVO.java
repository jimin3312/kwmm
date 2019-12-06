package com.kwmm0.Restaurant;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class MenuHeadVO extends MenuVO {
    private String category;
    private List<MenuVO> MenuChildList;

    public MenuHeadVO(String category, int type) {
        super(type);
        this.category = category;
        MenuChildList = new ArrayList<>();
    }

    public String getCategory() {
        return category;
    }

    public List<MenuVO> getMenuChildList() {
        return MenuChildList;
    }

    @Override
    public int compareTo(@NonNull MenuVO o) {
        return 0;
    }
}