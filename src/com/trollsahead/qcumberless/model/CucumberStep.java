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

package com.trollsahead.qcumberless.model;

import com.trollsahead.qcumberless.util.Util;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CucumberStep {
    private static final String PARAMETER_TAG = "\\(\\.\\*\\)";

    private String definition;
    public boolean isMatched;

    private List<String[]> validParameters = new ArrayList<String[]>();
    private String[] actualParameters = new String[0];
    
    private List<CucumberStepPart> parts = null;
    private boolean renderKeyword = true;

    public CucumberStep(String definition) {
        this(definition, true);
    }
    
    public CucumberStep(CucumberStepDefinition stepDefinition) {
        this(stepDefinition.getStepDefinition(), true);
        for (String[] parameter : stepDefinition.getParameters()) {
            validParameters.add(parameter);
        }
        resetParametersToDefault();
        parts = null;
    }

    public CucumberStep(String definition, boolean isMatched) {
        this.definition = Util.stripLeadingSpaces(definition);
        this.isMatched = isMatched;
    }

    public CucumberStep(CucumberStep step, String line) {
        this.definition = Util.stripLeadingSpaces(step.definition);
        this.validParameters = step.validParameters;
        this.actualParameters = new String[0];
        this.parts = null;
        findParameters(Util.stripLeadingSpaces(line));
        findParts();
        isMatched = true;
    }

    public CucumberStep duplicate() {
        CucumberStep step = new CucumberStep(definition);
        step.validParameters = this.validParameters;
        step.actualParameters = this.actualParameters.clone();
        step.renderKeyword = this.renderKeyword;
        step.isMatched = this.isMatched;
        return step;
    }

    public void findParameters(String line) {
        Matcher matcher = Pattern.compile(definition).matcher(Util.stripLeadingSpaces(line));
        matcher.find();
        if (matcher.groupCount() > 0) {
            actualParameters = new String[matcher.groupCount()];
            for (int i = 0; i < matcher.groupCount(); i++) {
                actualParameters[i] = matcher.group(i + 1);
            }
        }
    }

    public boolean matchedByStepDefinition() {
        return isMatched;
    }

    public List<CucumberStepPart> getParts() {
        if (parts == null) {
            findParts();
        }
        return parts;
    }

    public void resetParametersToDefault() {
        actualParameters = new String[validParameters.size()];
        for (int i = 0; i < validParameters.size(); i++) {
            actualParameters[i] = validParameters.get(i)[0];
        }
        updateRenderKeyword();
    }

    public void findParts() {
        parts = new ArrayList<CucumberStepPart>();
        String[] strings = definition.split(PARAMETER_TAG);
        int parameterIdx = 0;
        for (String str : strings) {
            parts.add(new CucumberStepPart(CucumberStepPart.PartType.TEXT, str));
            if (parameterIdx < validParameters.size()) {
                parts.add(new CucumberStepPart(CucumberStepPart.PartType.ARGUMENT, actualParameters[parameterIdx], validParameters.get(parameterIdx)));
                parameterIdx++;
            }
        }
        updateRenderKeyword();
    }

    public boolean matches(String line) {
        return line.matches(definition);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (CucumberStepPart part : getParts()) {
            sb.append(part.text);
        }
        return sb.toString();
    }

    public CucumberStepPart getFirstPart() {
        return getParts().get(0);
    }

    public void setShouldRenderKeyword(boolean renderKeyword) {
        this.renderKeyword = renderKeyword;
        updateRenderKeyword();
    }

    private void updateRenderKeyword() {
        if (!renderKeyword && parts != null) {
            boolean shouldRender = false;
            for (CucumberStepPart part : parts) {
                if (part.type == CucumberStepPart.PartType.ARGUMENT) {
                    part.render = shouldRender;
                    shouldRender = true;
                } else {
                    part.render = true;
                }
            }
        }
    }

    public static class CucumberStepPart {
        public static enum PartType {TEXT, ARGUMENT}
        public PartType type = PartType.TEXT;
        public String text;
        public String[] validParameters;

        public boolean isTouched = false;

        public List<String> wrappedText;

        public int startX = 0;
        public int startY = 0;
        public int endX = 0;
        public int endY = 0;

        public boolean render = true;

        public CucumberStepPart(PartType type, String text) {
            this(type, text, null);
        }

        public CucumberStepPart(PartType type, String text, String[] validParameters) {
            this.type = type;
            this.text = text;
            this.validParameters = validParameters;
        }

        public void wrapText(FontMetrics fontMetrics, int startX, int startY, int width) {
            this.startX = startX;
            this.startY = startY;
            this.endX = startX;
            this.endY = startY;
            wrappedText = new LinkedList<String>();
            wrappedText.add("");
            if (!render) {
                return;
            }
            if (Util.isEmpty(text)) {
                this.endX += fontMetrics.stringWidth(" ");
                return;
            }
            String[] words = type == PartType.TEXT ? text.split(" ") : new String[] {text};
            int offsetX = startX;
            for (String word : words) {
                offsetX = addWordToWrappedText(fontMetrics, word, width, offsetX);
            }
            if (Util.isEmpty(wrappedText.get(0))) {
                this.startX = 0;
                this.startY += fontMetrics.getHeight();
                wrappedText.remove(0);
            }
        }
        
        private int addWordToWrappedText(FontMetrics fontMetrics, String word, int width, int offsetX) {
            int index = wrappedText.size() - 1;
            String currentLine = wrappedText.get(index);
            endX = offsetX + fontMetrics.stringWidth(currentLine + word);
            if (endX < width) {
                wrappedText.set(index, currentLine + word + " ");
                return offsetX;
            } else {
                endX = fontMetrics.stringWidth(word);
                endY += fontMetrics.getHeight();
                wrappedText.add(word + " ");
                return 0;
            }
        }
    }
}
