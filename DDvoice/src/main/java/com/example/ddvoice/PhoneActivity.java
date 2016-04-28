package com.example.ddvoice;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.expandablelayout.library.ExpandableLayout;

import java.util.ArrayList;

/**
 * Created by owen_ on 2016-03-22.
 */
public class PhoneActivity extends Activity {

    private final String[] array = {"最近联系人"};
    //"Hello", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome"

    static int j ,i;

    public static ArrayList<CallRecentlyItem> callRecentlyItem = new ArrayList<CallRecentlyItem>();




    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.phone_activity);

        OnlineSpeechAction onlineSpeechAction = new OnlineSpeechAction(this);
        onlineSpeechAction.initIflytek();
        onlineSpeechAction.initUI();
        onlineSpeechAction.speechRecognition();
        RelativeLayout test = (RelativeLayout)findViewById(R.id.phoneRelativeLayout2);
        TextView tv = new TextView(this);
        RelativeLayout.LayoutParams tvRule = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvRule.addRule(RelativeLayout.BELOW, R.id.phoneContentText2);
        tv.setBackgroundColor(Color.parseColor("#99CCFF"));

        tv.setGravity(TextView.TEXT_ALIGNMENT_CENTER);

        tv.setText(Integer.toString(tv.getId()));
        test.addView(tv, tvRule);


        ExpandableLayout EL = (ExpandableLayout)findViewById(R.id.second);
        RelativeLayout phoneRelativeLayout1 = (RelativeLayout)findViewById(R.id.phoneRelativeLayout1);

        if(callRecentlyItem.isEmpty())
        {
            EL.setVisibility(View.GONE);
        }
        else
        {
            i = 1;
            j = 0;
            LinearLayout callRecLinearLayout = (LinearLayout)findViewById(R.id.callRecLayout);
            RelativeLayout[] callRecRelativeLayout = new RelativeLayout[50];
            final TextView[] nameTextView = new TextView[50];
            final TextView[] phoneTextView = new TextView[50];
            final TextView[] timeTextView = new TextView[50];

            RelativeLayout.LayoutParams[] rules = new RelativeLayout.LayoutParams[200];

            for (CallRecentlyItem key : callRecentlyItem) {

                callRecRelativeLayout[j] = new RelativeLayout(this);
                rules[i] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                callRecRelativeLayout[j].setId(i);
                callRecRelativeLayout[j].setTag(i);
                if (j%2==0)
                {
                    callRecRelativeLayout[j].setBackgroundColor(Color.parseColor("#01579B"));
                }
                else
                {
                    callRecRelativeLayout[j].setBackgroundColor(Color.parseColor("#CCCCFF"));
                }
                i++;

                nameTextView[j] = new TextView(this);
                rules[i] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            rules[i].addRule(RelativeLayout.ALIGN_PARENT_TOP);
                nameTextView[j].setText(key.getName());
//            nameTextView[j].setTextSize(25.0f);
                nameTextView[j].setId(i);
                i++;

                phoneTextView[j] = new TextView(this);
                rules[i] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                rules[i] .addRule(RelativeLayout.BELOW, i - 1);
                phoneTextView[j].setText(key.getPhoneNumber());
//            nameTextView[j].setTextSize(20.0f);
                phoneTextView[j].setId(i);
                i++;

                timeTextView[j] = new TextView(this);
                rules[i] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                rules[i] .addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                rules[i].addRule(RelativeLayout.CENTER_VERTICAL);
                timeTextView[j].setText(key.getCallTime());
//            nameTextView[j].setTextSize(20.0f);
                timeTextView[j].setId(i);
                i++;

                callRecRelativeLayout[j].addView(nameTextView[j],rules[i-3]);
                callRecRelativeLayout[j].addView(phoneTextView[j],rules[i-2]);
                callRecRelativeLayout[j].addView(timeTextView[j],rules[i-1]);
                callRecLinearLayout.addView(callRecRelativeLayout[j], rules[i - 4]);

/*                callRecRelativeLayout[j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int tag = (Integer)v.getTag();

                        TextView chooseText = (TextView)findViewById(R.id.chooseText);
                        TextView tv1 = (TextView)findViewById(tag+1);
                        TextView tv2 = (TextView)findViewById(tag+2);
                        TextView tv3 = (TextView)findViewById(tag+3);
                        chooseText.setText(tv1.getText()+"@"+tv2.getText()+"@"+tv3.getText());
                    }
                });*/

                j++;
            }
        }



        Button returnButton = (Button)findViewById(R.id.phoneReturnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent().setClass(PhoneActivity.this,MainActivity.class));
                PhoneActivity.this.finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.phone_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
