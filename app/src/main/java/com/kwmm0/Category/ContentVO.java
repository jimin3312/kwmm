package com.kwmm0.Category;

public class ContentVO {

    private String name;
    private String score;
    private int reviewNumber;

    public int getReviewNumber() {
        return reviewNumber;
    }

    public ContentVO(String name, String score, int reviewNumber) {
        this.name = name;
        this.score = score;
        this.reviewNumber = reviewNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }


}