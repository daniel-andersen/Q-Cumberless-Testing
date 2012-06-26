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

package com.trollsahead.qcumberless.model;

import com.trollsahead.qcumberless.gui.elements.Element;
import com.trollsahead.qcumberless.gui.elements.FeatureElement;
import com.trollsahead.qcumberless.gui.elements.RootElement;

import java.util.LinkedList;
import java.util.List;

public class UndoSnapshot {
    private List<FeatureSnapshot> features;

    public UndoSnapshot(RootElement root) {
        features = new LinkedList<FeatureSnapshot>();
        for (Element element : root.children) {
            if (!(element instanceof FeatureElement)) {
                continue;
            }
            features.add(
                    new FeatureSnapshot(
                            element.buildFeature(Element.ADD_STATE_NONE),
                            element.buildFeature(Element.ADD_STATE_RUN_OUTCOME | Element.ADD_STATE_VIEW),
                            ((FeatureElement) element).getFilename()));
        }
    }

    public List<FeatureSnapshot> getFeatureSnapshots() {
        return features;
    }

    public StringBuilder concatenateFeatures() {
        StringBuilder sb = new StringBuilder();
        for (FeatureSnapshot snapshot : features) {
            sb.append(snapshot.feature).append("\n\n");
        }
        return sb;
    }

    public boolean equals(Object o1) {
        if (o1 == null || !(o1 instanceof UndoSnapshot)) {
            return false;
        }
        UndoSnapshot otherSnapshot = (UndoSnapshot) o1;
        return concatenateFeatures().toString().equals(otherSnapshot.concatenateFeatures().toString());
    }

    public static class FeatureSnapshot {
        public StringBuilder featureWithState;
        public StringBuilder feature;
        public String filename;

        public FeatureSnapshot(StringBuilder feature, StringBuilder featureWithState, String filename) {
            this.feature = feature;
            this.featureWithState = featureWithState;
            this.filename = filename;
        }
    }
}
