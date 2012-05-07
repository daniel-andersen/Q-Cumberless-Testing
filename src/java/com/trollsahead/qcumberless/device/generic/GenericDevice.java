package com.trollsahead.qcumberless.device.generic;

import com.trollsahead.qcumberless.device.Device;
import com.trollsahead.qcumberless.device.DeviceCallback;
import com.trollsahead.qcumberless.engine.LogListener;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
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
    private static final Pattern patternScreenshotMessage = Pattern.compile("(\\s*)Screenshot: (.*)");

    private static BufferedImage thumbnailNormal;
    private static BufferedImage thumbnailHighlight;
    private static BufferedImage thumbnailPressed;

    private final Set<Capability> capabilities;
    private DeviceCallback deviceCallback;

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
            deviceCallback.onPlay();
            GenericDeviceHelper.runTests(feature, "singlerun.feature", tags, deviceLogListener);
            deviceCallback.afterPlayed();
        } catch (Exception e) {
            e.printStackTrace();
            deviceCallback.afterPlayFailed(e.getMessage());
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

        private void notifyScreenshotTaken(String log) {
            Matcher matcher = patternScreenshotMessage.matcher(log);
            if (matcher.find()) {
                //downloadScreenshots(matcher.group(2));
            }
        }
    };

    public void starting() {
    }

    public void ending() {
    }

    public void errorOccurred(String msg, Throwable t) {
    }
}
