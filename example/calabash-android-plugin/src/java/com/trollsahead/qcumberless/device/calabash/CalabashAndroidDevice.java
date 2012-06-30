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

import com.trollsahead.qcumberless.device.InteractiveDesignerClient;
import com.trollsahead.qcumberless.device.generic.GenericDevice;
import com.trollsahead.qcumberless.engine.DesignerEngine;
import com.trollsahead.qcumberless.engine.Player;
import com.trollsahead.qcumberless.gui.elements.Element;
import com.trollsahead.qcumberless.gui.elements.StepElement;
import com.trollsahead.qcumberless.model.Screenshot;
import com.trollsahead.qcumberless.util.ConfigurationManager;
import com.trollsahead.qcumberless.util.FileUtil;
import com.trollsahead.qcumberless.util.Util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.trollsahead.qcumberless.gui.Images.ThumbnailState;

public class CalabashAndroidDevice extends GenericDevice {
    private static final Pattern patternStarting = Pattern.compile("Feature: .*");
    private static final Pattern patternScreenshotBeingTakenMessage = Pattern.compile("(.*)Taking screenshoot to (.*) from device(.*)");
    private static final Pattern patternScreenshotTakenMessage = Pattern.compile("(.*)Screenshot taken(.*)");

    private static final Object LOADING_SCREENSHOT_LOCK = new Object();

    private static BufferedImage thumbnailNormal;
    private static BufferedImage thumbnailHighlight;
    private static BufferedImage thumbnailPressed;

    private final Set<Capability> capabilities;

    private String screenshotDir;
    private long screenshotTime = 0;

    static {
        try {
            thumbnailNormal = ImageIO.read(CalabashAndroidDevice.class.getResource("/resources/pictures/calabash_android_device_normal.png"));
            thumbnailHighlight = ImageIO.read(CalabashAndroidDevice.class.getResource("/resources/pictures/calabash_android_device_highlight.png"));
            thumbnailPressed = ImageIO.read(CalabashAndroidDevice.class.getResource("/resources/pictures/calabash_android_device_pressed.png"));
        } catch (Exception e) {
            throw new RuntimeException("Calabash Android-plugin refused to start", e);
        }
    }

    public CalabashAndroidDevice() {
        super();
        capabilities = new HashSet<Capability>();
        capabilities.add(Capability.PLAY);
        capabilities.add(Capability.STEP);
        capabilities.add(Capability.INTERACTIVE_DESIGNING);
        screenshotDir = ConfigurationManager.get("screenshotsDirectory");
        getAndUpdateNewestScreenshot();
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
        return "Calabash Android";
    }

    public void initializeStepMode() {
        FileUtil.deleteFile(getStepCommandFilename());
        List<StringBuilder> features = new LinkedList<StringBuilder>();
        StringBuilder feature = new StringBuilder();
        feature.append("Feature: Step Mode\n");
        feature.append("\tScenario: Step Mode\n");
        feature.append("\t\tGiven ").append(getStepPauseDefinition()).append("\n");
        features.add(feature);
        DesignerEngine.runInSingleStepMode(features);
    }

    public void resumeFromStepMode() {
        pushCommandToFile("PLAY");
    }

    public void step() {
        pushCommandToFile("STEP");
    }

    public void step(StepElement stepElement) {
        pushCommandToFile(stepElement.getTitleWithoutPrefix());
    }

    public String getStepPauseDefinition() {
        return "I'm in step mode";
    }

    public void stop() {
        if (Player.isStepMode()) {
            pushCommandToFile("STOP");
        }
        super.stop();
    }

    public void play(List<StringBuilder> features, Set<String> tags) {
        FileUtil.deleteFile(getStepCommandFilename());
        super.play(features, tags);
    }

    protected boolean isFinalizing() {
        synchronized (LOADING_SCREENSHOT_LOCK) {
            Util.sleep(10); // Just for the sake! ;)
        }
        return false;
    }

    public InteractiveDesignerClient getInteractiveDesignerClient() {
        return new InteractiveDesigner();
    }

    protected void checkStepFailed(String log) {
        Matcher matcher = getPatternStepFailed().matcher(log);
        if (matcher.find()) {
            downloadScreenshot();
            deviceCallback.afterStepFailed(matcher.group(1));
        }
    }

    protected Pattern getPatternStarting() {
        return patternStarting;
    }
    
    protected Pattern getPatternScreenshotBeingTakenMessage() {
        return patternScreenshotBeingTakenMessage;
    }
    
    protected Pattern getPatternScreenshotTakenMessage() {
        return patternScreenshotTakenMessage;
    }

    private String findNewestScreenshot() {
        List<String> screenshots = FileUtil.traverseDirectory(new File[] {new File(screenshotDir)}, "png");
        long time = 0;
        String newestScreenshot = null;
        for (String filename : screenshots) {
            long newTime = new File(filename).lastModified();
            if (newTime > time) {
                time = newTime;
                newestScreenshot = filename;
            }
        }
        return newestScreenshot;
    }

    private String getAndUpdateNewestScreenshot() {
        String newestScreenshot = findNewestScreenshot();
        if (Util.isEmpty(newestScreenshot)) {
            return null;
        }
        long newestTime = new File(newestScreenshot).lastModified();
        if (newestTime <= screenshotTime) {
            return null;
        }
        screenshotTime = newestTime;
        return newestScreenshot;
    }

    private void downloadScreenshot() {
        final Element screenshotElement = deviceCallback.getCurrentElement();
        if (screenshotElement == null) {
            return;
        }
        new Thread(new Runnable() {
            public void run() {
                synchronized (LOADING_SCREENSHOT_LOCK) {
                    String filename = waitForScreenshot();
                    if (Util.isEmpty(filename)) {
                        return;
                    }
                    loadScreenshot(screenshotElement, filename);
                }
            }

            private String waitForScreenshot() {
                for (int i = 0; i < 5; i++) {
                    String filename = getAndUpdateNewestScreenshot();
                    if (!Util.isEmpty(filename)) {
                        return filename;
                    }
                    Util.sleep(1000);
                }
                return null;
            }

            private void loadScreenshot(Element screenshotElement, String filename) {
                for (int i = 0; i < 5; i++) {
                    try {
                        Image screenshot = ImageIO.read(new File(filename));
                        deviceCallback.attachScreenshots(screenshotElement, new Screenshot(screenshot, filename));
                        return;
                    } catch (Exception e) {
                        System.out.println("Could not read screenshot - will try again!");
                    }
                    Util.sleep(1000);
                }
                System.out.println("Gave up loading screenshot! :(");
            }
        }).start();
    }

    private String getStepCommandFilename() {
        return FileUtil.addSlashToPath(ConfigurationManager.get("genericDevicePath")) + "step.txt";
    }

    private void pushCommandToFile(String command) {
        StringBuilder sb = new StringBuilder();
        sb.append(command);
        FileUtil.writeToFile(getStepCommandFilename(), sb);
    }
}
