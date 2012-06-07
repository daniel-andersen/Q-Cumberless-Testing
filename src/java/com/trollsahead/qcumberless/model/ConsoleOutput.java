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

import com.trollsahead.qcumberless.engine.Engine;
import com.trollsahead.qcumberless.util.Util;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

public class ConsoleOutput {
    private List<String> log;
    private List<String> textWrappedLog;

    private int textWrapWidth = -1;
    private FontMetrics textWrapMetrics = null;
    
    public ConsoleOutput() {
        log = new LinkedList<String>();
        textWrappedLog = new LinkedList<String>();
    }
    
    public void appendLog(String line) {
        if (Util.isEmpty(line)) {
            return;
        }
        line = Util.removePostfixedNewline(line);
        synchronized (Engine.DATA_LOCK) {
            log.add(line);
        }
        textWrappedLog.addAll(wrapLine(line));
    }

    public List<String> getLog() {
        return log;
    }

    public List<String> getTextWrappedLog(int wrapWidth, FontMetrics fontMetrics) {
        if (textWrapWidth != wrapWidth || fontMetrics != textWrapMetrics || textWrappedLog == null) {
            textWrapMetrics = fontMetrics;
            textWrapWidth = wrapWidth;
            wrapText();
        }
        return textWrappedLog;
    }

    private void wrapText() {
        textWrappedLog = new LinkedList<String>();
        for (String line : log) {
            textWrappedLog.addAll(wrapLine(line));
        }
    }

    private List<String> wrapLine(String line) {
        if (Util.isEmpty(line)) {
            return new LinkedList<String>();
        }
        if (textWrapWidth == -1) {
            LinkedList<String> list = new LinkedList<String>();
            list.add(line);
            return list;
        } else {
            return Util.wrapText(line, textWrapWidth, textWrapMetrics);
        }
    }

    public void clearLog() {
        synchronized (Engine.DATA_LOCK) {
            log = new LinkedList<String>();
            textWrappedLog = new LinkedList<String>();
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
            for (String line : log) {
                out.write(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Util.close(out);
        }
    }
}
