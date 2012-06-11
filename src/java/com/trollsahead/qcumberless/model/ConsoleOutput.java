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

package com.trollsahead.qcumberless.model;

import com.trollsahead.qcumberless.device.Device;
import com.trollsahead.qcumberless.engine.Engine;
import com.trollsahead.qcumberless.gui.elements.BaseBarElement;
import com.trollsahead.qcumberless.util.FileUtil;
import com.trollsahead.qcumberless.util.Util;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ConsoleOutput {
    private List<LogLine> log;
    private List<LogLine> textWrappedLog;

    private int textWrapWidth = -1;
    private FontMetrics textWrapMetrics = null;
    
    public ConsoleOutput() {
        log = new LinkedList<LogLine>();
        textWrappedLog = new LinkedList<LogLine>();
    }

    public void appendLog(String line) {
        appendLog(line, null);
    }
    
    public void appendLog(String line, BaseBarElement element) {
        if (Util.isEmpty(line)) {
            return;
        }
        line = Util.removePostfixedNewline(line);
        synchronized (Engine.DATA_LOCK) {
            for (String s : line.split("\n")) {
                log.add(new LogLine(s, element));
                textWrappedLog.addAll(wrapLine(new LogLine(s, element)));
            }
        }
    }

    public List<LogLine> getLog() {
        return log;
    }

    public List<LogLine> getTextWrappedLog(int wrapWidth, FontMetrics fontMetrics) {
        if (textWrapWidth != wrapWidth || fontMetrics != textWrapMetrics || textWrappedLog == null) {
            textWrapMetrics = fontMetrics;
            textWrapWidth = wrapWidth;
            wrapText();
        }
        return textWrappedLog;
    }

    private void wrapText() {
        textWrappedLog = new LinkedList<LogLine>();
        for (LogLine logLine : log) {
            textWrappedLog.addAll(wrapLine(logLine));
        }
    }

    private List<LogLine> wrapLine(LogLine logLine) {
        if (Util.isEmpty(logLine.log)) {
            return new LinkedList<LogLine>();
        }
        LinkedList<LogLine> list = new LinkedList<LogLine>();
        if (textWrapWidth == -1) {
            list.add(logLine);
        } else {
            List<String> wrappedLine = Util.wrapText(logLine.log, textWrapWidth, textWrapMetrics);
            for (String s : wrappedLine) {
                list.add(new LogLine(s, logLine.element));
            }
        }
        return list;
    }

    public void clearLog() {
        synchronized (Engine.DATA_LOCK) {
            log = new LinkedList<LogLine>();
            textWrappedLog = new LinkedList<LogLine>();
        }
    }

    public void exportLog(String filename, StringBuilder preample) {
        BufferedWriter out = null;
        try {
            File file = new File(filename);
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
            if (!Util.isEmpty(preample)) {
                out.write(preample.toString());
            }
            for (LogLine logLine : log) {
                out.write(logLine.log + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtil.close(out);
        }
    }

    public StringBuilder getPlainTextLog() {
        StringBuilder sb = new StringBuilder();
        for (LogLine logLine : log) {
            sb.append(logLine.log).append("\n");
        }
        return sb;
    }

    public static StringBuilder getPreample(Device device, Date date) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Q-Cumberless Testing\n");
        sb.append("# ").append(device.name()).append("\n");
        sb.append("# ").append(Util.prettyDate(date)).append("\n");
        sb.append("\n");
        return sb;
    }

    public static class LogLine {
        public String log = null;
        public BaseBarElement element = null;
        
        public LogLine(String log, BaseBarElement element) {
            this.log = log;
            this.element = element;
        }
    }
}
