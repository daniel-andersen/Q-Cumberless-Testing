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
