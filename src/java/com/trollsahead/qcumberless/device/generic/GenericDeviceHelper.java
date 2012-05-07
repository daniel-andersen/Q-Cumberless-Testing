package com.trollsahead.qcumberless.device.generic;

import com.trollsahead.qcumberless.engine.Helper;
import com.trollsahead.qcumberless.engine.LogListener;
import com.trollsahead.qcumberless.gui.CucumberlessDialog;
import com.trollsahead.qcumberless.util.Util;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Set;

public class GenericDeviceHelper {
    public static void runTests(StringBuilder feature, String featureFilename, Set<String> tags, LogListener logListener) {
        File tempFile = createFeatureFile(feature, featureFilename);
        String command = showEnterCommandDialog();
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

    private static String showEnterCommandDialog() {
        return (String) JOptionPane.showInputDialog(
                CucumberlessDialog.instance,
                "Enter command to run. $1 will be substituted with the feature file and $2 with the tags to run",
                "Run Tests",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "cucumber $1 --format QCumberless::Formatter --tags $2");
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
