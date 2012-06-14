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
import com.trollsahead.qcumberless.plugins.ElementMethodCallback;
import com.trollsahead.qcumberless.plugins.Plugin;
import com.trollsahead.qcumberless.util.Util;

import static com.trollsahead.qcumberless.gui.Images.ThumbnailState;
import static com.trollsahead.qcumberless.gui.ExtendedButtons.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class GuiUtil {
    public static final Color[] SHADOW_COLOR = {new Color(0.0f, 0.0f, 0.0f, 0.2f), new Color(0.0f, 0.0f, 0.0f, 0.4f), new Color(0.0f, 0.0f, 0.0f, 0.6f)};

    public static enum AnimationState {NONE, ACTIVATING, DEACTIVATING, FORWARD, BACKWARD}

    public static final float DISAPPEAR_SPEED = 0.03f;

    public static final int SHADOW_WIDTH = 200;
    public static final int SHADOW_STEP = SHADOW_WIDTH / 10;
    public static final float SHADOW_DEPTH = 0.5f;

    public static void renderAppearAnimation(Graphics2D g, BufferedImage background, AnimationState animationState, float progress) {
        boolean rightwards = animationState == AnimationState.BACKWARD || animationState == AnimationState.DEACTIVATING;
        int x = (int) ((1.0f - Math.cos((progress + 0.1f) * Math.PI / 2.0f)) * (Engine.windowWidth + SHADOW_WIDTH)) * (rightwards ? 1 : -1);
        if (Engine.fpsDetails != Engine.DETAILS_LOW) {
            int sx = x + (rightwards ? -SHADOW_STEP : Engine.windowWidth);
            for (int i = 0; i < SHADOW_WIDTH; i += SHADOW_STEP) {
                g.setColor(new Color(0.0f, 0.0f, 0.0f, SHADOW_DEPTH - ((float) i / SHADOW_WIDTH) * SHADOW_DEPTH));
                g.fillRect(sx, 0, SHADOW_STEP, Engine.windowHeight);
                sx += SHADOW_STEP * (rightwards ? -1 : 1);
            }
        } else {
            g.setColor(new Color(0.0f, 0.0f, 0.0f, SHADOW_DEPTH));
            g.fillRect(x + (rightwards ? -SHADOW_WIDTH : Engine.windowWidth), 0, SHADOW_WIDTH, Engine.windowHeight);
        }
        g.drawImage(background, x, 0, null);
    }

    public static void drawShadow(Graphics2D g, int x, int y, int width, int height, int rounding) {
        if (Engine.fpsDetails >= Engine.DETAILS_MEDIUM) {
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
        if (Engine.fpsDetails == Engine.DETAILS_LOW) {
            g.drawRect(x, y, width, height);
        } else {
            g.drawRoundRect(x, y, width, height, rounding, rounding);
        }
        g.setStroke(stroke);
    }

    public static void drawSquareBorder(Graphics2D g, int x, int y, int width, int height, int rounding, Color color, float strokeWidth) {
        Stroke stroke = g.getStroke();
        g.setColor(color);
        g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        g.drawRect(x, y, width, height);
        g.setStroke(stroke);
    }

    public static void drawBarFilling(Graphics2D g, int x, int y, int width, int height, int rounding, Color color) {
        g.setColor(color);
        if (Engine.fpsDetails == Engine.DETAILS_LOW) {
            g.fillRect(x, y, width, height);
        } else {
            g.fillRoundRect(x, y, width, height, rounding, rounding);
        }
    }

    public static void drawBarBorder(Graphics2D g, int x, int y, int width, int height, int rounding, Color color) {
        g.setColor(color);
        if (Engine.fpsDetails == Engine.DETAILS_LOW) {
            g.drawRect(x, y, width, height);
        } else {
            g.drawRoundRect(x, y, width, height, rounding, rounding);
        }
    }

    public static List<ElementPluginButton> getPluginButtonsForElement(final Element element) {
        List<ElementPluginButton> buttons = new LinkedList<ElementPluginButton>();
        for (Plugin plugin : Engine.plugins) {
            List<ElementMethodCallback> callbacks = plugin.getDefinedElementMethodsApplicableFor(element.type);
            if (Util.isEmpty(callbacks)) {
                continue;
            }
            for (final ElementMethodCallback callback : callbacks) {
                buttons.add(new ElementPluginButton(
                        0,
                        0,
                        callback.getThumbnail(ThumbnailState.NORMAL), callback.getThumbnail(ThumbnailState.HIGHLIGHTED), callback.getThumbnail(ThumbnailState.PRESSED),
                        Button.ALIGN_HORIZONTAL_CENTER | Button.ALIGN_VERTICAL_CENTER,
                        new Button.ButtonNotification() {
                            public void onClick() {
                                callback.trigger(element);
                            }
                        },
                        element,
                        callback)
                );
            }
        }
        return buttons;
    }
}
