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

package com.trollsahead.qcumberless.engine;

import com.trollsahead.qcumberless.gui.elements.BaseBarElement;
import com.trollsahead.qcumberless.gui.elements.Element;
import com.trollsahead.qcumberless.model.Locale;
import com.trollsahead.qcumberless.util.ElementHelper;
import com.trollsahead.qcumberless.util.Util;

public class FeatureBuilder {
    public static StringBuilder buildFeature(BaseBarElement cucumberTextElement) {
        if (cucumberTextElement.type == BaseBarElement.TYPE_FEATURE) {
            return cucumberTextElement.buildFeature();
        }
        StringBuilder sb = new StringBuilder();
        if (cucumberTextElement.type == BaseBarElement.TYPE_SCENARIO || cucumberTextElement.type == BaseBarElement.TYPE_BACKGROUND || cucumberTextElement.type == BaseBarElement.TYPE_SCENARIO_OUTLINE) {
            BaseBarElement parentTextElement = (BaseBarElement) cucumberTextElement.groupParent;
            if (!Util.isEmpty(parentTextElement.getComment())) {
                sb.append(parentTextElement.getComment()).append("\n");
            }
            if (!Util.isEmpty(parentTextElement.getTagsString())) {
                sb.append(parentTextElement.getTagsString()).append("\n");
            }
            sb.append(Locale.getString("feature")).append(": ").append(parentTextElement.getTitle()).append("\n\n");
            if (cucumberTextElement.type != BaseBarElement.TYPE_BACKGROUND) {
                Element background = ElementHelper.findBackgroundElement(parentTextElement);
                if (background != null) {
                    sb.append(background.buildFeature());
                }
            }
        }
        sb.append(cucumberTextElement.buildFeature());
        if (cucumberTextElement.type == BaseBarElement.TYPE_BACKGROUND) {
            sb.append("\n");
            sb.append(ElementHelper.EXPORT_INDENT).append(Locale.getString("scenario")).append(": Testing background\n");
            sb.append(ElementHelper.EXPORT_INDENT).append(ElementHelper.EXPORT_INDENT).append("# Just for testing background\n");
        }
        return sb;
    }
}
