package com.example.ddvoice;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.CheckBox;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import android.content.Context;
import com.example.ddvoice.MainActivity;
/**
 * Created by jf on 4/18/2016.
 */
public class OnlineSpeechAction implements AdapterView.OnItemClickListener,View.OnClickListener {
        private Context ctx;
        public OnlineSpeechAction(Context context){
            ctx = context;
            info=Toast.makeText(ctx, "", Toast.LENGTH_SHORT);
        }
        private SpeechSynthesizer mTts;
        private String voicer="xiaoyan";

        public static boolean serviceFlag=false;
        public static JSONObject semantic = null,slots =null,answer=null,datetime=null,location=null,data=null;public static String operation = null,service=null;
        public static JSONArray result=null;
        public static String receiver=null, name = null,price=null,code=null,song = null,keywords=null,content=null,
                url=null,text=null,time=null,date=null,city=null,sourceName=null,target=null,source=null;
        public static String[] weatherDate=null,weather=null,tempRange=null,airQuality=null,wind=null,humidity=null,windLevel=null;

        private TextUnderstander mTextUnderstander;// 语义理解对象（文本到语义）。
        private ListView mListView;
        private ArrayList<SiriListItem> list;
        ChatMsgViewAdapter mAdapter;
        private MediaPlayer player;
        public static  String SRResult="";	//识别结果
        private static String SAResult="";//语义识别结果
        private static String TAG = MainActivity.class.getSimpleName();
        //Toast提示消息
        private Toast info;
        //文本区域
        private TextView textView;
        //语音识别
        private SpeechRecognizer mIat;
        // 语音听写UI
        private RecognizerDialog mIatDialog;
        // 用HashMap存储听写结果
        private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
        // 引擎类型
        private String mEngineType = SpeechConstant.TYPE_CLOUD;
        private String mEngineTypeTTS = SpeechConstant.TYPE_CLOUD;
        private SharedPreferences mSharedPreferences;
        private SharedPreferences mSharedPreferencesTTS;
        MainActivity mActivity = new MainActivity();
        //语音识别监听器
        private RecognizerListener recognizerListener = new RecognizerListener() {
            public void onBeginOfSpeech() {

            }
            public void onError(SpeechError error) {
                speak("没有听到您说话。", false);
                showTip(error.getPlainDescription(true));
            }
            public void onEndOfSpeech() {
                showTip("结束说话");

            }
            public void onResult(RecognizerResult results, boolean isLast) {
                //Log.d("dd", results.getResultString());
                printResult(results,isLast);
                if (isLast) {
                    // TODO 最后的结果
                }
            }
            public void onVolumeChanged(int volume,byte[] data) {
               showTip("请对着麦克风讲话，音量为："+volume);
                //info.makeText(getApplicationContext(), "当前正在说话，音量大小：" + volume, 100).show();
            }
            public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

            }
        };
        /**
         * 初始化监听器。
         */
        private InitListener mInitListener = new InitListener() {


            public void onInit(int code) {
                Log.d(TAG, "SpeechRecognizer init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                   showTip("初始化失败，错误码"+code);
                }
            }
        };

        //初始化监听器（文本到语义）。
        private InitListener textUnderstanderListener = new InitListener() {
            public void onInit(int code) {
                Log.d(TAG, "textUnderstanderListener init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    //showTip("初始化失败,错误码："+code);
                    Log.d("dd","初始化失败,错误码："+code);
                }
            }
        };

        public void getJsonData() {
        //speak("here",false);
        try {
            JSONObject SAResultJson = new JSONObject(SAResult);

            operation=SAResultJson.optString("operation");
            service=SAResultJson.optString("service");
            semantic=SAResultJson.optJSONObject("semantic");
            answer=SAResultJson.optJSONObject("answer");
            data=SAResultJson.optJSONObject("data");

            if(data==null){
            }else result=data.optJSONArray("result");

            if(result==null){
            }else{
                //必须要初始化数组不然会有得不到任何结果的问题
                airQuality=new String[10];
                weatherDate=new String[10];
                wind=new String[10];
                humidity=new String[10];
                windLevel=new String[10];
                weather=new String[10];
                tempRange=new String[10];
                for(int i=1;i<7;i++){
                    airQuality[i-1]=result.getJSONObject(i).optString("airQuality");
                    weatherDate[i-1]=result.getJSONObject(i).optString("date");
                    wind[i-1]=result.getJSONObject(i).optString("wind");
                    humidity[i-1]=result.getJSONObject(i).optString("humidity");
                    windLevel[i-1]=result.getJSONObject(i).optString("windLevel");
                    weather[i-1]=result.getJSONObject(i).optString("weather");
                    tempRange[i-1]=result.getJSONObject(i).optString("tempRange");
                    sourceName=result.getJSONObject(i).optString("sourceName");
                }

            }

            if(answer==null){
            }else text=answer.optString("text");

            if(semantic==null){
            }else slots=semantic.optJSONObject("slots");

            if(slots==null){
            }else{
                receiver=slots.optString("receiver");
                location=slots.optJSONObject("location");
                name = slots.optString("name");
                price= slots.optString("price");
                code = slots.optString("code");
                song = slots.optString("song");
                keywords=slots.optString("keywords");
                content=slots.optString("content");
                url=slots.optString("url");
                target=slots.optString("target");
                source=slots.optString("source");
            }

            if(location==null){
            }else{
                city=location.optString("city");
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            speak("解析json数据有问题",false);
            e.printStackTrace();
        }
            SonarReaction();
    }

        public void SonarReaction(){
            SRResult=null;//置空
            SAResult=null;
            //speak("service:"+service+" operation:"+operation,false);
            //speak("serviceFlag",serviceFlag);
            if(serviceFlag==false){//如果不在一项服务中才进行服务的判断
                //speak("判断服务类型",false);
                switch(service){


                    case "telephone":{//1 电话相关服务

                        switch(operation){

                            case "CALL":{
                                CallAction callAction=new CallAction(name,code,ctx,this);
                                callAction.start();
                            }

                            case "VIEW":{	//查看电话拨打记录
                                //必要条件无
                                //可选条件【未接电话】【已拨电话】【已接电话】
                             break;
                            }

                            default :break;

                        }

                        break;
                    }

                    case "message":{//2 短信相关服务

                        switch(operation){

                            case "SEND":{//发送短信
                                break;
                            }

                            case "VIEW":{//查看发送短信页面


                                break;
                            }



                            case "SENDCONTACTS":{//发送名片,目前只能识别：名字发给名字

                                break;
                            }
                            default :break;
                        }

                        break;
                    }

                    case "app":{//3 应用相关服务

                        switch(operation){

                            case "LAUNCH":{//打开应用

                                break;
                            }

                            case "QUERY":{//应用中心搜索应用

                                break;
                            }

                            default:break;

                        }
                        break;
                    }

                    case "website":{//4 网站相关服务

                        switch(operation){

                            case "OPEN":{//打开指定网址


                                break;
                            }

                            default:break;
                        }

                        break;
                    }

                    case "websearch":{//5 搜索相关服务

                        switch(operation){

                            case "QUERY":{//搜索


                                break;
                            }

                            default:break;

                        }


                        break;
                    }

                    case "faq":{//6 社区问答相关服务

                        switch(operation){

                            case "ANSWER":{//社区问答



                                break;
                            }

                            default:break;
                        }

                        break;
                    }

                    case "chat":{//7 聊天相关服务

                        switch(operation){

                            case "ANSWER":{//聊天模式



                                break;
                            }

                            default:break;
                        }

                        break;
                    }

                    case "openQA":{//8 智能问答相关服务

                        switch(operation){

                            case "ANSWER":{//智能问答

                                OpenQA openQA = new OpenQA(text,this);
                                openQA.start();

                                break;
                            }

                            default:break;
                        }

                        break;
                    }

                    case "baike":{//9 百科知识相关服务

                        switch(operation){

                            case "ANSWER":{//百科



                                break;
                            }

                            default:break;
                        }

                        break;
                    }

                    case "schedule":{//10 日程相关服务

                        switch(operation){

                            case "CREATE":{//创建日程/闹钟(直接跳转相应设置界面)



                                break;
                            }

                            case "VIEW":{//查看闹钟/日历(未实现)


                                break;
                            }


                            default:break;
                        }

                        break;
                    }

                    case "weather":{//11 天气相关服务

                        switch(operation){

                            case "QUERY":{//查询天气



                                break;
                            }

                            default:break;

                        }

                        break;
                    }

                    case "translation":{//12 翻译相关服务

                        switch(operation){

                            case "TRANSLATION":{//翻译



                                break;
                            }

                            default:break;

                        }

                        break;
                    }

                    default:{
                        speak("不知道您要干嘛，不过我想过一段时间我就会懂了。",false);
                        break;
                    }
                }
            }//结束某项服务才跳出
        }
        public void initIflytek(){//初始讯飞设置
            Activity activity = (Activity)ctx;
            //找到Siri开关


            activity.findViewById(R.id.voice_input).setOnClickListener(this);
            SpeechUtility.createUtility(ctx, SpeechConstant.APPID + "=564f2dfe");

    }

    public void initUI(){//初始化UI和参数
        SRResult="";
        list = new ArrayList<SiriListItem>();
        if(ctx instanceof MainActivity) {
            list.add(new SiriListItem("text", true));
        }
        mAdapter = new ChatMsgViewAdapter(ctx, list);
        Activity activity = (Activity)ctx;
        mListView = (ListView) activity.findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setFastScrollEnabled(true);
        activity.registerForContextMenu(mListView);
    }

    public void speechRecognition(){//初始化
        //1.创建SpeechRecognizer对象，第二个参数： 本地听写时传InitListener
        mIat= SpeechRecognizer.createRecognizer(ctx, mInitListener);
        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        mIatDialog = new RecognizerDialog(ctx, mInitListener);
        //语义分析初始化
        mTextUnderstander = TextUnderstander.createTextUnderstander(ctx, textUnderstanderListener);

        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(ctx, mTtsInitListener);
    }

    public void startSpeenchRecognition(){//语音识别
        player = MediaPlayer.create(ctx, R.raw.begin);
        player.start();
        // 显示听写对话框
        mIatDialog.setListener(recognizerDialogListener);
        //mIatDialog.show();
        ret = mIat.startListening(recognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            Log.d(TAG, "" + ret);
            showTip("听写失败，错误码：" + ret);
            //info.makeText(getApplicationContext(), "听写失败,错误码：" + ret, 100).show();
        }

    }

    //语音识别结果监听器
    private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results,isLast);//得到识别结果
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            speak(error.getPlainDescription(true),true);
            info.makeText(ctx.getApplicationContext(), "error.getPlainDescription(true)", 1000).show();
            //showTip(error.getPlainDescription(true));
        }

    };



    //开始语义分析
    private void startAnalysis(){

        mTextUnderstander.setParameter(SpeechConstant.DOMAIN,  "iat");
        if(mTextUnderstander.isUnderstanding()){
            mTextUnderstander.cancel();
            //showTip("取消");
            Log.d("dd","取消");
        }else {
            //SRResult="明天会下雨吗？";
            ret = mTextUnderstander.understandText(SRResult, textListener);
            if(ret != 0)
            {
                //showTip("语义理解失败,错误码:"+ ret);
                Log.d("dd","语义理解失败,错误码:"+ ret);
            }
        }
    }
    //识别回调
    private TextUnderstanderListener textListener = new TextUnderstanderListener() {

        public void onResult(final UnderstanderResult result) {
            Activity activity = (Activity)ctx;
            activity.runOnUiThread(new Runnable() {

                public void run() {
                    if (null != result) {
                        // 显示
                        //Log.d(TAG, "understander result：" + result.getResultString());
                        String text = result.getResultString();
                        SAResult = text;
                        Log.d("dd", "SAResult:" + SAResult);

                        if (TextUtils.isEmpty(text)) {
                            //Log.d("dd", "understander result:null");
                            //showTip("识别结果不正确。");
                        }
                        //mainActivity.speak();
                        //speak(SAResult,false);
                        getJsonData();
                        //finish();
                    }
                }



					/*private void dialogueManagement(int mainServiceID,int branchServiceID) {//对话管理器
						// TODO Auto-generated method stub
						if(mainServiceID==1){
							if(branchServiceID==1){//进入了打电话服务，必要条件是【电话号码】,可选条件有【号码归属地】【运营商】【号段】【尾号】，
								//可由多个可选条件确定必要条件

							}
							if(branchServiceID==2){//进入了查看电话播放记录

							}

						}
						if(mainServiceID==2){//进入了发短信服务，必要条件是电话号码和短信内容

						}
					}*/
            });
        }

        public void onError(SpeechError error) {
            //showTip("onError Code："	+ error.getErrorCode());
            Log.d("dd","onError Code："	+ error.getErrorCode());
        }
    };


    protected void onActivityResult(int requestCode, int resultCode, Intent data){//重写onActivityResult
        if(requestCode == 0){
            //System.out.println("REQUESTCODE equal");
            if(resultCode == 0){
                //    System.out.println("RESULTCODE equal");
                SAResult = data.getStringExtra("SRResult");
            }
        }
    }



    private void printResult(RecognizerResult results,boolean isLast) {
        String text = JsonParser.parseIatResult(results.getResultString());

        //Log.d("dd","text:"+text);
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            Log.d("dd","json:"+results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        SRResult=resultBuffer.toString();
        if(isLast==true){
            speak(SRResult, true);
            //加入数据库

            startAnalysis();
		/*startSemanticAnalysis();*/
        }
    }

    int ret = 0; // 函数调用返回值

    @SuppressWarnings("static-access")
    @Override
    public void onClick(View view) {//语音识别过程


        startSpeenchRecognition();
    }

    public void setParam(){
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }
        // 设置语音前端点
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));
        // 设置语音后端点
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));
        // 设置标点符号
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));
        // 设置音频保存路径
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()
                + "/iflytek/wavaudio.pcm");
        // 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
        // 注：该参数暂时只对在线听写有效
        mIat.setParameter(SpeechConstant.ASR_DWA, mSharedPreferences.getString("iat_dwa_preference", "0"));
    }
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

    }


    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {

        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
               showTip("初始化失败，错误码："+code);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };


    private void setParamTTS(){
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        //设置合成
        if(mEngineTypeTTS.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            //设置发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME,voicer);
            //设置语速
            //mTts.setParameter(SpeechConstant.SPEED,mSharedPreferencesTTS.getString("speed_preference", "50"));
            //设置音调
            //mTts.setParameter(SpeechConstant.PITCH,mSharedPreferencesTTS.getString("pitch_preference", "50"));
            //设置音量
            //mTts.setParameter(SpeechConstant.VOLUME,mSharedPreferencesTTS.getString("volume_preference", "50"));
            //设置播放器音频流类型
            //mTts.setParameter(SpeechConstant.STREAM_TYPE,mSharedPreferencesTTS.getString("stream_preference", "3"));
        }else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            //设置发音人 voicer为空默认通过语音+界面指定发音人。
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
        }
    }

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        public void onSpeakBegin() {
        }


        public void onSpeakPaused() {
            showTip("暂停播放");
        }


        public void onSpeakResumed() {
            showTip("继续播放");
        }


        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
            //mPercentForBuffering = percent;
            //showTip(String.format(getString(R.string.tts_toast_format),
            //	mPercentForBuffering, mPercentForPlaying));
        }


        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            //mPercentForPlaying = percent;
            //showTip(String.format(getString(R.string.tts_toast_format),
            //	mPercentForBuffering, mPercentForPlaying));
        }


        public void onCompleted(SpeechError error) {
            if (error == null) {
            } else if (error != null) {
                showTip(error.getPlainDescription(true));
            }
        }


        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };

    private void textToSpeach(String text){//语音合成

        // 设置参数
        setParamTTS();
        int code = mTts.startSpeaking(text, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
           showTip("语音合成失败，错误码："+code);
        }
    }


    //from SiriCN
    public void speak(String msg, boolean isSiri) {
        addToList(msg, isSiri);//添加到对话列表
        if(isSiri==false){
            textToSpeach(msg);
        }
    }

    public void beginSpeak(String msg, boolean isSiri){
        if(isSiri==false){
            textToSpeach(msg);
        }
    }

    private void addToList(String msg, boolean isSiri) {
        //
        list.add(new SiriListItem(msg, isSiri));
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(list.size() - 1);
    }

    public class SiriListItem {
        String message;
        boolean isSiri;

        public SiriListItem(String msg, boolean siri) {
            message = msg;
            isSiri = siri;
        }
    }

    private void showTip(final String str) {
        info.setText(str);
        info.show();
    }

}
