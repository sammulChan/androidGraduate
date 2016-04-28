package com.example.ddvoice;

public class OpenQA {

	private String mText;
	OnlineSpeechAction vActivity;
	
	public OpenQA(String text,OnlineSpeechAction activity){
		mText=text;
		vActivity=activity;
	}
	
	public void start(){
		vActivity.speak(mText, false);
	}
	
}
