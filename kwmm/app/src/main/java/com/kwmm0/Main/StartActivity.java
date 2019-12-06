package com.kwmm0.Main;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;


import com.kwmm0.CallBackInterface.HttpCallBack;
import com.kwmm0.CallBackInterface.ConfirmCallBack;
import com.kwmm0.HttpConnection;
import com.kwmm0.R;
import com.kwmm0.ShowDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.StringTokenizer;

/**
 * Created by jade3 on 2018-11-06.
 */

public class StartActivity extends AppCompatActivity {
    private HttpConnection httpConnection;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        httpConnection.cancel(true);

    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.start_layout);


        try {
            httpConnection = new HttpConnection(StartActivity.this, new HttpCallBack() {
                @Override
                public void callBack(JSONObject jsonObject) {
                    try {
                        Handler handler = new Handler();

                        String data = jsonObject.getString("data");
                        final String latestVersion = jsonObject.getString("version");
                        PackageManager packageManager = getPackageManager();
                        final PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
                        final String preVersion = packageInfo.versionName;
                        final StringTokenizer latestVersionTokens = new StringTokenizer(latestVersion, ".");
                        final StringTokenizer preVersionTokens = new StringTokenizer(preVersion, ".");

                        if (data.equals("success")) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (!latestVersionTokens.nextToken().equals(preVersionTokens.nextToken()) || !latestVersionTokens.nextToken().equals(preVersionTokens.nextToken())) {
                                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                            finish();
                                        } catch (android.content.ActivityNotFoundException e) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                            finish();

                                        }
                                    } else {
                                        if(!StartActivity.this.isFinishing()) {
                                            Intent intent = new Intent(StartActivity.this, MainActivity.class);
                                            intent.putExtra("version", packageInfo.versionName);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            });
                        } else {
                            new ShowDialog(StartActivity.this, new ConfirmCallBack() {
                                @Override
                                public void callBack(Boolean isLogin) {
                                    if (isLogin == true) {
                                        finish();
                                    }
                                }
                            }).startFinish();
                        }

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            httpConnection.execute(new JSONObject().put("func", "connect"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}