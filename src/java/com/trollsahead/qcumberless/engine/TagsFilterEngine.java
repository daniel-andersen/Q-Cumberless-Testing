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
import com.trollsahead.qcumberless.gui.CumberlessMouseListener;
import com.trollsahead.qcumberless.gui.RenderOptimizer;
import com.trollsahead.qcumberless.gui.elements.BaseBarElement;
import com.trollsahead.qcumberless.gui.Button;
import com.trollsahead.qcumberless.util.Util;

import static com.trollsahead.qcumberless.gui.ExtendedButtons.TagButton;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TagsFilterEngine implements CucumberlessEngine {
    private static final Color TEXT_COLOR = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Color TITLE_UNDERLINE_COLOR = new Color(0.3f, 0.3f, 0.3f, 1.0f);

    private static final String NO_FEATURE_TAGS = "NO FEATURE TAGS DEFINED";
    private static final String NO_TAGS = "NO TAGS DEFINED";

    private static final String FEATURE_TAGS_TITLE = "FEATURE TAGS";
    private static final String ALL_TAGS_TITLE = "ALL TAGS";

    public static final int TAG_WIDTH = 120;
    public static final int TAG_HEIGHT = 50;

    public static final int TAG_PADDING_HORIZONTAL = 30;
    public static final int TAG_PADDING_VERTICAL = 50;
    public static final int TAGS_TITLE_PADDING = 10;

    private static final int TAG_BUTTON_OFFSET_HORIZONTAL = 5;
    private static final int TAG_BUTTON_OFFSET_VERTICAL = 5;

    private BufferedImage background = null;
    private Graphics2D backgroundGraphics = null;

    private static final float APPEAR_SPEED = 0.05f;

    private static final float BACKGROUND_FADE = 1.0f;
    private static final float BACKGROUND_SIZE = 0.5f;

    private static final float TAGS_FADE = 0.3f;
    private static final Color TAGS_BACKGROUND_COLOR = new Color(0.6f, 0.6f, 0.65f);

    public static final Color TAGS_OUTLINE_COLOR = new Color(0.3f, 0.3f, 0.325f);
    public static final int TAGS_OUTLINE_ROUNDING = 20;

    private enum AppearState {VISIBLE, HIDDEN, APPEARING, DISAPPEARING}

    private AppearState appearState = AppearState.HIDDEN;
    private AppearState appearStateOld = AppearState.HIDDEN;
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

    private List<TagButton> featureTagsButtons;
    private List<TagButton> allTagsButtons;

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
        updateTagsButtons();
    }

    public void hide() {
        backgroundGraphics.dispose();
        backgroundGraphics = null;
        background = null;
    }

    public void update() {
        updateAppearAnimation();
        for (Button button : featureTagsButtons) {
            button.update();
        }
        for (Button button : allTagsButtons) {
            button.update();
        }
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

    private void updateTagsButtons() {
        featureTagsButtons = new LinkedList<TagButton>();
        for (final String tag : featureTags) {
            featureTagsButtons.add(
                    new TagButton(
                            0,
                            0,
                            tag,
                            Button.ALIGN_HORIZONTAL_LEFT | Button.ALIGN_VERTICAL_TOP,
                            new Button.ButtonNotification() {
                                public void onClick() {
                                    DesignerEngine.filterFeaturesByTags("@" + tag);
                                    startHiding();
                                }
                            },
                            tag));
        }
        allTagsButtons = new LinkedList<TagButton>();
        for (final String tag : allTags) {
            allTagsButtons.add(
                    new TagButton(
                            0,
                            0,
                            tag,
                            Button.ALIGN_HORIZONTAL_LEFT | Button.ALIGN_VERTICAL_TOP,
                            new Button.ButtonNotification() {
                                public void onClick() {
                                    DesignerEngine.filterFeaturesByTags("@" + tag);
                                    startHiding();
                                }
                            },
                            tag));
        }
    }

    private void startHiding() {
        appearState = AppearState.DISAPPEARING;
    }

    public void render(Graphics2D g) {
        if (appearState == AppearState.DISAPPEARING) {
            if (appearStateOld != AppearState.DISAPPEARING) {
                renderDesignModeToBackgroundImage();
            }
        }
        calculatePosition();
        renderBackground(g);
        if (appearState == AppearState.VISIBLE) {
            renderFeatureTags(g);
            renderTags(g);
        }
        appearStateOld = appearState;
    }

    private void renderDesignModeToBackgroundImage() {
        Engine.designerEngine.update();
        Engine.designerEngine.render(backgroundGraphics);
        Engine.designerEngine.postRender();
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

        updateButtonPositions();
    }

    private void updateButtonPositions() {
        int i = 0;
        for (Button button : featureTagsButtons) {
            setButtonPosition(button, i, featureTagsX, featureTagsY, featureTagsWidth, featureTagsHeight);
            button.setOffset(TAG_BUTTON_OFFSET_HORIZONTAL, TAG_BUTTON_OFFSET_VERTICAL);
            i++;
        }
        i = 0;
        for (Button button : allTagsButtons) {
            setButtonPosition(button, i, allTagsX, allTagsY, allTagsWidth, allTagsHeight);
            button.setOffset(TAG_BUTTON_OFFSET_HORIZONTAL, TAG_BUTTON_OFFSET_VERTICAL);
            i++;
        }
    }

    private void setButtonPosition(Button button, int index, int x, int y, int width, int height) {
        int tagWidthPlusPadding = TAG_WIDTH + TAG_PADDING_HORIZONTAL;
        int tagHeightPlusPadding = TAG_HEIGHT + TAG_PADDING_VERTICAL;
        int countX = width / tagWidthPlusPadding;
        int countY = height / tagHeightPlusPadding;
        int offsetX = x + (width - ((countX * tagWidthPlusPadding) - TAG_PADDING_HORIZONTAL)) / 2;
        int offsetY = y + (height - ((countY * tagHeightPlusPadding) - TAG_PADDING_VERTICAL)) / 2;
        button.setPosition(offsetX + (index % countX) * tagWidthPlusPadding, offsetY + (index / countX) * tagHeightPlusPadding);
    }

    private void renderFeatureTags(Graphics2D g) {
        if (Util.isEmpty(featureTags)) {
            g.setColor(TEXT_COLOR);
            g.drawString(NO_FEATURE_TAGS, (Engine.windowWidth - Engine.fontMetrics.stringWidth(NO_FEATURE_TAGS)) / 2, featureTagsY + ((featureTagsHeight - Engine.fontMetrics.getHeight()) / 2));
            return;
        }
        drawTitle(g, FEATURE_TAGS_TITLE, featureTagsY);
        for (TagButton button : featureTagsButtons) {
            renderButton(g, button);
        }
    }

    private void renderTags(Graphics2D g) {
        if (Util.isEmpty(allTags)) {
            g.setColor(TEXT_COLOR);
            g.drawString(NO_TAGS, (Engine.windowWidth - Engine.fontMetrics.stringWidth(NO_TAGS)) / 2, allTagsY + ((allTagsHeight - Engine.fontMetrics.getHeight()) / 2));
            return;
        }
        drawTitle(g, ALL_TAGS_TITLE, allTagsY);
        for (TagButton button : allTagsButtons) {
            renderButton(g, button);
        }
    }
    
    private void renderButton(Graphics2D g, TagButton button) {
        button.render(g);
    }
    
    private void drawTitle(Graphics g, String title, int y) {
        int titleWidth = Engine.fontMetrics.stringWidth(title);
        int titleX = (Engine.windowWidth - titleWidth) / 2;
        int titleY = y + Engine.fontMetrics.getHeight() + TAGS_TITLE_PADDING;
        g.setColor(Color.BLACK);
        g.drawString(title, titleX + 1, titleY + 1);
        g.setColor(TEXT_COLOR);
        g.drawString(title, titleX, titleY);
        g.setColor(TITLE_UNDERLINE_COLOR);
        g.drawLine(titleX - 25, titleY + 5, titleX + titleWidth + 25, titleY + 5);
    }

    private void renderBackground(Graphics2D g) {
        if (appearState == AppearState.VISIBLE) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, Engine.windowWidth, Engine.windowHeight);
        } else {
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
        }

        g.setColor(new Color(0.0f, 0.0f, 0.0f, appearAnimation * BACKGROUND_FADE));
        g.fillRect(0, 0, Engine.windowWidth, Engine.windowHeight);

        g.setColor(new Color(TAGS_BACKGROUND_COLOR.getRed() / 255.0f, TAGS_BACKGROUND_COLOR.getGreen() / 255.0f, TAGS_BACKGROUND_COLOR.getBlue() / 255.0f, appearAnimation * TAGS_FADE));
        g.fillRect(featureTagsX, featureTagsY, featureTagsWidth, featureTagsHeight);
        g.fillRect(allTagsX, allTagsY, allTagsWidth, allTagsHeight);
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
        for (Button button : featureTagsButtons) {
            button.click();
        }
        for (Button button : allTagsButtons) {
            button.click();
        }
        if (!isMouseInsideTagsArea()) {
            startHiding();
        }
    }

    private boolean isMouseInsideTagsArea() {
        if (CumberlessMouseListener.mouseX >= featureTagsX && CumberlessMouseListener.mouseY >= featureTagsY &&
            CumberlessMouseListener.mouseX <= featureTagsX + featureTagsWidth && CumberlessMouseListener.mouseY <= featureTagsY + featureTagsHeight) {
            return true;
        }
        if (CumberlessMouseListener.mouseX >= allTagsX && CumberlessMouseListener.mouseY >= allTagsY &&
            CumberlessMouseListener.mouseX <= allTagsX + featureTagsWidth && CumberlessMouseListener.mouseY <= allTagsY + allTagsHeight) {
            return true;
        }
        return false;
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
