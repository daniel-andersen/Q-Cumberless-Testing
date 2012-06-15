// Copyright (c) 2012, Daniel Andersen (dani_ande@yahoo.dk)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
// 3. The name of the author may not be used to endorse or promote products derived
//    from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.trollsahead.qcumberless.designer;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.InstrumentationTestCase;
import android.os.Environment;

import com.jayway.android.robotium.solo.Solo;

import java.io.File;

public class InteractiveDesigner extends InstrumentationTestCase {
    private static final String INSTRUMENTATION_CLASS = "com.example.helloworld.HelloWorld";

    private Instrumentation instrumentation;
    private Solo solo;

    public void testInteractiveDesigner() {
        initialize();

        try {
            String command = null;
            while ("STOP".equals(command)) {
                Thread.sleep(100);
                command = getCommand();
                if (command == null) {
                    continue;
                }
                break; // TODO!
            }
        } catch (Exception e) {
            throw new RuntimeException("Test error", e);
        } finally {
            solo.finishOpenedActivities();
        }
    }

    private String getCommand() {
        return null;
    }
    
    private void initialize() {
        File directory = new File(Environment.getExternalStorageDirectory() + "/Interactive-Designer");
        directory.mkdir();
        deleteFilesInDirectory(directory);

        Instrumentation instrumentation = getInstrumentation();
        Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(INSTRUMENTATION_CLASS, null, false);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(instrumentation.getTargetContext(), INSTRUMENTATION_CLASS);
        instrumentation.startActivitySync(intent);

        Activity currentActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5);
        if (currentActivity == null) {
            throw new RuntimeException("Activity not started");
        }

        solo = new Solo(getInstrumentation(), currentActivity);
    }

    private void takeScreenshot() {
        deleteScreenshots();
        solo.takeScreenshot();
    }

    private void deleteScreenshots() {
        deleteFilesInDirectory(new File(Environment.getExternalStorageDirectory() + "/Robotium-Screenshots"));
    }

    private void deleteFilesInDirectory(File directory) {
        for (File file : directory.listFiles()) {
            file.delete();
        }
    }

    private String getScreenshotFilename() {
        File directory = new File(Environment.getExternalStorageDirectory() + "/Robotium-Screenshots");
        File[] files = directory.listFiles();
        if (files == null || files.length != 1) {
            return null;
        } else {
            return files[0].getAbsolutePath();
        }
    }
}
