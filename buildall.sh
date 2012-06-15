#!/usr/bin/bash
set -e
ant
cd example/calabash-android-plugin
ant
cd ../helloworld
ant release
calabash-android build
