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

package com.trollsahead.qcumberless.engine;

import com.trollsahead.qcumberless.device.Device;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Set;

public class InteractiveDesignerEngine implements CucumberlessEngine {
    private static final String WAITING_FOR_DEVICE_TEXT = "WAITING FOR DEVICE...";

    private static BufferedImage screenshot = null;
    
    public void initialize() {
    }

    public void show() {
        screenshot = null;
    }

    public void hide() {
    }

    public void update() {
    }
    
    public void render(Graphics2D g) {
        Engine.drawBackgroundPicture(g);

        drawScreenshot(g);
    }

    private void drawScreenshot(Graphics2D g) {
        if (screenshot == null) {
            drawWaitingForDeviceText(g);
            return;
        }
        int x = (Engine.windowWidth - screenshot.getWidth()) / 2;
        int y = (Engine.windowHeight - screenshot.getHeight()) / 2;
        g.drawImage(screenshot, x, y, null);
    }

    private void drawWaitingForDeviceText(Graphics2D g) {
        int x = (Engine.windowWidth - Engine.fontMetrics.stringWidth(WAITING_FOR_DEVICE_TEXT)) / 2;
        int y = Engine.windowHeight / 2;

        g.setColor(Color.WHITE);
        g.drawString(WAITING_FOR_DEVICE_TEXT, x, y);
    }

    public void postRender() {
    }

    public void resize() {
    }

    public void mouseMoved() {
    }

    public void mouseWheelMoved(int unitsToScroll) {
    }

    public void click(int clickCount) {
    }

    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
            Engine.prevEngine();
        }
    }

    public void startDrag(boolean isControlDown) {
    }

    public void endDrag() {
    }

    public void updateDrag() {
    }

    public void updateDevices(Set<Device> devices) {
    }
}
