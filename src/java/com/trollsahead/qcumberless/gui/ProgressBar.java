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

public class ProgressBar extends FlashingMessage {
    private static final int WIDTH = 500;
    private static final int HEIGHT = 50;

    private static final int PROGRESS_BAR_HEIGHT = 10;

    private static final int PROGRESS_BAR_GAP = 8;

    private static final Color PROGRESS_BAR_COLOR = new Color(0.8f, 0.8f, 0.4f, 0.6f);
    private static final Color PROGRESS_BAR_BACKGROUND_COLOR = new Color(0.2f, 0.2f, 0.2f, 0.6f);

    private float percent;

    public ProgressBar(String title) {
        super(title);
    }

    public void setProcess(float percent) {
        this.percent = percent;
    }

    public void render(Graphics2D g) {
        super.render(g);

        int x = (Engine.windowWidth - getWidth()) / 2;
        int y = (int) moveAnimation.renderY;

        int progressWidth = (int) ((float) getWidth() * percent / 100.0f);

        g.setColor(PROGRESS_BAR_BACKGROUND_COLOR);
        g.fillRect(x + PROGRESS_BAR_GAP, y + getHeight() - PROGRESS_BAR_GAP - PROGRESS_BAR_HEIGHT, getWidth() - (PROGRESS_BAR_GAP * 2), PROGRESS_BAR_HEIGHT);

        g.setColor(PROGRESS_BAR_COLOR);
        g.fillRect(x + PROGRESS_BAR_GAP, y + getHeight() - PROGRESS_BAR_GAP - PROGRESS_BAR_HEIGHT, progressWidth, PROGRESS_BAR_HEIGHT);
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }
}
