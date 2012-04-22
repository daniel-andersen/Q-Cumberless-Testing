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

import java.util.LinkedList;
import java.util.List;

public class CucumberStepDefinition {
    private final String stepDefinition;
    private final List<String[]> parameters = new LinkedList<String[]>();

    public CucumberStepDefinition(String stepDefinition) {
        this.stepDefinition = stepDefinition;
    }
    
    public void addParameter(String[] parameter) {
        parameters.add(parameter);
    }
    
    public String getStepDefinition() {
        return stepDefinition;
    }
    
    public List<String[]> getParameters() {
        return parameters;
    }
}
