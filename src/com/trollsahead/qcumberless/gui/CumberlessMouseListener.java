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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class CumberlessMouseListener implements MouseListener, MouseMotionListener {
    public static int mouseX = 0;
    public static int mouseY = 0;
    public static int oldMouseX = 0;
    public static int oldMouseY = 0;

    public static boolean isButtonPressed = false;

    public CumberlessMouseListener() {
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        updateMousePosition(mouseEvent);
        Engine.click();
    }

    public void mousePressed(MouseEvent mouseEvent) {
        updateMousePosition(mouseEvent);
        if (mouseEvent.getButton() != 1) {
            return;
        }
        isButtonPressed = true;
        Engine.mousePressed();
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
}
