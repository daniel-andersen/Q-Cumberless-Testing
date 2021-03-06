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

import com.trollsahead.qcumberless.engine.DesignerEngine;
import com.trollsahead.qcumberless.gui.Button;
import com.trollsahead.qcumberless.gui.DropDown;
import com.trollsahead.qcumberless.gui.elements.Element;
import com.trollsahead.qcumberless.gui.elements.BaseBarElement;

import java.io.File;
import java.util.LinkedList;

public class ElementHelper {
    public static final String EXPORT_INDENT = "    ";

    public static void filterFeaturesByTags(String tags) {
        for (Element element : DesignerEngine.featuresRoot.children) {
            boolean hideChildren;
            if (element.containsAnyOfTags(tags)) {
                element.show(false);
                hideChildren = false;
            } else {
                element.hide(false);
                hideChildren = true;
            }
            for (Element child : element.children) {
                if (hideChildren) {
                    child.hide(false);
                } else {
                    child.show(false);
                }
                child.stickChildrenToParentRenderPosition(true);
            }
        }
    }

    public static void filterScenariosByTags(String tags) {
        for (Element element : DesignerEngine.featuresRoot.children) {
            boolean childrenHasTags = false;
            for (Element child : element.children) {
                if (child.containsAnyOfTags(tags)) {
                    child.show(false);
                    childrenHasTags = true;
                } else {
                    child.hide(false);
                }
                child.stickChildrenToParentRenderPosition(true);
            }
            if (childrenHasTags) {
                element.show(false);
            } else {
                element.hide(false);
            }
        }
    }

    public static void filterFeaturesAndScenariosByTags(String tags) {
        for (Element element : DesignerEngine.featuresRoot.children) {
            if (element.containsAnyOfTags(tags)) {
                element.show(false);
                for (Element child : element.children) {
                    child.show(false);
                    child.stickChildrenToParentRenderPosition(true);
                }
                continue;
            }
            boolean childrenHasTags = false;
            for (Element child : element.children) {
                if (child.containsAnyOfTags(tags)) {
                    child.show(false);
                    childrenHasTags = true;
                } else {
                    child.hide(false);
                }
                child.stickChildrenToParentRenderPosition(true);
            }
            if (childrenHasTags) {
                element.show(false);
            } else {
                element.hide(false);
            }
        }
    }

    public static void filterFeaturesAndScenariosByElement(BaseBarElement filterElement) {
        for (Element element : DesignerEngine.featuresRoot.children) {
            if (element == filterElement) {
                element.show(false);
                for (Element child : element.children) {
                    child.show(false);
                    child.stickChildrenToParentRenderPosition(true);
                }
                continue;
            }
            boolean childVisible = false;
            for (Element child : element.children) {
                if (child == filterElement) {
                    child.show(false);
                    childVisible = true;
                } else {
                    child.hide(false);
                }
                child.stickChildrenToParentRenderPosition(true);
            }
            if (childVisible) {
                element.show(false);
            } else {
                element.hide(false);
            }
        }
    }

    public static void removeFilter() {
        for (Element element : DesignerEngine.featuresRoot.children) {
            for (Element child : element.children) {
                child.show(false);
                child.stickChildrenToParentRenderPosition(true);
            }
            element.show(false);
        }
    }
    
    public static void unfoldOnlyErrors() {
        DesignerEngine.featuresRoot.foldAll();
        for (Element feature : DesignerEngine.featuresRoot.children) {
            for (Element scenario : feature.children) {
                if (((BaseBarElement) scenario).getPlayResult().isFailed()) {
                    feature.unfold();
                    scenario.unfold();
                }
            }
        }
    }

    public static String getRelativePath(String filename) {
        if (!Util.isEmpty(DesignerEngine.featuresBaseDir) && filename.startsWith(DesignerEngine.featuresBaseDir)) {
            return Util.stripLeadingSlash(filename.substring(DesignerEngine.featuresBaseDir.length()));
        }
        return new File(filename).getName();
    }

    public static void deepCopyElement(BaseBarElement element) {
        BaseBarElement newElement = copyAndUnfoldElement(element);
        int index = element.groupParent.findChildIndex(element);
        element.groupParent.addChild(newElement, index);
        deepCopyElement(element, newElement);
        if (newElement.isFolded()) {
            newElement.foldFadeAnimation(0.0f, true);
        }
    }

    public static void deepCopyElement(BaseBarElement sourceElement, BaseBarElement destElement) {
        for (Element element : sourceElement.children) {
            BaseBarElement srcElement = (BaseBarElement) element;
            BaseBarElement newElement = copyAndUnfoldElement(srcElement);
            destElement.addChild(newElement);
            deepCopyElement(srcElement, newElement);
        }
    }
    
    public static BaseBarElement copyAndUnfoldElement(BaseBarElement element) {
        BaseBarElement newElement = element.duplicate();
        if (element.children.size() == 0) {
            newElement.unfold();
        }
        return newElement;
    }

    public static BaseBarElement findBackgroundElement(Element element) {
        if (element == null) {
            return null;
        }
        for (Element child : element.children) {
            if (child.type == BaseBarElement.TYPE_BACKGROUND) {
                return (BaseBarElement) child;
            }
        }
        return null;
    }

    public static BaseBarElement findExamplesElement(Element element) {
        BaseBarElement examplesElement = null;
        for (Element child : element.children) {
            if (child.type == BaseBarElement.TYPE_EXAMPLES) {
                examplesElement = (BaseBarElement) child;
            }
        }
        return examplesElement;
    }

    public static void bubbleStaticElementsIntoPlace(BaseBarElement element) {
        BaseBarElement backgroundElement = findBackgroundElement(element);
        if (backgroundElement != null && element.findChildIndex(backgroundElement) > 0) {
            element.updateElementIndex(backgroundElement, 0);
        }
        BaseBarElement examplesElement = findExamplesElement(element);
        if (examplesElement != null && element.findChildIndex(examplesElement) < element.children.size() - 1) {
            element.removeChild(examplesElement);
            element.children.add(examplesElement);
        }
    }
    
    public static boolean isElementsHighlightable() {
        return !Button.isOneTouched && !DropDown.isVisible;

    }

    public static String suggestFilenameIfNotPresent(Element element) {
        BaseBarElement baseBarElement = element instanceof BaseBarElement ? (BaseBarElement) element : null;
        if (baseBarElement != null && !Util.isEmpty(baseBarElement.getFilename())) {
            return FileUtil.removePostfixFromFilename(FileUtil.removePathFromFilename(baseBarElement.getFilename()));
        }
        if (element.groupParent != null) {
            String filename = suggestFilenameIfNotPresent(element.groupParent);
            return filename + "_" + element.groupParent.findChildIndex(element);
        }
        return "Feature";
    }

    public static void unfoldAllScenariosIfNotTooMany() {
        if (DesignerEngine.featuresRoot.children.size() > 6) {
            return;
        }
        for (Element element : DesignerEngine.featuresRoot.children) {
            element.unfold();
        }
    }

    public static boolean hasCommentInTitle(String title) {
        if (Util.isEmpty(title)) {
            return false;
        }
        return title.split("\n")[0].matches("^\\s*#.*");
    }

    public static String extractCommentFromTitle(String title) {
        if (Util.isEmpty(title)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String delimiter = "";
        for (String line : title.split("\n")) {
            if (!line.matches("^\\s*#.*")) {
                continue;
            }
            sb.append(delimiter).append(line);
            delimiter = "\n";
        }
        return sb.toString();
    }

    public static String removeCommentFromTitle(String title) {
        if (Util.isEmpty(title)) {
            return "-";
        }
        if (!hasCommentInTitle(title)) {
            return title;
        }
        StringBuilder sb = new StringBuilder();
        String delimiter = "";
        for (String line : title.split("\n")) {
            if (line.matches("^\\s*#.*")) {
                continue;
            }
            sb.append(delimiter).append(line);
            delimiter = "\n";
        }
        return Util.isEmpty(sb) ? "-" : sb.toString();
    }

    public static String ensureOnlyOneTitleLine(String title) {
        if (Util.isEmpty(title)) {
            return title;
        }
        String[] lines = title.split("\n");
        if (lines.length <= 1) {
            return title;
        }
        StringBuilder sb = new StringBuilder();
        String delimiter = "";
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            sb.append(delimiter);
            if (!line.matches("^\\s*#.*") && i < lines.length - 1) {
                sb.append("# ");
            }
            sb.append(line);
            delimiter = "\n";
        }
        return sb.toString();
    }
}
