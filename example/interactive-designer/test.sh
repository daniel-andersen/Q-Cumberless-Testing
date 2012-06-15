#!/usr/bin/bash
ant clean release && \
adb install -r bin/InteractiveDesigner-release.apk && \
adb shell am instrument -w com.trollsahead.qcumberless.designer/android.test.InstrumentationTestRunner
