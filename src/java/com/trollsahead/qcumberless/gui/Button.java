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

import com.trollsahead.qcumberless.engine.Engine;
import com.trollsahead.qcumberless.gui.elements.Element;
import com.trollsahead.qcumberless.util.Util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Button {
    public static final int STATE_NORMAL    = 0;
    public static final int STATE_HIGHLIGHT = 1;
    public static final int STATE_PRESSED   = 2;

    private static final Color TEXT_COLOR_HINT = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Color TEXT_BACKGROUND_COLOR_HINT = new Color(0.2f, 0.2f, 0.2f, 0.6f);

    private static final Color TEXT_COLOR_DISABLED = new Color(1.0f, 1.0f, 1.0f, 0.3f);
    private static final Color[] TEXT_COLOR = {new Color(1.0f, 1.0f, 1.0f, 1.0f), new Color(1.0f, 1.0f, 0.2f, 1.0f), new Color(0.7f, 0.7f, 0.1f, 1.0f)};
    private static final Color[] TEXT_BACKGROUND_COLOR_FILL = {new Color(0.5f, 0.5f, 0.5f, 0.4f), new Color(0.3f, 0.3f, 0.3f, 0.4f), new Color(0.1f, 0.1f, 0.1f, 0.4f)};
    private static final Color[] TEXT_BACKGROUND_COLOR_FILL_MARKED = {new Color(0.5f, 0.5f, 0.5f, 0.8f), new Color(0.3f, 0.3f, 0.3f, 0.8f), new Color(0.1f, 0.1f, 0.1f, 0.8f)};
    private static final Color[] TEXT_BACKGROUND_COLOR_BORDER = {new Color(0.0f, 0.0f, 0.0f, 0.3f), new Color(0.0f, 0.0f, 0.0f, 0.3f), new Color(0.0f, 0.0f, 0.0f, 0.3f)};

    public static final int TEXT_BACKGROUND_PADDING_HORIZONTAL = 8;
    public static final int TEXT_BACKGROUND_PADDING_VERTICAL = 2;

    private static final int HINT_BACKGROUND_PADDING = 4;
    private static final int HINT_BACKGROUND_GAP = 4;

    public static final int ALIGN_HORIZONTAL_LEFT            = 1 << 0;
    public static final int ALIGN_HORIZONTAL_CENTER          = 1 << 1;
    public static final int ALIGN_HORIZONTAL_RIGHT_OF_PARENT = 1 << 2;
    public static final int ALIGN_VERTICAL_TOP               = 1 << 3;
    public static final int ALIGN_VERTICAL_BOTTOM            = 1 << 4;
    public static final int ALIGN_VERTICAL_CENTER            = 1 << 5;
    public static final int ALIGN_VERTICAL_CENTER_OF_PARENT  = 1 << 6;

    private int x;
    private int y;
    private int offsetX;
    private int offsetY;
    private String text;
    private Element parent;
    private int alignment;
    private ButtonNotification notification;

    private Image[] image = new BufferedImage[3];

    public int renderX;
    public int renderY;
    public int renderWidth;
    public int renderHeight;

    private int state = STATE_NORMAL;
    private boolean visible = true;
    private boolean marked = false;
    private boolean enabled = true;
    private String hint;

    public static boolean isOneTouched = false;

    public Button(int x, int y, String text, Image normalImage, Image highlightImage, Image pressedImage, int alignment, ButtonNotification notification, Element parent) {
        this(x, y, text, normalImage, highlightImage, pressedImage, alignment, notification);
        this.parent = parent;
    }

    public Button(int x, int y, String text, int alignment, ButtonNotification notification, Element parent) {
        this(x, y, text, null, null, null, alignment, notification);
        this.parent = parent;
    }

    public Button(int x, int y, Image normalImage, Image highlightImage, Image pressedImage, int alignment, ButtonNotification notification, Element parent) {
        this(x, y, null, normalImage, highlightImage, pressedImage, alignment, notification);
        this.parent = parent;
    }

    public Button(int x, int y, String text, Image normalImage, Image highlightImage, Image pressedImage, int alignment, ButtonNotification notification) {
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
    
    public int getOffsetX() {
        return offsetX;
    }

    public int getX() {
        return x;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int getY() {
        return y;
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
                CumberlessMouseListener.mouseX >= renderX && CumberlessMouseListener.mouseX <= renderX + renderWidth &&
                CumberlessMouseListener.mouseY >= renderY && CumberlessMouseListener.mouseY <= renderY + renderHeight) {
            isOneTouched = true;
            if (CumberlessMouseListener.isButtonPressed) {
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
        calculatePosition();
        drawBackground(g);
        drawText(g);
        drawImage(g);
        drawHint(g);
    }

    private void drawBackground(Graphics g) {
        if (text != null) {
            g.setColor(!marked ? TEXT_BACKGROUND_COLOR_FILL[state] : TEXT_BACKGROUND_COLOR_FILL_MARKED[state]);
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
        if (enabled) {
            g.setColor(TEXT_COLOR[state]);
        } else {
            g.setColor(TEXT_COLOR_DISABLED);
        }
        g.drawString(text, renderX, renderY + Engine.fontMetrics.getHeight() - 3);
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
        int hintWidth = Engine.fontMetrics.stringWidth(hint);
        int y = renderY - Engine.fontMetrics.getHeight() - (HINT_BACKGROUND_PADDING * 2) - HINT_BACKGROUND_GAP;
        int x = renderX;
        if (x + hintWidth > Engine.windowWidth) {
            x = Engine.windowWidth - hintWidth - TEXT_BACKGROUND_PADDING_HORIZONTAL;
        }
        g.setColor(TEXT_BACKGROUND_COLOR_HINT);
        g.fillRoundRect(x - HINT_BACKGROUND_PADDING, y, hintWidth + HINT_BACKGROUND_PADDING * 2, Engine.fontMetrics.getHeight() + HINT_BACKGROUND_PADDING * 2, 5, 5);
        g.setColor(TEXT_COLOR_HINT);
        g.drawString(hint, x, y + Engine.fontMetrics.getHeight());
    }

    private void calculatePosition() {
        renderX = x + offsetX;
        renderY = y + offsetY;

        renderWidth = (text != null ? Engine.fontMetrics.stringWidth(text) : 0) + (image[state] != null ? image[state].getWidth(null) : 0);
        renderHeight = Math.max((text != null ? Engine.fontMetrics.getHeight() : 0), (image[state] != null ? image[state].getHeight(null) : 0));

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

    public boolean isVisible() {
        return visible;
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

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public interface ButtonNotification {
        void onClick();
    }
}
