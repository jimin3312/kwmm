package com.kwmm0.Login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kwmm0.CallBackInterface.HttpCallBack;
import com.kwmm0.HttpConnection;
import com.kwmm0.R;
import com.kwmm0.ShowDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

/**
 * Created by jade3 on 2018-11-07.
 */

public class SignUpActivity extends AppCompatActivity {

    EditText email, emailAuthentication;
    TextView emailCheck, authenticationCheck;
    String emailName;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_layout);

        getWindow().setStatusBarColor(Color.rgb(183,183,183));
        findViewFromXML();
    }

    private void findViewFromXML() {

        email = (EditText) findViewById(R.id.sign_up_etxt_email);
        emailAuthentication = (EditText) findViewById(R.id.sign_up_etxt_authentication);
        emailCheck = (TextView) findViewById(R.id.sign_up_txt_email_check);
        authenticationCheck = (TextView) findViewById(R.id.sign_up_txt_authentication_check);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_up_txt_authentication:

                JSONObject jsonObject = new JSONObject();
                try {
                    if (Pattern.matches("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$", email.getText().toString())) {

                        jsonObject.put("func", "certify");
                        jsonObject.put("webmail", email.getText().toString());

                        new HttpConnection(SignUpActivity.this, new HttpCallBack() {
                            @Override
                            public void callBack(JSONObject resultJSON) {
                                try {
                                    String result = resultJSON.getString("data");
                                    if (result.equals("usable webmail")) {
                                        emailCheck.setVisibility(View.VISIBLE);
                                        emailCheck.setText("인증번호를 발송하였습니다.");
                                        emailCheck.setTextColor(Color.BLUE);
                                        emailName = email.getText().toString();
                                    } else if (result.equals("unusable webmail")) {
                                        emailCheck.setText("이메일이 중복되었습니다.");
                                        emailCheck.setVisibility(View.VISIBLE);
                                    } else {
                                        new ShowDialog(SignUpActivity.this).show("알림", "인터넷에러");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).execute(jsonObject);
                    } else {
                        emailCheck.setText("잘못된 형식의 이메일입니다.");
                        emailCheck.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.sign_up_btn_next:
                JSONObject jsonObject1 = new JSONObject();
                try {
                    jsonObject1.put("func", "checkkey");
                    jsonObject1.put("key", emailAuthentication.getText().toString());
                    jsonObject1.put("webmail", email.getText().toString());

                    new HttpConnection(SignUpActivity.this, new HttpCallBack() {
                        @Override
                        public void callBack(JSONObject resultJSON) {
                            String result = null;
                            try {
                                result = resultJSON.getString("data");
                                if (result.equals("ok")) {
                                    Intent intent = new Intent(SignUpActivity.this, SignUpActivity2.class);
                                    intent.putExtra("email", email.getText().toString());
                                    startActivity(intent);
                                    finish();
                                } else if(result.equals("fail")){
                                    new ShowDialog(SignUpActivity.this).show("알림", "인증번호 확인");
                                }
                                else{
                                    new ShowDialog( SignUpActivity.this).show("알림", "인터넷 확인");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).execute(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                break;
        }
    }

}