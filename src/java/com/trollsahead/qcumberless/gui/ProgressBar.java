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

import java.awt.*;

import static com.trollsahead.qcumberless.gui.Animation.MoveAnimation;

public class ProgressBar {
    private static final float MOVE_SPEED = 0.8f;

    private static final int WIDTH = 500;
    private static final int HEIGHT = 50;

    private static final int PROGRESS_BAR_HEIGHT = 10;

    private static final int TEXT_GAP_VERTICAL = 2;
    private static final int PROGRESS_BAR_GAP = 8;

    private static final Color TEXT_COLOR = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Color BACKGROUND_COLOR = new Color(0.2f, 0.2f, 0.2f, 0.6f);
    private static final Color PROGRESS_BAR_COLOR = new Color(1.0f, 1.0f, 0.2f, 0.8f);
    private static final Color PROGRESS_BAR_BACKGROUND_COLOR = new Color(0.2f, 0.2f, 0.2f, 0.6f);

    private String title;

    private float percent;

    private boolean positionDefined = false;

    private MoveAnimation moveAnimation = new MoveAnimation();

    public ProgressBar(String title) {
        this.title = title;
    }

    public void setProcess(float percent) {
        this.percent = percent;
    }

    public void setPosition(int x, int y) {
        moveAnimation.setRealPosition(x, y, MOVE_SPEED);
        if (!positionDefined) {
            moveAnimation.setRenderPosition(x, y, MOVE_SPEED);
            positionDefined = true;
        }
    }

    public void update() {
        moveAnimation.update(false);
    }

    public void render(Graphics2D g) {
        FontMetrics fontMetrics = g.getFontMetrics();

        int x = (Engine.canvasWidth - getWidth()) / 2;
        int y = (int) moveAnimation.renderY;
        int progressWidth = (int) ((float) getWidth() * percent / 100.0f);
        g.setColor(BACKGROUND_COLOR);
        g.fillRoundRect(x, y, getWidth(), getHeight(), 20, 20);

        g.setColor(PROGRESS_BAR_BACKGROUND_COLOR);
        g.fillRect(x + PROGRESS_BAR_GAP, y + getHeight() - PROGRESS_BAR_GAP - PROGRESS_BAR_HEIGHT, getWidth() - (PROGRESS_BAR_GAP * 2), PROGRESS_BAR_HEIGHT);

        g.setColor(PROGRESS_BAR_COLOR);
        g.fillRect(x + PROGRESS_BAR_GAP, y + getHeight() - PROGRESS_BAR_GAP - PROGRESS_BAR_HEIGHT, progressWidth, PROGRESS_BAR_HEIGHT);

        g.setColor(TEXT_COLOR);
        g.drawString(title, (Engine.canvasWidth - fontMetrics.stringWidth(title)) / 2, (int) moveAnimation.renderY + TEXT_GAP_VERTICAL + fontMetrics.getHeight());
    }

    public int getWidth() {
        return Math.min(WIDTH, Engine.canvasWidth * 5 / 6);
    }

    public int getHeight() {
        return HEIGHT;
    }
}
