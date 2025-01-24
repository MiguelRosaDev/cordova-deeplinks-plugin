package com.example;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaWebView;

public class DeepLinksActivity extends CordovaActivity {

    private CordovaWebView appView; // Cordova WebView instance

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the initial Cordova URL
        loadUrl(launchUrl);

        // Initialize the WebView if needed
        if (appView == null) {
            appView = this.appView;
        }

        // Handle incoming intent for deep links
        handleDeepLink(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        // Handle the new deep link
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
