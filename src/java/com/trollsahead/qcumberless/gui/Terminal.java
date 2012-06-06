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

package com.trollsahead.qcumberless.gui;

import com.trollsahead.qcumberless.device.Device;
import com.trollsahead.qcumberless.engine.Engine;
import com.trollsahead.qcumberless.engine.Player;
import com.trollsahead.qcumberless.model.ConsoleOutput;

import static com.trollsahead.qcumberless.gui.Animation.MoveAnimation;

import java.awt.*;
import java.util.List;

public class Terminal {
    private static final Font TERMINAL_FONT = new Font("Courier New", Font.PLAIN, 13);
    private static final float TERMINAL_PROPORTIONAL_HEIGHT = 0.5f;

    private static final Color TEXT_COLOR = new Color(0.4f, 1.0f, 0.4f);

    private static FontMetrics fontMetrics = null;

    private static final int PADDING_LEFT = 10;
    private static final int PADDING_TOP = 4;

    private static boolean visible = false;

    private static MoveAnimation position = new MoveAnimation();
    private static int scroll = -1;

    private static Device currentDevice = null;

    public static void initialize() {
        resize();
    }

    public static void resize() {
        setPosition();
        position.setRenderPosition(position.realX, position.realY);
    }

    public static void update() {
        position.update(false);
        if (position.isMoving()) {
            Engine.updateRootPositions();
        }
    }

    public static void render(Graphics2D g) {
        int height = getHeight();
        if (height <= 0) {
            return;
        }
        g.setColor(Color.BLACK);
        g.fillRect(0, (int) position.renderY, Engine.windowWidth, height);
        drawText(g);
    }

    private static void drawText(Graphics2D g) {
        if (currentDevice == null) {
            return;
        }
        g.setColor(TEXT_COLOR);
        Font oldFont = g.getFont();
        g.setFont(TERMINAL_FONT);
        if (fontMetrics == null) {
            fontMetrics = g.getFontMetrics();
        }

        List<String> output = getConsoleOutput();

        int rows = getNumberOfRows();
        int y = (int) position.renderY + PADDING_TOP;
        int scrollValue = scroll == -1 ? 0 : scroll;
        for (int i = 0; i < rows; i++) {
            int idx = output.size() - rows + i - scrollValue;
            if (idx < 0 || idx >= output.size()) {
                continue;
            }
            y += fontMetrics.getHeight();
            if (y > Engine.windowHeight) {
                break;
            }
            g.drawString(output.get(idx), PADDING_LEFT, y);
        }
        g.setFont(oldFont);
    }

    public static void toggleTerminal() {
        if (visible) {
            hideTerminal();
        } else {
            showTerminal();
        }
    }

    private static void showTerminal() {
        visible = true;
        setPosition();
        if (currentDevice == null) {
            activateDevice();
        }
    }
    
    private static void activateDevice() {
        List<Device> devices = Player.getStartedDevices();
        if (devices != null && !devices.isEmpty()) {
            currentDevice = Player.getStartedDevices().get(0);
        } else {
            currentDevice = ButtonBar.instance.getDevices().get(0);
        }
    }

    private static void hideTerminal() {
        visible = false;
        setPosition();
    }

    private static void setPosition() {
        if (visible) {
            position.setRealPosition(0.0f, Engine.windowHeight - getProportionalHeight(), Animation.MOVEMENT_SPEED_TERMINAL);
        } else {
            position.setRealPosition(0.0f, Engine.windowHeight + 1.0f, Animation.MOVEMENT_SPEED_TERMINAL);
        }
    }

    public static int getHeight() {
        return Math.max(Engine.windowHeight - (int) position.renderY, 0);
    }
    
    private static int getProportionalHeight() {
        return (int) ((float) Engine.windowHeight * TERMINAL_PROPORTIONAL_HEIGHT);
    }
    
    private static int getNumberOfRows() {
        return (getProportionalHeight() / fontMetrics.getHeight()) + 1;
    }

    private static List<String> getConsoleOutput() {
        ConsoleOutput console = currentDevice.getConsoleOutput();
        return console.getTextWrappedLog(Engine.windowWidth, fontMetrics);
    }

    public static void scroll(int unitsToScroll) {
        int lastIndex = getConsoleOutput().size() - getNumberOfRows();
        if (scroll == -1) {
            scroll = lastIndex;
        }
        scroll += unitsToScroll;
        scroll = Math.max(0, Math.min(lastIndex, scroll));
    }
}
