package com.kwmm0.Main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kwmm0.CallBackInterface.ConfirmCallBack;
import com.kwmm0.CallBackInterface.HttpCallBack;
import com.kwmm0.Category.CategoryActivity;
import com.kwmm0.HttpConnection;
import com.kwmm0.Login.LoginActivity;
import com.kwmm0.MyEditProfileActivity;
import com.kwmm0.MyReview.MyReviewActivity;
import com.kwmm0.Notice.NoticeActivity;
import com.kwmm0.R;
import com.kwmm0.ShowDialog;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private ConstraintLayout layout_loginBefore, layout_loginAfter;
    LinearLayout constraintLayoutMain;
    ImageView imageViewLogo, profileImage;
    TextView nickname, version;
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_drawable_layout);

        getWindow().setStatusBarColor(Color.rgb(255, 117, 0));
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float width = size.x;

        findViewFromXML();
        setView(width);
    }

    private void setView(final float width) {
        setSupportActionBar(toolbar);

        imageViewLogo.post(new Runnable() {
            @Override
            public void run() {
                imageViewLogo.setX((width - imageViewLogo.getWidth()) / 2);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        String temp = " " + getIntent().getStringExtra("version") + " ver";
        version.setText(temp);
    }

    private void findViewFromXML() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        constraintLayoutMain = (LinearLayout) findViewById(R.id.constraint_main);
        imageViewLogo = (ImageView) findViewById(R.id.main_img_logo);
        profileImage = (ImageView) findViewById(R.id.after_profile);
        layout_loginBefore = (ConstraintLayout) findViewById(R.id.beforeLogin);
        layout_loginAfter = (ConstraintLayout) findViewById(R.id.afterLogin);
        nickname = (TextView) findViewById(R.id.txt_nickName);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        version = findViewById(R.id.main_txt_version);
    }


    @Override
    protected void onResume() {
        super.onResume();

        sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("func", "connect");
            if (!sharedPreferences.getString("id", "#").equals("#")) {
                jsonObject.put("id", sharedPreferences.getString("id", ""));
            }
            new HttpConnection(MainActivity.this, new HttpCallBack() {
                @Override
                public void callBack(JSONObject resultJSON) {
                    try {
                        String data = resultJSON.getString("data");
                        if (data.equals("success")) {
                            if (sharedPreferences.getString("login", "3").equals("1") || sharedPreferences.getString("login", "3").equals("2")) {

                                layout_loginBefore.setVisibility(View.GONE);
                                layout_loginAfter.setVisibility(View.VISIBLE);

                                nickname.setText(sharedPreferences.getString("nickname", "null"));

                                if (!sharedPreferences.getString("profile", "").equals(HttpConnection.profileURL)) {
                                    Glide.with(MainActivity.this)
                                            .load(sharedPreferences.getString("profile", "null"))
                                            .apply(RequestOptions.circleCropTransform())
                                            .into(profileImage);
                                } else {
                                    profileImage.setImageResource(R.drawable.ic_person_black_24dp);
                                }
                                nickname.setText(sharedPreferences.getString("nickname", "null"));
                                if (drawer.isDrawerOpen(GravityCompat.START)) {
                                    drawer.closeDrawer(GravityCompat.START);
                                }
                            } else {
                                layout_loginBefore.setVisibility(View.VISIBLE);
                                layout_loginAfter.setVisibility(View.GONE);
                            }
                        } else if (data.equals("ban")) {
                            editor.clear();
                            editor.commit();
                            layout_loginBefore.setVisibility(View.VISIBLE);
                            layout_loginAfter.setVisibility(View.GONE);
                            profileImage.setImageResource(R.mipmap.default_person_round);

                            new ShowDialog(MainActivity.this).show("알림", "해당 계정은 " + resultJSON.getString("date") + "까지 사용금지 상태입니다.");
                        } else {
                            new ShowDialog(MainActivity.this).show("알림", "인터넷 에러");
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


    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (backPressedTime == 0) {
                Toast.makeText(MainActivity.this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
                backPressedTime = System.currentTimeMillis();
            } else {
                long second = ((System.currentTimeMillis() - backPressedTime));
                if (second < 2000) {
                    super.onBackPressed();
                } else {
                    Toast.makeText(MainActivity.this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
                    backPressedTime = System.currentTimeMillis();
                }
            }

        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            //select category
            case R.id.main_constraint_rice_food:
                start_category("0");
                break;
            case R.id.main_constraint_chinese_food:
                start_category("1");
                break;
            case R.id.main_constraint_japanese_food:
                start_category("2");
                break;
            case R.id.main_constraint_snack_bar:
                start_category("3");
                break;
            case R.id.main_constraint_noodle:
                start_category("4");
                break;
            case R.id.main_constraint_fast_food:
                start_category("5");
                break;
            case R.id.main_constraint_chicken:
                start_category("6");
                break;
            case R.id.main_constraint_dessert:
                start_category("7");
                break;
            case R.id.main_txt_used_icon:
                startActivity(new Intent(MainActivity.this, UsedIconActivity.class));
                break;
            case R.id.main_txt_private:
                startActivity(new Intent(MainActivity.this, PrivateActivity.class));
                break;
            case R.id.txt_logout:
                new ShowDialog(MainActivity.this, new ConfirmCallBack() {
                    @Override
                    public void callBack(Boolean isLogin) {
                        if (isLogin == false) {
                            editor.clear();
                            editor.commit();
                            onResume();

                            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    }
                }).logout();
                break;

            case R.id.btn_login:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;

            case R.id.btn_notice:
                startActivity(new Intent(MainActivity.this, NoticeActivity.class));
                break;
            case R.id.btn_review:
                if (sharedPreferences.getString("login", "3").equals("1") || sharedPreferences.getString("login", "3").equals("2")) {
                    startActivity(new Intent(MainActivity.this, MyReviewActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                break;
            case R.id.btn_edit_info:
                if (sharedPreferences.getString("login", "3").equals("1")) {
                    new ShowDialog(MainActivity.this).confirmPassword(sharedPreferences.getString("id", ""));
                } else if (sharedPreferences.getString("login", "3").equals("2")) {
                    startActivity(new Intent(MainActivity.this, MyEditProfileActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                break;
            case R.id.btn_suggest:
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/text");
                String[] address = {"kwmminfo@gmail.com"};
                email.putExtra(Intent.EXTRA_EMAIL, address);
                startActivity(email);
                break;
            case R.id.version:

                String appPackageName = getPackageName();
                Log.d("TAG",appPackageName);

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    finish();
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    finish();
                }
                break;
        }
    }

    public void start_category(String foodCategory) {
        Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
        intent.putExtra("category", foodCategory);
        startActivity(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}