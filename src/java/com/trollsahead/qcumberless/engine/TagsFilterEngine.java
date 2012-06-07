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

package com.trollsahead.qcumberless.engine;

import com.trollsahead.qcumberless.device.Device;
import com.trollsahead.qcumberless.gui.RenderOptimizer;
import com.trollsahead.qcumberless.gui.elements.BaseBarElement;
import com.trollsahead.qcumberless.util.Util;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;

public class TagsFilterEngine implements CucumberlessEngine {
    private static final Color TEXT_COLOR = new Color(1.0f, 1.0f, 1.0f, 1.0f);

    private static final String NO_FEATURE_TAGS = "NO FEATURE TAGS DEFINED";
    private static final String NO_TAGS = "NO TAGS DEFINED";

    private BufferedImage background = null;
    private Graphics2D backgroundGraphics = null;

    private static final float APPEAR_SPEED = 0.05f;

    private static final float BACKGROUND_FADE = 1.0f;
    private static final float BACKGROUND_SIZE = 0.5f;

    private static final float TAGS_FADE = 0.3f;
    private static final Color TAGS_COLOR = new Color(0.6f, 0.6f, 0.65f);

    private enum AppearState {VISIBLE, HIDDEN, APPEARING, DISAPPEARING}
    private AppearState appearState = AppearState.HIDDEN;
    private float appearAnimation;

    private List<String> featureTags;
    private List<String> allTags;

    private int featureTagsX;
    private int featureTagsY;
    private int featureTagsWidth;
    private int featureTagsHeight;

    private int allTagsX;
    private int allTagsY;
    private int allTagsWidth;
    private int allTagsHeight;

    public void initialize() {
    }

    public void show() {
        background = RenderOptimizer.graphicsConfiguration.createCompatibleImage(Engine.windowWidth, Engine.windowHeight);
        backgroundGraphics = background.createGraphics();
        backgroundGraphics.drawImage(Engine.backbuffer, 0, 0, null);
        appearAnimation = 0.0f;
        appearState = AppearState.APPEARING;
        featureTags = DesignerEngine.getDefinedTags(BaseBarElement.TYPE_FEATURE);
        allTags = DesignerEngine.getDefinedTags();
    }

    public void hide() {
        backgroundGraphics.dispose();
        backgroundGraphics = null;
        background = null;
    }

    public void update() {
        updateAppearAnimation();
    }

    private void updateAppearAnimation() {
        if (appearState == AppearState.APPEARING) {
            appearAnimation += APPEAR_SPEED;
            if (appearAnimation >= 1.0f) {
                appearAnimation = 1.0f;
                appearState = AppearState.VISIBLE;
            }
        } else if (appearState == AppearState.DISAPPEARING) {
            appearAnimation -= APPEAR_SPEED;
            if (appearAnimation <= 0.0f) {
                appearAnimation = 0.0f;
                appearState = AppearState.HIDDEN;
                Engine.showEngine(Engine.designerEngine);
            }
        }
    }

    public void render(Graphics2D g) {
        calculatePosition();
        renderBackground(g);
        renderFeatureTags(g);
        renderTags(g);
    }

    private void calculatePosition() {
        featureTagsWidth = (int) (Engine.windowWidth * 0.8f);
        featureTagsHeight = (int) (Engine.windowHeight * 0.45f);
        featureTagsX = (Engine.windowWidth - featureTagsWidth) / 2;
        featureTagsY = ((Engine.windowHeight / 2) - featureTagsHeight) / 2;

        allTagsWidth = (int) (Engine.windowWidth * 0.8f);
        allTagsHeight = (int) (Engine.windowHeight * 0.45f);
        allTagsX = (Engine.windowWidth - allTagsWidth) / 2;
        allTagsY = (((Engine.windowHeight / 2) - allTagsHeight) / 2) + (Engine.windowHeight / 2);
    }

    private void renderFeatureTags(Graphics2D g) {
        g.setColor(new Color(TAGS_COLOR.getRed() / 255.0f, TAGS_COLOR.getGreen() / 255.0f, TAGS_COLOR.getBlue() / 255.0f, appearAnimation * TAGS_FADE));
        g.fillRect(featureTagsX, featureTagsY, featureTagsWidth, featureTagsHeight);

        if (Util.isEmpty(featureTags)) {
            g.setColor(TEXT_COLOR);
            g.drawString(NO_FEATURE_TAGS, (Engine.windowWidth - Engine.fontMetrics.stringWidth(NO_FEATURE_TAGS)) / 2, featureTagsY + ((featureTagsHeight - Engine.fontMetrics.getHeight()) / 2));
            return;
        }
    }

    private void renderTags(Graphics2D g) {
        g.setColor(new Color(TAGS_COLOR.getRed() / 255.0f, TAGS_COLOR.getGreen() / 255.0f, TAGS_COLOR.getBlue() / 255.0f, appearAnimation * TAGS_FADE));
        g.fillRect(allTagsX, allTagsY, allTagsWidth, allTagsHeight);

        if (Util.isEmpty(allTags)) {
            g.setColor(TEXT_COLOR);
            g.drawString(NO_TAGS, (Engine.windowWidth - Engine.fontMetrics.stringWidth(NO_TAGS)) / 2, allTagsY + ((allTagsHeight - Engine.fontMetrics.getHeight()) / 2));
            return;
        }
    }

    private void renderBackground(Graphics2D g) {
        if (appearState == AppearState.VISIBLE) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, Engine.windowWidth, Engine.windowHeight);
            return;
        }
        if (Engine.fpsDetails != Engine.DETAILS_LOW) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, Engine.windowWidth, Engine.windowHeight);

            float scale = 1.0f + (appearAnimation * BACKGROUND_SIZE);

            int width = (int) (Engine.windowWidth * scale);
            int height = (int) (Engine.windowHeight * scale);
            int x = (Engine.windowWidth - width) / 2;
            int y = (Engine.windowHeight - height) / 2;

            AffineTransform transform = g.getTransform();
            g.translate(x, y);
            g.scale(scale, scale);
            g.drawImage(background, 0, 0, null);
            g.setTransform(transform);
        } else {
            g.drawImage(background, 0, 0, null);
        }

        g.setColor(new Color(0.0f, 0.0f, 0.0f, appearAnimation * BACKGROUND_FADE));
        g.fillRect(0, 0, Engine.windowWidth, Engine.windowHeight);
    }

    public void postRender() {
    }

    public void resize() {
    }

    public void mouseMoved() {
    }

    public void mouseWheelMoved(int unitsToScroll) {
    }

    public void click(int clickCount) {
    }

    public void keyPressed(KeyEvent keyEvent) {
    }

    public void startDrag(boolean isControlDown) {
    }

    public void endDrag() {
    }

    public void updateDrag() {
    }

    public void updateDevices(Set<Device> devices) {
    }
}
