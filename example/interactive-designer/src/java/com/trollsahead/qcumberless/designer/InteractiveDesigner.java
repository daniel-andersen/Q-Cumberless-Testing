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
import android.test.InstrumentationTestCase;
import android.os.Environment;

import android.view.Display;
import android.view.View;
import android.widget.*;
import com.jayway.android.robotium.solo.Solo;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InteractiveDesigner extends InstrumentationTestCase {
    private static final String INSTRUMENTATION_CLASS = "com.example.helloworld.HelloWorld";
    
    private static final String stepClick = "Given I press the \"$1\" button";
    private static final String stepText = "Then I see the text \"$1\"";
    private static final String stepSpinnerItem = "Given I select \"$1\" from \"$2\"";

    private static String lastPrefix;
    private static Spinner clickedSpinner;

    private static final String LOG_PREFIX = "InteractiveDesigner: ";

    private Solo solo;

    public void testInteractiveDesigner() {
        initialize();

        addStep("Given My app is running");

        logLine("Started!");

        ScreenshotThread screenshotThread = new ScreenshotThread();
        try {
            String command = null;
            new Thread(screenshotThread).start();
            while (!"STOP".equals(command)) {
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

    private void logLine(String s) {
        System.out.println(LOG_PREFIX + s);
    }

    private String getCommand() {
        File command = getCommandFile();
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
        logLine("Command: " + command);
        Matcher matcher = Pattern.compile("CLICK\\((\\d*),(\\d*)\\)").matcher(command);
        if (matcher.find()) {
            click(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
        }
        File commandFile = getCommandFile();
        commandFile.delete();
    }

    private File getCommandFile() {
        return new File(Environment.getExternalStorageDirectory() + "/Interactive-Designer/command.txt");
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

        lastPrefix = null;

        solo = new Solo(getInstrumentation(), currentActivity);
    }

    private void click(int x, int y) {
        try {
            View rootView = solo.getCurrentViews().get(0).getRootView();
            Button button = getClickedView(x, y, solo.getCurrentButtons());
            if (button != null) {
                clickButton(button);
                return;
            }
            Spinner spinner = getClickedView(x, y, solo.getCurrentSpinners());
            if (spinner != null) {
                clickSpinner(spinner);
                return;
            }
            RadioButton radioButton = getClickedView(x, y, solo.getCurrentRadioButtons());
            if (radioButton != null) {
                System.out.println("CLICKED RADIO BUTTON!!!");
                //clickRadioButton(radioButton);
                return;
            }
            ToggleButton toggleButton = getClickedView(x, y, solo.getCurrentToggleButtons());
            if (toggleButton != null) {
                System.out.println("CLICKED TOGGLE BUTTON!!!");
                //clickRadioButton(radioButton);
                return;
            }
            TextView textView = getClickedView(x, y, solo.getCurrentTextViews(rootView));
            if (textView != null) {
                clickTextView(textView);
                return;
            }
            System.out.println("Nothing found");
        } finally {
            solo.clickOnScreen(x, y);
        }
    }

    private <T extends View> T getClickedView(int x, int y, List<T> views) {
        for (T view : views) {
            int[] xy = new int[2];
            view.getLocationOnScreen(xy);
            if (x >= xy[0] && y >= xy[1] && x <= xy[0] + view.getWidth() && y <= xy[1] + view.getHeight()) {
                return view;
            }
        }
        return null;
    }
    
    private void clickButton(Button button) {
        addStep(stepClick.replaceAll("\\$1", button.getText().toString()));
    }

    private void clickTextView(TextView textView) {
        if (textView instanceof CheckedTextView) {
            addStep(stepSpinnerItem
                    .replaceAll("\\$1", textView.getText().toString())
                    .replaceAll("\\$2", clickedSpinner.getContentDescription().toString()));
        } else {
            addStep(stepText.replaceAll("\\$1", textView.getText().toString()));
        }
    }

    private void clickSpinner(Spinner spinner) {
        clickedSpinner = spinner;
    }

    private void addStep(String step) {
        int prefixIndex = step.indexOf(" ");
        String prefix = step.substring(0, prefixIndex);
        if (prefix.equalsIgnoreCase(lastPrefix)) {
            step = "And" + step.substring(prefixIndex);
        }
        lastPrefix = prefix;
        System.out.println(LOG_PREFIX + "Step: " + step);
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
            logLine("Screenshot: \"" + filename + "\"" + " - (" + xy[0] + "," + xy[1] + "-" + rootView.getWidth() + "," + rootView.getHeight() + "-" + display.getWidth() + "," + display.getHeight() + ")");
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
