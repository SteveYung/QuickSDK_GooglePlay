package com.dolphingame.googleplay;

import android.content.Intent;
import android.util.Log;

import com.dolphingame.googleplay.util.IabHelper;
import com.dolphingame.googleplay.util.IabResult;
import com.dolphingame.googleplay.util.Purchase;
import com.u8.sdk.PayParams;
import com.u8.sdk.SDKParams;
import com.u8.sdk.UserExtraData;

/**
 * Created by paili on 2017/4/27.
 */

public class GooglePlaySDK {

    private static GooglePlaySDK instance;

    IabHelper mHelper;

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (mHelper == null) return;
            if (result.isSuccess()) {
            }
        }
    };


    private GooglePlaySDK(){

    }

    public static GooglePlaySDK getInstance(){
        if(instance == null){
            instance = new GooglePlaySDK();
        }
        return instance;
    }


    public void initSDK(SDKParams params){
        this.parseSDKParams(params);
        this.initSDK();
    }

    private void initSDK(){
        //TODO::这里调用AAA的SDK初始化方法
    }

    private void parseSDKParams(SDKParams params){

    }

    public void login(){
        //TODO::这里调用AAA的登录方法
    }

    public void switchLogin(){
        //TODO::这里调用AAA切换帐号的方法
        //如果没有提供切换帐号的方法，那么切换帐号的逻辑就是[先登出，再登录]，也就是先调用logout，再调用login
    }

    public void logout(){
        //TODO::调用AAA的登出方法
    }

    public void showUserCenter(){
        //TODO::调用AAA显示个人中心的方法
        //如果AAA没有提供对应的接口，则不用实现该方法
    }

    public void exit(){
        //TODO::调用AAA显示退出确认框接口
        //如果AAA没有提供对应的接口，则不用实现该方法
    }

    public void submitGameData(UserExtraData data){
        //TODO::调用AAA上报玩家数据接口
        //如果AAA没有提供对应的接口，则不用实现该方法
    }

    public void pay(PayParams data){
        //TODO::调用AAA充值接口
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper != null)
        {
            mHelper.handleActivityResult(requestCode, resultCode, data);
        }
    }
}
