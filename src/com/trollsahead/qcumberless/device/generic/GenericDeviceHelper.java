package com.trollsahead.qcumberless.device.generic;

import com.trollsahead.qcumberless.engine.Helper;
import com.trollsahead.qcumberless.engine.LogListener;
import com.trollsahead.qcumberless.gui.CucumberlessDialog;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class GenericDeviceHelper {
    public static void runTests(StringBuilder feature, String featureFilename, LogListener logListener) {
        File tempFile = createFeatureFile(feature, featureFilename);
        String command = showEnterCommandDialog();
        command = command
                .replaceAll("\\$1", tempFile.getAbsolutePath())
                .replaceAll("\\\\", "/");
        String executable = command;
        String path = null;
        if (command.contains("/")) {
            path = command.substring(0, command.indexOf("/"));
            executable = command.substring(command.indexOf("/") + 1);
        }
        Helper.executeCommand(executable, path, logListener);
    }

    private static String showEnterCommandDialog() {
        return (String) JOptionPane.showInputDialog(
                CucumberlessDialog.instance,
                "Enter command to run",
                "Run Tests",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "mvn package -Pintegration-test");
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
