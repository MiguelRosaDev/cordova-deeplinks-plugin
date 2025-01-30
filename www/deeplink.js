var exec = require('cordova/exec');

var DeeplinkPlugin = {
    updateWidget: function(successCallback, errorCallback) {
        exec(successCallback, errorCallback, "DeeplinksPlugin", "getDeepLink", []);
    }
};

module.exports = DeeplinksPlugin;
