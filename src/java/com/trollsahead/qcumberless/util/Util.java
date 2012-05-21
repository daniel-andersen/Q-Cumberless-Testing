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

package com.trollsahead.qcumberless.util;

import java.awt.*;
import java.io.Closeable;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static final String PSEUDO_NEWLINE = " * ";

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            // Ignore!
        }
    }

    public static boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("mac");
    }
    
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    public static boolean startsWithIgnoreWhitespace(String str, String prefix) {
        return str.matches("(\\s|\\t)*" + prefix + ".*");
    }

    public static String removeCommentFromLine(String line) {
        return line.substring(line.indexOf("#") + 1).trim();
    }

    public static String stripLeadingSpaces(String line) {
        Matcher matcher = Pattern.compile("[\\s\\t]*([^\\s\\t].*)").matcher(line);
        matcher.find();
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return line;
        }
    }

    public static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            // Ignore!
        }
    }
    public static String[] getFeatureFiles(File[] files) {
        return traverseDirectory(files).toArray(new String[0]);
    }

    private static List<String> traverseDirectory(File[] files) {
        List<String> foundFiles = new LinkedList<String>();
        if (files == null) {
            return foundFiles;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                foundFiles.addAll(traverseDirectory(file.listFiles()));
            } else if (file.getAbsolutePath().endsWith(".feature")) {
                foundFiles.add(file.getAbsolutePath());
            }
        }
        return foundFiles;
    }

    public static String stripLeadingSlash(String str) {
        if (!isEmpty(str) && str.startsWith("/")) {
            return str.substring(1);
        } else {
            return str;
        }
    }

    public static String getPath(String filename) {
        if (isEmpty(filename)) {
            return filename;
        }
        if (!filename.contains("/")) {
            return filename;
        }
        return filename.substring(0, filename.lastIndexOf("/"));
    }

    public static String addSlashToPath(String path) {
        if (Util.isEmpty(path)) {
            return "";
        } else {
            return path.endsWith("/") ? path : (path + "/");
        }
    }

    public static String stripPseudoNewLines(String str) {
        if (isEmpty(str)) {
            return str;
        }
        if (!str.contains(PSEUDO_NEWLINE)) {
            return str;
        }
        return str.substring(0, str.indexOf(PSEUDO_NEWLINE));
    }

    public static String removeLeadingSpaces(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("^\\s*", "");
    }

    public static String removeTrailingSpaces(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("\\s*$", "");
    }

    public static Color blendColor(Color source, Color dest) {
        return new Color(
                (int) (source.getRed()   + (float) (dest.getRed()   - source.getRed())   * (dest.getAlpha() / 255.0f)),
                (int) (source.getGreen() + (float) (dest.getGreen() - source.getGreen()) * (dest.getAlpha() / 255.0f)),
                (int) (source.getBlue()  + (float) (dest.getBlue()  - source.getBlue())  * (dest.getAlpha() / 255.0f)),
                (int) (source.getAlpha() + (float) (dest.getAlpha() - source.getAlpha()) * (dest.getAlpha() / 255.0f))
        );
    }

    public static List<String> sortedTagList(List<String> list) {
        String[] array = list.toArray(new String[0]);
        Arrays.sort(array, new Comparator<String>() {
            public int compare(String s1, String s2) {
                if ("*".equals(s1)) {
                    return 1;
                }
                if ("*".equals(s2)) {
                    return -1;
                }
                return s1.compareTo(s2);
            }
        });
        return Arrays.asList(array);
    }

    public static String tagsToString(Set<String> tagSet) {
        if (tagSet == null) {
            return "";
        }
        String tags = "";
        String delimiter = "";
        for (String tag : tagSet) {
            tags += delimiter + tag;
            delimiter = ",";
        }
        return tags;
    }

    public static String negatedTag(String tag) {
        return tag.startsWith("~") ? tag.substring(1) : ("~" + tag);
    }
}
