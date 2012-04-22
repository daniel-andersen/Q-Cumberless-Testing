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

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

public class CucumberCanvas extends JPanel {
    public CucumberCanvas() {
        setFocusable(true);
        setBackground(Color.BLACK);
    }

    public void paintComponent(Graphics g) {
        synchronized (CucumberEngine.LOCK) {
            g.drawImage(CucumberEngine.backbuffer, 0, 0, this);
        }
    }

    public void clear(Graphics g) {
        if (!EasterEgg.isBackgroundCoveringCanvas()) {
            drawBackgroundPicture(g);
        }
        if (EasterEgg.animation > 0) {
            EasterEgg.render(g);
        }
    }

    public void drawBackgroundPicture(Graphics g) {
        BufferedImage backgroundImage = Images.getImage(Images.IMAGE_BACKGROUND, Images.TYPE_NORMAL);
        for (int y = 0; y <= getHeight() / backgroundImage.getHeight(); y++) {
            for (int x = 0; x <= getWidth() / backgroundImage.getWidth(); x++) {
                g.drawImage(backgroundImage, x * backgroundImage.getWidth(), y * backgroundImage.getHeight(), null);
            }
        }
    }
}
