package com.kwmm0.Restaurant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;


import com.kwmm0.CallBackInterface.HttpCallBack;
import com.kwmm0.HttpConnection;
import com.kwmm0.R;
import com.kwmm0.ShowDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RestaurantActivity extends AppCompatActivity implements OnMapReadyCallback {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<View> viewList;
    private ArrayList<AnimationDrawable> animationDrawables;
    private String restaurantName;
    private ContentStore contentStore;
    private TextView title, totalRatingText;
    private RatingBar totalRatingBar;
    private InfoVO infoVO;
    private AppBarLayout appBarLayout;
    private float width, height, pagerHeight;
    private int TAB = 0;
    private boolean firstConnect = false, reConnect = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_layout);

        getWindow().setStatusBarColor(Color.rgb(183, 183, 183));

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        restaurantName = getIntent().getStringExtra("name");
        contentStore = new ContentStore();
        viewList = new ArrayList<>();
        animationDrawables = new ArrayList<>();

        findViewFromXML();
        makeView();
        setViewPager();
    }

    private void findViewFromXML() {
        tabLayout = findViewById(R.id.restaurant_tab);
        viewPager = findViewById(R.id.restaurant_viewPager);
        title = findViewById(R.id.txt_restaurant_name);
        totalRatingText = findViewById(R.id.txt_restaurant_rating);
        totalRatingBar = findViewById(R.id.restaurant_rating);
        appBarLayout = findViewById(R.id.restaurant_app_bar);
    }

    private float getViewHeight(View view) {
        view.measure(0, 0);
        return view.getMeasuredHeight();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (TAB == 0) {
            connectHttp(0, "메뉴별 리뷰", reConnect);
            connectHttp(1, "전체 리뷰", reConnect);
        }
    }

    private void connectHttp(final int tabPosition, String tabText, boolean reConnect) {
        if (tabPosition == 0) {
            if (reConnect) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("func", tabText);
                    jsonObject.put("restaurantName", restaurantName);

                    new HttpConnection(RestaurantActivity.this, new HttpCallBack() {
                        @Override
                        public void callBack(JSONObject resultJSON) {
                            try {
                                JSONArray jsonArray = resultJSON.getJSONArray("restaurant");
                                JSONObject jsonObject1;
                                String changedMenu = "";
                                if(contentStore.getMenuList().get(contentStore.getClickPosition()) instanceof MenuChildVO) {
                                     changedMenu = ((MenuChildVO) contentStore.getMenuList().get(contentStore.getClickPosition())).getName();
                                }
                                float rating = (float) ((Math.floor(resultJSON.getDouble("rate") * 10)) / 10);
                                totalRatingBar.setRating(rating);
                                totalRatingText.setText(String.valueOf(rating));

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jsonObject1 = jsonArray.getJSONObject(i);
                                    if (changedMenu.equals(jsonObject1.getString("menu"))) {
                                        ((MenuChildVO) contentStore
                                                .getMenuList()
                                                .get(contentStore.getClickPosition()))
                                                .setRate(jsonObject1.getString("rate"));
                                    }
                                }
                            setPage(viewList, tabPosition);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).execute(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                if (contentStore.isEmpty(tabPosition)) {
                    animationDrawables.get(tabPosition).start();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("func", tabText);
                        jsonObject.put("restaurantName", restaurantName);

                        new HttpConnection(RestaurantActivity.this, new HttpCallBack() {
                            @Override
                            public void callBack(JSONObject resultJSON) {
                                try {
                                    contentStore.addItem(resultJSON.getJSONArray("restaurant"), tabPosition);

                                    animationDrawables.get(tabPosition).stop();
                                    ((LinearLayout) viewList.get(tabPosition)).getChildAt(0).setVisibility(View.GONE);
                                    title.setText(restaurantName);
                                    setPage(viewList, tabPosition);
                                    float rating = (float) ((Math.floor(resultJSON.getDouble("rate") * 10)) / 10);
                                    totalRatingBar.setRating(rating);
                                    totalRatingText.setText(String.valueOf(rating));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).execute(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    setPage(viewList, tabPosition);
                }
            }
        } else if (tabPosition == 1) {
            if (reConnect) {
                if (!contentStore.isEmpty(tabPosition)) {
                    return;
                }
            }
            animationDrawables.get(tabPosition).start();
            JSONObject jsonObject = new JSONObject();
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
                jsonObject.put("func", tabText);
                jsonObject.put("restaurantName", restaurantName);
                jsonObject.put("id", sharedPreferences.getString("id", "null"));
                new HttpConnection(RestaurantActivity.this, new HttpCallBack() {
                    @Override
                    public void callBack(JSONObject resultJSON) {
                        try {
                            contentStore.addItem(resultJSON.getJSONArray("reviews"), tabPosition);
                            animationDrawables.get(tabPosition).stop();
                            ((LinearLayout) viewList.get(tabPosition)).getChildAt(0).setVisibility(View.GONE);
                            if (contentStore.isEmpty(1)) {
                                ((LinearLayout) viewList.get(tabPosition)).getChildAt(2).setVisibility(View.VISIBLE);
                            }
                            setPage(viewList, tabPosition);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).execute(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("func", "info");
                jsonObject.put("restaurantName", restaurantName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new HttpConnection(RestaurantActivity.this, new HttpCallBack() {
                @Override
                public void callBack(JSONObject resultJSON) {
                    try {
                        String result = resultJSON.getString("data");
                        if (result.equals("success")) {
                            JSONObject jsonObject1 = resultJSON.getJSONObject("info");
                            JSONArray jsonArray = new JSONArray();
                            JSONObject x = new JSONObject();
                            x.put("x", jsonObject1.getDouble("x"));
                            x.put("y", jsonObject1.getDouble("y"));
                            jsonArray.put(x);
                            contentStore.addItem(jsonArray, tabPosition);
                            infoVO = contentStore.getInfo();

                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                    .findFragmentById(R.id.map);
                            mapFragment.getMapAsync(RestaurantActivity.this);
                        } else {
                            new ShowDialog(RestaurantActivity.this).show("알림", "인터넷 에러");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute(jsonObject);
        }
    }


    private void makeView() {
        float appBarHeight = getViewHeight(appBarLayout);
        float tabHeight = getViewHeight(tabLayout);
        pagerHeight = height - appBarHeight - tabHeight;

        for (int i = 0; i < 2; i++) {
            RecyclerView recyclerView = new RecyclerView(RestaurantActivity.this);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            recyclerView.setLayoutParams(params);
            recyclerView.setLayoutManager(new LinearLayoutManager(RestaurantActivity.this, LinearLayoutManager.VERTICAL, false));
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            final ImageView loadingImage = new ImageView(RestaurantActivity.this);
            LinearLayout.LayoutParams loadSize = new LinearLayout.LayoutParams((int) (width * 0.25), (int) (width * 0.25));
            loadingImage.setLayoutParams(loadSize);
            loadingImage.post(new Runnable() {
                @Override
                public void run() {
                    loadingImage.setX((float) (width * 0.75 / 2));
                    loadingImage.setY((float) (pagerHeight - width * 0.25) / 2);
                }
            });


            Drawable loadingDrawable = getDrawable(R.drawable.anim_loading);
            loadingImage.setBackground(loadingDrawable);

            AnimationDrawable drawable = (AnimationDrawable) loadingImage.getBackground();
            animationDrawables.add(drawable);



            ImageView noReviewImage = new ImageView(RestaurantActivity.this);
            noReviewImage.setImageResource(R.drawable.no_review_image);
            noReviewImage.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
            noReviewImage.setVisibility(View.GONE);

            LinearLayout linearLayout = new LinearLayout(RestaurantActivity.this);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            linearLayout.setLayoutParams(params1);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            linearLayout.addView(loadingImage);
            linearLayout.addView(recyclerView);
            linearLayout.addView(noReviewImage);

            viewList.add(linearLayout);
        }

        View view = getLayoutInflater().inflate(R.layout.restaurant_info_layout, null);
        viewList.add(view);
    }

    private void setPage(ArrayList<View> viewList, final int tabPosition) {
        if (tabPosition == 0) {
            ((RecyclerView) ((LinearLayout) viewList.get(tabPosition)).getChildAt(1)).setAdapter(new MenuAdapter(contentStore, RestaurantActivity.this, restaurantName));

        } else if (tabPosition == 1) {
            ((RecyclerView) ((LinearLayout) viewList.get(tabPosition)).getChildAt(1)).setAdapter(new TotalReviewAdapter(contentStore.getReviewList(), RestaurantActivity.this));
        }
    }

    private void setViewPager() {

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(RestaurantActivity.this, viewList);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.addTab(tabLayout.newTab().setText("메뉴별 리뷰"));
        tabLayout.addTab(tabLayout.newTab().setText("전체 리뷰"));
        tabLayout.addTab(tabLayout.newTab().setText("위치"));
        connectHttp(0, "메뉴별 리뷰", firstConnect);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                connectHttp(tab.getPosition(), tab.getText().toString(), firstConnect);
                TAB = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final double x = infoVO.getX();
        final double y = infoVO.getY();
        LatLng Point = new LatLng(x, y);

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(Point);
        googleMap.addMarker(markerOptions);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(Point));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(RestaurantActivity.this, LocationInfo.class);
                intent.putExtra("x", x);
                intent.putExtra("y", y);
                startActivity(intent);
                return true;
            }
        });
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(RestaurantActivity.this, LocationInfo.class);
                intent.putExtra("x", x);
                intent.putExtra("y", y);
                startActivity(intent);
            }
        });
    }


}