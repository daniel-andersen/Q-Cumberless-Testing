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

import com.trollsahead.qcumberless.device.generic.GenericDevice;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
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

    static {
        try {
            thumbnailNormal = ImageIO.read(CalabashAndroidDevice.class.getResource("/resources/pictures/calabash_android_device_normal.png"));
            thumbnailHighlight = ImageIO.read(CalabashAndroidDevice.class.getResource("/resources/pictures/calabash_android_device_highlight.png"));
            thumbnailPressed = ImageIO.read(CalabashAndroidDevice.class.getResource("/resources/pictures/calabash_android_device_pressed.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CalabashAndroidDevice() {
        super();
        capabilities = new HashSet<Capability>();
        capabilities.add(Capability.PLAY);
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
