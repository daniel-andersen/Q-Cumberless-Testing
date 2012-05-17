// This file is part of Q-Cumberless Testing.
//
// Q-Cumberless Testing is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Q-Cumberless Testing is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Q-Cumberless Testing.  If not, see <http://www.gnu.org/licenses/>.
//
// Copyright 2012

// Daniel Andersen (dani_ande@yahoo.dk)

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
