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

package com.trollsahead.qcumberless.gui;

import com.trollsahead.qcumberless.engine.CucumberEngine;
import com.trollsahead.qcumberless.model.CucumberStep;
import com.trollsahead.qcumberless.util.Util;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CucumberFeatureLoader {
    private static final String FEATURE_PATTERN = ".{0,2}Feature: (.*)"; // Take into account encoding characters in start of file!
    private static final Pattern FEATURE_PATTERN_COMPILED = Pattern.compile(FEATURE_PATTERN);

    private static final String SCENARIO_PATTERN = ".{0,2}Scenario: (.*)"; // Take into account encoding characters in start of file!
    private static final Pattern SCENARIO_PATTERN_COMPILED = Pattern.compile(SCENARIO_PATTERN);

    private static final String BACKGROUND_PATTERN = ".{0,2}Background:"; // Take into account encoding characters in start of file!
    private static final Pattern BACKGROUND_PATTERN_COMPILED = Pattern.compile(BACKGROUND_PATTERN);

    private static final String TAG_PATTERN = "[^@]*(((@[^@\\s]*)\\s*)+)"; // Take into account encoding characters in start of file!
    private static final Pattern TAG_PATTERN_COMPILED = Pattern.compile(TAG_PATTERN);

    private static final String COMMENT_PATTERN = "\\t*(#.*)";
    private static final Pattern COMMENT_PATTERN_COMPILED = Pattern.compile(COMMENT_PATTERN);

    public static void parseFeatureFiles(String[] files) {
        CucumberEngine.resetFeatures();
        for (String filename : files) {
            parseFeatureFile(filename);
        }
    }

    private static void parseFeatureFile(String filename) {
        try {
            InputStream systemResourceAsStream = new FileInputStream(filename);
            Reader inputStreamReader = new InputStreamReader(systemResourceAsStream);
            BufferedReader in = new BufferedReader(inputStreamReader);
            CucumberTextElement feature = new CucumberTextElement(CucumberTextElement.TYPE_FEATURE, CucumberTextElement.ROOT_FEATURE_EDITOR);
            feature.setFilename(filename);
            CucumberEngine.featuresRoot.addChild(feature);
            CucumberTextElement scenario = null;
            CucumberTextElement background = null;
            String line;
            String tags = null;
            String comment = null;
            while ((line = in.readLine()) != null) {
                line = Util.removeTrailingSpaces(line);
                if (line.matches(FEATURE_PATTERN)) {
                    feature.setTitle(extractTitle(FEATURE_PATTERN_COMPILED, line));
                    feature.setTags(tags);
                    feature.setComment(comment);
                    tags = null;
                } else if (line.matches(BACKGROUND_PATTERN)) {
                    background = new CucumberTextElement(CucumberTextElement.TYPE_BACKGROUND, CucumberTextElement.ROOT_FEATURE_EDITOR);
                    background.setTitle("Background");
                    background.setTags(tags);
                    background.setComment(comment);
                    tags = null;
                    feature.addChild(background);
                } else if (line.matches(SCENARIO_PATTERN)) {
                    scenario = new CucumberTextElement(CucumberTextElement.TYPE_SCENARIO, CucumberTextElement.ROOT_FEATURE_EDITOR);
                    scenario.setTitle(extractTitle(SCENARIO_PATTERN_COMPILED, line));
                    scenario.setTags(tags);
                    scenario.setComment(comment);
                    tags = null;
                    feature.addChild(scenario);
                } else if (line.matches(TAG_PATTERN)) {
                    tags = extractTags(line);
                } else if (line.matches(COMMENT_PATTERN)) {
                    if (line.startsWith("\t")) {
                        addStep(feature, background, scenario, line);
                    } else {
                        comment = extractComment(line);
                    }
                } else {
                    addStep(feature, background, scenario, line);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading supported feature file " + filename, e);
        }
    }

    private static void addStep(CucumberTextElement feature, CucumberTextElement background, CucumberTextElement scenario, String line) {
        if (scenario != null) {
            addStepToScenario(scenario, line);
        } else if (background != null) {
            addStepToScenario(background, line);
        } else if (feature != null && !Util.isEmpty(line)) {
            feature.setTitle(feature.getTitle() + Util.PSEUDO_NEWLINE + line);
        }
    }

    private static void addStepToScenario(CucumberTextElement scenario, String line) {
        if (scenario == null || Util.isEmpty(line)) {
            return;
        }
        CucumberTextElement stepElement;
        if (Util.startsWithIgnoreWhitespace(line, "#")) {
            stepElement = new CucumberTextElement(CucumberTextElement.TYPE_COMMENT, CucumberTextElement.ROOT_FEATURE_EDITOR, Util.removeCommentFromLine(line));
        } else {
            stepElement = new CucumberTextElement(CucumberTextElement.TYPE_STEP, CucumberTextElement.ROOT_FEATURE_EDITOR, line, findMatchingStep(line));
        }
        scenario.addChild(stepElement);
    }

    public static CucumberStep findMatchingStep(String line) {
        for (CucumberStep step : CucumberEngine.stepDefinitions) {
            if (step.matches(line)) {
                return new CucumberStep(step, line);
            }
        }
        return new CucumberStep(line, false);
    }

    private static String extractComment(String line) {
        Matcher matcher = COMMENT_PATTERN_COMPILED.matcher(line);
        matcher.find();
        return matcher.group(1);
    }

    private static String extractTags(String line) {
        Matcher matcher = TAG_PATTERN_COMPILED.matcher(line);
        matcher.find();
        return matcher.group(1);
    }

    private static String extractTitle(Pattern pattern, String line) {
        Matcher matcher = pattern.matcher(line);
        matcher.find();
        return matcher.group(1);
    }
}
