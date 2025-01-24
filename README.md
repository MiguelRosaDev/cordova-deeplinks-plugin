
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

Example `assetlinks.json` file:
```json
[
  {
    "relation": ["delegate_permission/common.handle_all_urls"],
    "target": {
      "namespace": "You app namespace",
      "package_name": "your app package name",
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
---

## License

This project is licensed under the MIT License.
