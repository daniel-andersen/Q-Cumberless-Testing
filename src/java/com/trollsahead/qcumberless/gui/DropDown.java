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

package com.trollsahead.qcumberless.gui;

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
        renderX = calculateX(g);
        renderY = calculateY(g);
        renderWidth = calculateWidth(g);
        renderHeight = calculateHeight(g);
        if (!alignTop) {
            renderY = Math.max(0, renderY - renderHeight);
        }
        itemHeight = calculateItemHeight(g);
        drawBackground(g);
        drawText(g);
    }

    private static int calculateX(Graphics g) {
        return x - PADDING_HORIZONTAL;
    }

    private static int calculateY(Graphics g) {
        FontMetrics fontMetrics = g.getFontMetrics();
        return y + fontMetrics.getHeight() + 3 - PADDING_VERTICAL;
    }

    private static int calculateWidth(Graphics g) {
        FontMetrics fontMetrics = g.getFontMetrics();
        int maxWidth = 0;
        for (String item : items) {
            int width = fontMetrics.stringWidth(item);
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        int toggleWidth = toggleMode ? TOGGLE_OFFSET_X : 0;
        return maxWidth + (PADDING_HORIZONTAL * 2) + toggleWidth;
    }

    private static int calculateHeight(Graphics g) {
        FontMetrics fontMetrics = g.getFontMetrics();
        return (items.size() * fontMetrics.getHeight()) + (PADDING_VERTICAL * 2);
    }

    private static int calculateItemHeight(Graphics g) {
        FontMetrics fontMetrics = g.getFontMetrics();
        return fontMetrics.getHeight();
    }

    private static void drawBackground(Graphics g) {
        g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.7f));
        g.fillRoundRect(renderX, renderY, renderWidth, renderHeight, 3, 3);
    }

    private static void drawText(Graphics g) {
        FontMetrics fontMetrics = g.getFontMetrics();
        int offsetX = toggleMode ? TOGGLE_OFFSET_X : 0;
        for (int i = 0; i < items.size(); i++) {
            String item = items.get(i);
            int x = renderX + PADDING_HORIZONTAL;
            int y = renderY + PADDING_VERTICAL + (fontMetrics.getHeight() * (i + 1));
            if (toggleMode) {
                BufferedImage image = toggledModeCallbackHandler.getToggledImage(item);
                if (image != null) {
                    g.drawImage(image, x, y - ((fontMetrics.getHeight() + image.getHeight()) / 2), null);
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
