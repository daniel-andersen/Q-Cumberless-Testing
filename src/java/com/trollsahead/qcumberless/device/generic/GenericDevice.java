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
import com.trollsahead.qcumberless.gui.elements.Element;
import com.trollsahead.qcumberless.util.Util;

import static com.trollsahead.qcumberless.engine.ExecutionHelper.ExecutionStopper;
import static com.trollsahead.qcumberless.gui.Images.ThumbnailState;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericDevice extends Device {
    private static final Pattern patternStarting = Pattern.compile("Executing tasks");
    private static final Pattern patternStartingFeature = Pattern.compile("(\\s*)Feature: (.*)");
    private static final Pattern patternStartingBackground = Pattern.compile("(\\s*)Background: (.*)");
    private static final Pattern patternStartingScenario = Pattern.compile("(\\s*)Scenario: (.*)(\\s*)#(.*)");
    private static final Pattern patternStartingScenarioOutline = Pattern.compile("(\\s*)Scenario Outline: (.*)(\\s*)#(.*)");
    private static final Pattern patternStartingOutlineTable = Pattern.compile("(\\s*)Outline table(\\s*)");
    private static final Pattern patternStartingTableRow = Pattern.compile("(\\s*)Table row: (.*)");
    private static final Pattern patternRunningStep = Pattern.compile("(\\s*)Step: (.*)");
    private static final Pattern patternStepFailed = Pattern.compile("(\\s*)Step failed: (.*)");
    private static final Pattern patternScreenshotBeingTakenMessage = Pattern.compile("(.*)Taking screenshoot to (.*) from device(.*)");
    private static final Pattern patternScreenshotTakenMessage = Pattern.compile("(.*)Screenshot taken(.*)");

    private static BufferedImage thumbnailNormal;
    private static BufferedImage thumbnailHighlight;
    private static BufferedImage thumbnailPressed;

    private final Set<Capability> capabilities;
    private DeviceCallback deviceCallback;

    private String screenshotFilename = "";
    private Element screenshotElement = null;
    
    private ExecutionStopper executionStopper = new ExecutionStopper();
    
    private boolean isRunning = false;

    static {
        try {
            thumbnailNormal = ImageIO.read(GenericDevice.class.getResource("/resources/pictures/generic_device_normal.png"));
            thumbnailHighlight = ImageIO.read(GenericDevice.class.getResource("/resources/pictures/generic_device_highlight.png"));
            thumbnailPressed = ImageIO.read(GenericDevice.class.getResource("/resources/pictures/generic_device_pressed.png"));
        } catch (Exception e) {
            throw new RuntimeException("Q-Cumberless Testing refused to start", e);
        }
    }

    public GenericDevice() {
        reset();
        capabilities = new HashSet<Capability>();
        capabilities.add(Capability.PLAY);
    }

    private void reset() {
        isRunning = false;
        executionStopper = new ExecutionStopper();
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

    public void play(List<StringBuilder> features, Set<String> tags) {
        if (isRunning) {
            return;
        }
        try {
            reset();
            isRunning = true;
            deviceCallback.onPlay();
            GenericDeviceHelper.runTests(features, tags, deviceLogListener, executionStopper);
            deviceCallback.afterPlayed();
        } catch (Exception e) {
            e.printStackTrace();
            deviceLogListener.logLine(Util.stacktraceToString(e));
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
        executionStopper.stop();
    }

    private final LogListener deviceLogListener = new LogListener() {
        public void start() {
        }

        public void finish() {
        }

        public void logLine(String log) {
            deviceCallback.logLine(log);
            checkStarting(log);
            checkStartingFeature(log);
            checkStartingBackground(log);
            checkStartingScenario(log);
            checkStartingScenarioOutline(log);
            checkRunningStep(log);
            checkStartingOutlineTable(log);
            checkStartingTableRow(log);
            checkStepFailed(log);
            checkScreenshotBeingTaken(log);
            checkScreenshotTaken(log);
        }

        public void error(Throwable t) {
            deviceCallback.afterPlayFailed(t.getMessage());
        }
    };

    protected void checkStarting(String log) {
        Matcher matcher = getPatternStarting().matcher(log);
        if (matcher.find()) {
            deviceCallback.beforeFeatures();
        }
    }

    protected void checkStartingFeature(String log) {
        Matcher matcher = getPatternStartingFeature().matcher(log);
        if (matcher.find()) {
            deviceCallback.beforeFeature(matcher.group(2).trim());
        }
    }

    protected void checkStartingBackground(String log) {
        Matcher matcher = getPatternStartingBackground().matcher(log);
        if (matcher.find()) {
            deviceCallback.beforeBackground(matcher.group(2).trim());
        }
    }

    protected void checkStartingScenario(String log) {
        Matcher matcher = getPatternStartingScenario().matcher(log);
        if (matcher.find()) {
            deviceCallback.beforeScenario(matcher.group(2).trim());
        }
    }

    protected void checkStartingScenarioOutline(String log) {
        Matcher matcher = getPatternStartingScenarioOutline().matcher(log);
        if (matcher.find()) {
            deviceCallback.beforeScenario(matcher.group(2).trim());
        }
    }

    protected void checkStartingOutlineTable(String log) {
        Matcher matcher = getPatternStartingOutlineTable().matcher(log);
        if (matcher.find()) {
            deviceCallback.beforeOutlineTable();
        }
    }

    protected void checkStartingTableRow(String log) {
        Matcher matcher = getPatternStartingTableRow().matcher(log);
        if (matcher.find()) {
            deviceCallback.beforeTableRow(matcher.group(2).trim());
        }
    }

    protected void checkRunningStep(String log) {
        Matcher matcher = getPatternRunningStep().matcher(log);
        if (matcher.find()) {
            deviceCallback.beforeStep(matcher.group(2).trim());
        }
    }

    protected void checkStepFailed(String log) {
        Matcher matcher = getPatternStepFailed().matcher(log);
        if (matcher.find()) {
            deviceCallback.afterStepFailed(matcher.group(2));
        }
    }

    protected void checkScreenshotBeingTaken(String log) {
        final Element currentElement = deviceCallback.getCurrentElement();
        if (currentElement == null) {
            return;
        }
        Matcher matcher = getPatternScreenshotBeingTakenMessage().matcher(log);
        if (matcher.find()) {
            screenshotFilename = matcher.group(2);
            screenshotElement = currentElement;
        }
    }

    protected void checkScreenshotTaken(String log) {
        Matcher matcher = getPatternScreenshotTakenMessage().matcher(log);
        if (matcher.find()) {
            downloadScreenshots();
        }
    }

    protected void downloadScreenshots() {
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

    protected Pattern getPatternStarting() {
        return patternStarting;
    }

    protected Pattern getPatternStartingFeature() {
        return patternStartingFeature;
    }

    protected Pattern getPatternStartingBackground() {
        return patternStartingBackground;
    }

    protected Pattern getPatternStartingScenario() {
        return patternStartingScenario;
    }

    protected Pattern getPatternStartingScenarioOutline() {
        return patternStartingScenarioOutline;
    }

    protected Pattern getPatternRunningStep() {
        return patternRunningStep;
    }

    protected Pattern getPatternStartingOutlineTable() {
        return patternStartingOutlineTable;
    }

    protected Pattern getPatternStartingTableRow() {
        return patternStartingTableRow;
    }

    protected Pattern getPatternStepFailed() {
        return patternStepFailed;
    }

    protected Pattern getPatternScreenshotBeingTakenMessage() {
        return patternScreenshotBeingTakenMessage;
    }

    protected Pattern getPatternScreenshotTakenMessage() {
        return patternScreenshotTakenMessage;
    }
}
