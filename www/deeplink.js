var exec = require('cordova/exec');

var DeeplinkPlugin = {
    updateWidget: function(successCallback, errorCallback) {
        exec(successCallback, errorCallback, "DeepLinksActivity", "getDeepLink", []);
    }
};

module.exports = DeeplinkPlugin;
