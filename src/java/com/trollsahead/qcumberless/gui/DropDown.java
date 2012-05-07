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
import java.util.List;

public class DropDown {
    private static final int PADDING_HORIZONTAL = 5;
    private static final int PADDING_VERTICAL = 5;

    private static final Color COLOR_NORMAL = new Color(0.8f, 0.8f, 0.8f, 1.0f);
    private static final Color COLOR_HIGHLIGHTED = new Color(1.0f, 1.0f, 1.0f, 1.0f);

    public static boolean isVisible = false;

    private static int highlightIndex;

    private static int x;
    private static int y;

    private static DropDownCallback callbackHandler;
    private static List<String> items;
    
    private static int renderX;
    private static int renderY;
    private static int renderWidth;
    private static int renderHeight;
    private static int itemHeight;

    public static void show(int x, int y, DropDownCallback callbackHandler, List<String> items) {
        DropDown.x = x;
        DropDown.y = y;
        DropDown.callbackHandler = callbackHandler;
        DropDown.items = items;
        DropDown.isVisible = true;
        DropDown.highlightIndex = -1;
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
        return maxWidth + (PADDING_HORIZONTAL * 2);
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
        for (int i = 0; i < items.size(); i++) {
            if (i == highlightIndex) {
                g.setColor(COLOR_HIGHLIGHTED);
            } else {
                g.setColor(COLOR_NORMAL);
            }
            g.drawString(items.get(i), renderX + PADDING_HORIZONTAL, renderY + PADDING_VERTICAL + (fontMetrics.getHeight() * (i + 1)) - 3);
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
            callbackHandler.chooseItem(items.get(highlightIndex));
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
}
