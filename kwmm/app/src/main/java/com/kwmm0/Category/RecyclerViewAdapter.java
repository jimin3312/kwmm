//package com.example.jade3.kw_food.Category;
//
//import android.content.Context;
//import android.content.Intent;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.example.jade3.kw_food.R;
//import com.example.jade3.kw_food.Restaurant.RestaurantActivity;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//
//import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
//import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
//
//public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder> {
//
//    private ArrayList<ContentVO> restaurantList;
//    private Context context;
//    private RecyclerView recyclerView;
//    public class CustomViewHolder extends  RecyclerView.ViewHolder{
//        public TextView name, score;
//        public ImageView arrow;
//
//        public CustomViewHolder(View itemView) {
//            super(itemView);
//            this.name = (TextView)itemView.findViewById(R.id.txt_category_item);
//            this.score = (TextView)itemView.findViewById(R.id.txt_category_item2);
//            this.arrow = (ImageView) itemView.findViewById(R.id.img_arrow);
//        }
//    }
//    public RecyclerViewAdapter(ArrayList<ContentVO> list, Context context, RecyclerView recyclerView)
//    {
//        this.restaurantList=list;
//        this.context=context;
//        //this.recyclerView= recyclerView;
//    }
//
//    @Override
//    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.category_item_layout, parent, false);
//
//        CustomViewHolder viewHolder = new CustomViewHolder(view);
//
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(CustomViewHolder holder, final int position) {
//
//        holder.name.setText(restaurantList.get(position).getName());
//        holder.score.setText(restaurantList.get(position).getScore());
//        holder.itemView.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, RestaurantActivity.class);
//                intent.putExtra("name", restaurantList.get(position).getName());
//                context.startActivity(intent);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return restaurantList.size();
//    }
//
//    public void setRestaurantList(ArrayList<ContentVO> list)
//    {
//        restaurantList = list;
//    }
//
//
//}

package com.kwmm0.Category;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kwmm0.R;
import com.kwmm0.Restaurant.RestaurantActivity;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder> {

    private ArrayList<ContentVO> restaurantList;
    private Context context;
    private ContentStore contentStore;

    public class CustomViewHolder extends  RecyclerView.ViewHolder{
        public TextView name, score, total, reviewNumber;
        public ImageView star;

        public CustomViewHolder(View itemView) {
            super(itemView);
            this.name = (TextView)itemView.findViewById(R.id.txt_category_restaurant_title);
            this.score = (TextView)itemView.findViewById(R.id.txt_category_rate);
            this.reviewNumber = (TextView) itemView.findViewById(R.id.txt_category_total_review);
            this.total = (TextView) itemView.findViewById(R.id.txt_category_total);
            this.star = (ImageView) itemView.findViewById(R.id.img_category_star);
        }
    }
    public RecyclerViewAdapter(ContentStore contentStore, ArrayList<ContentVO> list, Context context)
    {
        this.contentStore = contentStore;
        this.restaurantList =list;
        this.context=context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item_layout, parent, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {

        holder.name.setText(restaurantList.get(position).getName());
        holder.score.setText(restaurantList.get(position).getScore());
        holder.reviewNumber.setText(String.valueOf(restaurantList.get(position).getReviewNumber()));
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RestaurantActivity.class);
                intent.putExtra("name", restaurantList.get(position).getName());
                intent.putExtra("rating", restaurantList.get(position).getScore());
                context.startActivity(intent);
                contentStore.setClickedRestaurantName(restaurantList.get(position).getName());
                contentStore.setClikedRate(restaurantList.get(position).getScore());
                contentStore.setClikedReviewNumber(String.valueOf(restaurantList.get(position).getReviewNumber()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public void setRestaurantList(ArrayList<ContentVO> restaurantList)
    {
        this.restaurantList = restaurantList;
    }


}