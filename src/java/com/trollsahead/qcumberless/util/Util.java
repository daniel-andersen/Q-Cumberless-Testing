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

import com.trollsahead.qcumberless.model.Constants;
import com.trollsahead.qcumberless.model.Locale;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
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

    public static String stripLeadingSlash(String str) {
        if (!isEmpty(str) && str.startsWith("/")) {
            return str.substring(1);
        } else {
            return str;
        }
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
                if (Constants.TAG_NEW.equals(s1)) {
                    return 1;
                }
                if (Constants.TAG_NEW.equals(s2)) {
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

    public static List<String> stringToTagList(String tags) {
        tags = tags.trim();
        List<String> tagList = new LinkedList<String>();
        for (String tag : tags.split(" ")) {
            if (!isEmpty(tag)) {
                tagList.add(tag);
            }
        }
        return tagList;
    }

    public static Set<String> stringToTagSet(String tags) {
        Set<String> tagSet = new HashSet<String>();
        for (String tag : tags.split(" ")) {
            if (!isEmpty(tag)) {
                tagSet.add(tag);
            }
        }
        return tagSet;
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
        if (isEmpty(text)) {
            lines.add(text);
            return lines;
        }
        text = removePostfixedNewline(text);
        if (!text.contains("\n") && fontMetrics.stringWidth(text) <= width) {
            lines.add(text);
            return lines;
        }

        for (String line : text.split("\n")) {
            StringBuilder currentLine = new StringBuilder();
            for (String word : line.split(" ")) {
                if (fontMetrics.stringWidth(currentLine.toString() + word) > width) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
                currentLine = currentLine.append(word).append(" ");
            }
            if (!isEmpty(currentLine.toString())) {
                lines.add(currentLine.toString());
            }
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

    public static boolean isInArray(int type, int[] typeFilter) {
        if (typeFilter == null) {
            return false;
        } else {
            for (int t : typeFilter) {
                if (t == type) {
                    return true;
                }
            }
            return false;
        }
    }
    
    public static String stacktraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    public static <T> List<T> restrictListSize(List<T> list, int size) {
        if (Util.isEmpty(list) || list.size() <= size) {
            return list;
        }
        List<T> newList = new LinkedList<T>();
        int i = 0;
        for (T element : list) {
            newList.add(element);
            if (i++ > size) {
                break;
            }
        }
        return newList;
    }

    public static String getFirstLine(String s) {
        if (Util.isEmpty(s) || !s.contains("\n")) {
            return s;
        }
        return s.split("\n")[0];
    }

    public static StringBuilder indentAllLines(String s, String indent) {
        StringBuilder sb = new StringBuilder();
        String delimiter = "";
        for (String line : s.split("\n")) {
            sb.append(delimiter).append(indent).append(line);
            delimiter = "\n";
        }
        return sb;
    }
}
