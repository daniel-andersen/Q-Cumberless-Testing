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

import com.trollsahead.qcumberless.model.Locale;
import com.trollsahead.qcumberless.util.ElementHelper;

import java.awt.*;

public class BackgroundElement extends BaseBarElement {
    public static final Color[] BG_COLOR_NORMAL = {new Color(0x888888), new Color(0xAAAAAA)};

    public BackgroundElement(int rootType) {
        super(TYPE_BACKGROUND, rootType);
        folded = true;
    }

    public BackgroundElement(int rootType, String title) {
        super(TYPE_BACKGROUND, rootType, title);
        folded = true;
    }

    public BackgroundElement(int rootType, String title, int width) {
        super(TYPE_BACKGROUND, rootType, title, width);
        folded = true;
    }

    public Color getNormalBackgroundColor() {
        return BG_COLOR_NORMAL[highlightToColorIndex()];
    }

    public BaseBarElement duplicate() {
        BaseBarElement element = new BackgroundElement(rootType, title, calculateRenderWidthFromRoot(rootType));
        duplicatePropertiesTo(element);
        return element;
    }

    protected void drawAdditionals(Graphics2D g) {
    }

    protected void addAdditionalButtons() {
    }

    protected void updateAdditionalButtonPositions() {
    }

    protected void updateAdditionalButtonsVisibleState() {
    }

    protected int getAdditionalRenderHeight() {
        return 0;
    }

    public boolean isFoldable() {
        return rootType == ROOT_FEATURE_EDITOR;
    }

    protected boolean isAttachable(int type) {
        return type == TYPE_STEP || type == TYPE_COMMENT;
    }

    public StringBuilder buildFeatureInternal(int addState, long time) {
        StringBuilder sb = super.buildFeatureInternal(addState, time);
        sb.append(ElementHelper.EXPORT_INDENT).append(Locale.getString("background")).append(":\n");
        return sb;
    }
}
