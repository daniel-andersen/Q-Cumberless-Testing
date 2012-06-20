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

import com.trollsahead.qcumberless.engine.Engine;
import com.trollsahead.qcumberless.gui.*;
import com.trollsahead.qcumberless.gui.Button;
import com.trollsahead.qcumberless.model.Step;
import com.trollsahead.qcumberless.util.ElementHelper;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class StepElement extends BaseBarElement {
    protected Table table = null;
    
    public static final Color[] BG_COLOR_NORMAL = {new Color(0xEE6666), new Color(0xDD9999)};
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

    protected void renderHintsInternal(Graphics2D g) {
        super.renderHintsInternal(g);
        if (table != null) {
            table.renderHints(g);
        }
    }
    
    public Color getNormalBackgroundColor() {
        if (step.matchedByStepDefinition() || rootType == ROOT_STEP_DEFINITIONS) {
            return BG_COLOR_NORMAL[highlightToColorIndex()];
        } else {
            return BG_COLOR_UNRECOGNIZED_STEP[highlightToColorIndex()];
        }
    }

    public BaseBarElement duplicate() {
        StepElement element = new StepElement(rootType, calculateRenderWidthFromRoot(rootType), title, step.duplicate());
        if (table != null) {
            element.table = table.duplicate(element);
        }
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

    protected boolean isFoldable() {
        return false;
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
                new Button.ButtonNotification() {
                    public void onClick() {
                        if (table == null) {
                            createTable();
                        } else {
                            DropDown.show(
                                    tableButton.renderX + TEXT_PADDING_HORIZONTAL,
                                    tableButton.renderY + TEXT_PADDING_VERTICAL,
                                    new DropDown.DropDownCallback() {
                                        public void chooseItem(String item) {
                                            synchronized (Engine.DATA_LOCK) {
                                                if ("Delete table".equalsIgnoreCase(item)) {
                                                    table = null;
                                                } else {
                                                    table.adjustTableSize(item);
                                                }
                                            }
                                        }
                                    },
                                    getTableActions());
                        }
                    }
                },
                this);
        buttons.add(tableButton);
    }

    protected List<String> getTableActions() {
        return Arrays.asList(new String[] {"Add row", "Add column", "Delete row", "Delete column", "Delete table"});
    }

    public void createTable() {
        table = new Table(this);
    }

    public void addRowToTable(String[] cells) {
        if (table == null) {
            table = new Table(this, cells);
        } else {
            table.addRow(cells);
        }
    }

    protected void updateAdditionalButtonPositions() {
        if (!canEdit()) {
            return;
        }
        buttonGroupWidth = addGroupButton(tableButton, buttonGroupWidth);
    }

    protected void updateAdditionalButtonsVisibleState() {
        tableButton.setVisible(buttonGroupVisible && canEdit());
    }

    protected int getAdditionalRenderHeight() {
        return table != null ? (table.getHeight() + TEXT_PADDING_VERTICAL) : 0;
    }

    public StringBuilder buildFeatureInternal(int addState, long time) {
        StringBuilder sb = super.buildFeatureInternal(addState, time);
        sb.append(ElementHelper.EXPORT_INDENT).append(ElementHelper.EXPORT_INDENT).append(step.toString()).append("\n");
        if (table != null) {
            sb.append(table.buildFeature());
        }
        return sb;
    }
}
