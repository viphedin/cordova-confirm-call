module.exports =
{
    init: function (successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            "ConfirmCall",
            "init",
            []
        );
    },
};