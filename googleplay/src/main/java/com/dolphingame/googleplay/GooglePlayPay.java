package com.dolphingame.googleplay;

import android.app.Activity;

import com.u8.sdk.IPay;
import com.u8.sdk.PayParams;
import com.u8.sdk.U8SDK;

/**
 * Created by paili on 2017/4/27.
 */

public class GooglePlayPay implements IPay {
    public GooglePlayPay(Activity context){
        GooglePlaySDK.getInstance().initSDK(U8SDK.getInstance().getSDKParams());
    }

    @Override
    public boolean isSupportMethod(String methodName) {
        return true;
    }

    @Override
    public void pay(PayParams data) {
        GooglePlaySDK.getInstance().pay(data);
    }
}
