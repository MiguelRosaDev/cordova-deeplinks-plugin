package com.deeplink;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import org.apache.cordova.CordovaActivity;

public class DeepLinksActivity extends CordovaActivity {

    private CordovaWebView appView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Cordova WebView
        if (appView == null) {
            appView = makeWebView();
            init(appView, this, makePreferences());
        }

        // Load initial Cordova page
        loadUrl(launchUrl);

        // Handle incoming deep links
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
                if (appView != null) {
                    appView.loadUrl("javascript:handleDeepLink('" + uri.toString() + "');");
                } else {
                    System.err.println("AppView is not initialized, cannot handle deep link.");
                }
            }
        }
    }
}
