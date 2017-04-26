package com.dolphingame.sdkbase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.u8.sdk.IU8SDKListener;
import com.u8.sdk.InitResult;
import com.u8.sdk.PayResult;
import com.u8.sdk.U8SDK;
import com.u8.sdk.plugin.U8Pay;
import com.u8.sdk.plugin.U8Share;
import com.u8.sdk.plugin.U8User;
import com.u8.sdk.verify.UToken;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutID = getResources().getIdentifier("activity_main", "layout", getPackageName());
        setContentView(layoutID);

        Button btn = (Button)findViewById(getResources().getIdentifier("btn_login", "id", getPackageName()));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U8User.getInstance().login();
            }
        });

        btn = (Button)findViewById(getResources().getIdentifier("btn_logout", "id", getPackageName()));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U8User.getInstance().logout();
            }
        });

        btn = (Button)findViewById(getResources().getIdentifier("btn_exit", "id", getPackageName()));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U8User.getInstance().exit();
            }
        });

        btn = (Button)findViewById(getResources().getIdentifier("btn_pay", "id", getPackageName()));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U8Pay.getInstance().pay(null);
            }
        });

        btn = (Button)findViewById(getResources().getIdentifier("btn_share", "id", getPackageName()));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U8Share.getInstance().share(null);
            }
        });

        U8SDK.getInstance().init(this);
        U8SDK.getInstance().setSDKListener(new IU8SDKListener() {
            @Override
            public void onResult(int code, String msg) {

            }

            @Override
            public void onInitResult(InitResult result) {

            }

            @Override
            public void onLoginResult(String data) {

            }

            @Override
            public void onSwitchAccount() {

            }

            @Override
            public void onSwitchAccount(String data) {

            }

            @Override
            public void onLogout() {

            }

            @Override
            public void onAuthResult(UToken authResult) {

            }

            @Override
            public void onPayResult(PayResult result) {

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        U8SDK.getInstance().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onStart(){
        U8SDK.getInstance().onStart();
        super.onStart();
    }

    public void onPause(){
        U8SDK.getInstance().onPause();
        super.onPause();
    }
    public void onResume(){
        U8SDK.getInstance().onResume();
        super.onResume();
    }
    public void onNewIntent(Intent newIntent){
        U8SDK.getInstance().onNewIntent(newIntent);
        super.onNewIntent(newIntent);
    }
    public void onStop(){
        U8SDK.getInstance().onStop();
        super.onStop();
    }
    public void onDestroy(){
        U8SDK.getInstance().onDestroy();
        super.onDestroy();
    }
    public void onRestart(){
        U8SDK.getInstance().onRestart();
        super.onRestart();
    }

}
