package com.dolphingame.googleplay;

import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.dolphingame.googleplay.util.IabHelper;
import com.dolphingame.googleplay.util.IabResult;
import com.dolphingame.googleplay.util.Inventory;
import com.dolphingame.googleplay.util.Purchase;
import com.u8.sdk.IActivityCallback;
import com.u8.sdk.PayParams;
import com.u8.sdk.SDKParams;
import com.u8.sdk.U8Code;
import com.u8.sdk.U8SDK;
import com.u8.sdk.UserExtraData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by paili on 2017/4/27.
 */

public class GooglePlaySDK implements IActivityCallback {
    private class ConsumeData {
        public String paypoint;
        public String orderId;
        public String signedData;
        public String signature;
    }

    static final String TAG = "InAppBilling";
    private static String CACHE_FILE_NAME = "googleplaycache.json";
    static final int REQUEST_CODE = 10001;
    private static GooglePlaySDK instance;

    private String mPublicKey = null;
    private Boolean mIsDebug = true;
    private String mNotifyUrl;

    private IabHelper mHelper = null;

    // key is paypoint, value is orderId
    private Map<String, String> purchaseBefore = new HashMap<>();

    // key is paypoint, value is orderInfo
    private Map<String, ConsumeData> consumeCompleted = new HashMap<>();


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
        if (mPublicKey != null)
        {
            mHelper = new IabHelper(U8SDK.getInstance().getContext(), mPublicKey);
            mHelper.enableDebugLogging(mIsDebug);

            // load game data
            loadData();

            Log.d(TAG, "Starting setup.");
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    Log.d(TAG, "Setup finished.");

                    if (!result.isSuccess()) {
                        U8SDK.getInstance().onResult(U8Code.CODE_INIT_FAIL, result.getMessage());
                        return;
                    }

                    // Have we been disposed of in the meantime? If so, quit.
                    if (mHelper == null) return;

                    // IAB is fully set up. Now, let's get an inventory of stuff we own.
                    Log.d(TAG, "Setup successful. Querying inventory.");
                    try {
                        mHelper.queryInventoryAsync(mGotInventoryListener);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            U8SDK.getInstance().setActivityCallback(this);
        }
    }

    private IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {

        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished. result: " + result);

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
            List<Purchase> purchases = inventory.getAllPurchases();
            if (!purchases.isEmpty()) {
                try {
                    mHelper.consumeAsync(inventory.getAllPurchases(), mConsumeMultiFinishedListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            Log.d(TAG, "Initial inventory query finished");
        }
    };

    private IabHelper.OnConsumeMultiFinishedListener mConsumeMultiFinishedListener = new IabHelper.OnConsumeMultiFinishedListener() {
        @Override
        public void onConsumeMultiFinished(final List<Purchase> purchases, List<IabResult> results) {
            for (int i = 0; i < purchases.size() && i < results.size(); ++i){
                Purchase purchase = purchases.get(i);
                IabResult result = results.get(i);
                if (result.isSuccess()){
                    ConsumeData data = new ConsumeData();
                    data.paypoint = purchase.getSku();
                    data.orderId = purchase.getDeveloperPayload();
                    data.signedData = purchase.getOriginalJson();
                    data.signature = purchase.getSignature();
                    consumeCompleted.put(purchase.getSku(), data);
                }
            }
            saveData();

//            if (mNotifyUrl != null){
//                HttpConnectionUtil httpRequest = new HttpConnectionUtil();
//                httpRequest.setUrl(mNotifyUrl);
//                JSONObject postData = new JSONObject();
//                try {
//                    for (Map.Entry<String, ConsumeData> entry : consumeCompleted.entrySet()){
//                        JSONObject subData = new JSONObject();
//                        subData.put("orderId", entry.getValue().orderId);
//                        subData.put("signedData", entry.getValue().signedData);
//                        subData.put("signature", entry.getValue().signature);
//                        postData.put(entry.getKey(), subData);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                httpRequest.setPostData(postData);
//                httpRequest.setRequestType(HttpConnectionUtil.RequestType.kPost);
//                httpRequest.setResponseListener(notifyListener);
//                httpRequest.execute();
//            }
        }
    };

    private void parseSDKParams(SDKParams params){
        this.mPublicKey = params.getString("GOOGLEPLAY_PUBLICKEY");
        this.mIsDebug = params.getBoolean("GOOGLEPLAY_IS_DEBUG");
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
        if (data != null)
        {
            String paypoint = data.getProductName();
            String orderId = data.getOrderID();
            Log.d(TAG, "[doPay] paypoint: " + paypoint);
            if (mHelper != null){
                try {
                    if (purchaseBefore.containsKey(paypoint)){
                        purchaseBefore.remove(paypoint);
                    }
                    purchaseBefore.put(paypoint, orderId);
                    mHelper.launchPurchaseFlow(U8SDK.getInstance().getContext(), paypoint, REQUEST_CODE, mPurchaseFinishedListener, orderId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                return;
            }

            purchaseBefore.remove(purchase.getSku());

            try {
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        @Override
        public void onConsumeFinished(final Purchase purchase, IabResult result) {
            if (result.isSuccess()){
                ConsumeData data = new ConsumeData();
                data.paypoint = purchase.getSku();
                data.orderId = purchase.getDeveloperPayload();
                data.signedData = purchase.getOriginalJson();
                data.signature = purchase.getSignature();
                consumeCompleted.put(purchase.getSku(), data);
                saveData();

//                if (sParams.containsKey("notify_url")){
//                    Log.i(TAG, "[onConsumeFinished] notify_url: " + sParams.get("notify_url"));
//                    HttpConnectionUtil httpRequest = new HttpConnectionUtil();
//                    httpRequest.setUrl(sParams.get("notify_url"));
//                    JSONObject postData = new JSONObject();
//                    try {
//                        JSONObject subData = new JSONObject();
//                        subData.put("orderId", purchase.getDeveloperPayload());
//                        subData.put("signedData", purchase.getOriginalJson());
//                        subData.put("signature", purchase.getSignature());
//                        postData.put(purchase.getSku(), subData);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    httpRequest.setPostData(postData);
//                    httpRequest.setRequestType(HttpConnectionUtil.RequestType.kPost);
//                    httpRequest.setResponseListener(notifyListener);
//                    httpRequest.execute();
//                }

            }
            else {
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper != null){
            mHelper.handleActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onNewIntent(Intent newIntent) {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onBackPressed() {

    }

//    private boolean checkPlayServices() {
//        boolean result = false;
//        try {
//            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//            int resultCode = apiAvailability.isGooglePlayServicesAvailable(mContext);
//            result = (resultCode == ConnectionResult.SUCCESS);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return result;
//    }

    private void loadData(){
        Log.d(TAG, "[loadData] entry");
        consumeCompleted.clear();

        Boolean hasCache = false;
        String[] fileList = U8SDK.getInstance().getApplication().fileList();
        for(String s : fileList){
            if (s == CACHE_FILE_NAME){
                hasCache = true;
                break;
            }
        }
        if (hasCache == false){
            return;
        }

        try {
            FileInputStream inputStream = U8SDK.getInstance().getApplication().openFileInput(CACHE_FILE_NAME);
            int length = inputStream.available();
            byte [] buffer = new byte[length];
            inputStream.read(buffer);
            String content = new String(buffer, "UTF-8");
            inputStream.close();

            JSONObject jsonObject = new JSONObject(content);
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()){
                String productId = iterator.next();
                JSONObject subObj = jsonObject.getJSONObject(productId);
                ConsumeData data = new ConsumeData();
                if (subObj.has("orderId") && subObj.has("sku") &&
                        subObj.has("signature") && subObj.has("signedData")){
                    data.paypoint = subObj.getString("sku");
                    data.orderId = subObj.getString("orderId");
                    data.signedData = subObj.getString("signedData");
                    data.signature = subObj.getString("signature");
                    consumeCompleted.put(productId, data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveData(){
        Log.d(TAG, "[saveData] entry");
        try{
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, ConsumeData> entry : consumeCompleted.entrySet()){
                JSONObject subObj = new JSONObject();
                subObj.put("sku", entry.getKey());
                subObj.put("orderId", entry.getValue().orderId);
                subObj.put("signedData", entry.getValue().signedData);
                subObj.put("signature", entry.getValue().signature);
                jsonObject.put(entry.getKey(), subObj);
            }
            String contentStr = jsonObject.toString();

            FileOutputStream outputStream = U8SDK.getInstance().getApplication().openFileOutput(CACHE_FILE_NAME, MODE_PRIVATE);

            outputStream.write(contentStr.getBytes());
            outputStream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /** Verifies the developer payload of a purchase. */
    private boolean verifyDeveloperPayload(Purchase p) {
        return purchaseBefore.containsKey(p.getSku()) && purchaseBefore.get(p.getSku()).compareTo(p.getDeveloperPayload()) == 0;
    }
}
