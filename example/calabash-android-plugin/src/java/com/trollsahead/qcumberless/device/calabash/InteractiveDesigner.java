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

package com.trollsahead.qcumberless.device.calabash;

import com.trollsahead.qcumberless.device.InteractiveDesignerCallback;
import com.trollsahead.qcumberless.device.InteractiveDesignerClient;
import com.trollsahead.qcumberless.engine.ExecutionHelper;
import com.trollsahead.qcumberless.engine.LogListener;
import com.trollsahead.qcumberless.util.Util;

import javax.imageio.ImageIO;

import static com.trollsahead.qcumberless.engine.ExecutionHelper.ExecutionStopper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InteractiveDesigner implements InteractiveDesignerClient {
    private static final String commandMake = "ant clean release";
    private static final String commandInstall = " install -r bin/InteractiveDesigner-release.apk";
    private static final String commandCleanLog = " -s $1 logcat -c";
    private static final String commandInstrument = " shell am instrument -w com.trollsahead.qcumberless.designer/android.test.InstrumentationTestRunner";
    private static final String commandMonitorLog = " -s $1 logcat -v time";

    private static final Pattern patternInteractiveDesigner = Pattern.compile(".*InteractiveDesigner: .*");
    private InteractiveDesignerCallback interactiveDesignerCallback;

    private ExecutionStopper logExecutionStopper;

    private static enum Command {STOP}

    private static String deviceId = null;

    private static boolean started;

    public void setCallback(InteractiveDesignerCallback interactiveDesignerCallback) {
        this.interactiveDesignerCallback = interactiveDesignerCallback;
    }

    public void start() {
        started = false;
        deviceId = AndroidHelper.getUniqueDevice();

        interactiveDesignerCallback.message("Compiling instrumentation...");
        ExecutionHelper.executeCommand(commandMake, "../../interactive-designer");

        interactiveDesignerCallback.message("Installing instrumentation...");
        ExecutionHelper.executeCommand(AndroidHelper.getPathToAdb() + commandInstall, "../../interactive-designer");

        interactiveDesignerCallback.message("Cleaning log...");
        ExecutionHelper.executeCommand(AndroidHelper.getPathToAdb() + commandCleanLog.replaceAll("\\$1", deviceId), "../../interactive-designer");

        interactiveDesignerCallback.message("Starting instrumentation...");
        new Thread(new Runnable() {
            public void run() {
                logExecutionStopper = new ExecutionStopper();
                ExecutionHelper.executeCommand(AndroidHelper.getPathToAdb() + commandMonitorLog.replaceAll("\\$1", deviceId), logListener, logExecutionStopper);
            }
        }).start();
        new Thread(new Runnable() {
            public void run() {
                ExecutionHelper.executeCommand(AndroidHelper.getPathToAdb() + commandInstrument);
            }
        }).start();

        waitUntilStarted();
        interactiveDesignerCallback.message("Started!");
    }

    public void stop() {
        pushCommand(Command.STOP);
        if (logExecutionStopper != null) {
            logExecutionStopper.stop();
        }
    }

    public void click(float percentX, float percentY) {
    }

    public void assertText(float percentX, float percentY) {
    }

    private void waitUntilStarted() {
        int i = 0;
        while (!started) {
            if (i++ > 100) {
                throw new RuntimeException("Instrumentation did not start");
            }
            Util.sleep(100);
        }
        System.out.println("Interactive Designer started!");
    }

    private void pushCommand(Command command) {
        pushCommandWithArguments(command);
    }

    private void pushCommandWithArguments(Command command, String ... arguments) {
        StringBuilder sb = new StringBuilder();
        sb.append(command.name()).append("\n");
        AndroidHelper.pushFileWithContent(deviceId, "command.txt", sb);
    }

    private LogListener logListener = new LogListener() {
        public void start() {
        }

        public void finish() {
        }

        public void logLine(String log) {
            if (Util.isEmpty(log)) {
                return;
            }
            Matcher matcher = patternInteractiveDesigner.matcher(log);
            if (!matcher.matches()) {
                return;
            }
            checkStarted(log);
            extractScreenshot(log);
        }

        private void checkStarted(String log) {
            Matcher matcher = Pattern.compile(".*Started!.*").matcher(log);
            if (matcher.matches()) {
                started = true;
            }
        }

        private void extractScreenshot(String log) {
            Matcher matcher = Pattern.compile(".*Screenshot: \"(.*)\" - \\((\\d*),(\\d*)-(\\d*),(\\d*)-(\\d*),(\\d*)\\)").matcher(log);
            if (matcher.find()) {
                downloadScreenshot(
                        matcher.group(1),
                        Integer.parseInt(matcher.group(2)),
                        Integer.parseInt(matcher.group(3)),
                        Integer.parseInt(matcher.group(4)),
                        Integer.parseInt(matcher.group(5)),
                        Integer.parseInt(matcher.group(6)),
                        Integer.parseInt(matcher.group(7))
                );
            }
        }

        public void error(Throwable t) {
        }
    };
    
    private void downloadScreenshot(final String filename, final int x, final int y, final int width, final int height, final int screenWidth, final int screenHeight) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    interactiveDesignerCallback.screenshot(ImageIO.read(AndroidHelper.downloadFile(deviceId, filename)), x, y, width, height, screenWidth, screenHeight);
                } catch (Exception e) {
                    throw new RuntimeException("Could not download screenshot", e);
                }
            }
        }).start();
    }
}
