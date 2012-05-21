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
