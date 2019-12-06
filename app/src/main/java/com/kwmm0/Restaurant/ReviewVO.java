package com.kwmm0.Restaurant;

import android.support.annotation.NonNull;

public class ReviewVO implements Comparable<ReviewVO>{

    private String nickname,time,rate,contents,thumbUpNumber;
    private String url, profileUrl;
    private String reviewId;
    private String id;
    private String menu;
    private String restaurantName;
    private int menuId;
    private int isRecommended;

    public String getThumbUpNumber() {
        return thumbUpNumber;
    }


    public ReviewVO(String nickname, String time, String rate, String contents , String url, String profileUrl, String reviewId, int menuId, String id,
                    String thumbUpNumber, int isRecommended, String menu, String restaurantName) {
        this.nickname = nickname;
        this.time = time;
        this.rate = rate;
        this.contents = contents;
        this.url = url;
        this.profileUrl = profileUrl;
        this.reviewId = reviewId;
        this.menuId = menuId;
        this.thumbUpNumber = thumbUpNumber;
        this.id = id;
        this.isRecommended = isRecommended;
        this.menu = menu;
        this.restaurantName = restaurantName;
    }

    public void setThumbUpNumber(String thumbUpNumber) {
        this.thumbUpNumber = thumbUpNumber;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRate() {
        return rate;
    }

    public String getContents() {
        return contents;
    }

    public String getUrl() {
        return url;
    }

    public String getReviewId() { return reviewId; }

    public String getId() { return id; }

    public String getMenu() { return menu; }

    public void setId(String id) { this.id = id; }

    public int getIsRecommended() {
        return isRecommended;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setIsRecommended(int isRecommended) {
        this.isRecommended = isRecommended;
    }

    @Override
    public int compareTo(@NonNull ReviewVO reviewVO) {
        return Integer.parseInt(reviewVO.thumbUpNumber) - Integer.parseInt(this.thumbUpNumber);
    }
}