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

import com.trollsahead.qcumberless.gui.ProgressBar;
import com.trollsahead.qcumberless.model.Constants;
import com.trollsahead.qcumberless.model.Locale;
import com.trollsahead.qcumberless.model.StepDefinition;
import com.trollsahead.qcumberless.model.StepDefinitionHook;

import static com.trollsahead.qcumberless.model.Locale.Language;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleRubyStepDefinitionParser {
    private static final String hooksPattern = "# qcumberless (.*)";

    public static Map<String, List<StepDefinition>> parseFiles(String[] filenames) {
        return parseFiles(filenames, null);
    }

    public static Map<String, List<StepDefinition>> parseFiles(String[] filenames, ProgressBar progressBar) {
        Map<String, List<StepDefinition>> stepDefinitionMap = new HashMap<String, List<StepDefinition>>();
        int count = 0;
        for (String filename : filenames) {
            System.out.println("Parsing ruby file: " + filename);
            if (progressBar != null) {
                progressBar.setProcess(((float) count / (float) filenames.length) * 100.0f);
            }
            try {
                stepDefinitionMap.put(FileUtil.removePostfixFromFilename(FileUtil.removePathFromFilename(filename)), parseFile(new FileInputStream(filename)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            count++;
        }
        return stepDefinitionMap;
    }

    public static Map<String, List<StepDefinition>> parseFiles(URL[] urls) {
        return parseFiles(urls, null);
    }

    public static Map<String, List<StepDefinition>> parseFiles(URL[] urls, ProgressBar progressBar) {
        Map<String, List<StepDefinition>> stepDefinitionMap = new HashMap<String, List<StepDefinition>>();
        int count = 0;
        for (URL url : urls) {
            System.out.println("Parsing URL: " + url.toString());
            if (progressBar != null) {
                progressBar.setProcess(((float) count / (float) urls.length) * 100.0f);
            }
            try {
                stepDefinitionMap.put(FileUtil.removePostfixFromFilename(FileUtil.removePathFromFilename(url.toString())), parseFile(url.openStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            count++;
        }
        return stepDefinitionMap;
    }

    private static List<StepDefinition> parseFile(InputStream inputStream) {
        List<StepDefinition> stepDefinitions = new LinkedList<StepDefinition>();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));
            String line;
            String hooks = null;
            while ((line = in.readLine()) != null) {
                String newHooks = parseHooks(line);
                if (!Util.isEmpty(newHooks)) {
                    hooks = newHooks;
                    continue;
                }
                StepDefinition stepDefinition = parseLine(line, hooks);
                if (stepDefinition != null) {
                    stepDefinitions.add(stepDefinition);
                }
                hooks = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            FileUtil.close(in);
        }
        return stepDefinitions;
    }

    private static String parseHooks(String line) {
        if (!line.matches(hooksPattern)) {
            return null;
        } else {
            Matcher matcher = Pattern.compile(hooksPattern).matcher(line);
            matcher.find();
            return matcher.group(1);
        }
    }

    private static StepDefinition parseLine(String line, String hooks) {
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
                return parseStepDefinition(keyword, matcher.group(1), hooks);
            }
        }
        return null;
    }

    private static StepDefinition parseStepDefinition(String keyword, String definition, String hooksComment) {
        StepDefinition stepDefinition = new StepDefinition("(.*) " + convertRegExpsToSimpleGroups(definition));
        stepDefinition.addHook(orderStepPrefixes(Locale.getString(keyword)));
        if (Util.isEmpty(hooksComment)) {
            for (StepDefinitionHook hook : getHooksFromDefinition(definition)) {
                stepDefinition.addHook(hook);
            }
        } else {
            for (String[] hook : getHooksFromComment(hooksComment)) {
                stepDefinition.addHook(hook);
            }
        }
        return stepDefinition;
    }

    private static String[] orderStepPrefixes(String keyword) {
        List<String> keywords = new LinkedList<String>();
        keywords.addAll(Arrays.asList(Constants.getStepPrefixs()));
        keywords.remove(keyword);
        keywords.add(0, keyword);
        return keywords.toArray(new String[0]);
    }

    private static List<String[]> getHooksFromComment(String parameters) {
        List<String[]> parameterList = new LinkedList<String[]>();
        int start;
        while ((start = parameters.indexOf("(")) != -1) {
            int end = parameters.indexOf(")");
            String params = parameters.substring(start + 1, end);
            parameterList.add(params.split("\\|"));
            parameters = parameters.substring(end + 1);
        }
        return parameterList;
    }

    private static List<StepDefinitionHook> getHooksFromDefinition(String definition) {
        List<StepDefinitionHook> hooks = new LinkedList<StepDefinitionHook>();
        while (!Util.isEmpty(definition)) {
            int start = definition.indexOf("(");
            if (start == -1) {
                break;
            }
            int end = definition.indexOf(")", start);
            String regexp = definition.substring(start + 1, end);
            hooks.add(comprehensiveRegExp(regexp));
            definition = definition.substring(end + 1);
        }
        return hooks;
    }

    private static StepDefinitionHook comprehensiveRegExp(String regexp) {
        String[] parameterList = extractSimpleParameterList(regexp);
        if (parameterList != null) {
            return new StepDefinitionHook(parameterList, regexp);
        }
        if (isAnyDigitsRegExp(regexp)) {
            return new StepDefinitionHook(new String[] {Constants.PARAMETER_DIGITS}, regexp);
        }
        return new StepDefinitionHook(new String[] {Constants.PARAMETER_STRING}, regexp);
    }

    private static boolean isAnyDigitsRegExp(String regexp) {
        return regexp.equals("\\d+") || regexp.equals("\\d*");
    }

    private static String[] extractSimpleParameterList(String regexp) {
        if (regexp.startsWith("?:")) {
            regexp = regexp.substring(2);
        }
        Matcher matcher = Pattern.compile("^(\\w+\\|?)+$").matcher(regexp);
        if (!matcher.find()) {
            return null;
        }
        List<String> parameters = new LinkedList<String>();
        while (true) {
            matcher = Pattern.compile("^(\\w+)\\|.*").matcher(regexp);
            if (!matcher.find()) {
                parameters.add(regexp);
                return parameters.toArray(new String[0]);
            }
            parameters.add(matcher.group(1));
            regexp = regexp.substring(matcher.end(1) + 1);
        }
    }

    private static String convertRegExpsToSimpleGroups(String definition) {
        return definition
                .replaceAll("\\([^\\)]*\\)[\\?\\+\\*]?", "§§§")
                .replaceAll("§§§", "(.*)");
    }

    private static String buildStepDefinitionPattern(String keyword) {
        return keyword + " /\\^(.*)\\$/.*";
    }
}
