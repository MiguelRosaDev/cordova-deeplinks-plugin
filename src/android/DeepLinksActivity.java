package com.example;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

public class DeepLinksActivity extends CordovaPlugin {

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleDeepLink(intent);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("handleDeepLink")) {
            handleDeepLink(cordova.getActivity().getIntent());
            callbackContext.success();
            return true;
        }
        return false;
    }

    private void handleDeepLink(Intent intent) {
        if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri != null) {
                String uriString = uri.toString();
                webView.loadUrl("javascript:handleDeepLink('" + uriString + "');");
            }
        }
    }
}

