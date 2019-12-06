package com.kwmm0.Login;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kwmm0.CallBackInterface.HttpCallBack;
import com.kwmm0.HttpConnection;
import com.kwmm0.R;
import com.kwmm0.ShowDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class IdPwFindActivity2 extends AppCompatActivity {
    TextView idTextView, passwordTextView,temp;
    LinearLayout pwLinearLayout,temporaryLinearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id_pw_find_layout2);
        getWindow().setStatusBarColor(Color.rgb(183,183,183));

        getId();
        findViewFromXML();
        setView();
        addListener();
    }

    private String initP(int size) {
        Random  ran = new Random();
        StringBuffer sb = new StringBuffer();
        int num;
        do {
            num = ran.nextInt(75)+48;
            if((num>=48 && num<=57) || (num>=65 && num<=90) || (num>=97 && num<=122)) {
                sb.append((char)num);
            }else {
                continue;
            }
        } while (sb.length() < size);
        return sb.toString();
    }

    private void addListener() {
        passwordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tempPassword = initP(8);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("func","edit pw");
                    jsonObject.put("id", idTextView.getText().toString());
                    jsonObject.put("pw", new Hashing().getSHA256(tempPassword));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new HttpConnection(IdPwFindActivity2.this, new HttpCallBack() {

                    @Override
                    public void callBack(JSONObject jsonObject) {
                        try {
                            String result = jsonObject.getString("data");
                            if(result.equals("success")) {
                                pwLinearLayout.setVisibility(View.GONE);
                                temp.setText(tempPassword);
                                temporaryLinearLayout.setVisibility(View.VISIBLE);
                            } else {
                                new ShowDialog(IdPwFindActivity2.this).show("알림","인터넷에러");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).execute(jsonObject);

            }
        });
    }

    private void getId(){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("func", "find2");
            jsonObject.put("webmail", getIntent().getStringExtra("webmail"));
            new HttpConnection(IdPwFindActivity2.this, new HttpCallBack() {
                @Override
                public void callBack(JSONObject resultJSON) {
                    try {
                        String result = resultJSON.getString("data");
                        if(result.equals("success"))
                            idTextView.setText(resultJSON.getString("id"));
                        else{
                            new ShowDialog(IdPwFindActivity2.this).show("알람", "인터넷 확인");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setView() {
        idTextView.setPaintFlags(idTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        passwordTextView.setPaintFlags(idTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    private void findViewFromXML() {
        idTextView = (TextView)findViewById(R.id.id_pw_find_txt_id);
        pwLinearLayout = (LinearLayout)findViewById(R.id.id_pw_find_linearlayout_pw);
        temporaryLinearLayout = (LinearLayout)findViewById(R.id.id_pw_find_linearlayout_temporary_number);
        temp = (TextView)findViewById(R.id.id_pw_find_txt_temporary_number);
        passwordTextView = (TextView)findViewById(R.id.id_pw_find_txt_pw);
    }
}
