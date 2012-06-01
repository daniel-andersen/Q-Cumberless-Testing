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

import java.awt.event.*;

public class CumberlessMouseListener implements MouseListener, MouseMotionListener, MouseWheelListener {
    public static int mouseX = 0;
    public static int mouseY = 0;
    public static int oldMouseX = 0;
    public static int oldMouseY = 0;

    public static boolean isButtonPressed = false;

    public CumberlessMouseListener() {
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        updateMousePosition(mouseEvent);
        Engine.click(mouseEvent.getClickCount());
    }

    public void mousePressed(MouseEvent mouseEvent) {
        updateMousePosition(mouseEvent);
        if (mouseEvent.getButton() != 1) {
            return;
        }
        isButtonPressed = true;
        Engine.mousePressed(mouseEvent.isControlDown());
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        updateMousePosition(mouseEvent);
        if (mouseEvent.getButton() != 1) {
            return;
        }
        isButtonPressed = false;
        Engine.mouseReleased();
    }

    public void mouseEntered(MouseEvent mouseEvent) {
        updateMousePosition(mouseEvent);
    }

    public void mouseExited(MouseEvent mouseEvent) {
        updateMousePosition(mouseEvent);
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        updateMousePosition(mouseEvent);
        Engine.mouseDragged();
    }

    public void mouseMoved(MouseEvent mouseEvent) {
        updateMousePosition(mouseEvent);
        Engine.mouseMoved();
    }

    private void updateMousePosition(MouseEvent mouseEvent) {
        oldMouseX = mouseX;
        oldMouseY = mouseY;
        mouseX = mouseEvent.getX();
        mouseY = mouseEvent.getY();
    }

    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        Engine.mouseWheelMoved(mouseWheelEvent.getUnitsToScroll());
    }
}
