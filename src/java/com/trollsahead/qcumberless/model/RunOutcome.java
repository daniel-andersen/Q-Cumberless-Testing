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
import com.trollsahead.qcumberless.engine.FeatureBuilder;
import com.trollsahead.qcumberless.gui.elements.BaseBarElement;
import com.trollsahead.qcumberless.util.ElementHelper;
import com.trollsahead.qcumberless.util.FileUtil;
import com.trollsahead.qcumberless.util.Util;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RunOutcome {
    private static final String COMMENT_DELIMITER_START = "<#";
    private static final String COMMENT_DELIMITER_END = "#>";
    private static final String PATTERN_DELIMITER_START = "<#";
    private static final String PATTERN_DELIMITER_END = "#>";

    public static final String COMMENT_QCUMBERLESS = "# qcumberless run: ";
    public static final String PATTERN_QCUMBERLESS = "# qcumberless run: ";

    private static final String COMMENT_STATUS = COMMENT_DELIMITER_START + "status: $status" + COMMENT_DELIMITER_END;
    private static final String PATTERN_STATUS = PATTERN_DELIMITER_START + "status: (.*)" + PATTERN_DELIMITER_END;

    private static final String COMMENT_DATE = COMMENT_DELIMITER_START + "date: $date" + COMMENT_DELIMITER_END;
    private static final String PATTERN_DATE = PATTERN_DELIMITER_START + "date: (.*)" + PATTERN_DELIMITER_END;

    private static final String COMMENT_ERROR_MESSAGE = COMMENT_DELIMITER_START + "errmsg: $errmsg" + COMMENT_DELIMITER_END;
    private static final String PATTERN_ERROR_MESSAGE = PATTERN_DELIMITER_START + "errmsg: (.*)" + PATTERN_DELIMITER_END;

    private static final String COMMENT_SCREENSHOT = COMMENT_DELIMITER_START + "screenshot: $screenshot" + COMMENT_DELIMITER_END;
    private static final String PATTERN_SCREENSHOT = PATTERN_DELIMITER_START + "screenshot: (.*)" + PATTERN_DELIMITER_END;

    private String filename;
    
    private StringBuilder featuresWithOutcome;

    public RunOutcome(String filename, StringBuilder featuresWithOutcome) {
        this.filename = filename;
        this.featuresWithOutcome = featuresWithOutcome;
    }

    public static String getRunOutcomeComment(BaseBarElement element, long time) {
        StringBuilder sb = new StringBuilder();
        sb.append(COMMENT_QCUMBERLESS);
        sb.append(COMMENT_DATE.replaceAll("\\$date", Long.toString(time)));
        if (element.getPlayState().isFailed()) {
            sb.append(COMMENT_STATUS.replaceAll("\\$status", "failed"));
            if (element.getPlayState().hasErrorMessage()) {
                sb.append(COMMENT_ERROR_MESSAGE.replaceAll("\\$errmsg", element.getPlayState().getErrorMessage()));
            }
            if (element.getPlayState().hasScreenshots()) {
                sb.append(getRunOutcomeScreenshotComment(element));
            }
        } else if (element.getPlayState().isSuccess()) {
            sb.append(COMMENT_STATUS.replaceAll("\\$status", "success"));
        } else {
            sb.append(COMMENT_STATUS.replaceAll("\\$status", "not yet played"));
        }
        sb.append("\n");
        return sb.toString();
    }

    private static String getRunOutcomeScreenshotComment(BaseBarElement element) {
        if (!element.getPlayState().hasScreenshots()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Screenshot screenshot : element.getPlayState().getScreenshots()) {
            sb.append(COMMENT_SCREENSHOT.replaceAll("\\$screenshot", screenshot.getFilename()));
        }
        return sb.toString();
    }

    public static File saveRunOutcome(Device device, List<BaseBarElement> features, long startTime) {
        File dir = new File(RunHistory.RUN_HISTORY_DIR + "/" + FileUtil.prettyFilenameDate(new Date(startTime)) + "/" + FileUtil.prettyFilenameTime(new Date(startTime)));
        if (!dir.mkdirs()) {
            throw new RuntimeException("Could not create directory for run history at: " + dir.getAbsolutePath());
        }
        for (BaseBarElement element : features) {
            String filename = FileUtil.addSlashToPath(dir.getAbsolutePath()) + ElementHelper.suggestFilenameIfNotPresent(element) + ".feature";
            File file = FileUtil.writeToFile(filename, FeatureBuilder.buildFeature(element, true, startTime));
            System.out.println("Wrote feature history to: " + file.getAbsolutePath());
        }
        String logFilename = FileUtil.addSlashToPath(dir.getAbsolutePath()) + FileUtil.toFilename(device.name()) + ".log";
        device.getConsoleOutput().exportLog(logFilename, ConsoleOutput.getPreample(device, new Date(startTime)));
        System.out.println("Wrote log history to: " + logFilename);
        return dir;
    }

    public static PlayState getPlayResultFromComment(String line) {
        Matcher matcher = Pattern.compile(PATTERN_STATUS).matcher(line);
        if (!matcher.find()) {
            return new PlayState(PlayState.State.NOT_YET_PLAYED);
        }
        if ("success".equalsIgnoreCase(matcher.group(1))) {
            return new PlayState(PlayState.State.SUCCESS);
        } else if ("failed".equalsIgnoreCase(matcher.group(1))) {
            return extractFailedPlayStateFromComment(line);
        } else {
            return new PlayState(PlayState.State.NOT_YET_PLAYED);
        }
    }

    private static PlayState extractFailedPlayStateFromComment(String line) {
        PlayState playState = new PlayState(PlayState.State.FAILED);
        Matcher matcher = Pattern.compile(PATTERN_ERROR_MESSAGE.replaceAll("\\$errmsg", "(.*)")).matcher(line);
        if (matcher.find()) {
            playState.setErrorMessage(matcher.group(2));
        }
        // TODO! Screenshot!
        return playState;
    }
}
