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

package com.trollsahead.qcumberless.engine;

import com.trollsahead.qcumberless.util.ConfigurationManager;
import com.trollsahead.qcumberless.util.FileUtil;
import com.trollsahead.qcumberless.util.Util;

import java.awt.*;
import java.io.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ExecutionHelper {
    public static List<String> executeCommand(String command) {
        return executeCommand(command, null);
    }
    
    public static List<String> executeCommand(String command, String dir) {
        final List<String> output = new LinkedList<String>();
        executeCommand(command, dir, new LogListener() {
            public void start() {
            }

            public void finish() {
            }

            public void logLine(String log) {
                output.add(log);
            }

            public void error(Throwable t) {
                t.printStackTrace();
            }
        });
        return output;
    }

    public static void executeCommand(String command, String dir, LogListener logListener) {
        executeCommand(command, dir, logListener, new ExecutionStopper());
    }

    public static void executeCommand(String command, LogListener logListener, ExecutionStopper executionStopper) {
        executeCommand(command, null, logListener, executionStopper);
    }

    public static void executeCommand(String command, String dir, LogListener logListener, ExecutionStopper executionStopper) {
        logListener.logLine("# Time: " + Util.prettyDate(new Date()));
        logListener.logLine("# Executing: '" + command + (!Util.isEmpty(dir) ? "' from dir '" + dir + "'" : "'"));
        logListener.logLine("");
        BufferedReader stdin = null;
        try {
            logListener.start();

            Process process = dir != null ?
                    Runtime.getRuntime().exec(command, null, new File(dir)) :
                    Runtime.getRuntime().exec(command);

            executionStopper.setProcess(process);

            stdin = new BufferedReader(new InputStreamReader((process.getInputStream()), "UTF8"));

            String line;
            try {
                while (!executionStopper.isStopped() && (line = stdin.readLine()) != null) {
                    logListener.logLine(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!executionStopper.isStopped()) {
                int res = 1;
                try {
                    res = process.waitFor();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (res > 0) {
                    throw new RuntimeException("Process failed with return value: " + res);
                }
            } else {
                if (!executionStopper.isQuietlyStopped()) {
                    logListener.error(new RuntimeException("Stopped by user!"));
                }
            }
            logListener.finish();
        } catch (Throwable t) {
            t.printStackTrace();
            logListener.logLine(Util.stacktraceToString(t));
            logListener.error(t);
        } finally {
            FileUtil.close(stdin);
        }
    }

    public static File writeFeatureToTemporaryFile(StringBuilder feature, String filename) {
        BufferedWriter out = null;
        try {
            String prefix = filename.substring(0, filename.lastIndexOf("."));
            String suffix = filename.substring(filename.lastIndexOf(".") + 1);

            File file = File.createTempFile(prefix, suffix);
            file.deleteOnExit();

            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
            out.write(feature.toString());
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            FileUtil.close(out);
        }
    }

    public static File writeFeatureToFile(StringBuilder feature, String filename) {
        BufferedWriter out = null;
        try {
            File file = new File(filename);
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
            out.write(feature.toString());
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            FileUtil.close(out);
        }
    }

    public static void setElementColorFromConfig(String propertyPrefix, Color[] colors) {
        String normalColor = ConfigurationManager.get(propertyPrefix + "Normal");
        if (!Util.isEmpty(normalColor)) {
            if (normalColor.startsWith("0x")) {
                normalColor = normalColor.substring(2);
            }
            colors[0] = new Color(Integer.parseInt(normalColor, 16));
        }
        String highlightColor = ConfigurationManager.get(propertyPrefix + "Highlight");
        if (!Util.isEmpty(highlightColor)) {
            if (highlightColor.startsWith("0x")) {
                highlightColor = highlightColor.substring(2);
            }
            colors[1] = new Color(Integer.parseInt(highlightColor, 16));
        }
    }

    public static class ExecutionStopper {
        private boolean stopped = false;
        private Process process = null;
        private boolean quietlyStopped = false;

        public void stop() {
            if (process != null) {
                process.destroy();
            }
            stopped = true;
        }

        public void stopQuietly() {
            if (process != null) {
                process.destroy();
            }
            stopped = true;
            quietlyStopped = true;
        }

        public boolean isStopped() {
            return stopped;
        }

        public void setProcess(Process process) {
            this.process = process;
        }

        public boolean isQuietlyStopped() {
            return quietlyStopped;
        }
    }
}
