package com.deeplink;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import org.apache.cordova.CordovaActivity;

public class DeepLinksActivity extends CordovaActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) { // Bundle is now imported
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
                if (appView != null) { // Ensure appView is initialized
                    appView.loadUrl("javascript:handleDeepLink('" + uri.toString() + "');");
                } else {
                    System.err.println("AppView is not initialized, cannot handle deep link.");
                }
            }
        }
    }
}
