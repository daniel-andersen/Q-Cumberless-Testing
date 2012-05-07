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

import com.trollsahead.qcumberless.engine.Engine;

import java.awt.*;

public class GuiUtil {
    public static final Color[] SHADOW_COLOR = {new Color(0.0f, 0.0f, 0.0f, 0.2f), new Color(0.0f, 0.0f, 0.0f, 0.4f), new Color(0.0f, 0.0f, 0.0f, 0.6f)};

    public static void drawShadow(Graphics2D g, int x, int y, int width, int height, int rounding) {
        if (Engine.fpsDetails == Engine.DETAILS_FEWER) {
            return;
        }
        g.setColor(SHADOW_COLOR[0]);
        g.fillRoundRect(x + 3, y + 3, width - 1, height - 1, rounding, rounding);

        g.setColor(SHADOW_COLOR[1]);
        g.fillRoundRect(x + 2, y + 2, width - 1, height - 1, rounding, rounding);

        g.setColor(SHADOW_COLOR[2]);
        g.fillRoundRect(x + 1, y + 1, width - 1, height - 1, rounding, rounding);
    }

    public static void drawBorder(Graphics2D g, int x, int y, int width, int height, int rounding, Color color, float strokeWidth) {
        Stroke stroke = g.getStroke();

        g.setColor(color);
        g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        g.drawRoundRect(x, y, width, height, rounding, rounding);

        g.setStroke(stroke);
    }

    public static void drawBarFilling(Graphics2D g, int x, int y, int width, int height, int rounding, Color color) {
        g.setColor(color);
        g.fillRoundRect(x, y, width, height, rounding, rounding);
    }

    public static void drawBarBorder(Graphics2D g, int x, int y, int width, int height, int rounding, Color color) {
        g.setColor(color);
        g.drawRoundRect(x, y, width, height, rounding, rounding);
    }
}
