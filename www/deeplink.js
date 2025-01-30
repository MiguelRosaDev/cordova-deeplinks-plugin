var exec = require('cordova/exec');

var DeeplinksPlugin = {
    getDeepLink: function(successCallback, errorCallback) {
        exec(successCallback, errorCallback, "DeeplinksPlugin", "getDeepLink", []);
    }
};

module.exports = DeeplinksPlugin;
