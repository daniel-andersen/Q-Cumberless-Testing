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

package com.trollsahead.qcumberless.device.generic;

import com.trollsahead.qcumberless.device.Device;
import com.trollsahead.qcumberless.device.DeviceCallback;
import com.trollsahead.qcumberless.engine.LogListener;
import com.trollsahead.qcumberless.gui.Element;
import com.trollsahead.qcumberless.util.Util;

import static com.trollsahead.qcumberless.gui.Images.ThumbnailState;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericDevice extends Device {
    private static final Pattern patternStarting = Pattern.compile("Running the cucumber tests");
    private static final Pattern patternStartingFeature = Pattern.compile("(\\s*)Feature: (.*)");
    private static final Pattern patternStartingBackground = Pattern.compile("(\\s*)Background: (.*)");
    private static final Pattern patternStartingScenario = Pattern.compile("(.*)Scenario: (.*)(\\s*)#(.*)");
    private static final Pattern patternRunningStep = Pattern.compile("(\\s*)Step: (.*)");
    private static final Pattern patternStepFailed = Pattern.compile("(\\s*)Step failed: (.*)");
    private static final Pattern patternScreenshotBeingTakenMessage = Pattern.compile("(\\s*)Taking screenshoot to (.*) from device(.*)");
    private static final Pattern patternScreenshotTakenMessage = Pattern.compile("(\\s*)Screenshot taken(.*)");

    private static BufferedImage thumbnailNormal;
    private static BufferedImage thumbnailHighlight;
    private static BufferedImage thumbnailPressed;

    private final Set<Capability> capabilities;
    private DeviceCallback deviceCallback;

    private String screenshotFilename = "";
    private Element screenshotElement = null;
    
    private boolean isRunning = false;

    static {
        try {
            thumbnailNormal = ImageIO.read(GenericDevice.class.getResource("/resources/pictures/generic_device_normal.png"));
            thumbnailHighlight = ImageIO.read(GenericDevice.class.getResource("/resources/pictures/generic_device_highlight.png"));
            thumbnailPressed = ImageIO.read(GenericDevice.class.getResource("/resources/pictures/generic_device_pressed.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GenericDevice() {
        reset();
        capabilities = new HashSet<Capability>();
        capabilities.add(Capability.PLAY);
    }

    private void reset() {
        isRunning = false;
    }

    public void setDeviceCallback(DeviceCallback deviceCallback) {
        this.deviceCallback = deviceCallback;
    }

    public Set<Capability> getCapabilities() {
        return capabilities;
    }

    public Image getThumbnail(ThumbnailState thumbnailState) {
        if (thumbnailState == ThumbnailState.HIGHLIGHTED) {
            return thumbnailHighlight;
        } else if (thumbnailState == ThumbnailState.PRESSED) {
            return thumbnailPressed;
        } else {
            return thumbnailNormal;
        }
    }

    public String name() {
        return "Generic Device";
    }

    public void play(StringBuilder feature, Set<String> tags) {
        if (isRunning) {
            return;
        }
        try {
            reset();
            isRunning = true;
            deviceCallback.onPlay();
            GenericDeviceHelper.runTests(feature, "singlerun.feature", tags, deviceLogListener);
            deviceCallback.afterPlayed();
        } catch (Exception e) {
            e.printStackTrace();
            deviceCallback.afterPlayFailed(e.getMessage());
        } finally {
            isRunning = false;
        }
    }

    public void pause() {
    }

    public void resume() {
    }

    public void stop() {
    }

    private final LogListener deviceLogListener = new LogListener() {
        public void start() {
        }

        public void finish() {
        }

        public void logLine(String log) {
            notifyStarting(log);
            notifyStartingFeature(log);
            notifyStartingBackground(log);
            notifyStartingScenario(log);
            notifyRunningStep(log);
            notifyStepFailed(log);
            notifyScreenshotBeingTaken(log);
            notifyScreenshotTaken(log);
        }

        public void error(Throwable t) {
            deviceCallback.afterPlayFailed(t.getMessage());
        }

        private void notifyStarting(String log) {
            Matcher matcher = patternStarting.matcher(log);
            if (matcher.find()) {
                deviceCallback.beforeFeatures();
            }
        }

        private void notifyStartingFeature(String log) {
            Matcher matcher = patternStartingFeature.matcher(log);
            if (matcher.find()) {
                deviceCallback.beforeFeature(matcher.group(2).trim());
            }
        }

        private void notifyStartingBackground(String log) {
            Matcher matcher = patternStartingBackground.matcher(log);
            if (matcher.find()) {
                deviceCallback.beforeBackground(matcher.group(2).trim());
            }
        }

        private void notifyStartingScenario(String log) {
            Matcher matcher = patternStartingScenario.matcher(log);
            if (matcher.find()) {
                deviceCallback.beforeScenario(matcher.group(2).trim());
            }
        }

        private void notifyRunningStep(String log) {
            Matcher matcher = patternRunningStep.matcher(log);
            if (matcher.find()) {
                deviceCallback.beforeStep(matcher.group(2).trim());
            }
        }

        private void notifyStepFailed(String log) {
            Matcher matcher = patternStepFailed.matcher(log);
            if (matcher.find()) {
                deviceCallback.afterStepFailed(matcher.group(2));
            }
        }

        private void notifyScreenshotBeingTaken(String log) {
            final Element currentElement = deviceCallback.getCurrentElement();
            if (currentElement == null) {
                return;
            }
            Matcher matcher = patternScreenshotBeingTakenMessage.matcher(log);
            if (matcher.find()) {
                screenshotFilename = matcher.group(2);
                screenshotElement = currentElement;
            }
        }

        private void notifyScreenshotTaken(String log) {
            Matcher matcher = patternScreenshotTakenMessage.matcher(log);
            if (matcher.find()) {
                downloadScreenshots();
            }
        }

        private void downloadScreenshots() {
            final Element element = screenshotElement;
            if (element == null) {
                return;
            }
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Image screenshot = ImageIO.read(new File(Util.addSlashToPath(GenericDeviceHelper.getPath()) + screenshotFilename));
                        deviceCallback.attachScreenshots(element, screenshot);
                    } catch (Exception e) {
                        System.out.println("Error while loading screenshots!");
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };
}
