package com.example.ddvoice;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ddvoice.OnlineSpeechAction.SiriListItem;
import com.gc.materialdesign.views.LayoutRipple;

public class ChatMsgViewAdapter extends BaseAdapter {
    int backgroundColor = Color.parseColor("#1E88E5");
    private ArrayList<SiriListItem> list;
    private Context ctx;
    private LayoutInflater mInflater;
  
    public ChatMsgViewAdapter(Context context, ArrayList<SiriListItem> l) {
        ctx = context;
        list = l;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getItemViewType(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
       Activity context = (Activity)ctx;
    	ViewHolder viewHolder = null;
    	SiriListItem  item=list.get(position);
        if(position == 0 && context instanceof MainActivity){
                convertView = mInflater.inflate(R.layout.main_ui,null);
                LayoutRipple layoutRipple = (LayoutRipple)convertView.findViewById(R.id.phoneitems);
                layoutRipple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent(ctx, PhoneActivity.class);
                        intent.putExtra("BACKGROUND", backgroundColor);
                        ctx.startActivity(intent);
                    }
                });
        }
        else{

                convertView = mInflater.inflate(R.layout.list_item, null);
                viewHolder=new ViewHolder(
                        (View) convertView.findViewById(R.id.list_child),
                        (TextView) convertView.findViewById(R.id.chat_msg)
                );
                convertView.setTag(viewHolder);

                String siri = String.valueOf(item.isSiri);
                if(item.isSiri)viewHolder.child.setBackgroundResource(R.drawable.msgbox_rec);
                else viewHolder.child.setBackgroundResource(R.drawable.msgbox_send);

                viewHolder.msg.setText(item.message);
                Log.v("????",item.message);

        }

        return convertView;
    }
    
    class ViewHolder {
    	  protected View child;
          protected TextView msg;
          public ViewHolder(View child, TextView msg){
              this.child = child;
              this.msg = msg;
          }
    }
}

