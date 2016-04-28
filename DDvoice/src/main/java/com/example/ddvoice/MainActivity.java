package com.example.ddvoice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

public class MainActivity extends Activity {

	    private MediaPlayer player;//播放音乐
	    //Toast提示消息
	    private Toast info;


	@SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		info=Toast.makeText(this, "", Toast.LENGTH_SHORT);

		    OnlineSpeechAction vbutton = new OnlineSpeechAction(this);
			/*mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("正在初始化，请稍候…… ^_^");
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();*/
			showTip("初始化中...");
			//info.makeText(getApplicationContext(), "初始化中...", 5).show();
	       // showTip("hai");



			vbutton.initIflytek();
			vbutton.initUI();
			vbutton.speechRecognition();
			//mProgressDialog.dismiss();
		    showTip("初始化完毕");
			//info.makeText(getApplicationContext(), "初始化完毕", 5).show();

			player = MediaPlayer.create(MainActivity.this, R.raw.lock);
			player.start();
			vbutton.beginSpeak("你好，我是Sonar，您的智能语音助手。", false);


    }
	
	private void showTip(final String str) {
		info.setText(str);
		info.show();
	}
	
	
}
