package pt.nos.osatmospheretest;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

public class DeepLinksActivity extends CordovaPlugin {
    private static final String TAG = "DeepLinksActivity";
    private static CallbackContext persistentCallback = null;
    private static String lastDeepLink = null;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // Handle the intent that started the activity (if any)
        Intent intent = cordova.getActivity().getIntent();
        handleDeepLink(intent);
    }
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getDeepLink")) {
            persistentCallback = callbackContext;

            if (lastDeepLink != null) {
                sendDeepLinkToWebView(lastDeepLink);
                lastDeepLink = null;
            } else {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            }
            return true;
        }
        return false;
    }

    private void handleDeepLink(Intent intent) {
        if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri != null) {
                lastDeepLink = uri.toString();
                Log.d(TAG, "Received deep link: " + lastDeepLink);

                if (persistentCallback != null) {
                    sendDeepLinkToWebView(lastDeepLink);
                    lastDeepLink = null;
                }
            }
        }
    }

    private void sendDeepLinkToWebView(String deepLink) {
        if (persistentCallback != null) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, deepLink);
            pluginResult.setKeepCallback(true);
            persistentCallback.sendPluginResult(pluginResult);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        handleDeepLink(intent);
    }
}
