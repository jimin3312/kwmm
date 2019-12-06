package com.kwmm0.Notice;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kwmm0.R;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    private ArrayList<ContentVO> listVOArrayNotice = new ArrayList<ContentVO>();
    private Context currentContext;

    public ListAdapter(Context currentContext)
    {
        this.currentContext = currentContext;
    }
    @Override
    public int getCount() {
        return listVOArrayNotice.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if(convertView ==null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.notice_item_layout, parent, false);
        }

        TextView title =(TextView)convertView.findViewById(R.id.notice_item_txt_title);
        TextView date = (TextView)convertView.findViewById(R.id.notice_item_txt_date);
        final ContentVO contentVO = listVOArrayNotice.get(position);


        //아이템 설정
        title.setText(contentVO.getTitle());
        date.setText(contentVO.getTime());

        convertView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(currentContext, NoticeContentActivity.class);
                intent.putExtra("title", contentVO.getTitle());
                intent.putExtra("time", contentVO.getTime());
                intent.putExtra("id", contentVO.getId());
                currentContext.startActivity(intent);

            }
        });

        return convertView;
    }
    public void addVO(String title, String date, String id)
    {
        ContentVO listItem= new ContentVO();
        listItem.setTitle(title);
        listItem.setTime(date);
        listItem.setId(id);
        listVOArrayNotice.add(listItem);
    }
}
