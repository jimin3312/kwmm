package com.kwmm0.Restaurant;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kwmm0.R;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<MenuVO> list;
    private Context context;
    private String restaurantName;
    private ContentStore contentStore;

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    public class HeadViewHolder extends RecyclerView.ViewHolder{
        public TextView category;
        public ImageView expandToggleImage;

        public HeadViewHolder(View itemView) {
            super(itemView);
            this.category = (TextView)itemView.findViewById(R.id.txt_menu_category_head);
            this.expandToggleImage = (ImageView)itemView.findViewById(R.id.img_menu_category);
        }
    }

    public class ChildViewHolder extends  RecyclerView.ViewHolder{
        public TextView name, rate, price;
        public ImageView star;

        public ChildViewHolder(View itemView) {
            super(itemView);
            this.name = (TextView)itemView.findViewById(R.id.txt_menu_name);
            this.rate = (TextView)itemView.findViewById(R.id.txt_menu_rate);
            this.price = (TextView)itemView.findViewById(R.id.txt_menu_price);
            this.star = (ImageView)itemView.findViewById(R.id.img_menu_star);

        }
    }
    public MenuAdapter(ContentStore contentStore, Context context, String restaurantName)
    {
        this.contentStore = contentStore;
        this.list= contentStore.getMenuList();
        this.context=context;
        this.restaurantName = restaurantName;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == MenuVO.HEAD){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.restaurant_menu_head_layout, parent, false);

            RecyclerView.ViewHolder viewHolder = new MenuAdapter.HeadViewHolder(view);
            return viewHolder;
        }else{
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.restaurant_menu_child_layout, parent, false);

            RecyclerView.ViewHolder viewHolder = new MenuAdapter.ChildViewHolder(view);
            return viewHolder;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MenuVO menuList = list.get(position);

        if(menuList.getType() == MenuVO.HEAD){
            final HeadViewHolder headViewHolder = (HeadViewHolder)holder;
            headViewHolder.category.setText(((MenuHeadVO)list.get(position)).getCategory());
            if(((MenuHeadVO)menuList).getMenuChildList().size() == 0){
                headViewHolder.expandToggleImage.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            }else{
                headViewHolder.expandToggleImage.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            }
            headViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(((MenuHeadVO)menuList).getMenuChildList().size() == 0){
                        int count = 0;
                        while(list.size() > position+1 && list.get(position + 1).getType() == MenuVO.CHILD){
                            ((MenuHeadVO)menuList).getMenuChildList().add(list.remove(position + 1));
                            count++;
                        }
                        notifyDataSetChanged();
                        headViewHolder.expandToggleImage.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    }else{
                        int count = position + 1;

                        for(MenuVO child : ((MenuHeadVO) menuList).getMenuChildList()){
                            list.add(count, child);
                            count++;
                        }
                        notifyDataSetChanged();
                        headViewHolder.expandToggleImage.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                        ((MenuHeadVO) menuList).getMenuChildList().clear();
                    }
                }
            });
        }else {
            ChildViewHolder childViewHolder = (ChildViewHolder)holder;
            MenuChildVO menuChildVO = (MenuChildVO) list.get(position);

            childViewHolder.name.setText(menuChildVO.getName());
            childViewHolder.price.setText(menuChildVO.getPrice());
            childViewHolder.rate.setText(menuChildVO.getRate());

            childViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contentStore.setClickPosition(position);
                    Intent intent = new Intent(context, MenuReview.class);
                    intent.putExtra("restaurantName", restaurantName);
                    intent.putExtra("menu", ((MenuChildVO)list.get(position)).getName());
                    intent.putExtra("menuId", ((MenuChildVO)list.get(position)).getMenuId());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}