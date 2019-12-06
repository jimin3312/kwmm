package com.kwmm0.Notice;

/**
 * Created by jade3 on 2018-11-09.
 */

public class ContentVO {

    private String title;
    private String time;
    private String id;

    public String getTitle() {
        return title;
    }
    public String getId(){ return id;}
    public void setTime(String time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setId(String id) {this.id = id;}
    public String getTime() {
        return time;
    }

}
