package pt.nos.osatmospheretest;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

public class DeepLinksActivity extends CordovaPlugin {
    private static final String TAG = "DeepLinksActivity";
    private static String lastDeepLink = null;
    private static CallbackContext persistentCallback = null; // Armazena o callback

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getDeepLink")) {
            persistentCallback = callbackContext; // Guarda o callback para chamadas futuras

            if (lastDeepLink != null) {
                sendDeepLinkToWebView(lastDeepLink);
                lastDeepLink = null;
            } else {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
                pluginResult.setKeepCallback(true); // Mantém o callback ativo para futuros deep links
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
                    lastDeepLink = null; // Reseta para evitar chamadas duplicadas
                }
            }
        }
    }

    private void sendDeepLinkToWebView(String deepLink) {
        if (persistentCallback != null) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, deepLink);
            pluginResult.setKeepCallback(true); // Mantém o callback ativo para futuros deep links
            persistentCallback.sendPluginResult(pluginResult);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleDeepLink(intent);
    }
}
