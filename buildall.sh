#!/usr/bin/bash
set -e
ant
cd example/calabash-android-plugin
ant
cd ../android
ant release
calabash-android build
