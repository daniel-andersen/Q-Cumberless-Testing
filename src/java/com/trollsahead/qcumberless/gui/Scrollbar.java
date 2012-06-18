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

import java.awt.*;

public class Scrollbar {
    private static final int SCROLLBAR_WIDTH = 10;
    private static final int SCROLLBAR_ROUNDING = 7;

    private static final Color COLOR_BG_NORMAL    = new Color(1.0f, 1.0f, 1.0f, 0.5f);
    private static final Color COLOR_BG_HIGHLIGHT = new Color(1.0f, 1.0f, 1.0f, 0.6f);

    private int renderX = 0;
    private int renderY = 0;
    private int renderHeight = 1;

    private int scrollbarY = 0;
    private int scrollbarBarHeight = 1;

    private int oldMouseY = -1;

    private boolean isDraggingScrollbar = false;

    public void setBounds(int x, int y, int height, int areaHeight) {
        renderX = x;
        renderY = y;
        renderHeight = height;
        scrollbarBarHeight = renderHeight < areaHeight ? (int) (renderHeight * ((float) renderHeight / areaHeight)) : renderHeight;
    }

    public void scroll(int deltaY) {
        dragScrollbar(deltaY);
    }

    public void startDrag() {
        if (isScrollbarTouched()) {
            isDraggingScrollbar = true;
            oldMouseY = CumberlessMouseListener.mouseY;
            dragScrollbar(0);
        }
    }

    public void endDrag() {
        isDraggingScrollbar = false;
    }

    private void dragScrollbar(int deltaY) {
        scrollbarY += deltaY;
        scrollbarY = Math.max(0, Math.min(renderHeight - scrollbarBarHeight, scrollbarY));
    }

    public void updateDrag() {
        if (isDraggingScrollbar) {
            dragScrollbar(CumberlessMouseListener.mouseY - oldMouseY);
            oldMouseY = CumberlessMouseListener.mouseY;
        }
    }

    public void render(Graphics g) {
        g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.5f));
        g.fillRoundRect(renderX, renderY, SCROLLBAR_WIDTH, renderHeight, SCROLLBAR_ROUNDING, SCROLLBAR_ROUNDING);

        g.setColor(isScrollbarTouched() || isDraggingScrollbar ? COLOR_BG_HIGHLIGHT : COLOR_BG_NORMAL);
        g.fillRoundRect(renderX + 1, renderY + scrollbarY + 1, SCROLLBAR_WIDTH - 2, scrollbarBarHeight - 2, SCROLLBAR_ROUNDING, SCROLLBAR_ROUNDING);
    }

    public boolean isScrollbarTouched() {
        return  CumberlessMouseListener.mouseX >= renderX && CumberlessMouseListener.mouseX <= renderX + SCROLLBAR_WIDTH &&
                CumberlessMouseListener.mouseY >= renderY && CumberlessMouseListener.mouseY <= renderY + renderHeight;
    }

    public int getScroll() {
        return scrollbarY;
    }
}
