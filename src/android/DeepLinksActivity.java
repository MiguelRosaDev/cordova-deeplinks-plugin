package pt.nos.osatmospheretest;

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

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.d(TAG, "DeepLinksActivity initialized");
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("handleDeepLink")) {
            handleDeepLink(cordova.getActivity().getIntent(), callbackContext);
            return true;
        }
        return false;
    }

    private void handleDeepLink(Intent intent, CallbackContext callbackContext) {
        String intentString = intent.toString();
        Log.d(TAG, "Handling deep link. Intent: " + intentString);
        
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW)) {
            Uri uri = intent.getData();
            if (uri != null) {
                String uriString = uri.toString();
                Log.d(TAG, "Deep link URI: " + uriString);
                callbackContext.success(uriString);
                webView.loadUrl("javascript:handleDeepLink('" + uriString + "');");
            } else {
                Log.d(TAG, "Deep link URI is null");
                callbackContext.error("Deep link URI is null");
            }
        } else {
            Log.d(TAG, "Not a VIEW intent");
            callbackContext.error("Not a VIEW intent");
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent called");
        handleDeepLink(intent, null);
    }
}

