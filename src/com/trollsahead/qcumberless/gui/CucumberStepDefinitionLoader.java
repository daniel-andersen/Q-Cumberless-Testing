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
        Engine.resetStepDefinitions();
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
