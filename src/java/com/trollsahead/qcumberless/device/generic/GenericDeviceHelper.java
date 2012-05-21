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

import com.trollsahead.qcumberless.engine.Helper;
import com.trollsahead.qcumberless.engine.LogListener;
import com.trollsahead.qcumberless.gui.CucumberlessDialog;
import com.trollsahead.qcumberless.util.ConfigurationManager;
import com.trollsahead.qcumberless.util.Util;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Set;

public class GenericDeviceHelper {
    public static void runTests(StringBuilder feature, String featureFilename, Set<String> tags, LogListener logListener) {
        File tempFile = createFeatureFile(feature, featureFilename);
        String command = getCommand();
        String path = getPath();
        command = command.replaceAll("\\\\", "/");
        command = command.replaceAll("\\$1", tempFile.getAbsolutePath());
        command = command.replaceAll("\\$2", Util.tagsToString(tags));
        Helper.executeCommand(command, path, logListener);
    }

    public static String getCommand() {
        String command = ConfigurationManager.get("genericDeviceCommand");
        if (Util.isEmpty(command)) {
            return showEnterCommandDialog();
        }
        return command;
    }

    public static String getPath() {
        String command = ConfigurationManager.get("genericDevicePath");
        if (Util.isEmpty(command)) {
            return showEnterPathDialog();
        }
        return command;
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

    private static File createFeatureFile(StringBuilder feature, String featureFilename) {
        try {
            String prefix = featureFilename.substring(0, featureFilename.lastIndexOf("."));
            String suffix = featureFilename.substring(featureFilename.lastIndexOf(".") + 1);

            File tempFile = File.createTempFile(prefix, suffix);
            tempFile.deleteOnExit();

            BufferedWriter tempWriter = new BufferedWriter(new FileWriter(tempFile));
            tempWriter.write(feature.toString());
            tempWriter.close();

            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
