package com.kwmm0.Login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kwmm0.CallBackInterface.HttpCallBack;
import com.kwmm0.HttpConnection;
import com.kwmm0.Main.MainActivity;
import com.kwmm0.R;
import com.kwmm0.ShowDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class SignUpActivity2 extends AppCompatActivity implements View.OnClickListener {
    EditText nickname, password, id;
    Button submit;
    TextView nicknameConfirm, idConfirm, passwordConfirm;
    ImageView checkIdImage, checkNicknameImage;
    boolean isIdChecked, isPwChecked, isNicknameChecked;
    String email = "2354taeng@naver.com";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_layout2);

        getWindow().setStatusBarColor(Color.rgb(183,183,183));
        findViewFromXML();
        addListener();
    }

    private void addListener() {
        id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!id.getText().toString().equals("")) {
                    char firstCharacter = id.getText().toString().charAt(0);
                    if (!('A' <= firstCharacter && firstCharacter <= 'z')) {
                        idConfirm.setVisibility(View.VISIBLE);
                        idConfirm.setText("알파벳으로 시작");
                        checkIdImage.setVisibility(View.GONE);
                        isIdChecked = false;
                    }
                    else if (!Pattern.matches("^[0-9a-zA-Z]{4,10}$", id.getText().toString())) {
                        idConfirm.setVisibility(View.VISIBLE);
                        idConfirm.setText("영문 숫자 조합, 4~10자");
                        checkIdImage.setVisibility(View.GONE);
                        isIdChecked = false;
                    } else {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("func", "checkusable id");
                            jsonObject.put("id", id.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new HttpConnection(SignUpActivity2.this, new HttpCallBack() {
                            @Override
                            public void callBack(JSONObject resultJSON) {
                                try {
                                    String result = resultJSON.getString("data");
                                    if (result.equals("overlapped")) {
                                        idConfirm.setVisibility(View.VISIBLE);
                                        idConfirm.setText("중복된 아이디입니다. ");
                                        checkIdImage.setVisibility(View.GONE);
                                        isIdChecked = false;
                                    } else if (result.equals("ok")) {
                                        idConfirm.setVisibility(View.GONE);
                                        checkIdImage.setVisibility(View.VISIBLE);
                                        isIdChecked = true;
                                    } else {
                                        new ShowDialog(SignUpActivity2.this).show("알림", "인터넷에러");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).execute(jsonObject);
                    }
                }
            }
        });

        nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                JSONObject jsonObject = new JSONObject();
                if(Pattern.matches("^[가-힣a-zA-Z0-9]{1,8}+$", nickname.getText().toString())){
                    try {
                        jsonObject.put("func", "checkusable nickname");
                        jsonObject.put("nickname", nickname.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new HttpConnection(SignUpActivity2.this, new HttpCallBack() {
                        @Override
                        public void callBack(JSONObject resultJSON) {
                            try {
                                String result = resultJSON.getString("data");
                                if(result.equals("overlapped")){
                                    nicknameConfirm.setVisibility(View.VISIBLE);
                                    nicknameConfirm.setText("중복된 닉네임입니다.");
                                    isNicknameChecked = false;
                                    checkNicknameImage.setVisibility(View.GONE);
                                }
                                else if(result.equals("ok")){
                                    nicknameConfirm.setVisibility(View.GONE);
                                    checkNicknameImage.setVisibility(View.VISIBLE);
                                    isNicknameChecked = true;
                                }else{
                                    new ShowDialog(SignUpActivity2.this).show("알림", "인터넷에러");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).execute(jsonObject);
                }
                else{
                    nicknameConfirm.setVisibility(View.VISIBLE);
                    nicknameConfirm.setText("특수문자 사용 불가, 8자이하 ");
                    isNicknameChecked = false;
                    checkNicknameImage.setVisibility(View.GONE);
                }
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(password.getText().toString().length() < 8){
                    passwordConfirm.setVisibility(View.VISIBLE);
                    passwordConfirm.setText("8자 이상");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(password.getText().toString().length() >= 8){
                    passwordConfirm.setVisibility(View.GONE);
                    isPwChecked = true;
                }else{
                    isPwChecked = false;
                }
            }
        });
    }

    private void findViewFromXML(){
        id = (EditText)findViewById(R.id.sign_up_etxt_id);
        nickname = (EditText)findViewById(R.id.sign_up_etxt_nickname);
        password = (EditText)findViewById(R.id.sign_up_etxt_password);
        submit = (Button)findViewById(R.id.sign_up_btn_submit);
        nicknameConfirm = (TextView)findViewById(R.id.sign_up_nickname_confirm);
        idConfirm = (TextView)findViewById(R.id.sign_up_id_confirm);
        passwordConfirm = (TextView)findViewById(R.id.sign_up_pw_confirm);
        checkIdImage= (ImageView)findViewById(R.id.sign_up_img_id_check);
        checkNicknameImage = (ImageView)findViewById(R.id.sign_up_img_nickname_check);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_up_btn_submit:
                if(isIdChecked && isNicknameChecked && isPwChecked) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("func", "signup");
                        jsonObject.put("id", id.getText().toString());
                        jsonObject.put("nickname", nickname.getText().toString());
                        jsonObject.put("pwd", new Hashing().getSHA256(password.getText().toString()));
                        jsonObject.put("webmail", getIntent().getStringExtra("email"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new HttpConnection(SignUpActivity2.this, new HttpCallBack() {
                        @Override
                        public void callBack(JSONObject resultJSON) {
                            try {
                                if (resultJSON.getString("data").equals("signup ok")) {
                                    new ShowDialog(SignUpActivity2.this).show("알람", "회원가입 완료");
                                    startActivity(new Intent(SignUpActivity2.this, MainActivity.class));
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).execute(jsonObject);
                }
        }
    }
}