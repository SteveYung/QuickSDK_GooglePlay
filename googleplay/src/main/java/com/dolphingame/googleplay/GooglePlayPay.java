package com.dolphingame.googleplay;

import com.u8.sdk.IPay;
import com.u8.sdk.PayParams;

/**
 * Created by paili on 2017/4/27.
 */

public class GooglePlayPay implements IPay {
    @Override
    public boolean isSupportMethod(String methodName) {
        return true;
    }

    @Override
    public void pay(PayParams data) {
        GooglePlaySDK.getInstance().pay(data);
    }
}
