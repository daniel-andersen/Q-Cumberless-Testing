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

public class StepDefinitionHook {
    private String actualParameter;
    private String[] validParameters;
    private String regExp;

    public StepDefinitionHook(String[] validParameters, String regExp) {
        this.actualParameter = validParameters[0];
        this.validParameters = validParameters;
        this.regExp = regExp;
    }

    public StepDefinitionHook(String actualParameter, String[] validParameters, String regExp) {
        this.actualParameter = actualParameter;
        this.validParameters = validParameters;
        this.regExp = regExp;
    }

    public void setActualParameter(String parameter) {
        this.actualParameter = parameter;
    }

    public String getActualParameter() {
        return actualParameter;
    }

    public String[] getValidParameters() {
        return validParameters;
    }

    public String getRegExp() {
        return regExp;
    }

    public StepDefinitionHook duplicate() {
        return new StepDefinitionHook(actualParameter, validParameters.clone(), regExp);
    }
}
