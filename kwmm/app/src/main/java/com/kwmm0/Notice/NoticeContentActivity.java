package com.kwmm0.Notice;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.kwmm0.CallBackInterface.HttpCallBack;
import com.kwmm0.HttpConnection;
import com.kwmm0.R;
import com.kwmm0.ShowDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jade3 on 2018-11-13.
 */

public class NoticeContentActivity extends AppCompatActivity {
    public TextView contents,title,date;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_content_bar_layout);
        getWindow().setStatusBarColor(Color.rgb(255, 117, 0));

        findVIewFromXML();
        setView();
        connectHttp();

    }

    private void connectHttp() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("func","notice content");
            jsonObject.put("id", getIntent().getStringExtra("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpConnection(NoticeContentActivity.this, new HttpCallBack() {
            @Override
            public void callBack(JSONObject resultJSON) {
                try {
                    String result = resultJSON.getString("data");
                    if(result.equals("notice content")) {
                        String content = resultJSON.getString("content");
                        contents.setText(content);
                    } else{
                        new ShowDialog(NoticeContentActivity.this).show("알림","인터넷에러");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute(jsonObject);
    }

    private void setView() {
        title.setText(getIntent().getStringExtra("title"));
        date.setText(getIntent().getStringExtra("time"));
    }

    private void findVIewFromXML() {
        title = (TextView)findViewById(R.id.notice_conent_title);
        date = (TextView)findViewById(R.id.notice_content_date);
        contents= (TextView)findViewById(R.id.txt_notice_content);
    }
}
