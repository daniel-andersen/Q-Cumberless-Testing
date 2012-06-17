#!/usr/bin/bash
cd ../interactive-designer && \
ant clean release && \
cd ../calabash-android-plugin && \
ant && \
cp src/resources/qcumberless.conf dist/ && \
cp ../../dist/qcumberless.jar dist/ && \
cd dist && \
java -cp qcumberless.jar:calabash_android_plugin.jar com.trollsahead.qcumberless.Main
