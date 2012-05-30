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
    private static boolean localeSet = false;

    public enum Language {
        EN,
        DA
    }

    private static Map<Language, Map<String, String>> localeMap;
    
    static {
        localeMap = new HashMap<Language, Map<String, String>>();

        Map<String, String> en = new HashMap<String, String>();
        en.put("feature", "Feature");
        en.put("scenario", "Scenario");
        en.put("scenario outline", "Scenario Outline");
        en.put("background", "Background");
        en.put("comment", "Comment");
        en.put("new step", "New step");
        en.put("table", "Table");
        en.put("Given", "Given");
        en.put("When", "When");
        en.put("Then", "Then");
        en.put("And", "And");
        en.put("But", "But");
        en.put("Examples", "Examples");
        localeMap.put(Language.EN, en);

        Map<String, String> da = new HashMap<String, String>();
        da.put("feature", "Egenskab");
        da.put("scenario", "Scenarie");
        da.put("scenario outline", "Scenarie Outline");
        da.put("background", "Baggrund");
        da.put("comment", "Kommentar");
        da.put("new step", "Nyt step");
        da.put("table", "Tabel");
        da.put("Given", "Givet");
        da.put("When", "Når");
        da.put("Then", "Så");
        da.put("And", "Og");
        da.put("But", "Men");
        da.put("Examples", "Eksempler");
        localeMap.put(Language.DA, da);
    }

    public static void setLocale(String language) {
        Locale.language = Language.valueOf(language);
        localeSet = true;
    }
    
    public static void setLocale(Language language) {
        Locale.language = language;
        localeSet = true;
    }
    
    public static Language getLocale() {
        return language;
    }

    public static boolean isLocaleSet() {
        return localeSet;
    }

    public static String getString(String key) {
        return getCurrentLocaleMap().get(key);
    }
    
    private static Map<String, String> getCurrentLocaleMap() {
        return localeMap.get(getLocale());
    }
}
