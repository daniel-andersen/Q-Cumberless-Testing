#!/usr/bin/bash
cd ../.. && \
ant && \
cd example/calabash-android-plugin && \
ant && \
cp src/resources/qcumberless.conf dist/ && \
cp ../../dist/qcumberless.jar dist/ && \
cd dist && \
java -cp qcumberless.jar:calabash_android_plugin.jar:pdf_report_plugin.jar:gnujpdf.jar com.trollsahead.qcumberless.Main
