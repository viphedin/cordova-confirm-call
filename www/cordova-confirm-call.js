module.exports =
{
    init: function (phoneNumber, targetUrl, token, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            "ConfirmCall",
            "init",
            [phoneNumber, targetUrl, token]
        );
    },
};