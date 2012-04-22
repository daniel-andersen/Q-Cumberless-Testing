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

import com.trollsahead.qcumberless.engine.Engine;
import com.trollsahead.qcumberless.util.Util;

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
        BufferedImage spotlightImage = Images.getImage(Images.IMAGE_SPOTLIGHT, Images.TYPE_NORMAL);
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
