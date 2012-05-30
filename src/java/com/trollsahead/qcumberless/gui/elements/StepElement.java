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

package com.trollsahead.qcumberless.gui.elements;

import com.trollsahead.qcumberless.gui.*;
import com.trollsahead.qcumberless.gui.Button;
import com.trollsahead.qcumberless.model.Step;

import java.awt.*;

public class StepElement extends BaseBarElement {
    private Table table = null;
    
    public static final Color[] BG_COLOR_NORMAL = {new Color(0xFF6666), new Color(0xDD9999)};
    public static final Color[] BG_COLOR_FAILED = {new Color(0xFF0000), new Color(0xFF5555)};
    public static final Color[] BG_COLOR_UNRECOGNIZED_STEP = {new Color(0xFF66FF), new Color(0xDD99DD)};

    protected Button tableButton;

    public StepElement(int rootType, String title) {
        super(BaseBarElement.TYPE_STEP, rootType, title);
    }

    public StepElement(int rootType, String title, Step step) {
        super(BaseBarElement.TYPE_STEP, rootType, title, step);
    }

    public StepElement(int rootType, int width, String title, Step step) {
        super(BaseBarElement.TYPE_STEP, rootType, width, title, step);
    }

    public Color getBackgroundColor() {
        int highlightToIndex = isHighlighted() ? 1 : 0;
        if (isFailed) {
            return BG_COLOR_FAILED[highlightToIndex];
        } else if (step.matchedByStepDefinition() || rootType == ROOT_STEP_DEFINITIONS) {
            return BG_COLOR_NORMAL[highlightToIndex];
        } else {
            return BG_COLOR_UNRECOGNIZED_STEP[highlightToIndex];
        }
    }

    public BaseBarElement duplicate() {
        BaseBarElement element = new StepElement(rootType, calculateRenderWidthFromRoot(rootType), title, step.duplicate());
        duplicatePropertiesTo(element);
        return element;
    }

    public void click(int clickCount) {
        if (table != null) {
            if (table.click()) {
                return;
            }
        }
        super.click(clickCount);
    }
    
    protected void drawAdditionals(Graphics2D g) {
        if (table != null) {
            table.render(g, getTextPaddingLeft(), renderHeight - table.getHeight() - TEXT_PADDING_VERTICAL);
        }
    }

    protected boolean isAttachable(int type) {
        return false;
    }

    protected void addAdditionalButtons() {
        tableButton = new Button(
                0,
                0,
                null,
                Images.getImage(Images.IMAGE_TABLE, Images.ThumbnailState.NORMAL.ordinal()), Images.getImage(Images.IMAGE_TABLE, Images.ThumbnailState.HIGHLIGHTED.ordinal()), Images.getImage(Images.IMAGE_TABLE, Images.ThumbnailState.PRESSED.ordinal()),
                Button.ALIGN_HORIZONTAL_CENTER | Button.ALIGN_VERTICAL_CENTER,
                new Button.CucumberButtonNotification() {
                    public void onClick() {
                        addTable();
                    }
                },
                this);
        buttons.add(tableButton);
    }

    private void addTable() {
        table = new Table(this);

    }

    protected void updateAdditionalButtonPositions() {
        if (hasTableButton())
        buttonGroupWidth = addGroupButton(tableButton, buttonGroupWidth);
    }

    protected void updateAdditionalButtonsVisibleState() {
        tableButton.setVisible(hasTableButton() && buttonGroupVisible);
    }

    protected int getAdditionalRenderHeight() {
        return table != null ? (table.getHeight() + TEXT_PADDING_VERTICAL) : 0;
    }

    private boolean hasTableButton() {
        return table == null;
    }
}
