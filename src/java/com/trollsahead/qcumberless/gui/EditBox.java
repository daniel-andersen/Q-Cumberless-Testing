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

import static com.trollsahead.qcumberless.model.Step.CucumberStepPart;

public class EditBox {
    public static boolean isVisible = false;

    private static final int TYPE_ELEMENT = 0;
    private static final int TYPE_PART    = 1;
    private static final int TYPE_TAGS    = 2;

    private static int editType;

    private static TextElement element;
    private static CucumberStepPart part;

    public static void showEditPart(CucumberStepPart part) {
        EditBox.part = part;
        EditBox.element = null;
        EditBox.editType = TYPE_PART;
        CucumberlessDialog.elementTextField.setText(part.text);
        show();
    }
    
    public static void showEditElement(TextElement element) {
        EditBox.element = element;
        EditBox.part = null;
        EditBox.editType = TYPE_ELEMENT;
        CucumberlessDialog.elementTextField.setText(element.step.getFirstPart().text);
        show();
    }

    public static void showEditTags(TextElement element) {
        EditBox.element = element;
        EditBox.part = null;
        EditBox.editType = TYPE_TAGS;
        CucumberlessDialog.elementTextField.setText(element.tags.toString());
        show();
    }

    private static void show() {
        EditBox.isVisible = true;
        CucumberlessDialog.elementTextField.setVisible(true);
        CucumberlessDialog.mainPanel.doLayout();
        CucumberlessDialog.elementTextField.requestFocus();
    }

    public static void hide() {
        isVisible = false;
        element = null;
        CucumberlessDialog.elementTextField.setVisible(false);
        CucumberlessDialog.mainPanel.doLayout();
    }

    public static boolean click() {
        if (!isVisible) {
            return false;
        }
        hide();
        return false;
    }

    public static boolean mousePressed() {
        if (!isVisible) {
            return false;
        }
        hide();
        return false;
    }

    public static boolean mouseReleased() {
        if (!isVisible) {
            return false;
        }
        hide();
        return false;
    }

    public static boolean mouseMoved() {
        if (!isVisible) {
            return false;
        }
        return false;
    }

    public static boolean mouseDragged() {
        hide();
        return false;
    }

    public static void setText(String text) {
        synchronized (Engine.LOCK) {
            if (!isVisible) {
                return;
            }
            if (editType == TYPE_ELEMENT) {
                element.setTitle(text);
            } else if (editType == TYPE_PART) {
                part.text = text;
            } else if (editType == TYPE_TAGS) {
                element.setTags(text);
            }
        }
    }

    public static boolean isEditing(TextElement cucumberTextElement) {
        return cucumberTextElement == element;
    }
}
