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

import com.trollsahead.qcumberless.util.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class Helper {
    public static List<String> executeCommand(String command) {
        final List<String> output = new LinkedList<String>();
        executeCommand(command, null, new LogListener() {
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
        System.out.println("Executing: '" + command + (!Util.isEmpty(dir) ? "' from dir '" + dir + "'" : "'"));
        BufferedReader stdin = null;
        try {
            logListener.start();

            Process exec = dir != null ? 
                    Runtime.getRuntime().exec(command, null, new File(dir)) :
                    Runtime.getRuntime().exec(command);

            stdin = new BufferedReader(new InputStreamReader((exec.getInputStream())));

            String line;
            while ((line = stdin.readLine()) != null) {
                logListener.logLine(line);
            }
            int res = 1;
            try {
                res = exec.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (res > 0) {
                throw new RuntimeException("Process failed with return value: " + res);
            }
            logListener.finish();
        } catch (Throwable t) {
            t.printStackTrace();
            logListener.error(t);
        } finally {
            Util.close(stdin);
        }
    }

    public static void executeCommand(String command, LogListener logListener, ExecutorStopper executorStopper) {
        executeCommand(command, null, logListener, executorStopper);
    }

    public static void executeCommand(String command, String dir, LogListener logListener, ExecutorStopper executorStopper) {
        System.out.println("Executing with stopper: " + command);
        BufferedReader stdin = null;
        try {
            logListener.start();

            Process exec = dir != null ?
                    Runtime.getRuntime().exec(command, null, new File(dir)) :
                    Runtime.getRuntime().exec(command);

            stdin = new BufferedReader(new InputStreamReader((exec.getInputStream())));

            String line;
            boolean running = true;
            while (running && !executorStopper.isStopped()) {
                while ((line = stdin.readLine()) != null && running && !executorStopper.isStopped()) {
                    logListener.logLine(line);
                }
                try {
                    int res = exec.exitValue();
                    running = false;
                    if (res > 0) {
                        throw new RuntimeException("Process failed with return value: " + res);
                    }
                } catch (IllegalThreadStateException e) {
                    running = true;
                }
            }
            logListener.finish();
        } catch (Throwable t) {
            t.printStackTrace();
            logListener.error(t);
        } finally {
            Util.close(stdin);
        }
    }

    public static class ExecutorStopper {
        private boolean stopped = false;

        public void stop() {
            stopped = true;
        }

        public boolean isStopped() {
            return stopped;
        }
    }
}
