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

import com.trollsahead.qcumberless.util.Util;

import java.util.LinkedList;
import java.util.List;

public class PlayState {
    public enum State {NOT_YET_PLAYED, SUCCESS, FAILED}

    private State state = State.NOT_YET_PLAYED;
    private String errorMessage = null;
    private List<Screenshot> screenshots = null;

    public PlayState() {
    }

    public PlayState(State state) {
        this.state = state;
    }

    public PlayState(State state, String errorMessage) {
        this.state = state;
        this.errorMessage = errorMessage;
    }

    public State getState() {
        return state;
    }

    public boolean isSuccess() {
        return state == State.SUCCESS;
    }

    public boolean isFailed() {
        return state == State.FAILED;
    }

    public boolean isNotYetPlayed() {
        return state == State.NOT_YET_PLAYED;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public void setScreenshots(List<Screenshot> screenshots) {
        this.screenshots = screenshots;
    }

    public void setScreenshots(Screenshot ... screenshots) {
        this.screenshots = new LinkedList<Screenshot>();
        for (Screenshot screenshot : screenshots) {
            this.screenshots.add(screenshot);
        }
    }

    public void addScreenshots(Screenshot ... screenshots) {
        if (this.screenshots == null) {
            this.screenshots = new LinkedList<Screenshot>();
        }
        for (Screenshot screenshot : screenshots) {
            this.screenshots.add(screenshot);
        }
    }

    public List<Screenshot> getScreenshots() {
        return screenshots;
    }
    
    public boolean hasErrorMessage() {
        return !Util.isEmpty(errorMessage);
    }
    
    public boolean hasScreenshots() {
        return screenshots != null && screenshots.size() > 0;
    }
}
