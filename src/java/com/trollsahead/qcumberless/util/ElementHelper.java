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

import com.trollsahead.qcumberless.engine.Engine;
import com.trollsahead.qcumberless.gui.Element;
import com.trollsahead.qcumberless.gui.TextElement;

import java.io.File;

public class ElementHelper {
    public static String getRelativePath(String filename) {
        if (!Util.isEmpty(Engine.featuresBaseDir) && filename.startsWith(Engine.featuresBaseDir)) {
            return Util.stripLeadingSlash(filename.substring(Engine.featuresBaseDir.length()));
        }
        return new File(filename).getName();
    }

    public static void deepCopyElement(TextElement element) {
        TextElement newElement = copyAndUnfoldElement(element);
        int index = element.groupParent.findChildIndex(element);
        element.groupParent.addChild(newElement, index);
        deepCopyElement(element, newElement);
        if (newElement.isFolded()) {
            newElement.foldFadeAnimation(0.0f);
        }
    }

    public static void deepCopyElement(TextElement sourceElement, TextElement destElement) {
        for (Element element : sourceElement.children) {
            TextElement oldElement = (TextElement) element;
            TextElement newElement = copyAndUnfoldElement(oldElement);
            destElement.addChild(newElement);
            deepCopyElement(oldElement, newElement);
        }
    }
    
    public static TextElement copyAndUnfoldElement(TextElement element) {
        TextElement newElement = element.duplicate();
        if (element.children.size() == 0) {
            newElement.unfold();
        }
        return newElement;
    }

    public static TextElement findBackgroundElement(Element element) {
        for (Element child : element.children) {
            if (child.type == TextElement.TYPE_BACKGROUND) {
                return (TextElement) child;
            }
        }
        return null;
    }

    public static void bubbleBackgroundToTop(TextElement element) {
        TextElement backgroundElement = findBackgroundElement(element);
        if (backgroundElement == null || element.findChildIndex(backgroundElement) == 0) {
            return;
        }
        element.updateElementIndex(backgroundElement, 0);
    }
}