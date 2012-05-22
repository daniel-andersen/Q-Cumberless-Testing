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

package com.trollsahead.qcumberless.gui;

import com.trollsahead.qcumberless.engine.Engine;
import com.trollsahead.qcumberless.model.Step;
import com.trollsahead.qcumberless.model.StepDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CucumberStepDefinitionLoader {
    public static void parseStepDefinitions(List<StepDefinition> stepDefinitions) {
        List<TextElement> elements = new ArrayList<TextElement>();
        Engine.resetStepDefinitions(true);
        for (StepDefinition stepDefinition : stepDefinitions) {
            Step currentStep = new Step(stepDefinition);
            currentStep.setShouldRenderKeyword(false);
            Engine.stepDefinitions.add(currentStep);
            elements.add(new TextElement(TextElement.TYPE_STEP, TextElement.ROOT_STEP_DEFINITIONS, stepDefinition.getStepDefinition(), currentStep));
        }
        Collections.sort(elements, new Comparator<TextElement>() {
            public int compare(TextElement element1, TextElement element2) {
                return element1.step.toString().compareTo(element2.step.toString());
            }
        });
        for (TextElement element : elements) {
            Engine.stepsRoot.addChild(element);
        }
    }
}
