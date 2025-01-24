
# Cordova Plugin: Deeplinks Handler

This plugin allows Cordova-based Android applications to handle deep links. It enables deep link handling using intent filters and supports Android App Links with `assetlinks.json` verification. 

## Features

- Handle incoming deep links via `android.intent.action.VIEW`.
- Support for Android App Links (`assetlinks.json` verification).
- Seamless communication between native Android code and JavaScript.

---

## Installation

To install the plugin into your Cordova project:

```bash
cordova plugin add path/to/cordova-plugin-deeplinks
```

---

## Configuration

### 1. **assetlinks.json**

To enable Android App Links, host an `assetlinks.json` file on your server at:  
```
https://<your-domain>/.well-known/assetlinks.json
```

Example `assetlinks.json` file with sensitive values hidden:
```json
[
  {
    "relation": ["delegate_permission/common.handle_all_urls"],
    "target": {
      "namespace": "OSTESTAPP",
      "package_name": "pt.nos.osatmospheretest",
      "sha256_cert_fingerprints": [
        "<your-certificate-sha256-fingerprint>"
      ]
    }
  }
]
```

**Note:** Replace `<your-certificate-sha256-fingerprint>` with the actual SHA256 certificate fingerprint for your app.

---

## Usage

### 1. **JavaScript Code**

Add the following JavaScript code in your Cordova app to handle deep links.

```javascript
document.addEventListener('deviceready', function () {
    console.log('Device is ready');

    // Check for the initial intent when the app is launched
    cordova.exec(
        function (uri) {
            console.log("App opened with deep link: ", uri);
            handleDeepLink(uri); // Pass the URI to your logic
        },
        function (error) {
            console.error("No deep link detected at startup: ", error);
        },
        "DeeplinksPlugin",  // Name of your plugin (as defined in plugin.xml)
        "handleDeepLink",   // Action to execute
        []
    );

    // Listen for deep link updates (when app is already running)
    window.handleDeepLink = function (uri) {
        console.log("Deep link received: ", uri);
        // Add your logic here to handle the deep link
        // For example, navigate to a specific page in your app
        if (uri.includes("OSTESTAPP/Deeplinks")) {
            // Example: If the URI matches a specific path
            navigateToPage(uri);
        }
    };

    function navigateToPage(uri) {
        console.log("Navigating based on deep link: ", uri);
        // Replace with navigation logic for your app
        alert("Navigating to: " + uri);
    }
});
```

### 2. **Testing the Plugin**

To test deep linking, use the following command with `adb`:

```bash
adb shell am start -a android.intent.action.VIEW -d "https://outsystemsdevdmz.corporativo.pt/OSTESTAPP/Deeplinks" pt.nos.osatmospheretest
```

---

## Plugin XML

Here’s the `plugin.xml` configuration for your plugin:

```xml
<?xml version="1.0" encoding="utf-8"?>
<plugin xmlns="http://apache.org/cordova/ns/plugins/1.0" id="cordova-plugin-deeplinks" version="1.0.0">
    <name>DeeplinksPlugin</name>
    <description>Plugin to handle Android deeplinks</description>
    <license>MIT</license>
    <keywords>cordova, deeplinks</keywords>
    <engines>
        <engine name="cordova-android" version=">=7.0.0"/>
    </engines>
    <platform name="android">
        <config-file target="res/xml/config.xml" parent="/*">
            <feature name="DeeplinksPlugin">
                <param name="android-package" value="com.example.DeeplinksPlugin" />
                <param name="onload" value="true" />
            </feature>
        </config-file>
        
        <config-file target="AndroidManifest.xml" parent="/manifest/application">
            <activity android:name="com.example.DeepLinksActivity"
                      android:exported="true"
                      android:launchMode="singleTask">
                <intent-filter android:autoVerify="true">
                    <action android:name="android.intent.action.VIEW"/>
                    <category android:name="android.intent.category.DEFAULT"/>
                    <category android:name="android.intent.category.BROWSABLE"/>
                    <data android:scheme="https" android:host="outsystemsdevdmz.corporativo.pt" android:pathPrefix="/OSTESTAPP/"/>
                </intent-filter>
            </activity>
        </config-file>
        <source-file src="src/android/DeepLinksActivity.java" target-dir="src/com/example/" />
    </platform>
</plugin>
```

---

## Java Implementation

### 1. **DeepLinksActivity.java**

Ensure your `DeepLinksActivity` handles incoming deep links and passes them to JavaScript.

```java
package com.example;

import android.content.Intent;
import android.net.Uri;
import org.apache.cordova.*;

public class DeepLinksActivity extends CordovaActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadUrl(launchUrl);

        // Handle the intent if it contains a deep link
        handleDeepLink(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleDeepLink(intent);
    }

    private void handleDeepLink(Intent intent) {
        if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri != null) {
                // Pass the deep link URL to your JavaScript logic
                webView.loadUrl("javascript:handleDeepLink('" + uri.toString() + "');");
            }
        }
    }
}
```

---

## License

This project is licensed under the MIT License.
