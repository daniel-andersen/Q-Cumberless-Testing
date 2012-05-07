package com.trollsahead.qcumberless.plugins.generic;

import com.trollsahead.qcumberless.device.Device;
import com.trollsahead.qcumberless.device.generic.GenericDevice;
import com.trollsahead.qcumberless.model.StepDefinition;
import com.trollsahead.qcumberless.plugins.Plugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}
