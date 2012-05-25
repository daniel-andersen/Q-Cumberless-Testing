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

package com.trollsahead.qcumberless.gui;

import com.trollsahead.qcumberless.engine.Engine;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class BarOptimizer {
    private static final int MAX_UNUSED_COUNT = 10;

    private static Map<String, BarTemplate> templates;

    public static void reset() {
        templates = new HashMap<String, BarTemplate>();
    }

    public static void update() {
        if (hasUnusedTemplates()) {
            removeUnusedTemplates();
        }
    }

    private static boolean hasUnusedTemplates() {
        for (BarTemplate template : templates.values()) {
            template.unusedCount++;
            if (template.unusedCount > MAX_UNUSED_COUNT) {
                return true;
            }
        }
        return false;
    }

    private static void removeUnusedTemplates() {
        Map<String, BarTemplate> newTemplates = new HashMap<String, BarTemplate>();
        for (String key : templates.keySet()) {
            BarTemplate template = templates.get(key);
            if (template.unusedCount <= MAX_UNUSED_COUNT) {
                newTemplates.put(key, template);
            }
        }
        templates = newTemplates;
    }

    public static BufferedImage getBarTemplate(int width, int height, Color color) {
        String key = getKey(width, height, color);
        BarTemplate template = templates.get(key);
        if (template == null) {
            return null;
        }
        template.unusedCount = 0;
        return template.image;
    }
    
    public static Graphics2D createBarTemplate(int width, int height, Color color) {
        String key = getKey(width, height, color);
        System.out.println("New template: " + key);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setFont(Engine.FONT_DEFAULT);
        templates.put(key, new BarTemplate(image, graphics));
        return graphics;
    }
    
    private static String getKey(int width, int height, Color color) {
        return width + "," + height + "," + color.toString();
    }

    private static class BarTemplate {
        public BufferedImage image;
        public Graphics2D graphics;
        public int unusedCount;

        public BarTemplate(BufferedImage image, Graphics2D graphics) {
            this.image = image;
            this.graphics = graphics;
            unusedCount = 0;
        }
    }
}
