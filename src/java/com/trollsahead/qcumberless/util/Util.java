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
//
// Copyright 2012

// Daniel Andersen (dani_ande@yahoo.dk)

package com.trollsahead.qcumberless.util;

import java.awt.*;
import java.io.Closeable;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
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
}
