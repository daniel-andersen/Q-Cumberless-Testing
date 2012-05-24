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

package com.trollsahead.qcumberless.device.generic;

import com.trollsahead.qcumberless.engine.ExecutionHelper;
import com.trollsahead.qcumberless.engine.LogListener;
import com.trollsahead.qcumberless.gui.CucumberlessDialog;
import com.trollsahead.qcumberless.util.ConfigurationManager;
import com.trollsahead.qcumberless.util.Util;

import static com.trollsahead.qcumberless.engine.ExecutionHelper.ExecutionStopper;

import javax.swing.*;
import java.io.File;
import java.util.Set;

public class GenericDeviceHelper {
    private static final String FEATURE_FILENAME = "qcumberless_singlerun.feature";
    private static final String QCUMBERLESS_TAG = "@qcumberless";

    public static void runTests(StringBuilder feature, Set<String> tags, LogListener logListener, ExecutionStopper executionStopper) {
        File file = ExecutionHelper.writeFeatureToFile(Util.insertTagIntoFeature(feature, QCUMBERLESS_TAG), Util.addSlashToPath(getPath()) + "features/" + FEATURE_FILENAME);
        try {
            String command = getCommand();
            String path = getPath();
            command = command.replaceAll("\\\\", "/");
            command = command.replaceAll("\\$1", file.getAbsolutePath());
            command = command.replaceAll("\\$2", getTags(Util.tagsToString(tags)));
            ExecutionHelper.executeCommand(command, path, logListener, executionStopper);
        } finally {
            file.delete();
        }
    }

    private static String getTags(String tags) {
        return "--tags=" + QCUMBERLESS_TAG + (!Util.isEmpty(tags) ? " --tags=" + tags : "");
    }

    public static String getCommand() {
        String command = ConfigurationManager.get("genericDeviceCommand");
        if (Util.isEmpty(command)) {
            return showEnterCommandDialog();
        }
        return command;
    }

    public static String getPath() {
        String path = ConfigurationManager.get("genericDevicePath");
        if (Util.isEmpty(path)) {
            return showEnterPathDialog();
        }
        return path;
    }

    public static String showEnterCommandDialog() {
        String command = (String) JOptionPane.showInputDialog(
                CucumberlessDialog.instance,
                "Enter command to run. $1 will be substituted with the feature file and $2 with the tags to run",
                "Specify Command",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "cucumber $1 --format QCumberless::Formatter --tags $2");
        ConfigurationManager.put("genericDeviceCommand", command);
        return command;
    }

    public static String showEnterPathDialog() {
        String path = (String) JOptionPane.showInputDialog(
                CucumberlessDialog.instance,
                "Enter working directory",
                "Specify Directory",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "example");
        ConfigurationManager.put("genericDevicePath", path);
        return path;
    }
}
