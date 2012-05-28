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
import java.util.Map;

public class Locale {
    private static Language language = Language.EN;

    public enum Language {
        EN,
        DK
    }

    private static Map<Language, Map<String, String>> localeMap;
    
    static {
        localeMap = new HashMap<Language, Map<String, String>>();

        Map<String, String> en = new HashMap<String, String>();
        en.put("feature", "Feature");
        en.put("scenario", "Scenario");
        en.put("background", "Background");
        en.put("comment", "Comment");
        en.put("new step", "New step");
        en.put("Given", "Given");
        en.put("When", "When");
        en.put("Then", "Then");
        en.put("And", "And");
        en.put("But", "But");
        localeMap.put(Language.EN, en);

        Map<String, String> dk = new HashMap<String, String>();
        dk.put("feature", "Egenskab");
        dk.put("scenario", "Scenarie");
        dk.put("background", "Baggrund");
        dk.put("comment", "Kommentar");
        dk.put("new step", "Nyt step");
        dk.put("Given", "Givet");
        dk.put("When", "Når");
        dk.put("Then", "Så");
        dk.put("And", "Og");
        dk.put("But", "Men");
        localeMap.put(Language.DK, dk);
    }

    public static void setLocale(String language) {
        Locale.language = Language.valueOf(language);
    }
    
    public static void setLocale(Language language) {
        Locale.language = language;
    }
    
    public static Language getLocale() {
        return language;
    }
    
    public static String getString(String key) {
        return getCurrentLocaleMap().get(key);
    }
    
    private static Map<String, String> getCurrentLocaleMap() {
        return localeMap.get(getLocale());
    }
}
