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
import com.trollsahead.qcumberless.util.Util;

import static com.trollsahead.qcumberless.gui.Images.ThumbnailState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Spotlight {
    private static final int PADDING_IMAGE_TO_TEXT = 8;
    private static final int PADDING_HORIZONTAL = 16;
    private static final int PADDING_VERTICAL = 10;

    private static final int BAR_HEIGHT = 30;
    private static final int BAR_ROUNDING = TextElement.BAR_ROUNDING;

    private static final float[] COLOR_BACKGROUND_NORMAL = {0.0f, 0.0f, 0.0f, 0.6f};
    private static final float COLOR_DISAPPEAR_SPEED = 0.1f;

    private static final Color COLOR_TEXT = new Color(1.0f, 1.0f, 1.0f, 1.0f);

    public boolean visible;
    public String searchString;

    private Animation animation;

    public Spotlight() {
        searchString = "";
        animation = new Animation();
        clear();
    }

    public void clear() {
        visible = false;
        animation.colorAnimation.setAlpha(0.0f, COLOR_DISAPPEAR_SPEED);
        if (Engine.stepsRoot != null) {
            Engine.stepsRoot.pushToPosition(0);
            Engine.stepsRoot.filterChildren(".*");
        }
    }

    public void searchKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            clear();
            EasterEgg.hide();
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (!Util.isEmpty(searchString)) {
                searchString = searchString.substring(0, searchString.length() - 1);
                filter();
            }
            return;
        }
        char ch = e.getKeyChar();
        boolean isCharValid = false;
        isCharValid |= ch >= 'a' && ch <= 'z';
        isCharValid |= ch >= 'A' && ch <= 'Z';
        isCharValid |= ch >= '0' && ch <= '9';
        isCharValid |= ch == 'æ' || ch == 'Æ';
        isCharValid |= ch == 'ø' || ch == 'Ø';
        isCharValid |= ch == 'å' || ch == 'Å';
        isCharValid |= ch == '+' || ch == '-';
        isCharValid |= ch == ' ' || ch == '\'';
        if (isCharValid) {
            addCharacter(ch);
            filter();
        }
    }

    private void filter() {
        String regexp = ".*";
        for (int i = 0; i < searchString.length(); i++) {
            String s = searchString.substring(i, i + 1);
            regexp += "[" + s.toLowerCase() + s.toUpperCase() + "]";
        }
        regexp += ".*";
        Engine.stepsRoot.filterChildren(regexp);
    }
    
    public void addCharacter(char ch) {
        if (!visible) {
            letThereBeLight();
        }
        searchString = Util.removeLeadingSpaces(searchString + ch);
    }

    private void letThereBeLight() {
        searchString = "";
        animation = new Animation(COLOR_BACKGROUND_NORMAL);
        visible = true;
        Engine.stepsRoot.pushToPosition(BAR_HEIGHT + PADDING_VERTICAL);
    }

    public void update() {
        animation.update();
    }
    
    public void render(Graphics g) {
        if (!visible && !animation.colorAnimation.isVisible()) {
            return;
        }
        BufferedImage spotlightImage = Images.getImage(Images.IMAGE_SPOTLIGHT, ThumbnailState.NORMAL.ordinal());
        FontMetrics fontMetrics = g.getFontMetrics();
        int width = spotlightImage.getWidth() + PADDING_IMAGE_TO_TEXT + fontMetrics.stringWidth(searchString) + (PADDING_HORIZONTAL * 2);
        int x = (int) Engine.stepsRoot.animation.moveAnimation.renderX;
        int y = PADDING_VERTICAL;

        g.setColor(animation.colorAnimation.getColor());
        g.fillRoundRect(x, y, width, BAR_HEIGHT, BAR_ROUNDING, BAR_ROUNDING);

        g.drawImage(spotlightImage, x + PADDING_HORIZONTAL, y + (BAR_HEIGHT - spotlightImage.getHeight()) / 2, null);

        g.setColor(COLOR_TEXT);
        g.drawString(searchString, x + PADDING_HORIZONTAL + spotlightImage.getWidth() + PADDING_IMAGE_TO_TEXT, y + (BAR_HEIGHT + fontMetrics.getHeight()) / 2 - 3);
    }
}
