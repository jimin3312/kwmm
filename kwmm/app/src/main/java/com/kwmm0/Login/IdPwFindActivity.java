package com.kwmm0.Login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kwmm0.CallBackInterface.HttpCallBack;
import com.kwmm0.HttpConnection;
import com.kwmm0.R;
import com.kwmm0.ShowDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class IdPwFindActivity extends AppCompatActivity {

    EditText email, emailAuthentication;
    Button nextStep;
    TextView emailCheck, authenticationCheck, authentication;
    String authenticationNumber, emailName;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id_pw_find_layout);

        getWindow().setStatusBarColor(Color.rgb(183,183,183));

        findViewFromXML();
        addListener();

    }

    private void addListener() {
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject1 = new JSONObject();
                try {
                    jsonObject1.put("func", "checkkey");
                    jsonObject1.put("key", emailAuthentication.getText().toString());
                    jsonObject1.put("webmail", email.getText().toString());

                    new HttpConnection(IdPwFindActivity.this, new HttpCallBack() {
                        @Override
                        public void callBack(JSONObject resultJSON) {
                            String result = null;
                            try {
                                result = resultJSON.getString("data");
                                if (result.equals("ok")) {
                                    Intent intent = new Intent(IdPwFindActivity.this, IdPwFindActivity2.class);
                                    intent.putExtra("webmail", email.getText().toString());
                                    startActivity(intent);
                                    finish();
                                } else if(result.equals("fail")){
                                    new ShowDialog(IdPwFindActivity.this).show("알림", "인증번호 확인");
                                }
                                else{
                                    new ShowDialog( IdPwFindActivity.this).show("알림", "인터넷 확인");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).execute(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        authentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    if (Pattern.matches("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$", email.getText().toString())) {
                        String s[] = email.getText().toString().split("@");

                        jsonObject.put("func", "find1");
                        jsonObject.put("webmail", email.getText().toString());

                        new HttpConnection(IdPwFindActivity.this, new HttpCallBack() {
                            @Override
                            public void callBack(JSONObject resultJSON) {
                                try {
                                    String result = resultJSON.getString("data");
                                    if (result.equals("exist")) {
                                        emailCheck.setVisibility(View.VISIBLE);
                                        emailCheck.setText("인증번호를 발송하였습니다.");
                                        emailCheck.setTextColor(Color.BLUE);
                                        authenticationNumber = resultJSON.getString("key");
                                        emailName = email.getText().toString();
                                    } else if (result.equals("unexist")) {
                                        emailCheck.setVisibility(View.VISIBLE);
                                        emailCheck.setText("존재하지 않는 이메일입니다.");
                                        emailCheck.setTextColor(Color.RED);
                                    } else {
                                        new ShowDialog(IdPwFindActivity.this).show("알림", "인터넷에러");
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
            }
        });

    }

    private void findViewFromXML() {
        email = (EditText) findViewById(R.id.find_etxt_email);
        emailAuthentication = (EditText) findViewById(R.id.find_etxt_authentication);
        emailCheck = (TextView) findViewById(R.id.find_txt_email_check);
        authenticationCheck = (TextView) findViewById(R.id.find_txt_authentication_check);
        authentication = (TextView)findViewById(R.id.find_txt_authentication);
        nextStep = (Button)findViewById(R.id.find_btn_next);
    }

}
