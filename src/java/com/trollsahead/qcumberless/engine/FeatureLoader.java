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

import com.trollsahead.qcumberless.gui.TextElement;
import com.trollsahead.qcumberless.model.Locale;
import com.trollsahead.qcumberless.model.Step;
import com.trollsahead.qcumberless.util.Util;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeatureLoader {
    public static void parseFeatureFiles(String[] files) {
        Engine.resetFeatures();
        for (String filename : files) {
            parseFeatureFile(filename);
        }
    }

    private static void parseFeatureFile(String filename) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF8"));
            TextElement feature = new TextElement(TextElement.TYPE_FEATURE, TextElement.ROOT_FEATURE_EDITOR);
            feature.setFilename(filename);
            Engine.featuresRoot.addChild(feature);
            TextElement scenario = null;
            TextElement background = null;
            String line;
            String tags = null;
            String comment = null;
            while ((line = in.readLine()) != null) {
                line = Util.removeTrailingSpaces(line);
                if (Util.isEmptyOrContainsOnlyTabs(line)) {
                    continue;
                }
                if (line.matches(getFeaturePattern())) {
                    feature.setTitle(extractTitle(Pattern.compile(getFeaturePattern()), line));
                    feature.setTags(tags);
                    feature.setComment(comment);
                    tags = null;
                    comment = null;
                } else if (line.matches(getBackgroundPattern())) {
                    background = new TextElement(TextElement.TYPE_BACKGROUND, TextElement.ROOT_FEATURE_EDITOR);
                    background.setTitle("Background");
                    background.setTags(tags);
                    background.setComment(comment);
                    tags = null;
                    comment = null;
                    feature.addChild(background);
                } else if (line.matches(getScenarioPattern())) {
                    scenario = new TextElement(TextElement.TYPE_SCENARIO, TextElement.ROOT_FEATURE_EDITOR);
                    scenario.setTitle(extractTitle(Pattern.compile(getScenarioPattern()), line));
                    scenario.setTags(tags);
                    scenario.setComment(comment);
                    tags = null;
                    comment = null;
                    feature.addChild(scenario);
                } else if (line.matches(getTagPattern())) {
                    tags = extractTags(line);
                } else if (line.matches(getCommentPattern())) {
                    comment = extractComment(line);
                } else {
                    if (comment != null) {
                        addStep(feature, background, scenario, comment);
                    }
                    addStep(feature, background, scenario, line);
                    tags = null;
                    comment = null;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading supported feature file " + filename, e);
        } finally {
            Util.close(in);
        }
    }

    private static void addStep(TextElement feature, TextElement background, TextElement scenario, String line) {
        if (scenario != null) {
            addStepToScenario(scenario, line);
        } else if (background != null) {
            addStepToScenario(background, line);
        } else if (feature != null && !Util.isEmpty(line)) {
            feature.setTitle(feature.getTitle() + Util.PSEUDO_NEWLINE + line);
        }
    }

    private static void addStepToScenario(TextElement scenario, String line) {
        if (scenario == null || Util.isEmpty(line)) {
            return;
        }
        TextElement stepElement;
        if (Util.startsWithIgnoreWhitespace(line, "#")) {
            stepElement = new TextElement(TextElement.TYPE_COMMENT, TextElement.ROOT_FEATURE_EDITOR, Util.removeCommentFromLine(line));
        } else {
            stepElement = new TextElement(TextElement.TYPE_STEP, TextElement.ROOT_FEATURE_EDITOR, line, findMatchingStep(line));
        }
        scenario.addChild(stepElement);
    }

    public static Step findMatchingStep(String line) {
        for (Step step : Engine.stepDefinitions) {
            if (step.matches(line)) {
                return new Step(step, line);
            }
        }
        return new Step(line, false);
    }

    private static String extractComment(String line) {
        Matcher matcher = Pattern.compile(getCommentPattern()).matcher(line);
        matcher.find();
        return matcher.group(1);
    }

    private static String extractTags(String line) {
        Matcher matcher = Pattern.compile(getTagPattern()).matcher(line);
        matcher.find();
        return matcher.group(1);
    }

    private static String extractTitle(Pattern pattern, String line) {
        Matcher matcher = pattern.matcher(line);
        matcher.find();
        return matcher.group(1);
    }

    private static String getFeaturePattern() {
        return Locale.getString("feature") + ": (.*)";
    }

    private static String getScenarioPattern() {
        return "\\s*" + Locale.getString("scenario") + ": (.*)";
    }

    private static String getBackgroundPattern() {
        return "\\s*" + Locale.getString("background") + ":";
    }

    private static String getTagPattern() {
        return "[^@]*(((@[^@\\s]*)\\s*)+)";
    }

    private static String getCommentPattern() {
        return "\\s*(#.*)";
    }
}
