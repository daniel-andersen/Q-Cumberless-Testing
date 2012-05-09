package com.trollsahead.qcumberless.plugins.generic;

import com.trollsahead.qcumberless.device.Device;
import com.trollsahead.qcumberless.device.generic.GenericDevice;
import com.trollsahead.qcumberless.model.StepDefinition;
import com.trollsahead.qcumberless.plugins.ButtonBarMethodCallback;
import com.trollsahead.qcumberless.plugins.ElementMethodCallback;
import com.trollsahead.qcumberless.plugins.Plugin;

import java.util.*;

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

    public List<ElementMethodCallback> getElementMethodsApplicableFor(int type) {
        return null;
    }

    public List<ButtonBarMethodCallback> getButtonBarMethods() {
        List<ButtonBarMethodCallback> list = new LinkedList<ButtonBarMethodCallback>();
        list.add(new GenericDeviceSettings());
        return list;
    }
}
