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
        command = command.replaceAll("\\\\", "/");
        String executable = command;
        String path = null;
        if (command.contains("/")) {
            path = command.substring(0, command.lastIndexOf("/"));
            executable = "./" + command.substring(command.lastIndexOf("/") + 1);
        }
        executable = executable.replaceAll("\\$1", tempFile.getAbsolutePath());
        executable = executable.replaceAll("\\$2", Util.tagsToString(tags));
        Helper.executeCommand(executable, path, logListener);
    }

    private static String getCommand() {
        String command = ConfigurationManager.get("genericDeviceCommand");
        if (Util.isEmpty(command)) {
            return showEnterCommandDialog();
        }
        return command;
    }

    public static String showEnterCommandDialog() {
        String command = (String) JOptionPane.showInputDialog(
                CucumberlessDialog.instance,
                "Enter command to run. $1 will be substituted with the feature file and $2 with the tags to run",
                "Run Tests",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "cucumber $1 --format QCumberless::Formatter --tags $2");
        ConfigurationManager.put("genericDeviceCommand", command);
        return command;
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
