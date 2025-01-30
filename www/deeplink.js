var exec = require('cordova/exec');

var DeeplinksPlugin = {
    updateWidget: function(successCallback, errorCallback) {
        exec(successCallback, errorCallback, "DeeplinksPlugin", "getDeepLink", []);
    }
};

module.exports = DeeplinksPlugin;
