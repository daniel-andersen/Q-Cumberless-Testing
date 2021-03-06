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
import com.trollsahead.qcumberless.gui.elements.BaseBarElement;
import com.trollsahead.qcumberless.gui.elements.Table;
import com.trollsahead.qcumberless.util.Util;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;

import static com.trollsahead.qcumberless.model.Step.CucumberStepPart;

public class EditBox {
    public static boolean isVisible = false;

    private static final int TYPE_SINGLELINE_ELEMENT = 0;
    private static final int TYPE_MULTILINE_ELEMENT  = 1;
    private static final int TYPE_PART               = 2;
    private static final int TYPE_TAGS               = 3;
    private static final int TYPE_TABLE_CELL         = 4;

    private static int editType;

    private static BaseBarElement element;
    private static CucumberStepPart part;
    private static Table table;
    private static JTextComponent textComponent;

    public static void showEditPart(CucumberStepPart part) {
        EditBox.part = part;
        EditBox.editType = TYPE_PART;
        CucumberlessDialog.elementTextField.setText(part.getText());
        show();
    }
    
    public static void showSinglelineEditElement(BaseBarElement element) {
        EditBox.element = element;
        EditBox.editType = TYPE_SINGLELINE_ELEMENT;
        CucumberlessDialog.elementTextField.setText(element.step.getFirstPart().getText());
        show();
    }

    public static void showMultilineEditElement(BaseBarElement element) {
        EditBox.element = element;
        EditBox.editType = TYPE_MULTILINE_ELEMENT;
        if (Util.isEmpty(element.getComment())) {
            CucumberlessDialog.elementTextArea.setText(element.step.getFirstPart().getText());
        } else {
            CucumberlessDialog.elementTextArea.setText(element.getComment() + "\n" + element.step.getFirstPart().getText());
        }
        show();
    }

    public static void showEditTags(BaseBarElement element) {
        EditBox.element = element;
        EditBox.editType = TYPE_TAGS;
        CucumberlessDialog.elementTextField.setText(element.tags.toString());
        show();
    }

    public static void showEditTable(Table table) {
        EditBox.table = table;
        EditBox.editType = TYPE_TABLE_CELL;
        CucumberlessDialog.elementTextField.setText(table.getEditedCellText());
        show();
    }

    private static void show() {
        hide();
        textComponent = editType == TYPE_MULTILINE_ELEMENT ? CucumberlessDialog.elementTextArea : CucumberlessDialog.elementTextField;
        EditBox.isVisible = true;
        CucumberlessDialog.mainPanel.add(textComponent, BorderLayout.SOUTH);
        textComponent.setVisible(true);
        CucumberlessDialog.mainPanel.doLayout();
        textComponent.requestFocus();
    }

    public static void hide() {
        if (!isVisible) {
            return;
        }
        isVisible = false;
        element = null;
        part = null;
        table = null;
        CucumberlessDialog.elementTextField.setVisible(false);
        CucumberlessDialog.elementTextArea.setVisible(false);
        CucumberlessDialog.mainPanel.remove(textComponent);
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
        synchronized (Engine.DATA_LOCK) {
            if (!isVisible) {
                return;
            }
            if (editType == TYPE_SINGLELINE_ELEMENT || editType == TYPE_MULTILINE_ELEMENT) {
                element.setTitleAndComment(text);
            } else if (editType == TYPE_PART) {
                part.setText(text);
            } else if (editType == TYPE_TAGS) {
                element.setTags(text);
            } else if (editType == TYPE_TABLE_CELL) {
                table.setEditedCellText(text);
            }
        }
    }

    public static boolean isEditing(BaseBarElement cucumberTextElement) {
        return cucumberTextElement == element;
    }

    public static void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE || (keyEvent.getKeyCode() == KeyEvent.VK_ENTER && editType != TYPE_MULTILINE_ELEMENT)) {
            hide();
        }
    }
}
