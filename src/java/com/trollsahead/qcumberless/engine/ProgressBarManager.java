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

import com.trollsahead.qcumberless.gui.ProgressBar;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class ProgressBarManager {
    private static final int PROGRESS_BAR_TOP = 50;
    private static final int PROGRESS_BAR_GAP = 15;

    private static List<ProgressBar> progressBars = new LinkedList<ProgressBar>();

    public static void initialize() {
    }

    public static void addProgressBar(ProgressBar progressBar) {
        synchronized (Engine.LOCK) {
            progressBars.add(progressBar);
            repositionProgressBars();
        }
    }

    public static void removeProgressBar(ProgressBar progressBar) {
        synchronized (Engine.LOCK) {
            progressBars.remove(progressBar);
            repositionProgressBars();
        }
    }
    
    private static void repositionProgressBars() {
        int y = PROGRESS_BAR_TOP;
        for (ProgressBar progressBar : progressBars) {
            progressBar.setPosition((Engine.canvasWidth - progressBar.getWidth()) / 2, y);
            y += progressBar.getHeight() + PROGRESS_BAR_GAP;
        }
    }

    public static void update() {
        for (ProgressBar progressBar : progressBars) {
            progressBar.update();
        }
    }

    public static void render(Graphics2D g) {
        for (ProgressBar progressBar : progressBars) {
            progressBar.render(g);
        }
    }
}