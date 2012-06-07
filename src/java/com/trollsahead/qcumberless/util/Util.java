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

import com.trollsahead.qcumberless.model.Locale;

import java.awt.*;
import java.io.Closeable;
import java.io.File;
import java.text.SimpleDateFormat;
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

    public static boolean isEmpty(StringBuilder sb) {
        return sb == null || isEmpty(sb.toString());
    }

    public static boolean isEmptyOrContainsOnlyTabs(String str) {
        return str == null || "".equals(str.replaceAll("\\t", "").trim());
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
        return traverseDirectory(files, ".feature").toArray(new String[0]);
    }

    public static List<String> traverseDirectory(File[] files, String suffix) {
        List<String> foundFiles = new LinkedList<String>();
        if (files == null) {
            return foundFiles;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                foundFiles.addAll(traverseDirectory(file.listFiles(), suffix));
            } else if (file.getAbsolutePath().endsWith(suffix)) {
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

    public static String removeTrailingSlash(String filename) {
        if (!isEmpty(filename) && filename.startsWith("/")) {
            return filename.substring(1);
        } else {
            return filename;
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

    public static String removePostfixedNewline(String s) {
        if (isEmpty(s)) {
            return s;
        } else {
            return s.endsWith("\n") ? s.substring(0, s.length() - 1) : s;
        }
    }
    
    public static Color blendColor(Color source, Color dest) {
        return new Color(
                (int) (source.getRed()   + (float) (dest.getRed()   - source.getRed())   * (dest.getAlpha() / 255.0f)),
                (int) (source.getGreen() + (float) (dest.getGreen() - source.getGreen()) * (dest.getAlpha() / 255.0f)),
                (int) (source.getBlue()  + (float) (dest.getBlue()  - source.getBlue())  * (dest.getAlpha() / 255.0f)),
                (int) (source.getAlpha() + (float) (dest.getAlpha() - source.getAlpha()) * (dest.getAlpha() / 255.0f))
        );
    }

    public static Color blendColorKeepAlpha(Color source, Color dest) {
        return new Color(
                (int) (source.getRed()   + (float) (dest.getRed()   - source.getRed())   * (dest.getAlpha() / 255.0f)),
                (int) (source.getGreen() + (float) (dest.getGreen() - source.getGreen()) * (dest.getAlpha() / 255.0f)),
                (int) (source.getBlue()  + (float) (dest.getBlue()  - source.getBlue())  * (dest.getAlpha() / 255.0f)),
                source.getAlpha()
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
    
    public static StringBuilder insertTagIntoFeature(StringBuilder feature, String tag) {
        String featureText = Locale.getString("feature") + ": ";
        return new StringBuilder(feature.toString().replace(featureText, tag + "\n" + featureText));
    }

    public static String firstCharUpperCase(String str) {
        str = Util.removeLeadingSpaces(str);
        if (str.length() == 0) {
            return str;
        } else {
            return str.substring(0, 1).toUpperCase() + (str.length() > 1 ? str.substring(1) : "");
        }
    }
    
    public static boolean isWord(String str) {
        return str.matches("\\w*");
    }

    public static String fillChar(char ch, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }

    public static float[] colorToFloatArray(Color color) {
        return new float[] {
            (float) color.getRed() / 255.0f,
            (float) color.getGreen() / 255.0f,
            (float) color.getBlue() / 255.0f,
            (float) color.getAlpha() / 255.0f
        };
    }

    public static List<String> wrapText(String text, int width, FontMetrics fontMetrics) {
        LinkedList<String> lines = new LinkedList<String>();
        if (isEmpty(text) || fontMetrics.stringWidth(text) <= width) {
            lines.add(text);
            return lines;
        }
        text = removePostfixedNewline(text);
        StringBuilder currentLine = new StringBuilder();
        for (String word : text.split(" ")) {
            if (fontMetrics.stringWidth(currentLine.toString() + word) > width) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
            }
            currentLine = currentLine.append(word).append(" ");
        }
        if (!isEmpty(currentLine.toString())) {
            lines.add(currentLine.toString());
        }
        return lines;
    }

    public static String convertSpacesToSlashes(String s) {
        if (Util.isEmpty(s)) {
            return s;
        } else {
            return s.replaceAll(" ", "_");
        }
    }

    public static String prettyDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String prettyFilenameDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(date);
    }
}
