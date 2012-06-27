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

import com.trollsahead.qcumberless.gui.elements.StepElement;

public class FeatureBuildState {
    public static final int ADD_STATE_NONE        = 0;
    public static final int ADD_STATE_RUN_OUTCOME = 1 << 1;
    public static final int ADD_STATE_VIEW        = 1 << 2;
    public static final int ADD_STATE_RUN_TO      = 1 << 3;
    public static final int ADD_STATE_STEP_MODE   = 1 << 4;

    private int state = ADD_STATE_NONE;

    private long time = System.currentTimeMillis();
    private String stepPauseDefinition = null;
    private StepElement stepPauseElement = null;

    public FeatureBuildState(int ... states) {
        setState(states);
    }

    public FeatureBuildState(StepElement stepPauseElement, String stepPauseDefinition, int ... states) {
        this.stepPauseElement = stepPauseElement;
        this.stepPauseDefinition = stepPauseDefinition;
        setState(states);
    }

    public FeatureBuildState(Long time, int ... addStates) {
        this.time = time;
        setState(addStates);
    }

    public boolean hasState(int state) {
        return (this.state & state) != 0;
    }

    public void setState(int... states) {
        state = ADD_STATE_NONE;
        for (int state : states) {
            addState(state);
        }
    }

    public void addState(int state) {
        this.state |= state;
    }

    public void removeState(int state) {
        this.state = ~state & this.state;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setStepPauseDefinition(String stepPauseDefinition) {
        this.stepPauseDefinition = stepPauseDefinition;
    }

    public String getStepPauseDefinition() {
        return stepPauseDefinition;
    }

    public StepElement getStepPauseElement() {
        return stepPauseElement;
    }

    public void setStepPauseElement(StepElement stepPauseElement) {
        this.stepPauseElement = stepPauseElement;
    }
}
