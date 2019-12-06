package com.kwmm0.Category;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kwmm0.CallBackInterface.HttpCallBack;
import com.kwmm0.HttpConnection;
import com.kwmm0.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by jade3 on 2018-11-14.
 */

public class CategoryActivity extends AppCompatActivity {

    private HorizontalScrollView CategoryHorizontalScrollView;
    private ArrayList<View> viewList;
    private ArrayList<AnimationDrawable> animationDrawables;
    private RecyclerViewAdapter adapter;
    private LinearLayout categoryLinearLayout;
    private TextView previousChildPosition = null;
    private ViewPager viewPager;
    private ContentStore contentStore;
    private AppBarLayout appBarLayout;
    private String category = "한식";
    private int categoryPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_bar_layout);
        getWindow().setStatusBarColor(Color.rgb(255, 117, 0));

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float width = size.x;
        float height = size.y;

        findViewFromXML();
        makeView(width, height);
        setViewPage();
        setInitPage();

    }

    private void setInitPage() {
        contentStore = new ContentStore();
        adapter = new RecyclerViewAdapter(contentStore, null, this);

        final int n = Integer.parseInt(getIntent().getStringExtra("category"));
        CategoryHorizontalScrollView.post(new Runnable() {
            @Override
            public void run() {
                if (n == 0) {
                    animationDrawables.get(0).start();
                    setScrollViewCenter(categoryLinearLayout.getChildAt(n), n);
                    return;
                }
                viewPager.setCurrentItem(n);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart(){
        super.onRestart();

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("func", "categoryList");
            jsonObject.put("category", category);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new HttpConnection(CategoryActivity.this, new HttpCallBack() {
            @Override
            public void callBack(JSONObject resultJSON) {
                JSONArray categoryJsonArray;
                try {
                    String rate = "", reviewNumber = "";
                    categoryJsonArray = resultJSON.getJSONArray("list");
                    for(int i=0; i<categoryJsonArray.length(); i++){
                       String restaurantName = categoryJsonArray.getJSONObject(i).getString("dinnerName");
                       if(restaurantName.equals(contentStore.getClickedRestaurantName())){
                           rate = categoryJsonArray.getJSONObject(i).getString("rate");
                           reviewNumber = String.valueOf(categoryJsonArray.getJSONObject(i).getInt("numberOfRate"));
                           break;
                       }
                    }
                    if((!rate.equals(contentStore.getClikedRate())) || (!reviewNumber.equals(contentStore.getClikedReviewNumber()))){
                        contentStore.clear(category);
                        contentStore.addItem(categoryJsonArray, resultJSON.getString("categoryName"));
                        updateList(categoryPosition);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute(jsonObject);
    }

    private void setViewPage() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(CategoryActivity.this, viewList);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setScrollViewCenter(categoryLinearLayout.getChildAt(position), position);
                categoryPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void makeView(final float width, final float height) {
        animationDrawables = new ArrayList<>();
        appBarLayout.measure(0,0);
        float appbarBarHeight = appBarLayout.getMeasuredHeight();
        CategoryHorizontalScrollView.measure(0, 0);
        final float pagerHeight = height - CategoryHorizontalScrollView.getMeasuredHeight()-appbarBarHeight;

        viewList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            RecyclerView recyclerView = new RecyclerView(CategoryActivity.this);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            recyclerView.setLayoutParams(params);
            recyclerView.setLayoutManager(new LinearLayoutManager(CategoryActivity.this, LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter);
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

            final ImageView loadingImage = new ImageView(CategoryActivity.this);
            LinearLayout.LayoutParams loadSize = new LinearLayout.LayoutParams((int)(width*0.25), (int)(width*0.25));
            loadingImage.setLayoutParams(loadSize);
            loadingImage.post(new Runnable() {
                @Override
                public void run() {
                    loadingImage.setX((float)(width*0.75/2));
                    loadingImage.setY((float)(pagerHeight- width*0.25)/2);
                }
            });

            Drawable loadingDrawable = getDrawable(R.drawable.anim_loading);
            loadingImage.setBackground(loadingDrawable);

            AnimationDrawable drawable = (AnimationDrawable) loadingImage.getBackground();
            animationDrawables.add(drawable);

            LinearLayout linearLayout = new LinearLayout(CategoryActivity.this);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            linearLayout.setLayoutParams(params1);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.addView(loadingImage);
            linearLayout.addView(recyclerView);
            viewList.add(linearLayout);

        }
    }

    private void findViewFromXML() {
        categoryLinearLayout = findViewById(R.id.horizontal_categoryList_linearLayout);
        CategoryHorizontalScrollView = findViewById(R.id.category_horizontal_view);
        viewPager = findViewById(R.id.viewPager);
        appBarLayout = findViewById(R.id.category_appbar);
    }


    public void onClick(View view) {
        for (int i = 0; i < categoryLinearLayout.getChildCount(); ++i) {
            if (view == categoryLinearLayout.getChildAt(i))
                viewPager.setCurrentItem(i);
        }
        category = ((TextView) view).getText().toString();
    }

    public void setScrollViewCenter(View view, final int categoryLabel) {

        int screenWidth = CategoryHorizontalScrollView.getWidth();
        int scrollX = view.getLeft() + (view.getWidth() / 2) - (screenWidth / 2);

        CategoryHorizontalScrollView.smoothScrollTo(scrollX, 0);

        if (previousChildPosition != null) {
            previousChildPosition.setTypeface(null, Typeface.NORMAL);
            ((TextView) view).setTypeface(null, Typeface.BOLD);
            previousChildPosition = (TextView) view;
        } else {
            ((TextView) view).setTypeface(null, Typeface.BOLD);
            previousChildPosition = (TextView) view;
        }
        if (contentStore.isEmpty(((TextView) view).getText().toString())) {

            ((LinearLayout) viewList.get(categoryLabel)).getChildAt(1).setVisibility(View.GONE);
            ((LinearLayout) viewList.get(categoryLabel)).getChildAt(0).setVisibility(View.VISIBLE);
            animationDrawables.get(categoryLabel).start();
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("func", "categoryList");
                jsonObject.put("category", ((TextView) view).getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            new HttpConnection(CategoryActivity.this, new HttpCallBack() {
                @Override
                public void callBack(JSONObject resultJSON) {
                    JSONArray categoryJsonArray;
                    try {
                        categoryJsonArray = resultJSON.getJSONArray("list");
                        contentStore.addItem(categoryJsonArray, resultJSON.getString("categoryName"));

                        animationDrawables.get(categoryLabel).stop();

                        ((LinearLayout) viewList.get(categoryLabel)).getChildAt(0).setVisibility(View.GONE);
                        ((LinearLayout) viewList.get(categoryLabel)).getChildAt(1).setVisibility(View.VISIBLE);
                        updateList(categoryLabel);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute(jsonObject);
        } else {
            updateList(categoryLabel);
        }
    }

    /*category label
    0.한식 1.중식 2.일식 3.분식 4.면 5.햄버거/피자 6.치킨 7.디저트 */
    public void updateList(int categoryLabel) {
            switch (categoryLabel) {
                case 0:
                    adapter.setRestaurantList(contentStore.getKoreanDish());
                    ((LinearLayout) viewList.get(categoryLabel)).getChildAt(0).setVisibility(View.GONE);
                    ((RecyclerView)((LinearLayout) viewList.get(0)).getChildAt(1)).setAdapter(adapter);
                    break;
                case 1:
                    adapter.setRestaurantList(contentStore.getChineseDish());
                    ((RecyclerView)((LinearLayout) viewList.get(1)).getChildAt(1)).setAdapter(adapter);
                    break;
                case 2:
                    adapter.setRestaurantList(contentStore.getJapaneseDish());
                    ((RecyclerView)((LinearLayout) viewList.get(2)).getChildAt(1)).setAdapter(adapter);
                    break;
                case 3:
                    adapter.setRestaurantList(contentStore.getSnackBar());
                    ((RecyclerView)((LinearLayout) viewList.get(3)).getChildAt(1)).setAdapter(adapter);
                    break;
                case 4:
                    adapter.setRestaurantList(contentStore.getNoodle());
                    ((RecyclerView)((LinearLayout) viewList.get(4)).getChildAt(1)).setAdapter(adapter);
                    break;
                case 5:
                    adapter.setRestaurantList(contentStore.getFastFood());
                    ((RecyclerView)((LinearLayout) viewList.get(5)).getChildAt(1)).setAdapter(adapter);
                    break;
                case 6:
                    adapter.setRestaurantList(contentStore.getChicken());
                    ((RecyclerView)((LinearLayout) viewList.get(6)).getChildAt(1)).setAdapter(adapter);
                    break;
                case 7:
                    adapter.setRestaurantList(contentStore.getDessert());
                    ((RecyclerView)((LinearLayout) viewList.get(7)).getChildAt(1)).setAdapter(adapter);
                    break;

                default:
                    break;
            }


    }

}