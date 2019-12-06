package com.kwmm0.Restaurant;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;

import com.kwmm0.CallBackInterface.HttpCallBack;
import com.kwmm0.HttpConnection;
import com.kwmm0.R;

import org.json.JSONException;
import org.json.JSONObject;

public class MenuReview extends AppCompatActivity {

    private int menuId;
    private RecyclerView recyclerView;
    private ImageView noReviewImage;
    final ContentStore contentStore= new ContentStore();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_menu_review_layout);
        getWindow().setStatusBarColor(Color.rgb(183,183,183));

        noReviewImage = findViewById(R.id.menu_review_no_review_image);
        recyclerView = (RecyclerView) findViewById(R.id.menu_review_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void onResume() {
        super.onResume();
        menuId = getIntent().getIntExtra("menuId",-1);
        connectHttp();

    }

    private void connectHttp() {
        JSONObject jsonObject = new JSONObject();
        sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);

        try {
            jsonObject.put("func", "리뷰");
            jsonObject.put("menuId", menuId);
            jsonObject.put("id", sharedPreferences.getString("id","null"));

            new HttpConnection(MenuReview.this, new HttpCallBack() {
                @Override
                public void callBack(JSONObject resultJSON) {

                    try {
                        contentStore.addItem(resultJSON.getJSONArray("reviews"), 1);
                        if(contentStore.isEmpty(1)){
                            noReviewImage.setVisibility(View.VISIBLE);
                        }else{
                            noReviewImage.setVisibility(View.GONE);
                        }
                        recyclerView.setAdapter(new ReviewAdapter(contentStore.getReviewList(), MenuReview.this, menuId));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}