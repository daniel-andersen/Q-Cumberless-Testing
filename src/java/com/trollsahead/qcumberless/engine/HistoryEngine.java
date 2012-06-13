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
import com.trollsahead.qcumberless.gui.*;
import com.trollsahead.qcumberless.gui.Button;
import com.trollsahead.qcumberless.gui.elements.RootElement;
import com.trollsahead.qcumberless.util.FileUtil;
import com.trollsahead.qcumberless.util.HistoryHelper;
import com.trollsahead.qcumberless.util.Util;

import static com.trollsahead.qcumberless.gui.elements.Element.ColorScheme;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class HistoryEngine implements CucumberlessEngine {
    private static final float DISAPPEAR_SPEED = 0.03f;

    private static final int SHADOW_WIDTH = 200;
    private static final int SHADOW_STEP = SHADOW_WIDTH / 10;
    private static final float SHADOW_DEPTH = 0.5f;

    private static final int BUTTON_PADDING = 50;

    private static final String NO_HISTORY = "NO HISTORY";
    private static final String HISTORY_VIEW = "HISTORY VIEW";

    private static RootElement originalCucumberRoot = null;
    private static RootElement originalFeaturesRoot = null;
    private static RootElement originalStepsRoot = null;
    private static ColorScheme originalColorScheme;

    private static BufferedImage background = null;
    private static Graphics2D backgroundGraphics = null;

    private static enum AnimationState {NONE, ACTIVATING, DEACTIVATING, FORWARD, BACKWARD}

    private static AnimationState animationState;
    private static float moveAnimation;

    private static List<String> historyDirs;
    private static String historyDate;
    private static int historyDirsIndex;

    private static Button leftArrowButton;
    private static Button rightArrowButton;

    public void initialize() {
        leftArrowButton = new Button(
                0, 0,
                Images.getImage(Images.IMAGE_ARROW_LEFT, Images.ThumbnailState.NORMAL.ordinal()),
                Images.getImage(Images.IMAGE_ARROW_LEFT, Images.ThumbnailState.HIGHLIGHTED.ordinal()),
                Images.getImage(Images.IMAGE_ARROW_LEFT, Images.ThumbnailState.PRESSED.ordinal()),
                Button.ALIGN_HORIZONTAL_CENTER | Button.ALIGN_VERTICAL_CENTER,
                new Button.ButtonNotification() {
                    public void onClick() {
                        prevDate();
                    }
                },
                null);
        rightArrowButton = new Button(
                0, 0,
                Images.getImage(Images.IMAGE_ARROW_RIGHT, Images.ThumbnailState.NORMAL.ordinal()),
                Images.getImage(Images.IMAGE_ARROW_RIGHT, Images.ThumbnailState.HIGHLIGHTED.ordinal()),
                Images.getImage(Images.IMAGE_ARROW_RIGHT, Images.ThumbnailState.PRESSED.ordinal()),
                Button.ALIGN_HORIZONTAL_CENTER | Button.ALIGN_VERTICAL_CENTER,
                new Button.ButtonNotification() {
                    public void onClick() {
                        nextDate();
                    }
                },
                null);
    }

    public void show() {
        reset();
        background = RenderOptimizer.graphicsConfiguration.createCompatibleImage(Engine.windowWidth, Engine.windowHeight);
        backgroundGraphics = background.createGraphics();
        backupCucumberRoot();
        historyDirs = HistoryHelper.sortDirs(HistoryHelper.findHistoryDirs());
        if (!Util.isEmpty(historyDirs)) {
            showHistory(AnimationState.ACTIVATING);
            FlashingMessageManager.addMessage(new FlashingMessage(HISTORY_VIEW));
        } else {
            showNoHistory();
        }
    }

    public void hide() {
        backgroundGraphics.dispose();
        backgroundGraphics = null;
        background = null;
    }

    private void startHiding() {
        if (moveAnimation < 1.0f) {
            return;
        }
        historyDirsIndex = -1;
        restoreCucumberRoot();
        showHistory(AnimationState.DEACTIVATING);
        FlashingMessageManager.removeAllMessages();
    }

    private void reset() {
        historyDirs = null;
        historyDate = null;
        historyDirsIndex = 0;
    }

    private void backupCucumberRoot() {
        originalCucumberRoot = DesignerEngine.cucumberRoot;
        originalFeaturesRoot = DesignerEngine.featuresRoot;
        originalStepsRoot = DesignerEngine.stepsRoot;
        originalColorScheme = DesignerEngine.colorScheme;
    }

    private void restoreCucumberRoot() {
        DesignerEngine.cucumberRoot = originalCucumberRoot;
        DesignerEngine.featuresRoot = originalFeaturesRoot;
        DesignerEngine.stepsRoot = originalStepsRoot;
        DesignerEngine.setColorScheme(originalColorScheme);
    }

    private void showHistory(AnimationState newAnimationState) {
        synchronized (Engine.DATA_LOCK) {
            animationState = newAnimationState;
            renderCurrentRootToBackground();
            if (historyDirsIndex != -1) {
                createNewRoot();
                loadFeatures(historyDirs.get(historyDirsIndex));
                getHistoryDate(historyDirs.get(historyDirsIndex));
                DesignerEngine.setColorScheme(ColorScheme.PLAY);
            }
            moveAnimation = 0.0f;
        }
    }

    private void showNoHistory() {
        animationState = AnimationState.ACTIVATING;
        renderCurrentRootToBackground();
        createNewRoot();
        moveAnimation = 0.0f;
        FlashingMessageManager.addMessage(new FlashingMessage(NO_HISTORY));
    }

    private void getHistoryDate(String dir) {
        Date date = HistoryHelper.extractDateFromDir(dir);
        historyDate = new SimpleDateFormat("MM-dd-yyyy HH:mm").format(date);
    }

    private void renderCurrentRootToBackground() {
        if (animationState == AnimationState.ACTIVATING) {
            backgroundGraphics.drawImage(Engine.backbuffer, 0, 0, null);
        } else {
            render(backgroundGraphics, 0);
        }
    }

    private void loadFeatures(String dir) {
        List<String> features = FileUtil.getFeatureFiles(dir);
        FeatureLoader.parseFeatureFiles(features.toArray(new String[0]), true);
    }

    private void createNewRoot() {
        DesignerEngine.cucumberRoot = new RootElement();
        DesignerEngine.cucumberRoot.setBounds(0, 0, 0, 0);
        DesignerEngine.featuresRoot = new RootElement();
        DesignerEngine.stepsRoot = new RootElement();
    }

    public void update() {
        leftArrowButton.update();
        rightArrowButton.update();
        Engine.designerEngine.update();
        FlashingMessageManager.update();
        if (moveAnimation < 1.0f) {
            moveAnimation = Math.min(1.0f, moveAnimation + DISAPPEAR_SPEED);
        }
        if (animationState == AnimationState.DEACTIVATING && moveAnimation >= 1.0f) {
            Engine.showEngine(Engine.designerEngine);
        }
    }

    public void render(Graphics2D g) {
        render(g, animationState == AnimationState.ACTIVATING ? 2 : 1);
    }

    private void render(Graphics2D g, int whenToRenderButtonBar) {
        if (animationState == AnimationState.DEACTIVATING) {
            Engine.designerEngine.render(g);
        } else {
            Engine.designerEngine.clear(g);
            Engine.designerEngine.renderOnlyElements(g);
            if (whenToRenderButtonBar == 2) {
                renderButtonbar(g);
            }
        }
        if (animationState != AnimationState.NONE) {
            boolean rightwards = animationState == AnimationState.BACKWARD || animationState == AnimationState.DEACTIVATING;
            int x = (int) ((1.0f - Math.cos((moveAnimation + 0.1f) * Math.PI / 2.0f)) * (Engine.windowWidth + SHADOW_WIDTH)) * (rightwards ? 1 : -1);
            if (Engine.fpsDetails != Engine.DETAILS_LOW) {
                int sx = x + (rightwards ? -SHADOW_STEP : Engine.windowWidth);
                for (int i = 0; i < SHADOW_WIDTH; i += SHADOW_STEP) {
                    g.setColor(new Color(0.0f, 0.0f, 0.0f, SHADOW_DEPTH - ((float) i / SHADOW_WIDTH) * SHADOW_DEPTH));
                    g.fillRect(sx, 0, SHADOW_STEP, Engine.windowHeight);
                    sx += SHADOW_STEP * (rightwards ? -1 : 1);
                }
            } else {
                g.setColor(new Color(0.0f, 0.0f, 0.0f, SHADOW_DEPTH));
                g.fillRect(x + (rightwards ? -SHADOW_WIDTH : Engine.windowWidth), 0, SHADOW_WIDTH, Engine.windowHeight);
            }
            g.drawImage(background, x, 0, null);
        }
        if (whenToRenderButtonBar == 1) {
            renderButtonbar(g);
        }
    }

    private void renderButtonbar(Graphics2D g) {
        if (Util.isEmpty(historyDirs)) {
            return;
        }
        if (animationState == AnimationState.DEACTIVATING) {
            return;
        }

        g.setColor(ButtonBar.COLOR_BACKGROUND_NORMAL);
        g.fillRect(0, Engine.windowHeight - ButtonBar.BUTTONBAR_HEIGHT, Engine.windowWidth, ButtonBar.BUTTONBAR_HEIGHT);
        
        int dateWidth = Engine.fontMetrics.stringWidth(historyDate);
        int dateX = (Engine.windowWidth - dateWidth) / 2;

        g.setColor(Color.WHITE);
        g.drawString(historyDate, dateX, Engine.windowHeight - (ButtonBar.BUTTONBAR_HEIGHT + Engine.fontMetrics.getHeight()) / 2 + Engine.fontMetrics.getHeight() - 3);

        int buttonY = Engine.windowHeight - (ButtonBar.BUTTONBAR_HEIGHT / 2);
        leftArrowButton.setPosition(dateX - BUTTON_PADDING, buttonY);
        rightArrowButton.setPosition(dateX + dateWidth + BUTTON_PADDING, buttonY);

        leftArrowButton.render(g);
        rightArrowButton.render(g);
    }

    public void postRender() {
        Engine.designerEngine.postRender();
    }

    public void resize() {
        Engine.designerEngine.resize();
    }

    public void mouseMoved() {
        Engine.designerEngine.mouseMoved();
    }

    public void mouseWheelMoved(int unitsToScroll) {
        Engine.designerEngine.mouseWheelMoved(unitsToScroll);
    }

    public void click(int clickCount) {
        if (leftArrowButton.click()) {
            return;
        }
        if (rightArrowButton.click()) {
            return;
        }
        Engine.designerEngine.click(clickCount);
    }

    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
            prevDate();
        }
        if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
            nextDate();
        }
        if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
            startHiding();
        }
    }

    public void nextDate() {
        if (moveAnimation < 1.0f) {
            return;
        }
        if (!Util.isEmpty(historyDirs) && historyDirsIndex < historyDirs.size() - 1) {
            historyDirsIndex++;
            showHistory(AnimationState.FORWARD);
        }
    }

    public void prevDate() {
        if (moveAnimation < 1.0f) {
            return;
        }
        if (!Util.isEmpty(historyDirs) && historyDirsIndex > 0) {
            historyDirsIndex--;
            showHistory(AnimationState.BACKWARD);
        }
    }

    public void startDrag(boolean isControlDown) {
        Engine.designerEngine.startDrag(isControlDown);
    }

    public void endDrag() {
        Engine.designerEngine.endDrag();
    }

    public void updateDrag() {
        Engine.designerEngine.updateDrag();
    }

    public void updateDevices(Set<Device> devices) {
        Engine.designerEngine.updateDevices(devices);
    }
}
