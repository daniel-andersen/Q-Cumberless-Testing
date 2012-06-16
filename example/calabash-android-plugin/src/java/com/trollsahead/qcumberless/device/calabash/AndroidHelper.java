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

package com.trollsahead.qcumberless.device.calabash;

import com.trollsahead.qcumberless.engine.ExecutionHelper;
import com.trollsahead.qcumberless.util.ConfigurationManager;
import com.trollsahead.qcumberless.util.FileUtil;
import com.trollsahead.qcumberless.util.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AndroidHelper {
    public static String getUniqueDevice() {
        List<String> devices = listDevices();
        if (Util.isEmpty(devices)) {
            throw new RuntimeException("No devices found");
        }
        if (devices.size() > 1) {
            throw new RuntimeException("More than one android device connected");
        }
        return devices.get(0);
    }
    
    public static List<String> listDevices() {
        try {
            Pattern pattern = Pattern.compile("(.*)\\t(.*)");
            List<String> devices = new LinkedList<String>();
            List<String> output = ExecutionHelper.executeCommand(getPathToAdb() + " devices");
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
            throw new RuntimeException("Could not list android devices", e);
        }
    }

    public static void pushFileWithContent(String deviceId, String filename, StringBuilder content) {
        try {
            File temp = createTempFile(filename);

            BufferedWriter tempWriter = new BufferedWriter(new FileWriter(temp));
            tempWriter.write(content.toString());
            tempWriter.close();

            String command = getPathToAdb() + " -s " + deviceId + " push " + temp.getAbsolutePath() + " /sdcard/Interactive-Designer/" + filename;
            ExecutionHelper.executeCommand(command);
        } catch (Exception e) {
            throw new RuntimeException("Could not push file " + filename + " to device with id: " + deviceId, e);
        }
    }

    public static File downloadFile(String deviceId, String filename) {
        File temp = createTempFile(FileUtil.removePathFromFilename(filename));
        ExecutionHelper.executeCommand(AndroidHelper.getPathToAdb() + " -s " + deviceId + " pull " + filename +" " + temp.getAbsolutePath());
        return temp;
    }

    public static String getPathToAdb() {
        String path = ConfigurationManager.get("adbPath");
        return Util.isEmpty(path) ? "adb" : path;
    }

    private static File createTempFile(String filename) {
        try {
            int dotIndex = filename.lastIndexOf(".");
            String prefix = filename.substring(0, dotIndex);
            String suffix = filename.substring(dotIndex + 1);
            File temp = File.createTempFile(prefix, suffix);
            temp.deleteOnExit();
            return temp;
        } catch (Exception e) {
            throw new RuntimeException("Could not create temp file: " + filename, e);
        }
    }
}
