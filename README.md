
# cordova-custom-deeplinks-plugin

Cordova plugin to handle **universal links** and **deep links** on Android and iOS, with automatic event dispatch and support for retrieving pending links on app launch.

---

## ⚠️ Important Requirements

To ensure deep links work properly with **App Links (Android)** and **Universal Links (iOS)**, your domain **must** be correctly configured with the following files:

- `https://<your-domain>/.well-known/assetlinks.json` (Android)
- `https://<your-domain>/.well-known/apple-app-site-association` (iOS)

> These files must be publicly accessible and contain the correct app association data.  
> See the [official Android documentation](https://developer.android.com/training/app-links/deep-linking) and [Apple documentation](https://developer.apple.com/documentation/xcode/supporting-associated-domains) for more details.

---

## 📦 Installation

Install the plugin via the Cordova CLI:

```bash
cordova plugin add https://github.com/MiguelRosaDev/cordova-deeplinks-plugin \
  --variable APP_SCHEME=https \
  --variable APP_HOST=your domain \
  --variable APP_PATHPATTERN=".*"
```

> Replace with your own repository URL and values if needed.

## 📱 Supported Platforms

- ✅ Android (Cordova Android >= 10)
- ✅ iOS (Cordova iOS >= 5)

## ⚙️ Preferences

These are the configurable plugin variables:

- `APP_SCHEME`: the URI scheme (e.g. `https`)
- `APP_HOST`: the host/domain (e.g. `your app host`)
- `APP_PATHPATTERN`: path pattern (e.g. `.*` for all paths)

## 🚀 Usage

### Handle deep link events

When the app is open and receives a link:

```js
window.addEventListener('deeplinks', function (event) {
  const url = event.detail.url;
  console.log('Received deep link:', url);
  // Handle navigation here
});
```

### Get pending link on app launch

If the app was opened via a deep link, retrieve it after `deviceready`:

```js
document.addEventListener('deviceready', function () {
  window.CustomDeeplinks.getPendingDeeplink(function (url) {
    if (url) {
      console.log('Pending deep link:', url);
      // Handle navigation here
    }
  });
});
```

## 🛠 Android Configuration

The plugin injects an `<intent-filter>` into `AndroidManifest.xml` with a new activity CustomDeeplinksActivity:

```xml
 <config-file target="AndroidManifest.xml" parent="/manifest/application">
     <activity android:exported="true"
               android:name="com.cordova.deeplinks.plugin.CustomDeeplinksActivity"
               android:theme="@android:style/Theme.NoDisplay">
         <intent-filter android:autoVerify="true">
             <action android:name="android.intent.action.VIEW" />
             <category android:name="android.intent.category.DEFAULT" />
             <category android:name="android.intent.category.BROWSABLE" />
             <data android:scheme="https" android:host="$APP_HOST" android:pathPrefix="$APP_PATHPATTERN" />
             <data android:scheme="http" android:host="$APP_HOST" android:pathPrefix="$APP_PATHPATTERN" />
         </intent-filter>
     </activity>
 </config-file>
```

Make sure your domain is properly verified for **App Links** in your assetlinks.json.

## 🍏 iOS Configuration

The plugin sets up Universal Links by modifying the entitlements:

```xml
<key>com.apple.developer.associated-domains</key>
<array>
  <string>applinks:your app host</string>
</array>
```

Also handles universal link routing inside the `AppDelegate`.

## 🧪 Example Test Link

```
https://<your app host>/<your app path pattern>/some page
```

---

Developed and maintained by [MiguelRosaDev](https://github.com/MiguelRosaDev)
