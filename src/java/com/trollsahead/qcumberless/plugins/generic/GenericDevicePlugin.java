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

package com.trollsahead.qcumberless.plugins.generic;

import com.trollsahead.qcumberless.device.Device;
import com.trollsahead.qcumberless.device.generic.GenericDevice;
import com.trollsahead.qcumberless.model.StepDefinition;
import com.trollsahead.qcumberless.plugins.ButtonBarMethodCallback;
import com.trollsahead.qcumberless.plugins.ElementMethodCallback;
import com.trollsahead.qcumberless.plugins.Plugin;

import java.util.*;
import java.util.List;

public class GenericDevicePlugin implements Plugin {
    private GenericDevice genericDevice;

    public void initialize() {
        genericDevice = new GenericDevice();
    }

    public Set<Device> getDevices() {
        Set<Device> devices = new HashSet<Device>();
        devices.add(genericDevice);
        return devices;
    }

    public List<StepDefinition> getStepDefinitions() {
        return null;
    }

    public List<ElementMethodCallback> getDefinedElementMethodsApplicableFor(int type) {
        return null;
    }

    public List<ButtonBarMethodCallback> getButtonBarMethods() {
        List<ButtonBarMethodCallback> list = new LinkedList<ButtonBarMethodCallback>();
        list.add(new GenericDeviceSettings());
        return list;
    }
}
