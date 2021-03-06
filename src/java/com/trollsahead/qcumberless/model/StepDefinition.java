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

import java.util.LinkedList;
import java.util.List;

public class StepDefinition {
    private final String stepDefinition;
    private final List<StepDefinitionHook> hooks = new LinkedList<StepDefinitionHook>();

    public StepDefinition(String stepDefinition) {
        this.stepDefinition = stepDefinition;
    }
    
    public StepDefinition(String stepDefinition, List<StepDefinitionHook> hooks) {
        this.stepDefinition = stepDefinition;
        this.hooks.addAll(hooks);
    }

    public StepDefinition(String stepDefinition, String[][] hooks) {
        this.stepDefinition = stepDefinition;
        for (String[] param : hooks) {
            this.hooks.add(new StepDefinitionHook(param[0], param, stringArrayToRegExp(param)));
        }
    }

    private String stringArrayToRegExp(String[] params) {
        String regExp = "";
        String delimiter = "";
        for (String s : params) {
            regExp += delimiter;
            if ("*".equals(s)) {
                regExp += "\\*";
            } else {
                regExp += s;
            }
            delimiter = "|";
        }
        return regExp;
    }

    public void addParameter(String[] parameter) { // For backwards compatibility!
        addHook(parameter);
    }
    
    public void addHook(String[] hook) {
        hooks.add(new StepDefinitionHook(hook[0], hook, stringArrayToRegExp(hook)));
    }

    public void addHook(StepDefinitionHook hook) {
        hooks.add(hook);
    }

    public String getStepDefinition() {
        return stepDefinition;
    }
    
    public List<StepDefinitionHook> getHooks() {
        return hooks;
    }
}
