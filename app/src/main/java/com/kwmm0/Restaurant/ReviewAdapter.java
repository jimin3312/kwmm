package com.kwmm0.Restaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kwmm0.CallBackInterface.HttpCallBack;
import com.kwmm0.HttpConnection;
import com.kwmm0.ImageExpandActivity;
import com.kwmm0.Login.LoginActivity;
import com.kwmm0.R;
import com.kwmm0.WriteReviewActivity;
import com.kwmm0.ShowDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.CustomViewHolder> {
    private ArrayList<ReviewVO> list;
    private Context context;
    int VIEW_TYPE_FIRST = 0;
    int VIEW_TYPE_CELL = 1;
    private int menuId;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public SharedPreferences sharedPreferences;

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_FIRST : VIEW_TYPE_CELL;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView nickname, time, contents, report, recentOrder, rateOrder, thumbUpNumber, menu, restaurant;
        public ImageView image, imageProfile,thumUp;
        public String url, profileUrl;
        public Button submitButton;
        public RatingBar rating;

        public CustomViewHolder(View itemView, int ViewType) {
            super(itemView);
            if (ViewType == VIEW_TYPE_CELL) {
                this.menu = (TextView)itemView.findViewById(R.id.txt_review_menu);
                this.imageProfile = (ImageView) itemView.findViewById(R.id.img_review_profile);
                this.nickname = (TextView) itemView.findViewById(R.id.txt_review_nickname);
                this.time = (TextView) itemView.findViewById(R.id.txt_review_time);
                this.rating = (RatingBar) itemView.findViewById(R.id.review_rating);
                this.contents = (TextView) itemView.findViewById(R.id.txt_review_contents);
                this.image = (ImageView) itemView.findViewById(R.id.img_review_image);
                this.report = (TextView) itemView.findViewById(R.id.txt_review_report);
                this.thumbUpNumber = (TextView) itemView.findViewById(R.id.txt_review_thum_up_number);
                this.thumUp = (ImageView)itemView.findViewById(R.id.review_img_thum_up);
                this.restaurant = (TextView)itemView.findViewById(R.id.txt_review_restaurant);
            } else {
                this.submitButton = (Button) itemView.findViewById(R.id.review_btn_submit);
                this.recentOrder = (TextView) itemView.findViewById(R.id.txt_recent_order);
                this.rateOrder = (TextView) itemView.findViewById(R.id.txt_rate_order);
            }
        }
    }

    public ReviewAdapter(ArrayList<ReviewVO> list, Context context, int menuId) {
        this.menuId = menuId;
        this.context = context;
        this.list = list;
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
    }

    @Override
    public ReviewAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CELL) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.restaurant_review_layout, parent, false);

            ReviewAdapter.CustomViewHolder viewHolder = new ReviewAdapter.CustomViewHolder(view, VIEW_TYPE_CELL);

            return viewHolder;
        } else {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.restaurant_review_top_item_layout, parent, false);

            ReviewAdapter.CustomViewHolder viewHolder = new ReviewAdapter.CustomViewHolder(view, VIEW_TYPE_FIRST);

            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(final ReviewAdapter.CustomViewHolder holder, int position) {
        if (position != 0) {
            position--;
            holder.url = list.get(position).getUrl();
            holder.profileUrl = list.get(position).getProfileUrl();
            holder.nickname.setText(list.get(position).getNickname());
            try {
                Date date = dateFormat.parse(list.get(position).getTime());
                Long time = date.getTime();
                Long currentMinusReviewTime = (System.currentTimeMillis()-time)/1000;

                String s;
                if(currentMinusReviewTime >= 2419200){
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                    s = simpleDateFormat.format(new Date(time));
                }else if(currentMinusReviewTime >= 604800){
                    s = currentMinusReviewTime/604800 + "주 전";
                }else if(currentMinusReviewTime >= 86400)
                {
                    s = currentMinusReviewTime/86400 + "일 전";
                }else if(currentMinusReviewTime >= 3600)
                {
                    s = currentMinusReviewTime/3600 + "시간 전";
                }else if(currentMinusReviewTime >= 60){
                    s = currentMinusReviewTime/60 + "분 전";
                }else {
                    s = "1분 미만";
                }
                holder.time.setText(s);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.rating.setRating(Float.parseFloat(list.get(position).getRate()));
            holder.contents.setText(list.get(position).getContents());
            holder.thumbUpNumber.setText(list.get(position).getThumbUpNumber());

            if(list.get(position).getIsRecommended()==1)
            {
                holder.thumUp.setImageResource(R.drawable.thum_up_color);
            } else
            {
                holder.thumUp.setImageResource(R.drawable.thumb_up_ff7500);
            }
            if (!holder.profileUrl.equals(HttpConnection.profileURL)) {

                Glide.with(context)
                        .load(holder.profileUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.imageProfile);
            }else {
                holder.imageProfile.setImageResource(R.mipmap.default_person_round);
            }

            if (!holder.url.equals(HttpConnection.reviewURL)) {
                holder.image.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(holder.url)
                        .into(holder.image);
            } else {
                holder.image.setVisibility(View.GONE);
            }
            final int finalPosition = position;


            if(!list.get(position).getNickname().equals(sharedPreferences.getString("nickname","null")))
            {
                holder.thumUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("func","recommend");
                            jsonObject.put("reviewId",list.get(finalPosition).getReviewId());
                            jsonObject.put("id",sharedPreferences.getString("id","null"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new HttpConnection(context, new HttpCallBack() {
                            @Override
                            public void callBack(JSONObject jsonObject) {
                                String result = null;
                                try {
                                    result = jsonObject.getString("data");
                                    if(result.equals("recommended"))
                                    {
                                        list.get(finalPosition).setIsRecommended(1);
                                        list.get(finalPosition).setThumbUpNumber(String.valueOf(Integer.valueOf(list.get(finalPosition).getThumbUpNumber())+1));
                                        holder.thumUp.setImageResource(R.drawable.thum_up_color);
                                        holder.thumbUpNumber.setText(list.get(finalPosition).getThumbUpNumber());
                                    } else if(result.equals("unrecommended"))
                                    {
                                        list.get(finalPosition).setIsRecommended(0);
                                        list.get(finalPosition).setThumbUpNumber(String.valueOf(Integer.valueOf(list.get(finalPosition).getThumbUpNumber())-1));
                                        holder.thumUp.setImageResource(R.drawable.thumb_up_ff7500);
                                        holder.thumbUpNumber.setText(list.get(finalPosition).getThumbUpNumber());
                                    } else{
                                        new ShowDialog(context).show("알림","인터넷 확인");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).execute(jsonObject);
                    }
                });
                holder.report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);

                        if (sharedPreferences.getString("login", "3").equals("1")) {
                            new ShowDialog(context).report(list.get(finalPosition).getReviewId(), sharedPreferences.getString("id", "null"));

                        } else {
                            context.startActivity(new Intent(context, LoginActivity.class));
                        }
                    }
                });
            } else{
                holder.report.setVisibility(View.INVISIBLE);
            }

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImageExpandActivity.class);
                    intent.putExtra("image", holder.url);
                    context.startActivity(intent);
                }
            });

            holder.menu.setVisibility(View.GONE);
            holder.restaurant.setVisibility(View.GONE);
        } else {
            holder.submitButton.setText("리뷰 작성");
            holder.submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);

                    if (sharedPreferences.getString("login", "3").equals("1") || sharedPreferences.getString("login","3").equals("2")) {
                        Intent intent = new Intent(context, WriteReviewActivity.class);
                        intent.putExtra("menuId", menuId);
                        context.startActivity(intent);
                    } else {
                        context.startActivity(new Intent(context, LoginActivity.class));
                    }

                }
            });
            holder.rateOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.rateOrder.setTextColor(Color.rgb(0xFF,0x75,0x00));
                    holder.rateOrder.setTypeface(null, Typeface.BOLD);
                    holder.recentOrder.setTextColor(Color.rgb(0,0,0));
                    holder.recentOrder.setTypeface(null, Typeface.NORMAL);
                    Collections.sort(list);
                    notifyDataSetChanged();
                }
            });
            holder.recentOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.rateOrder.setTextColor(Color.rgb(0,0,0));
                    holder.rateOrder.setTypeface(null, Typeface.NORMAL);
                    holder.recentOrder.setTextColor(Color.rgb(0xFF,0x75,0x00));
                    holder.recentOrder.setTypeface(null, Typeface.BOLD);

                    sortByDate(list);
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    public void sortByDate(ArrayList<ReviewVO> reviews) {
        ReviewVO temp;
        Date date1, date2;

        for (int i = 0; i < reviews.size() - 1; i++) {
            for (int j = i + 1; j < reviews.size(); j++) {
                try {
                    date1 = dateFormat.parse(reviews.get(i).getTime());
                    date2 = dateFormat.parse(reviews.get(j).getTime());
                    if (date1.getTime() < date2.getTime()) {
                        temp = reviews.get(i);
                        reviews.set(i, reviews.get(j));
                        reviews.set(j, temp);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}