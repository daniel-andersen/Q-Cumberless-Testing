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

import com.trollsahead.qcumberless.engine.DesignerEngine;
import com.trollsahead.qcumberless.engine.Engine;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class DropDown {
    private static final int PADDING_HORIZONTAL = 5;
    private static final int PADDING_VERTICAL = 5;

    private static final int TOGGLE_OFFSET_X = 16 + PADDING_HORIZONTAL;

    private static final Color COLOR_NORMAL = new Color(0.8f, 0.8f, 0.8f, 1.0f);
    private static final Color COLOR_HIGHLIGHTED = new Color(1.0f, 1.0f, 1.0f, 1.0f);

    public static boolean isVisible = false;

    private static int highlightIndex;

    private static int x;
    private static int y;

    private static DropDownCallback callbackHandler;
    private static DropDownToggleModeCallback toggledModeCallbackHandler;
    private static List<String> items;
    
    private static int renderX;
    private static int renderY;
    private static int renderWidth;
    private static int renderHeight;
    private static int itemHeight;
    private static boolean toggleMode;
    private static boolean alignTop;

    public static void show(int x, int y, DropDownCallback callbackHandler, List<String> items) {
        DropDown.x = x;
        DropDown.y = y;
        DropDown.callbackHandler = callbackHandler;
        DropDown.items = items;
        DropDown.isVisible = true;
        DropDown.highlightIndex = -1;
        DropDown.toggleMode = false;
        DropDown.alignTop = true;
    }

    public static void showUpright(int x, int y, DropDownCallback callbackHandler, List<String> items) {
        show(x, y, callbackHandler, items);
        DropDown.alignTop = false;
    }

    public static void showInToggleMode(int x, int y, DropDownToggleModeCallback toggledModeCallbackHandler, List<String> items) {
        DropDown.x = x;
        DropDown.y = y;
        DropDown.toggledModeCallbackHandler = toggledModeCallbackHandler;
        DropDown.items = items;
        DropDown.isVisible = true;
        DropDown.highlightIndex = -1;
        DropDown.toggleMode = true;
        DropDown.alignTop = false;
    }

    public static void hide() {
        isVisible = false;
    }

    public static void update() {
    }

    public static void render(Graphics g) {
        renderX = calculateX();
        renderY = calculateY();
        renderWidth = calculateWidth();
        renderHeight = calculateHeight();
        if (!alignTop) {
            renderY = Math.max(0, renderY - renderHeight);
        }
        if (Engine.currentEngine == Engine.designerEngine) {
            if (renderY + renderHeight > DesignerEngine.canvasHeight - ButtonBar.BUTTONBAR_HEIGHT - PADDING_VERTICAL) {
                renderY = DesignerEngine.canvasHeight - renderHeight - ButtonBar.BUTTONBAR_HEIGHT - PADDING_VERTICAL;
            }
        }
        itemHeight = calculateItemHeight();
        drawBackground(g);
        drawText(g);
    }

    private static int calculateX() {
        return x - PADDING_HORIZONTAL;
    }

    private static int calculateY() {
        return y + Engine.fontMetrics.getHeight() + 3 - PADDING_VERTICAL;
    }

    private static int calculateWidth() {
        int maxWidth = 0;
        for (String item : items) {
            int width = Engine.fontMetrics.stringWidth(item);
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        int toggleWidth = toggleMode ? TOGGLE_OFFSET_X : 0;
        return maxWidth + (PADDING_HORIZONTAL * 2) + toggleWidth;
    }

    private static int calculateHeight() {
        return (items.size() * Engine.fontMetrics.getHeight()) + (PADDING_VERTICAL * 2);
    }

    private static int calculateItemHeight() {
        return Engine.fontMetrics.getHeight();
    }

    private static void drawBackground(Graphics g) {
        g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.7f));
        g.fillRoundRect(renderX, renderY, renderWidth, renderHeight, 3, 3);
    }

    private static void drawText(Graphics g) {
        int offsetX = toggleMode ? TOGGLE_OFFSET_X : 0;
        for (int i = 0; i < items.size(); i++) {
            String item = items.get(i);
            int x = renderX + PADDING_HORIZONTAL;
            int y = renderY + PADDING_VERTICAL + (Engine.fontMetrics.getHeight() * (i + 1));
            if (toggleMode) {
                BufferedImage image = toggledModeCallbackHandler.getToggledImage(item);
                if (image != null) {
                    g.drawImage(image, x, y - ((Engine.fontMetrics.getHeight() + image.getHeight()) / 2), null);
                }
            }
            if (i == highlightIndex) {
                g.setColor(COLOR_HIGHLIGHTED);
            } else {
                g.setColor(COLOR_NORMAL);
            }
            g.drawString(item, x + offsetX, y - 3);
        }
    }

    public static boolean isTouched() {
        return isVisible &&
               CumberlessMouseListener.mouseX >= renderX && CumberlessMouseListener.mouseX <= renderX + renderWidth &&
               CumberlessMouseListener.mouseY >= renderY && CumberlessMouseListener.mouseY <= renderY + renderHeight;
    }

    public static boolean click() {
        if (!isVisible) {
            return false;
        }
        if (highlightIndex >= 0 && highlightIndex < items.size()) {
            if (!toggleMode) {
                callbackHandler.chooseItem(items.get(highlightIndex));
            } else {
                toggledModeCallbackHandler.toggleItem(items.get(highlightIndex));
                return false;
            }
        }
        hide();
        return true;
    }

    public static boolean mousePressed() {
        if (!isVisible) {
            return false;
        }
        if (!isTouched()) {
            hide();
        }
        return true;
    }

    public static boolean mouseReleased() {
        if (!isVisible) {
            return false;
        }
        if (!isTouched()) {
            hide();
        }
        return true;
    }

    public static boolean mouseMoved() {
        if (!isVisible) {
            return false;
        }
        if (!isTouched()) {
            highlightIndex = -1;
            return true;
        }
        highlightIndex = (CumberlessMouseListener.mouseY - renderY - PADDING_VERTICAL) / itemHeight;
        if (highlightIndex >= items.size()) {
            highlightIndex = -1;
        }
        return true;
    }

    public static boolean mouseDragged() {
        return isVisible;
    }

    public interface DropDownCallback {
        void chooseItem(String item);
    }

    public interface DropDownToggleModeCallback {
        void toggleItem(String item);
        BufferedImage getToggledImage(String item);
    }
}
