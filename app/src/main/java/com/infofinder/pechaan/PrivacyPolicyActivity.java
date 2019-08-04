package com.infofinder.pechaan;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.WebView;

public class PrivacyPolicyActivity extends AppCompatActivity {
    WebView web;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policyctivity);

        web =(WebView)findViewById(R.id.webView);
        web.loadUrl("file:///android_asset/privacy_policy.html");

    }
}
