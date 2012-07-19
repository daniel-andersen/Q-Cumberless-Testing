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

package com.trollsahead.qcumberless.model;

import com.trollsahead.qcumberless.gui.RenderOptimizer;
import com.trollsahead.qcumberless.util.Util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Screenshot {
    private Image image = null;
    private String filename = null;

    private BufferedImage scaledImage = null;
    private int scaledImageWidth;
    private int scaledImageHeight;

    public Screenshot(String filename) {
        this(null, filename);
    }

    public Screenshot(Image image, String filename) {
        this.image = image;
        this.filename = filename;
    }

    public Image getImage() {
        if (image != null) {
            return image;
        }
        if (Util.isEmpty(filename)) {
            return null;
        }
        try {
            image = ImageIO.read(new File(filename));
            return image;
        } catch (Exception e) {
            return null;
        }
    }

    public Image getScaledImage(int width, int height) {
        if (scaledImage == null || scaledImageWidth != width || scaledImageHeight != height) {
            scaleImage(width, height);
        }
        return scaledImage;
    }

    private void scaleImage(int width, int height) {
        Image originalImage = getImage();
        if (originalImage == null) {
            return;
        }
        Image scaledInstance = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        scaledImage = RenderOptimizer.graphicsConfiguration.createCompatibleImage(width, height);
        Graphics g = scaledImage.createGraphics();
        g.drawImage(scaledInstance, 0, 0, null);
        g.dispose();
        scaledImageWidth = width;
        scaledImageHeight = height;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
