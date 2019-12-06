package com.kwmm0.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ContentStore {
    private ArrayList<MenuVO> menuList;
    private ArrayList<ReviewVO> reviewList;
    private InfoVO info;
    private int clickPosition;

    public ContentStore(){
        menuList = new ArrayList<>();
        reviewList = new ArrayList<>();
        info = new InfoVO();
    }

    public void clear(){
        menuList.clear();
        reviewList.clear();
    }

    public void addItem(JSONArray jsonArray, int label){
        HashMap<String, MenuHeadVO> categoryMap = new HashMap<>();
        if(jsonArray ==null)
            return;
        JSONObject jsonObject;
        if(label == 0){
            try {
                ArrayList<String> keys = new ArrayList<>();

                for(int i=0; i<jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    String categoryString = jsonObject.getString("category");
                    if (categoryMap.get(categoryString) == null) {
                        keys.add(categoryString);
                        categoryMap.put(categoryString, new MenuHeadVO(categoryString, MenuVO.HEAD));
                    }
                }

                for(int i=0; i<jsonArray.length(); i++){
                    jsonObject = jsonArray.getJSONObject(i);
                    MenuHeadVO tempVO = categoryMap.get(jsonObject.getString("category"));
                    tempVO.getMenuChildList().add(new MenuChildVO(
                            jsonObject.getString("menu"),
                            jsonObject.getString("rate"),
                            jsonObject.getString("price"),
                            jsonObject.getInt("menuId"),
                            MenuVO.CHILD
                    ));
                }

                for(String key : keys){
                    Collections.sort(categoryMap.get(key).getMenuChildList());
                    menuList.add(categoryMap.get(key));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(label == 2){
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                    info.setX(jsonObject.getDouble("x"));
                    info.setY(jsonObject.getDouble("y"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            reviewList.clear();
            for(int i=jsonArray.length()-1; i>-1; i--){
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                    if(jsonObject.isNull("menuName")){
                        reviewList.add(new ReviewVO(
                                jsonObject.getString("nickname"),
                                jsonObject.getString("time"),
                                jsonObject.getString("rate"),
                                jsonObject.getString("contents"),
                                jsonObject.getString("reviewPic"),
                                jsonObject.getString("profilePic"),
                                jsonObject.getString("reviewId"),
                                jsonObject.getInt("menuId"),
                                jsonObject.getString("id"),
                                jsonObject.getString("recommend"),
                                jsonObject.getInt("isRecommended"),
                                "필요없는 메뉴",
                                "필요없는 식당이름"
                        ));
                    }else{
                        reviewList.add(new ReviewVO(
                                jsonObject.getString("nickname"),
                                jsonObject.getString("time"),
                                jsonObject.getString("rate"),
                                jsonObject.getString("contents"),
                                jsonObject.getString("reviewPic"),
                                jsonObject.getString("profilePic"),
                                jsonObject.getString("reviewId"),
                                jsonObject.getInt("menuId"),
                                jsonObject.getString("id"),
                                jsonObject.getString("recommend"),
                                jsonObject.getInt("isRecommended"),
                                jsonObject.getString("menuName"),
                                "필요없는 식당이름"
                        ));                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }

        }
    }



    public boolean isEmpty(int label){
        if(label == 0){
            if(menuList.size() == 0)
                return true;
        }else if(label == 1){
            if(reviewList.size() == 0)
                return true;
        }else{
            if(info.getX() == null)
                return true;
        }
        return false;

    }

    public InfoVO getInfo() { return info; }

    public ArrayList<MenuVO> getMenuList() {
        return menuList;
    }

    public ArrayList<ReviewVO> getReviewList() {
        return reviewList;
    }

    public int getClickPosition() {
        return clickPosition;
    }

    public void setClickPosition(int clickPosition) {
        this.clickPosition = clickPosition;
    }
}