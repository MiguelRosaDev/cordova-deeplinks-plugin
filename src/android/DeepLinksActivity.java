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
    private static String lastDeepLink = null; 
    private CallbackContext callbackContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.d(TAG, "DeepLinksActivity initialized");
        Intent intent = cordova.getActivity().getIntent();
        handleDeepLink(intent);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;

        if (action.equals("getDeepLink")) {
            if (lastDeepLink != null) {
                callbackContext.success(lastDeepLink);
                lastDeepLink = null; 
            } else {
                callbackContext.error("No deep link received");
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

                if (webView != null) {
                    final String js = "window.handleDeepLink && window.handleDeepLink('" + lastDeepLink + "');";
                    cordova.getActivity().runOnUiThread(() -> webView.loadUrl("javascript:" + js));
                }
            } else {
                Log.e(TAG, "Deep link URI is null");
            }
        } else {
            Log.e(TAG, "Intent is not a VIEW intent");
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent called");
        handleDeepLink(intent);
    }
}
