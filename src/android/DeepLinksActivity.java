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
import org.json.JSONObject;

public class DeepLinksActivity extends CordovaPlugin {
    private static final String TAG = "DeepLinksActivity";
    private String assetLinksJson;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        // Carrega o JSON da preferência configurada no plugin.xml
        this.assetLinksJson = preferences.getString("ASSET_LINKS_JSON", "{}");
        Log.d(TAG, "DeepLinksActivity initialized with assetLinksJson: " + this.assetLinksJson);

        // Valida se o JSON foi carregado corretamente
        if (this.assetLinksJson.equals("{}")) {
            Log.e(TAG, "Asset Links JSON is not configured or invalid.");
        }
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
        Log.d(TAG, "Handling deep link. Intent: " + intent.toString());

        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW)) {
            Uri uri = intent.getData();
            if (uri != null) {
                String uriString = uri.toString();
                Log.d(TAG, "Deep link URI: " + uriString);

                // Valida o deep link com base no JSON carregado
                if (isDeepLinkValid(uriString)) {
                    Log.d(TAG, "Deep link is valid.");
                    webView.loadUrl("javascript:window.handleDeepLink('" + uriString + "');");
                    if (callbackContext != null) {
                        callbackContext.success(uriString);
                    }
                } else {
                    Log.e(TAG, "Deep link is invalid.");
                    if (callbackContext != null) {
                        callbackContext.error("Deep link is invalid.");
                    }
                }
            } else {
                Log.e(TAG, "Deep link URI is null.");
                if (callbackContext != null) {
                    callbackContext.error("Deep link URI is null.");
                }
            }
        } else {
            Log.e(TAG, "Intent is not a VIEW intent.");
            if (callbackContext != null) {
                callbackContext.error("Not a VIEW intent.");
            }
        }
    }

    private boolean isDeepLinkValid(String uri) {
        try {
            // Converte o JSON da preferência em um objeto JSON
            JSONObject jsonObject = new JSONObject(this.assetLinksJson);
            JSONObject target = jsonObject.getJSONObject("target");

            // Extrai o host e o prefixo esperado do JSON
            String expectedHost = target.getString("namespace"); // Exemplo: "OSTESTAPP"
            String expectedPathPrefix = preferences.getString("APP_DOMAIN_PATH", "/");

            // Verifica se o URI contém o host e o prefixo esperado
            return uri.contains(expectedHost) && uri.contains(expectedPathPrefix);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing Asset Links JSON.", e);
            return false;
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent called");
        handleDeepLink(intent, null);
    }
}
