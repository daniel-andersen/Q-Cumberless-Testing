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

package com.trollsahead.qcumberless.plugins.generic;

import com.trollsahead.qcumberless.device.generic.GenericDeviceHelper;
import com.trollsahead.qcumberless.plugins.ButtonBarMethodCallback;

import javax.imageio.ImageIO;

import static com.trollsahead.qcumberless.gui.Images.ThumbnailState;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GenericDeviceSettings implements ButtonBarMethodCallback {
    private static BufferedImage thumbnailNormal;
    private static BufferedImage thumbnailHighlight;
    private static BufferedImage thumbnailPressed;

    static {
        try {
            thumbnailNormal = ImageIO.read(GenericDeviceSettings.class.getResource("/resources/pictures/generic_device_settings_normal.png"));
            thumbnailHighlight = ImageIO.read(GenericDeviceSettings.class.getResource("/resources/pictures/generic_device_settings_highlight.png"));
            thumbnailPressed = ImageIO.read(GenericDeviceSettings.class.getResource("/resources/pictures/generic_device_settings_pressed.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void trigger() {
        GenericDeviceHelper.showEnterCommandDialog();
        GenericDeviceHelper.showEnterPathDialog();
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

    public String getTooltip() {
        return "Generic Device Settings";
    }
}
