import { Injectable } from '@angular/core';
import { Plugin, IonicNativePlugin, Cordova } from '@ionic-native/core';

@Plugin({
    pluginName: 'ConfirmCall', // should match the name of the wrapper class
    plugin: 'cordova-confirm-call', // NPM package name
    pluginRef: 'cordova.plugins.confirm.call', // name of the object exposed by the plugin
    platforms: ['Android'] // supported platforms
})

@Injectable()
export class ConfirmCall extends IonicNativePlugin {

    @Cordova()
    init(): Promise<any> { return; }
}