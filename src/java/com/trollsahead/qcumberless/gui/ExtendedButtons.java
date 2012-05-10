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
//
// Copyright 2012

// Daniel Andersen (dani_ande@yahoo.dk)

package com.trollsahead.qcumberless.gui;

import com.trollsahead.qcumberless.device.Device;
import com.trollsahead.qcumberless.plugins.ElementMethodCallback;

import java.awt.*;

public class ExtendedButtons {
    public static class DeviceButton extends Button {
        private Device device;

        public DeviceButton(int x, int y, Image normalImage, Image highlightImage, Image pressedImage, int alignment, CucumberButtonNotification notification, Device device) {
            super(x, y, normalImage, highlightImage, pressedImage, alignment, notification, null);
            this.device = device;
        }

        public Device getDevice() {
            return device;
        }

        public int getRenderX() {
            return super.renderX;
        }

        public int getRenderY() {
            return super.renderY;
        }
    }

    public static class ElementPluginButton extends Button {
        private ElementMethodCallback callback;

        public ElementPluginButton(int x, int y, Image normalImage, Image highlightImage, Image pressedImage, int alignment, CucumberButtonNotification notification, Element parent, ElementMethodCallback callback) {
            super(x, y, normalImage, highlightImage, pressedImage, alignment, notification, parent);
            this.callback = callback;
        }

        public ElementMethodCallback getCallback() {
            return callback;
        }
    }
}
