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
import android.graphics.Bitmap;
import android.graphics.Point;
import android.test.InstrumentationTestCase;
import android.os.Environment;

import android.view.Display;
import android.view.View;
import com.jayway.android.robotium.solo.Solo;

import java.io.*;

public class InteractiveDesigner extends InstrumentationTestCase {
    private static final String INSTRUMENTATION_CLASS = "com.example.helloworld.HelloWorld";

    private static final String LOG_PREFIX = "InteractiveDesigner: ";

    private Solo solo;

    public void testInteractiveDesigner() {
        initialize();

        ScreenshotThread screenshotThread = new ScreenshotThread();
        try {
            String command = null;
            new Thread(screenshotThread).start();
            while (!"STOP".equals(command)) {
                System.out.println("Waiting for command...");
                Thread.sleep(100);
                command = getCommand();
                if (command == null) {
                    continue;
                }
                performCommand(command);
            }
        } catch (Exception e) {
            throw new RuntimeException("Test error", e);
        } finally {
            solo.finishOpenedActivities();
            screenshotThread.stop();
        }
    }

    private String getCommand() {
        File command = new File(Environment.getExternalStorageDirectory() + "/Interactive-Designer/command.txt");
        if (!command.exists()) {
            return null;
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(command), "UTF8"));
            return in.readLine();
        } catch (Exception e) {
            // Ignore!
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    // Ignore!
                }
            }
        }
        return null;
    }

    private void performCommand(String command) {
        System.out.println("Performing command: " + command);
        // TODO! Commands
        File directory = new File(Environment.getExternalStorageDirectory() + "/Interactive-Designer");
        deleteFilesInDirectory(directory);
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

        System.out.println(LOG_PREFIX + "Started!");
    }

    public void takeScreenshot() {
        int[] xy = new int[2];
        Display display = solo.getCurrentActivity().getWindowManager().getDefaultDisplay();
        View rootView = solo.getCurrentViews().get(0).getRootView();
        rootView.getLocationOnScreen(xy);
        rootView.setDrawingCacheEnabled(true);
        rootView.buildDrawingCache();
        Bitmap bitmap = rootView.getDrawingCache();
        if (bitmap == null) {
            throw new RuntimeException("Unable to take screenshot");
        }
        try {
            String filename = Environment.getExternalStorageDirectory() + "/Interactive-Designer/screenshot_" + System.currentTimeMillis() + ".png";
            File screenshot = new File(filename);
            screenshot.createNewFile();
            FileOutputStream out = new FileOutputStream(screenshot);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            filename = filename.startsWith("/mnt") ? filename.substring("/mnt".length()) : filename;
            System.out.println(LOG_PREFIX + "Screenshot: \"" + filename + "\"" + " - (" + xy[0] + "," + xy[1] + "-" + rootView.getWidth() + "," + rootView.getHeight() + "-" + display.getWidth() + "," + display.getHeight() + ")");
        } catch (Exception e) {
            throw new RuntimeException("Could not take screenshot", e);
        }
    }

    private void deleteFilesInDirectory(File directory) {
        for (File file : directory.listFiles()) {
            file.delete();
        }
    }

    private class ScreenshotThread implements Runnable {
        private boolean stopped = false;

        public void stop() {
            stopped = true;
        }

        public void run() {
            while (!stopped) {
                takeScreenshot();
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    // Ignore!
                }
            }
        }
    }
}
