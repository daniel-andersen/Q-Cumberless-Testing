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

package com.trollsahead.qcumberless.gui;

import com.trollsahead.qcumberless.engine.CucumberEngine;
import com.trollsahead.qcumberless.util.Util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CucumberButton {
    public static final int STATE_NORMAL    = 0;
    public static final int STATE_HIGHLIGHT = 1;
    public static final int STATE_PRESSED   = 2;

    private static final Color TEXT_COLOR_HINT = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Color TEXT_BACKGROUND_COLOR_HINT = new Color(0.2f, 0.2f, 0.2f, 0.6f);

    private static final Color TEXT_COLOR_DISABLED = new Color(1.0f, 1.0f, 1.0f, 0.3f);
    private static final Color[] TEXT_COLOR = {new Color(1.0f, 1.0f, 1.0f, 1.0f), new Color(1.0f, 1.0f, 0.2f, 1.0f), new Color(0.7f, 0.7f, 0.1f, 1.0f)};
    private static final Color[] TEXT_BACKGROUND_COLOR_FILL = {new Color(0.5f, 0.5f, 0.5f, 0.4f), new Color(0.2f, 0.2f, 0.2f, 0.1f), new Color(0.2f, 0.2f, 0.2f, 0.2f)};
    private static final Color[] TEXT_BACKGROUND_COLOR_BORDER = {new Color(0.0f, 0.0f, 0.0f, 0.3f), new Color(0.0f, 0.0f, 0.0f, 0.3f), new Color(0.0f, 0.0f, 0.0f, 0.3f)};

    public static final int TEXT_BACKGROUND_PADDING_HORIZONTAL = 8;
    public static final int TEXT_BACKGROUND_PADDING_VERTICAL = 2;

    private static final int HINT_BACKGROUND_PADDING = 4;
    private static final int HINT_BACKGROUND_GAP = 4;

    public static final int ALIGN_HORIZONTAL_LEFT            = 1 << 0;
    public static final int ALIGN_HORIZONTAL_CENTER          = 1 << 1;
    public static final int ALIGN_HORIZONTAL_RIGHT_OF_PARENT = 1 << 2;
    public static final int ALIGN_VERTICAL_BOTTOM            = 1 << 3;
    public static final int ALIGN_VERTICAL_CENTER            = 1 << 4;
    public static final int ALIGN_VERTICAL_CENTER_OF_PARENT  = 1 << 5;

    private int x;
    private int y;
    private int offsetX;
    private int offsetY;
    private String text;
    private CucumberElement parent;
    private int alignment;
    private CucumberButtonNotification notification;

    private boolean visible;

    private Image[] image = new BufferedImage[3];

    protected int renderX;
    protected int renderY;
    protected int renderWidth;
    protected int renderHeight;

    private int state;
    private boolean enabled = true;

    public static boolean isOneTouched = false;
    private String hint;

    public CucumberButton(int x, int y, String text, Image normalImage, Image highlightImage, Image pressedImage, int alignment, CucumberButtonNotification notification, CucumberElement parent) {
        this(x, y, text, normalImage, highlightImage, pressedImage, alignment, notification);
        this.parent = parent;
    }

    public CucumberButton(int x, int y, String text, int alignment, CucumberButtonNotification notification, CucumberElement parent) {
        this(x, y, text, null, null, null, alignment, notification);
        this.parent = parent;
    }

    public CucumberButton(int x, int y, Image normalImage, Image highlightImage, Image pressedImage, int alignment, CucumberButtonNotification notification, CucumberElement parent) {
        this(x, y, null, normalImage, highlightImage, pressedImage, alignment, notification);
        this.parent = parent;
    }

    public CucumberButton(int x, int y, String text, Image normalImage, Image highlightImage, Image pressedImage, int alignment, CucumberButtonNotification notification) {
        this.x = x;
        this.y = y;
        this.offsetX = 0;
        this.offsetY = 0;
        this.text = text;
        this.image[STATE_NORMAL] = normalImage;
        this.image[STATE_HIGHLIGHT] = highlightImage;
        this.image[STATE_PRESSED] = pressedImage;
        this.alignment = alignment;
        this.notification = notification;
        this.parent = null;
        this.state = STATE_NORMAL;
        this.visible = true;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public void setImages(Image normalImage, Image highlightImage, Image pressedImage) {
        this.image[STATE_NORMAL] = normalImage;
        this.image[STATE_HIGHLIGHT] = highlightImage;
        this.image[STATE_PRESSED] = pressedImage;
    }

    public void setOffset(int x, int y) {
        this.offsetX = x;
        this.offsetY = y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        if (!visible || !enabled) {
            state = STATE_NORMAL;
            return;
        }
        if (!isOneTouched &&
                CucumberMouseListener.mouseX >= renderX && CucumberMouseListener.mouseX <= renderX + renderWidth &&
                CucumberMouseListener.mouseY >= renderY && CucumberMouseListener.mouseY <= renderY + renderHeight) {
            isOneTouched = true;
            if (CucumberMouseListener.isButtonPressed) {
                state = STATE_PRESSED;
            } else {
                state = STATE_HIGHLIGHT;
            }
        } else {
            state = STATE_NORMAL;
        }
    }

    public boolean click() {
        if (isTouched()) {
            notification.onClick();
            return true;
        } else {
            return false;
        }
    }

    public void render(Graphics g) {
        if (!visible) {
            return;
        }
        calculatePosition(g);
        drawBackground(g);
        drawText(g);
        drawImage(g);
        drawHint(g);
    }

    private void drawBackground(Graphics g) {
        if (text != null) {
            g.setColor(TEXT_BACKGROUND_COLOR_FILL[state]);
            g.fillRoundRect(
                    renderX - TEXT_BACKGROUND_PADDING_HORIZONTAL, renderY - TEXT_BACKGROUND_PADDING_VERTICAL,
                    renderWidth + TEXT_BACKGROUND_PADDING_HORIZONTAL * 2, renderHeight + TEXT_BACKGROUND_PADDING_VERTICAL * 2,
                    10, 10);
            g.setColor(TEXT_BACKGROUND_COLOR_BORDER[state]);
            g.drawRoundRect(
                    renderX - TEXT_BACKGROUND_PADDING_HORIZONTAL, renderY - TEXT_BACKGROUND_PADDING_VERTICAL,
                    renderWidth + TEXT_BACKGROUND_PADDING_HORIZONTAL * 2, renderHeight + TEXT_BACKGROUND_PADDING_VERTICAL * 2,
                    10, 10);
        }
    }

    private void drawText(Graphics g) {
        if (Util.isEmpty(text)) {
            return;
        }
        FontMetrics fontMetrics = g.getFontMetrics();
        if (enabled) {
            g.setColor(TEXT_COLOR[state]);
        } else {
            g.setColor(TEXT_COLOR_DISABLED);
        }
        g.drawString(text, renderX, renderY + fontMetrics.getHeight() - 3);
    }
    
    private void drawImage(Graphics g) {
        if (image[state] == null) {
            return;
        }
        g.drawImage(image[state], renderX, renderY, null);
    }

    private void drawHint(Graphics g) {
        if (Util.isEmpty(hint)) {
            return;
        }
        if (state == STATE_NORMAL) {
            return;
        }
        FontMetrics fontMetrics = g.getFontMetrics();
        int hintWidth = fontMetrics.stringWidth(hint);
        int y = renderY - fontMetrics.getHeight() - (HINT_BACKGROUND_PADDING * 2) - HINT_BACKGROUND_GAP;
        int x = renderX;
        if (x + hintWidth > CucumberEngine.canvasWidth) {
            x = CucumberEngine.canvasWidth - hintWidth - TEXT_BACKGROUND_PADDING_HORIZONTAL;
        }
        g.setColor(TEXT_BACKGROUND_COLOR_HINT);
        g.fillRoundRect(x - HINT_BACKGROUND_PADDING, y, hintWidth + HINT_BACKGROUND_PADDING * 2, fontMetrics.getHeight() + HINT_BACKGROUND_PADDING * 2, 5, 5);
        g.setColor(TEXT_COLOR_HINT);
        g.drawString(hint, x, y + fontMetrics.getHeight());
    }

    private void calculatePosition(Graphics g) {
        FontMetrics fontMetrics = g.getFontMetrics();
        
        renderX = x + offsetX;
        renderY = y + offsetY;

        renderWidth = (text != null ? fontMetrics.stringWidth(text) : 0) + (image[state] != null ? image[state].getWidth(null) : 0);
        renderHeight = Math.max((text != null ? fontMetrics.getHeight() : 0), (image[state] != null ? image[state].getHeight(null) : 0));

        if (isAlignType(ALIGN_HORIZONTAL_RIGHT_OF_PARENT)) {
            renderX = parent.renderWidth - x;
        }
        if (parent != null) {
            renderX += parent.animation.moveAnimation.renderX;
            renderY += parent.animation.moveAnimation.renderY;
        }
        if (isAlignType(ALIGN_HORIZONTAL_CENTER)) {
            renderX -= renderWidth / 2;
        }
        if (isAlignType(ALIGN_VERTICAL_CENTER)) {
            renderY -= renderHeight / 2;
        }
        if (isAlignType(ALIGN_VERTICAL_BOTTOM)) {
            renderY -= renderHeight;
        }
        if (isAlignType(ALIGN_VERTICAL_CENTER_OF_PARENT)) {
            renderY += (parent.renderHeight - renderHeight) / 2;
        }
    }

    public boolean isTouched() {
        return state != STATE_NORMAL && visible && enabled;
    }

    private boolean isAlignType(int type) {
        return (alignment & type) != 0;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String toString() {
        return text != null ? text : "";
    }

    public int getImageWidth() {
        return image != null && image.length > 0 ? image[0].getWidth(null) : 0;
    }

    public int getImageHeight() {
        return image != null && image.length > 0 ? image[0].getHeight(null) : 0;
    }

    public interface CucumberButtonNotification {
        void onClick();
    }
}
