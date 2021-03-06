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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.trollsahead.qcumberless.model.Locale.Language;

public class Constants {
    private static Map<Language, String[]> stepPrefixs;

    public static final Set<Character> reservedKeys = new HashSet<Character>();

    public static final String PARAMETER_STRING = "<abc>";
    public static final String PARAMETER_DIGITS = "<123>";
    public static final String PARAMETER_RESOURCE = "<key>";

    public static final String TAG_NEW = "<new>";

    static {
        stepPrefixs = new HashMap<Language, String[]>();
        stepPrefixs.put(Language.EN, new String[] {"Given", "When", "Then", "And", "But", "*"});
        stepPrefixs.put(Language.DA, new String[] {"Givet", "Når", "Så", "Og", "Men", "*"});

        reservedKeys.add(' ');
        reservedKeys.add('+');
        reservedKeys.add('-');
    }

    public static String[] getStepPrefixs() {
        return getStepPrefixsForLocale(Locale.getLocale());
    }
    
    public static String[] getStepPrefixsForLocale(Language language) {
        return stepPrefixs.get(language);
    }

    public static boolean isStringParameter(String s) {
        return s.equals(PARAMETER_STRING) || s.equals(PARAMETER_DIGITS) || s.equals(PARAMETER_RESOURCE);
    }
}
