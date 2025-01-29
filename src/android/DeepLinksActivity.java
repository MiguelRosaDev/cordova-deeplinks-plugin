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
    private static String lastDeepLink = null; // Guarda o último deep link
    private static boolean isReactReady = false; // Verifica se o React Native está pronto
    private CallbackContext callbackContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.d(TAG, "DeepLinksActivity initialized");

        isReactReady = true; // Marca que o React está pronto para receber deep links

        // Se já recebemos um deep link enquanto a app estava fechada, envia agora
        if (lastDeepLink != null) {
            sendDeepLinkToWebView(lastDeepLink);
        }

        // Processa o intent inicial
        Intent intent = cordova.getActivity().getIntent();
        handleDeepLink(intent);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;

        if (action.equals("getDeepLink")) {
            if (lastDeepLink != null) {
                callbackContext.success(lastDeepLink);
                lastDeepLink = null; // Reseta para evitar chamadas duplicadas
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

                // Se o React Native já está pronto, enviamos imediatamente
                if (isReactReady) {
                    sendDeepLinkToWebView(lastDeepLink);
                }
            } else {
                Log.e(TAG, "Deep link URI is null");
            }
        } else {
            Log.e(TAG, "Intent is not a VIEW intent");
        }
    }

    private void sendDeepLinkToWebView(String deepLink) {
        if (webView != null) {
            final String js = "window.handleDeepLink && window.handleDeepLink('" + deepLink + "');";
            cordova.getActivity().runOnUiThread(() -> webView.loadUrl("javascript:" + js));
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent called");
        handleDeepLink(intent);
    }
}
