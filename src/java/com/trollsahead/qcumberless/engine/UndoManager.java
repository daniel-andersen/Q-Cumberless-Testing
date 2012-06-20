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

import com.trollsahead.qcumberless.gui.elements.Element;
import com.trollsahead.qcumberless.gui.elements.FeatureElement;
import com.trollsahead.qcumberless.gui.elements.RootElement;
import com.trollsahead.qcumberless.model.UndoSnapshot;
import com.trollsahead.qcumberless.util.Util;

import static com.trollsahead.qcumberless.model.UndoSnapshot.FeatureSnapshot;

import java.util.LinkedList;
import java.util.List;

public class UndoManager {
    private static final int UNDO_BUFFER = 64;

    private static List<UndoSnapshot> snapshots;
    private static UndoSnapshot lastPoppedElement;

    public static void reset() {
        snapshots = new LinkedList<UndoSnapshot>();
        lastPoppedElement = null;
    }

    public static List<FeatureElement> pop(RootElement currentRoot) {
        if (isEmpty()) {
            return null;
        }
        UndoSnapshot undoSnapshot = snapshots.remove(0);
        UndoSnapshot rootSnapshot = new UndoSnapshot(currentRoot);
        while (undoSnapshot.equals(rootSnapshot)) {
            if (Util.isEmpty(snapshots)) {
                return null;
            }
            undoSnapshot = snapshots.remove(0);
        }
        System.out.println("POPPED: \n" + undoSnapshot.getFeatureSnapshots().get(0).feature.toString());
        List<FeatureElement> features = new LinkedList<FeatureElement>();
        for (FeatureSnapshot snapshot : undoSnapshot.getFeatureSnapshots()) {
            features.add(FeatureLoader.parseFeatureFile(snapshot.featureWithState, snapshot.filename, Element.ADD_STATE_RUN_OUTCOME | Element.ADD_STATE_FOLD));
        }
        lastPoppedElement = undoSnapshot;
        snapshots.add(0, undoSnapshot);
        return features;
    }

    public static void takeSnapshot(RootElement root) {
        UndoSnapshot snapshot = new UndoSnapshot(root);
        if (!isEmpty() && snapshot.equals(snapshots.get(0))) {
            return;
        }
        if (lastPoppedElement != null && snapshot.equals(lastPoppedElement)) {
            return;
        }
        if (snapshots.size() >= UNDO_BUFFER) {
            snapshots.remove(snapshots.size() - 1);
        }
        snapshots.add(0, snapshot);
        lastPoppedElement = null;
    }

    public static boolean isEmpty() {
        return Util.isEmpty(snapshots) || snapshots.size() == 1;
    }
}
