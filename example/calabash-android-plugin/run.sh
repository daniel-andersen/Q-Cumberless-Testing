#!/usr/bin/bash
ant && cp src/resources/qcumberless.conf dist/ && cp ../../dist/qcumberless.jar dist/ && cd dist && java -cp qcumberless.jar:calabash_android_plugin.jar com.trollsahead.qcumberless.Main
