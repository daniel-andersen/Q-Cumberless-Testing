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
import com.trollsahead.qcumberless.gui.elements.Element;
import com.trollsahead.qcumberless.util.ConfigurationManager;
import com.trollsahead.qcumberless.util.FileUtil;
import com.trollsahead.qcumberless.util.Util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

import static com.trollsahead.qcumberless.gui.Images.ThumbnailState;

public class CalabashAndroidDevice extends GenericDevice {
    private static final Pattern patternStarting = Pattern.compile("Feature: .*");
    private static final Pattern patternScreenshotBeingTakenMessage = Pattern.compile("(.*)Taking screenshoot to (.*) from device(.*)");
    private static final Pattern patternScreenshotTakenMessage = Pattern.compile("(.*)Screenshot taken(.*)");

    private static BufferedImage thumbnailNormal;
    private static BufferedImage thumbnailHighlight;
    private static BufferedImage thumbnailPressed;

    private final Set<Capability> capabilities;

    private List<String> screenshotFiles;
    private String screenshotDir;
    private Element screenshotElement;

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
        capabilities.add(Capability.INTERACTIVE_DESIGNING);
        screenshotDir = ConfigurationManager.get("screenshotsDirectory");
        screenshotFiles = findScreenshots();
        screenshotElement = null;
    }

    private List<String> findScreenshots() {
        System.out.println("-------------");
        List<String> screenshotsList = FileUtil.traverseDirectory(new File[] {new File(screenshotDir)}, "png");
        String[] screenshotsArray = screenshotsList.toArray(new String[0]);
        Arrays.sort(screenshotsArray);
        List<String> sortedScreenshotsList = new LinkedList<String>();
        sortedScreenshotsList.addAll(Arrays.asList(screenshotsArray));
        for (String s : sortedScreenshotsList) {
            System.out.println("--> " + s);
        }
        return sortedScreenshotsList;
    }

    private String updateScreenshotsAndReturnNewest() {
        List<String> oldScreenshotFiles = screenshotFiles;
        screenshotFiles = findScreenshots();
        if (screenshotFiles.size() == oldScreenshotFiles.size()) {
            return null;
        }
        List<String> newScreenshotFiles = new LinkedList<String>();
        newScreenshotFiles.addAll(screenshotFiles);
        newScreenshotFiles.removeAll(oldScreenshotFiles);
        if (newScreenshotFiles.size() == 1) {
            return newScreenshotFiles.get(0);
        }
        return null;
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

    public InteractiveDesignerClient getInteractiveDesignerClient() {
        return new InteractiveDesigner();
    }

    protected void checkStepFailed(String log) {
        super.checkStepFailed(log);
        screenshotElement = deviceCallback.getCurrentElement();
    }

    protected void checkScreenshotTaken(String log) {
        if (screenshotElement == null) {
            return;
        }
        String filename = updateScreenshotsAndReturnNewest();
        System.out.println("Looking for screenshot: " + filename);
        if (Util.isEmpty(filename)) {
            return;
        }
        System.out.println("Downloading screenshot!");
        downloadScreenshots(screenshotElement, filename);
        screenshotElement = null;
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
}
