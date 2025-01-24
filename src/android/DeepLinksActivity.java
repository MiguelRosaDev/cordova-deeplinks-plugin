package com.example.DeepLinksActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import org.apache.cordova.*;

public class DeeplinkHandler extends CordovaPlugin {

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) {
        if ("openDeeplink".equals(action)) {
            Intent intent = cordova.getActivity().getIntent();
            handleDeepLink(intent, callbackContext);
            return true;
        }
        return false;
    }

    private void handleDeepLink(Intent intent, CallbackContext callbackContext) {
        if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri != null) {
                callbackContext.success(uri.toString());
            } else {
                callbackContext.error("No deep link found");
            }
        } else {
            callbackContext.error("Intent action is not VIEW");
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleDeepLink(intent, null);
    }
}
