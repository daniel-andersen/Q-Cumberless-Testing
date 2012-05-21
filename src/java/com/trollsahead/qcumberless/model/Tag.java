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

import com.trollsahead.qcumberless.util.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Tag {
    private String tags = "";
    
    public Tag(String tags) {
        this.tags = tags;
        streamlineTags();
    }
    
    public String toString() {
        return tags;
    }

    public boolean hasTags() {
        return !Util.isEmpty(tags);
    }

    public Set<String> toSet() {
        Set<String> set = new HashSet<String>();
        if (!hasTags()) {
            return set;
        }
        String[] array = tags.split("@");
        for (String tag : array) {
            tag = tag.replaceAll("@", "").trim();
            if (!Util.isEmpty(tag)) {
                set.add(tag);
            }
        }
        return set;
    }

    public List<String> toList() {
        return new ArrayList<String>(toSet());
    }

    public void add(String tag) {
        if (Util.isEmpty(tags)) {
            tags = "";
        }
        if (!tag.startsWith("@")) {
            tags += " @" + tag;
        } else {
            tags += " " + tag;
        }
        streamlineTags();
    }

    public void remove(String tag) {
        tag = !tag.startsWith("@") ? ("@" + tag) : tag;
        tags = tags
                .replaceFirst(tag + "$", "")
                .replaceFirst(tag + "\\s@", "@");
        streamlineTags();
    }

    private void streamlineTags() {
        if (tags == null) {
            return;
        }
        tags = tags
                .replaceAll("  ", " ")
                .trim();
    }
}
