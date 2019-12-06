package com.kwmm0.MyReview;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kwmm0.CallBackInterface.HttpCallBack;
import com.kwmm0.HttpConnection;
import com.kwmm0.R;
import com.kwmm0.Restaurant.ReviewVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyReviewActivity extends AppCompatActivity {
    private RecyclerView myReviewRecyclerView;
    private ArrayList<ReviewVO> myReviews;
    private ImageView imageLoading;
    private AppBarLayout appBarLayout;
    private AnimationDrawable drawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_review_layout);
        getWindow().setStatusBarColor(Color.rgb(183, 183, 183));

        myReviews = new ArrayList<>();
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        final float height = point.y;
        final float width = point.x;

        findViewFromXML();
        setView(width, height);
        connectHttp();

    }

    private void setView(final float width, final float height) {
        appBarLayout.measure(0, 0);
        final float appbarHeight = appBarLayout.getMeasuredHeight();

        imageLoading.setLayoutParams(new LinearLayout.LayoutParams((int) (width * 0.25), (int) (width * 0.25)));

        imageLoading.post(new Runnable() {
            @Override
            public void run() {
                imageLoading.setX((float) (width * 0.375));
                imageLoading.setY((float) ((height - appbarHeight - (width * 0.25)) / 2));
            }
        });

        drawable = (AnimationDrawable) imageLoading.getBackground();
        drawable.start();
        myReviewRecyclerView.setLayoutManager(new LinearLayoutManager(MyReviewActivity.this, LinearLayoutManager.VERTICAL, false));

    }

    private void findViewFromXML() {
        appBarLayout = findViewById(R.id.my_review_appbar);
        imageLoading = findViewById(R.id.my_review_img_loading);
        myReviewRecyclerView = findViewById(R.id.my_review_list);
    }

    private void connectHttp() {
        JSONObject jsonObject = new JSONObject();
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
            jsonObject.put("func", "myReview");
            jsonObject.put("id", sharedPreferences.getString("id", null));

            new HttpConnection(MyReviewActivity.this, new HttpCallBack() {
                @Override
                public void callBack(JSONObject jsonObject) {

                    try {
                        JSONArray myReviewArray = jsonObject.getJSONArray("reviews");
                        for (int i = myReviewArray.length() - 1; i > -1; i--) {
                            JSONObject resultJSON = myReviewArray.getJSONObject(i);
                            myReviews.add(new ReviewVO(
                                    resultJSON.getString("nickname"),
                                    resultJSON.getString("time"),
                                    resultJSON.getString("rate"),
                                    resultJSON.getString("contents"),
                                    resultJSON.getString("reviewPic"),
                                    resultJSON.getString("profilePic"),
                                    resultJSON.getString("reviewId"),
                                    0,
                                    resultJSON.getString("id"),
                                    String.valueOf(resultJSON.getInt("recommend")),
                                    0,
                                    resultJSON.getString("menuName"),
                                    resultJSON.getString("restaurantName")
                            ));
                        }
                        drawable.stop();
                        imageLoading.setVisibility(View.GONE);
                        setReviewList();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setReviewList() {
        RecyclerViewAdapter reviewAdapter = new RecyclerViewAdapter(MyReviewActivity.this, myReviews, myReviewRecyclerView);
        myReviewRecyclerView.setAdapter(reviewAdapter);

    }
}