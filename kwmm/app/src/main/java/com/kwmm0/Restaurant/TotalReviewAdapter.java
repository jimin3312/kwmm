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
import com.kwmm0.ShowDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class TotalReviewAdapter extends RecyclerView.Adapter<TotalReviewAdapter.CustomViewHolder>{

    private ArrayList<ReviewVO> reviewList;
    private Context context;
    private SharedPreferences sharedPreferences;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    int VIEW_TYPE_FIRST = 0;
    int VIEW_TYPE_CELL = 1;

    public TotalReviewAdapter(ArrayList<ReviewVO> reviewList, Context context){
        this.reviewList = reviewList;
        this.context = context;
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_FIRST : VIEW_TYPE_CELL;
    }

    @Override
    public TotalReviewAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CELL) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.restaurant_review_layout, parent, false);

            TotalReviewAdapter.CustomViewHolder viewHolder = new TotalReviewAdapter.CustomViewHolder(view, VIEW_TYPE_CELL);

            return viewHolder;
        } else {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.restaurant_review_top_item_layout, parent, false);

            TotalReviewAdapter.CustomViewHolder viewHolder = new TotalReviewAdapter.CustomViewHolder(view, VIEW_TYPE_FIRST);

            return viewHolder;
        }
    }
    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {

        if (position != 0) {
            position--;
            holder.nickname.setText(reviewList.get(position).getNickname());
            try {
                Date date = dateFormat.parse(reviewList.get(position).getTime());
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
            holder.rating.setRating(Float.parseFloat(reviewList.get(position).getRate()));
            holder.contents.setText(reviewList.get(position).getContents());
            holder.thumbUpNumber.setText(reviewList.get(position).getThumbUpNumber());

            if(reviewList.get(position).getIsRecommended()==1)
            {
                holder.thumUp.setImageResource(R.drawable.thum_up_color);
            } else
            {
                holder.thumUp.setImageResource(R.drawable.thumb_up_ff7500);
            }

            if (!reviewList.get(position).getProfileUrl().equals(HttpConnection.profileURL)) {
                Glide.with(context)
                        .load(reviewList.get(position).getProfileUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.imageProfile);
            }else {
                holder.imageProfile.setImageResource(R.mipmap.default_person_round);
            }

            if (!reviewList.get(position).getUrl().equals(HttpConnection.reviewURL)) {
                holder.imageReview.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(reviewList.get(position).getUrl())
                        .into(holder.imageReview);
            } else {
                holder.imageReview.setVisibility(View.GONE);
            }
            final int finalPosition = position;


            if(!reviewList.get(position).getNickname().equals(sharedPreferences.getString("nickname","null")))
            {
                holder.thumUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("func","recommend");
                            jsonObject.put("reviewId", reviewList.get(finalPosition).getReviewId());
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
                                        reviewList.get(finalPosition).setIsRecommended(1);
                                        reviewList.get(finalPosition).setThumbUpNumber(String.valueOf(Integer.valueOf(reviewList.get(finalPosition).getThumbUpNumber())+1));
                                        holder.thumUp.setImageResource(R.drawable.thum_up_color);
                                        holder.thumbUpNumber.setText(reviewList.get(finalPosition).getThumbUpNumber());
                                    } else if(result.equals("unrecommended"))
                                    {
                                        reviewList.get(finalPosition).setIsRecommended(0);
                                        reviewList.get(finalPosition).setThumbUpNumber(String.valueOf(Integer.valueOf(reviewList.get(finalPosition).getThumbUpNumber())-1));
                                        holder.thumUp.setImageResource(R.drawable.thumb_up_ff7500);
                                        holder.thumbUpNumber.setText(reviewList.get(finalPosition).getThumbUpNumber());
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
                            new ShowDialog(context).report(reviewList.get(finalPosition).getReviewId(), sharedPreferences.getString("id", "null"));

                        } else {
                            context.startActivity(new Intent(context, LoginActivity.class));
                        }
                    }
                });
            } else{
                holder.report.setVisibility(View.INVISIBLE);
            }

            holder.imageReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImageExpandActivity.class);
                    intent.putExtra("image", reviewList.get(finalPosition).getUrl());
                    context.startActivity(intent);
                }
            });

            holder.menu.setText(reviewList.get(position).getMenu());
            holder.restaurant.setVisibility(View.GONE);
        } else {
            holder.rateOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.rateOrder.setTextColor(Color.rgb(0xFF,0x75,0x00));
                    holder.rateOrder.setTypeface(null, Typeface.BOLD);
                    holder.recentOrder.setTextColor(Color.rgb(0,0,0));
                    holder.recentOrder.setTypeface(null, Typeface.NORMAL);
                    Collections.sort(reviewList);
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

                    sortByDate(reviewList);
                    notifyDataSetChanged();
                }
            });
            holder.submit.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size()+1;
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageProfile, imageReview, thumUp;
        public TextView nickname, time, contents, report, thumbUpNumber, recentOrder,rateOrder, menu, restaurant;
        public RatingBar rating;
        public Button submit;

        public CustomViewHolder(View itemView,int viewType) {
            super(itemView);

            if(viewType == VIEW_TYPE_FIRST ){
                    this.recentOrder = (TextView) itemView.findViewById(R.id.txt_recent_order);
                    this.rateOrder = (TextView) itemView.findViewById(R.id.txt_rate_order);
                    this.submit = itemView.findViewById(R.id.review_btn_submit);
            } else {
                this.restaurant = (TextView)itemView.findViewById(R.id.txt_review_restaurant);
                this.menu = (TextView)itemView.findViewById(R.id.txt_review_menu);
                this.imageProfile = (ImageView) itemView.findViewById(R.id.img_review_profile);
                this.nickname = (TextView) itemView.findViewById(R.id.txt_review_nickname);
                this.time = (TextView) itemView.findViewById(R.id.txt_review_time);
                this.rating = (RatingBar) itemView.findViewById(R.id.review_rating);
                this.contents = (TextView) itemView.findViewById(R.id.txt_review_contents);
                this.imageReview = (ImageView) itemView.findViewById(R.id.img_review_image);
                this.report = (TextView) itemView.findViewById(R.id.txt_review_report);
                this.thumbUpNumber = (TextView) itemView.findViewById(R.id.txt_review_thum_up_number);
                this.thumUp = (ImageView)itemView.findViewById(R.id.review_img_thum_up);
            }
        }
    }
    public void sortByDate(ArrayList<ReviewVO> reviews) {
        ReviewVO temp;
        Date date1, date2;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
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


