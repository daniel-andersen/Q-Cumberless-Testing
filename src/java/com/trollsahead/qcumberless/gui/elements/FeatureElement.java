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

import java.awt.*;

public class FeatureElement extends BaseBarElement {
    public static final Color[] BG_COLOR_NORMAL = {new Color(0xAAAAFF), new Color(0xBBBBEE)};

    public FeatureElement(int rootType) {
        super(TYPE_FEATURE, rootType);
    }

    public FeatureElement(int rootType, String title) {
        super(TYPE_FEATURE, rootType, title);
    }

    public FeatureElement(int rootType, String title, int width) {
        super(TYPE_FEATURE, rootType, title, width);
    }

    public Color getNormalBackgroundColor() {
        return BG_COLOR_NORMAL[highlightToColorIndex()];
    }

    public BaseBarElement duplicate() {
        BaseBarElement element = new FeatureElement(rootType, title, calculateRenderWidthFromRoot(rootType));
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

    protected boolean isAttachable(int type) {
        return type == TYPE_SCENARIO || type == TYPE_BACKGROUND || type == TYPE_SCENARIO_OUTLINE;
    }

    public StringBuilder buildFeatureInternal() {
        StringBuilder sb = super.buildFeatureInternal();
        sb.append(Locale.getString("feature")).append(": ").append(convertNewlines(title)).append("\n\n");
        return sb;
    }

    private String convertNewlines(String title) {
        return title.replaceAll("\\s\\*\\s", "\n");
    }
}
