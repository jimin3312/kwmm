package com.kwmm0.Notice;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.kwmm0.CallBackInterface.HttpCallBack;
import com.kwmm0.HttpConnection;
import com.kwmm0.R;
import com.kwmm0.ShowDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jade3 on 2018-11-07.
 */

public class NoticeActivity extends AppCompatActivity {

    private ListView listView;
    private ListAdapter adapter;
    private ImageView imageLoading;
    private AppBarLayout appBarLayout;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_layout);

        getWindow().setStatusBarColor(Color.rgb(255, 117, 0));

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float width = size.x;
        float height = size.y;

        adapter = new ListAdapter(NoticeActivity.this);

        findViewFromXML();
        setView(width, height);
        connectHttp();
    }

    private void connectHttp() {
        final AnimationDrawable animationDrawable = (AnimationDrawable) imageLoading.getBackground();
        animationDrawable.start();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("func", "notice");
        } catch (JSONException e) {
            e.printStackTrace();
        }

       new HttpConnection(NoticeActivity.this, new HttpCallBack() {
            @Override
            public void callBack(JSONObject resultJSON) {
                try {
                    String result = resultJSON.getString("data");
                    if (result.equals("notice")) {

                        JSONArray jsonArray = resultJSON.getJSONArray("titles");
                        JSONObject jsonObject = null;
                        for (int i = jsonArray.length() - 1; i > -1; i--) {
                            jsonObject = jsonArray.getJSONObject(i);
                            adapter.addVO(jsonObject.getString("title"), jsonObject.getString("time"), jsonObject.getString("id"));
                        }
                        animationDrawable.stop();
                        imageLoading.setVisibility(View.GONE);
                        listView.setAdapter(adapter);

                    } else {
                        new ShowDialog(NoticeActivity.this).show("알람", "인터넷 확인");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute(jsonObject);
    }

    private void setView(final float width, final float height) {
        appBarLayout.measure(0,0);
        imageLoading.setLayoutParams(new LinearLayout.LayoutParams((int) (width * 0.25), (int) (width * 0.25)));
        final float finalActionBarHeight = appBarLayout.getMeasuredHeight();
        imageLoading.post(new Runnable() {
            @Override
            public void run() {
                imageLoading.setX((float) (width * 0.375));
                imageLoading.setY(((float) (height - finalActionBarHeight - (width * 0.5)) / 2));
            }
        });
    }

    private void findViewFromXML() {
        listView = (ListView) findViewById(R.id.notice_llstView);
        imageLoading = (ImageView) findViewById(R.id.notice_img_loading);
        appBarLayout = (AppBarLayout)findViewById(R.id.notice_app_bar);
    }
}