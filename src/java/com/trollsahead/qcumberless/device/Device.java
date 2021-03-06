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

package com.trollsahead.qcumberless.device;

import com.trollsahead.qcumberless.gui.Images;
import com.trollsahead.qcumberless.gui.elements.StepElement;
import com.trollsahead.qcumberless.model.ConsoleOutput;

import java.awt.*;
import java.util.Set;
import java.util.List;

public abstract class Device {
    public enum Capability {PLAY, PAUSE, STOP, STEP, INTERACTIVE_DESIGNING}

    private boolean enabled = false;

    private ConsoleOutput consoleOutput = new ConsoleOutput();

    public abstract void setDeviceCallback(DeviceCallback deviceCallback);

    public abstract Set<Capability> getCapabilities();

    public abstract Image getThumbnail(Images.ThumbnailState thumbnailState);

    public abstract String name();

    public abstract void play(List<StringBuilder> features, Set<String> tags);

    public void stop() {
    }

    public void pause() {
    }

    public void resume() {
    }

    public void initializeStepMode() {
    }

    public void resumeFromStepMode() {
    }

    public void step() {
    }

    public void step(StepElement stepElement) {
    }

    public String getStepPauseDefinition() {
        return "Undefined";
    }

    public InteractiveDesignerClient getInteractiveDesignerClient() {
        return null;
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public ConsoleOutput getConsoleOutput() {
        return consoleOutput;
    }
}
