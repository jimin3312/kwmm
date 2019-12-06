package com.kwmm0.MyReview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kwmm0.CallBackInterface.ConfirmCallBack;
import com.kwmm0.HttpConnection;
import com.kwmm0.ImageExpandActivity;
import com.kwmm0.R;
import com.kwmm0.Restaurant.ReviewVO;
import com.kwmm0.ShowDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder> {
    private ArrayList<ReviewVO> myReviews;
    private Context context;
    private RecyclerView recyclerView;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public RecyclerViewAdapter(Context context, ArrayList<ReviewVO> myReviews, RecyclerView recyclerView) {
        this.context = context;
        this.myReviews = myReviews;
        this.recyclerView = recyclerView;
    }

    public class CustomViewHolder extends  RecyclerView.ViewHolder{

        public TextView nickname, time, contents, delete, thumUpNumber, menu, restaurant;
        public ImageView image, imageProfile;
        public String url,profileUrl;
        public RatingBar rate;

        public CustomViewHolder(View itemView) {
            super(itemView);
            this.imageProfile = (ImageView) itemView.findViewById(R.id.img_review_profile);
            this.nickname = (TextView) itemView.findViewById(R.id.txt_review_nickname);
            this.time = (TextView) itemView.findViewById(R.id.txt_review_time);
            this.rate = (RatingBar) itemView.findViewById(R.id.review_rating);
            this.contents = (TextView) itemView.findViewById(R.id.txt_review_contents);
            this.image = (ImageView) itemView.findViewById(R.id.img_review_image);
            this.delete = (TextView) itemView.findViewById(R.id.txt_review_report);
            this.thumUpNumber = (TextView) itemView.findViewById(R.id.txt_review_thum_up_number);
            this.menu = (TextView)itemView.findViewById(R.id.txt_review_menu);
            this.restaurant = (TextView)itemView.findViewById(R.id.txt_review_restaurant);
        }
    }

    @Override
    public RecyclerViewAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_review_layout, parent, false);

        RecyclerViewAdapter.CustomViewHolder viewHolder = new RecyclerViewAdapter.CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.CustomViewHolder holder, final int position) {
        holder.url = myReviews.get(position).getUrl();
        holder.profileUrl = myReviews.get(position).getProfileUrl();
        holder.nickname.setText(myReviews.get(position).getNickname());
        holder.rate.setRating(Float.parseFloat(myReviews.get(position).getRate()));
        holder.contents.setText(myReviews.get(position).getContents());
        holder.thumUpNumber.setText(myReviews.get(position).getThumbUpNumber());
        try {
            Date date = dateFormat.parse(myReviews.get(position).getTime());
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

        if (!holder.url.equals(HttpConnection.reviewURL)) {

            holder.image.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(holder.url)
                    .into(holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
        }

        if (!holder.profileUrl.equals(HttpConnection.profileURL)) {
            Glide.with(context)
                    .load(holder.profileUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.imageProfile);
        }else {
            holder.imageProfile.setImageResource(R.mipmap.default_person_round);
        }

        holder.delete.setText("삭제");
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("func", "review delete");
                    jsonObject.put("reviewId", myReviews.get(position).getReviewId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new ShowDialog(context, new ConfirmCallBack() {
                    @Override
                    public void callBack(Boolean isDelete) {
                        if(isDelete)
                            update(position);
                    }
                }).confirmDelete(jsonObject);
            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(context, ImageExpandActivity.class);
                intent.putExtra("image", holder.url);
                context.startActivity(intent);
            }
        });

        holder.menu.setText(myReviews.get(position).getMenu());

        holder.restaurant.setText(myReviews.get(position).getRestaurantName());
    }

    @Override
    public int getItemCount() {
        return myReviews.size();
    }
    public void update(int position){
        myReviews.remove(position);
        recyclerView.removeViewAt(position);
        this.notifyItemRemoved(position);
        this.notifyItemRangeChanged(position, myReviews.size());
    }
}