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

import com.trollsahead.qcumberless.model.FeatureBuildState;
import com.trollsahead.qcumberless.model.Locale;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ExamplesElement extends StepElement {
    public static final Color[] BG_COLOR_NORMAL = {new Color(0.45f, 0.45f, 0.86f), new Color(0.55f, 0.55f, 0.96f)};

    public ExamplesElement() {
        super(ROOT_FEATURE_EDITOR, Locale.getString("Examples") + ":");
        type = TYPE_EXAMPLES;
        createTable();
    }

    public Color getNormalBackgroundColor() {
        return BG_COLOR_NORMAL[highlightToColorIndex()];
    }

    public boolean isFoldable() {
        return false;
    }

    public boolean isVisible() {
        return groupParent.rootType == ROOT_FEATURE_EDITOR;
    }

    protected void renderBefore(Graphics2D g) {
        if (groupParent.rootType != ROOT_FEATURE_EDITOR) {
            return;
        }
        super.renderBefore(g);
    }

    public BaseBarElement duplicate() {
        ExamplesElement element = new ExamplesElement();
        if (table != null) {
            element.table = table.duplicate(element);
        }
        duplicatePropertiesTo(element);
        return element;
    }

    protected List<String> getTableActions() {
        return Arrays.asList(new String[] {"Add row", "Add column", "Delete row", "Delete column"});
    }

    public StringBuilder buildFeatureInternal(FeatureBuildState buildState) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(super.buildFeatureInternal(buildState));
        return sb;
    }

    protected boolean hasTrashcanButton() {
        return false;
    }

    public void clearTable() {
        table = null;
    }

    public void clearRunStatus() {
        super.clearRunStatus();
        table.clearRunStatus();
    }

    public boolean isFailed(int row) {
        return table.isFailed(row);
    }

    public void setFailed(int row, String errorMessage) {
        table.setFailed(row, errorMessage);
    }

    public void setSuccess(int row) {
        table.setSuccess(row);
    }
}
