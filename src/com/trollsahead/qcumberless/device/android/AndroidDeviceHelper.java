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

package com.trollsahead.qcumberless.device.android;

import com.trollsahead.qcumberless.engine.Helper;
import com.trollsahead.qcumberless.engine.LogListener;
import com.trollsahead.qcumberless.util.ConfigurationManager;
import com.trollsahead.qcumberless.util.Util;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AndroidDeviceHelper {
    public static final String PROP_ADB_PATH = "adbPath";

    public static List<String> listDevices() {
        try {
            Pattern pattern = Pattern.compile("(.*)\\t(.*)");
            List<String> devices = new LinkedList<String>();
            List<String> output = Helper.executeCommand(getPathToAdb() + getAdbExecutable() + " devices");
            for (String s : output) {
                Matcher matcher = pattern.matcher(s);
                if (!matcher.matches()) {
                    continue;
                }
                if (!"device".equalsIgnoreCase(matcher.group(2))) {
                    continue;
                }
                devices.add(matcher.group(1));
            }
            return devices;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String getDeviceProperty(String deviceId, String key) {
        try {
            Pattern pattern = Pattern.compile("\\[(.*)\\]: \\[(.*)\\]");
            List<String> output = Helper.executeCommand(getPathToAdb() + getAdbExecutable() + " -s " + deviceId + " shell getprop");
            for (String s : output) {
                Matcher matcher = pattern.matcher(s);
                if (!matcher.matches()) {
                    continue;
                }
                if (key.equalsIgnoreCase(matcher.group(1))) {
                    return matcher.group(2);
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void runTests(String deviceId, StringBuilder feature, String tags, String featureFilename, String testRunners, LogListener logListener) {
        Helper.ExecutorStopper executorStopper = null;
        try {
            pushFeatureFile(deviceId, feature, featureFilename);
            cleanDeviceLog(deviceId);
            executorStopper = monitorLog(deviceId, logListener);
            runFeature(deviceId, testRunners, tags);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (executorStopper != null) {
                executorStopper.stop();
            }
        }
    }

    private static Helper.ExecutorStopper monitorLog(final String deviceId, final LogListener logListener) {
        final Helper.ExecutorStopper executorStopper = new Helper.ExecutorStopper();
        new Thread(new Runnable() {
            public void run() {
                String command = getPathToAdb() + getAdbExecutable() + " -s " + deviceId + " logcat -v time";
                Helper.executeCommand(command, logListener, executorStopper);
            }
        }).start();
        return executorStopper;
    }

    private static void runFeature(String deviceId, String testRunners, String tags) {
        String command = getPathToAdb() + getAdbExecutable() + " -s " + deviceId + " shell am instrument  -w -e tags " + tags + " -e class " + testRunners;
        Helper.executeCommand(command);
    }

    private static void pushFeatureFile(String deviceId, StringBuilder feature, String featureFilename) throws Exception {
        pushFileWithContent(deviceId, featureFilename, feature);
    }

    private static void cleanDeviceLog(String deviceId) throws Exception {
        String command = getPathToAdb() + getAdbExecutable() + " -s " + deviceId + " logcat -c";
        Helper.executeCommand(command);
    }

    public static void pushPauseFile(String deviceId) throws Exception {
        pushFileWithContent(deviceId, "pause.txt", new StringBuilder().append("Pause - now! :)"));
    }

    public static void removePauseFile(String deviceId) {
        String command = getPathToAdb() + getAdbExecutable() + " -s " + deviceId + " shell rm /sdcard/pause.txt";
        Helper.executeCommand(command);
    }

    public static void pushStopFile(String deviceId) throws Exception {
        pushFileWithContent(deviceId, "stop.txt", new StringBuilder().append("Stop - now! :)"));
    }

    private static void pushFileWithContent(String deviceId, String filename, StringBuilder content) throws Exception {
        int dotIndex = filename.lastIndexOf(".");
        String prefix = filename.substring(0, dotIndex);
        String suffix = filename.substring(dotIndex + 1);
        File temp = File.createTempFile(prefix, suffix);
        temp.deleteOnExit();

        BufferedWriter tempWriter = new BufferedWriter(new FileWriter(temp));
        tempWriter.write(content.toString());
        tempWriter.close();

        String command = getPathToAdb() + getAdbExecutable() + " -s " + deviceId + " push " + temp.getAbsolutePath() + " /sdcard/" + filename;
        Helper.executeCommand(command);
    }

    public static String getPathToAdb() {
        String path = ConfigurationManager.get(PROP_ADB_PATH);
        if (Util.isEmpty(path)) {
            return "";
        } else {
            return path.endsWith("/") ? path : (path + "/");
        }
    }

    public static String getAdbExecutable() {
        return Util.isMac() ? "adb" : "adb.exe";
    }
}
