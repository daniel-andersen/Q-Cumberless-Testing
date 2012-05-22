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

package com.trollsahead.qcumberless.util;

import com.trollsahead.qcumberless.model.Constants;
import com.trollsahead.qcumberless.model.StepDefinition;

import static com.trollsahead.qcumberless.model.Locale.Language;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RubyStepDefinitionParser {
    public static List<StepDefinition> parseFiles(String[] filenames) {
        List<StepDefinition> stepDefinitions = new LinkedList<StepDefinition>();
        for (String filename : filenames) {
            stepDefinitions.addAll(parseFile(filename));
        }
        return stepDefinitions;
    }

    private static List<StepDefinition> parseFile(String filename) {
        System.out.println("Parsing ruby file: " + filename);
        List<StepDefinition> stepDefinitions = new LinkedList<StepDefinition>();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF8"));
            String line;
            while ((line = in.readLine()) != null) {
                StepDefinition stepDefinition = parseLine(line);
                if (stepDefinition != null) {
                    stepDefinitions.add(stepDefinition);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            Util.close(in);
        }
        return stepDefinitions;
    }

    private static StepDefinition parseLine(String line) {
        if (Util.isEmpty(line)) {
            return null;
        }
        for (String keyword : Constants.getStepPrefixsForLocale(Language.EN)) {
            if (!Util.isWord(keyword)) {
                continue;
            }
            String pattern = buildStepDefinitionPattern(keyword);
            if (line.matches(pattern)) {
                Matcher matcher = Pattern.compile(pattern).matcher(line);
                matcher.find();
                return parseStepDefinition(matcher.group(1), matcher.group(2));
            }
        }
        return null;
    }

    private static StepDefinition parseStepDefinition(String keyword, String definition) {
        StepDefinition stepDefinition = new StepDefinition("(.*) " + convertGroups(definition));
        stepDefinition.addParameter(Constants.getStepPrefixs());
        for (String[] parameter : getParametersFromDefinition(definition)) {
            stepDefinition.addParameter(parameter);
        }
        return stepDefinition;
    }

    private static List<String[]> getParametersFromDefinition(String definition) {
        List<String[]> parameters = new LinkedList<String[]>();
        while (!Util.isEmpty(definition)) {
            int start = definition.indexOf("(");
            if (start == -1) {
                break;
            }
            int end = definition.indexOf(")", start);
            String group = definition.substring(start + 1, end);
            parameters.add(new String[] {"*"});
            definition = definition.substring(end + 1);
        }
        return parameters;
    }

    private static String convertGroups(String definition) {
        return definition
                .replaceAll("\\([^\\)]*\\)", "§§§")
                .replaceAll("§§§", "(.*)");
    }

    private static String buildStepDefinitionPattern(String keyword) {
        return "(" + keyword + ") /\\^(.*)\\$/.*";
    }
}
