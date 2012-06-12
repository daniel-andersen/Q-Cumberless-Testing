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

import com.trollsahead.qcumberless.engine.FeatureLoader;
import com.trollsahead.qcumberless.util.HistoryHelper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RunHistory {
    private static final Object LOCK = new Object();

    private static List<String> features;
    private static Map<String, TagHistory> tagHistory;
    
    public static void initialize() {
        clear();
        new Thread(new Runnable() {
            public void run() {
                updateTagHistory(HistoryHelper.findFeatureFiles());
            }
        }).start();
    }
    
    public static void clear() {
        synchronized (LOCK) {
            features = new LinkedList<String>();
            tagHistory = new HashMap<String, TagHistory>();
        }
    }
    
    public static TagHistory getTagHistory(String tag) {
        synchronized (LOCK) {
            return tagHistory.get(tag);
        }
    }
    
    public static void addFeature(String filename) {
        synchronized (LOCK) {
            features.add(filename);
            updateTagHistory(filename);
        }
    }

    private static void updateTagHistory(List<String> features) {
        for (String feature : features) {
            updateTagHistory(feature);
        }
    }

    private static void updateTagHistory(String feature) {
        synchronized (LOCK) {
            FeatureLoader.extractTagHistory(tagHistory, feature);
        }
    }
}
