#!/usr/bin/env node

const fs = require('fs');
const version = require('./package.json').version;
console.log('Updating version to', version);
let data = fs.readFileSync('./plugin.xml', "utf8");
data = data.replace(/plugin id="cordova-confirm-call" version="[^"]+"/, `plugin id="cordova-confirm-call" version="${version}"`);
console.log('Updated!');
fs.writeFileSync('./plugin.xml', data);