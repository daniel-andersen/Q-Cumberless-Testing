// This file is part of Q-Cumberless Testing.
//
// Q-Cumberless Testing is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Q-Cumberless Testing is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Q-Cumberless Testing.  If not, see <http://www.gnu.org/licenses/>.

package com.trollsahead.qcumberless.device;

import java.awt.*;
import java.util.Set;

public abstract class CucumberDevice {
    public enum Capability {PLAY, PAUSE, STOP}
    public enum ThumbnailState {NORMAL, HIGHLIGHTED, PRESSED}

    private boolean enabled = false;

    public abstract void setDeviceCallback(CucumberDeviceCallback deviceCallback);

    public abstract Set<Capability> getCapabilities();

    public abstract Image getThumbnail(ThumbnailState thumbnailState);

    public abstract String name();

    public abstract void play(StringBuilder feature);
    public abstract void pause();
    public abstract void resume();
    public abstract void stop();

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
