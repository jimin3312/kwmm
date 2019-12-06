package com.kwmm0.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kwmm0.CallBackInterface.HttpCallBack;
import com.kwmm0.HttpConnection;
import com.kwmm0.R;
import com.kwmm0.ShowDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jade3 on 2018-11-07.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText id,pw;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private GoogleSignInClient googleSignInClient;
    private SignInButton googleSignInButton;
    private GoogleSignInOptions gso;
    public int RC_SIGN_IN = 101;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        getWindow().setStatusBarColor(Color.rgb(154,154,154));
        
        id = (EditText)findViewById(R.id.login_etxt_id);
        pw = (EditText)findViewById(R.id.login_etxt_password);

        googleSignInButton = (SignInButton) findViewById(R.id.login_btn_google_login);

        TextView textView = (TextView) googleSignInButton.getChildAt(0);
        textView.setText("구글 로그인");
        textView.setGravity(Gravity.LEFT);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignInClient.signOut();
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("func","google");
                jsonObject.put("id",account.getId());
                jsonObject.put("webmail",account.getEmail());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new HttpConnection(this, new HttpCallBack() {
                @Override
                public void callBack(JSONObject resultJSON) {
                    try {
                        String data = resultJSON.getString("data");
                        if(data.equals("signup ok"))
                        {

                            sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
                            editor = sharedPreferences.edit();

                            editor.putString("id", account.getId());
                            editor.putString("nickname", resultJSON.getString("nickname"));
                            editor.putString("login","2");
                            editor.putString("profile", resultJSON.getString("profile"));
                            editor.putString("autoLogin","1");

                            editor.commit();
                            finish();
                        } else if(data.equals("fail")){
                            new ShowDialog(LoginActivity.this).show("알림","이미 중복된 이메일입니다.");
                            googleSignInClient.signOut();
                        } else if(data.equals("ban")){
                            googleSignInClient.signOut();
                            finish();
                        } else {
                            new ShowDialog(LoginActivity.this).show("알림", "인터넷 확인");
                            googleSignInClient.signOut();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute(jsonObject);
            // Signed in successfully, show authenticated UI.

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
        }
    }
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.login_btn_find:
                startActivity(new Intent(LoginActivity.this, IdPwFindActivity.class));
                break;
            case R.id.btn_signUp:
                startActivity(new Intent(LoginActivity.this, TermsActivity.class));
                break;
            case R.id.login_btn_login:

                if(id.getText().toString().equals("") || pw.getText().toString().equals(""))
                {
                    new ShowDialog(LoginActivity.this).show("알림","회원정보를 입력하세요.");
                    break;
                }
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("func","login");
                    jsonObject.put("id",id.getText().toString());
                    jsonObject.put("pwd",new Hashing().getSHA256(pw.getText().toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpConnection httpConnection= new HttpConnection(LoginActivity.this, new HttpCallBack() {
                    @Override
                    public void callBack(JSONObject resultJSON) {
                        try {
                            String result = resultJSON.getString("data");
                            if (result.equals("로그인")) {
                                SharedPreferences sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putString("id", id.getText().toString());
                                editor.putString("nickname", resultJSON.getString("nickname"));
                                editor.putString("login","1");
                                editor.putString("profile", resultJSON.getString("profile"));
                                editor.putString("autoLogin","1");
                                editor.commit();

                                finish();
                            } else if(result.equals("로그인 실패"))
                            {
                                new ShowDialog(LoginActivity.this).show("로그인 실패", "회원정보를 확인하세요.");
                            } else if(result.equals("ban")){
                                new ShowDialog(LoginActivity.this).show("알림","해당 계정은 "+resultJSON.getString("date") +"까지 사용금지 상태입니다.");
                            }
                            else{
                                new ShowDialog(LoginActivity.this).show("알림", "인터넷 확인");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                httpConnection.execute(jsonObject);
                break;


        }


    }
}
