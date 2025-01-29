import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.apache.cordova.CordovaActivity;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

public class DeepLinksActivity extends CordovaActivity { // Mudança para CordovaActivity
    private static final String TAG = "DeepLinksActivity";
    private static String lastDeepLink = null;
    private static CallbackContext persistentCallback = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Chamando o método da superclasse
        handleDeepLink(getIntent()); // Chama a função para verificar o Deep Link logo ao abrir
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
        super.onNewIntent(intent);
        handleDeepLink(intent);
    }
}
