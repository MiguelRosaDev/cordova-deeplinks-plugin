package com.example;

import android.content.Intent;
import android.net.Uri;
import org.apache.cordova.*;

public class DeepLinksActivity extends CordovaActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadUrl(launchUrl);

        // Handle the intent if it contains a deep link
        handleDeepLink(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleDeepLink(intent);
    }

    private void handleDeepLink(Intent intent) {
        if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri != null) {
                // Pass the deep link URL to your JavaScript logic
                webView.loadUrl("javascript:handleDeepLink('" + uri.toString() + "');");
            }
        }
    }
}
