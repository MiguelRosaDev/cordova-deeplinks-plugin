package com.example;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

public class DeepLinksActivity extends CordovaPlugin {
    private static final String TAG = "DeepLinksActivity";
    private String assetLinksPath;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.assetLinksPath = preferences.getString("ASSET_LINKS_PATH", "/.well-known/assetlinks.json");
        Log.d(TAG, "Initialized with assetLinksPath: " + this.assetLinksPath);
    }

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
        Log.d(TAG, "handleDeepLink called");
        if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri != null) {
                String uriString = uri.toString();
                Log.d(TAG, "Deep link received: " + uriString);
                webView.loadUrl("javascript:handleDeepLink('" + uriString + "');");
            }
        }
    }
}

