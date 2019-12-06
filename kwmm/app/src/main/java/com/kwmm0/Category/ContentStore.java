package com.kwmm0.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContentStore {

    private ArrayList<ContentVO> koreanDish;
    private ArrayList<ContentVO> chineseDish;
    private ArrayList<ContentVO> japaneseDish;
    private ArrayList<ContentVO> snackBar;
    private ArrayList<ContentVO> noodle;
    private ArrayList<ContentVO> fastFood;
    private ArrayList<ContentVO> chicken;
    private ArrayList<ContentVO> dessert;
    private String clikedRate;
    private String clikedReviewNumber;
    private String clickedRestaurantName;

    public ContentStore(){
        koreanDish = new ArrayList<>();
        chineseDish = new ArrayList<>();
        japaneseDish = new ArrayList<>();
        fastFood = new ArrayList<>();
        snackBar = new ArrayList<>();
        noodle = new ArrayList<>();
        chicken = new ArrayList<>();
        dessert = new ArrayList<>();
    }

    public void clear(String label){
        ArrayList<ContentVO> arrayList = null;
        switch (label){
            case "한식":
                arrayList = koreanDish;
                break;
            case "중식":
                arrayList = chineseDish;
                break;
            case "일식":
                arrayList = japaneseDish;
                break;
            case "분식":
                arrayList = snackBar;
                break;
            case "면":
                arrayList = noodle;
                break;
            case "햄버거/피자":
                arrayList = fastFood;
                break;
            case "치킨":
                arrayList = chicken;
                break;
            case "디저트":
                arrayList = dessert;
                break;
        }
        arrayList.clear();
    }

   public void addItem(JSONArray jsonArray, String label){
       JSONObject jsonObject;
       ArrayList<ContentVO> arrayList = null;
       switch (label){
           case "한식":
               arrayList = koreanDish;
               break;
           case "중식":
               arrayList = chineseDish;
               break;
           case "일식":
               arrayList = japaneseDish;
               break;
           case "분식":
               arrayList = snackBar;
               break;
           case "면":
               arrayList = noodle;
               break;
           case "햄버거/피자":
               arrayList = fastFood;
               break;
           case "치킨":
               arrayList = chicken;
               break;
           case "디저트":
               arrayList = dessert;
               break;
       }

       for(int i=0; i<jsonArray.length(); i++){
           try {
               jsonObject = jsonArray.getJSONObject(i);
               arrayList.add(new ContentVO(jsonObject.getString("dinnerName"), jsonObject.getString("rate"), jsonObject.getInt("numberOfRate")));
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }
   }

    public ArrayList<ContentVO> getKoreanDish() {
        return koreanDish;
    }

    public ArrayList<ContentVO> getChineseDish() {
        return chineseDish;
    }

    public ArrayList<ContentVO> getJapaneseDish() {
        return japaneseDish;
    }

    public ArrayList<ContentVO> getSnackBar() {
        return snackBar;
    }

    public ArrayList<ContentVO> getNoodle() {
        return noodle;
    }

    public ArrayList<ContentVO> getFastFood() {
        return fastFood;
    }

    public ArrayList<ContentVO> getChicken() { return chicken; }

    public ArrayList<ContentVO> getDessert() { return dessert; }

    public String getClikedRate() {
        return clikedRate;
    }

    public void setClikedRate(String clikedRate) {
        this.clikedRate = clikedRate;
    }

    public String getClikedReviewNumber() {
        return clikedReviewNumber;
    }

    public String getClickedRestaurantName() {
        return clickedRestaurantName;
    }

    public void setClickedRestaurantName(String clickedRestaurantName) {
        this.clickedRestaurantName = clickedRestaurantName;
    }


    public void setClikedReviewNumber(String clikedReviewNumber) {
        this.clikedReviewNumber = clikedReviewNumber;
    }

    public boolean isEmpty(String categoryLabel){
        boolean b = false;
        switch (categoryLabel){
            case "한식":
                if(koreanDish.size() == 0)
                    b = true;
                break;
            case "중식":
                if(chineseDish.size() == 0)
                    b = true;
                break;
            case "일식":
                if(japaneseDish.size() == 0)
                    b = true;
                break;
            case "분식":
                if(snackBar.size() == 0)
                    b = true;
                break;
            case "면":
                if(noodle.size() == 0)
                    b = true;
                break;
            case "햄버거/피자":
                if(fastFood.size() == 0)
                    b = true;
                break;
            case "치킨":
                if(chicken.size() == 0)
                    b = true;
                break;
            case "디저트":
                if(dessert.size() == 0)
                    b = true;
                break;
        }
        return b;
    }
}
