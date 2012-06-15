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
import com.trollsahead.qcumberless.util.ElementHelper;
import com.trollsahead.qcumberless.util.FileUtil;
import com.trollsahead.qcumberless.util.HistoryHelper;
import com.trollsahead.qcumberless.util.Util;

import static com.trollsahead.qcumberless.engine.Engine.AnimationState;
import static com.trollsahead.qcumberless.engine.Engine.animationBackground;
import static com.trollsahead.qcumberless.gui.elements.Element.ColorScheme;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class HistoryEngine implements CucumberlessEngine {
    private static final Color COLOR_HISTORY_DATE_NORMAL = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Color COLOR_HISTORY_DATE_HIGHLIGHT = new Color(1.0f, 1.0f, 0.0f, 1.0f);

    private static final int DATES_PADDING = 40;
    private static final int BUTTON_PADDING = 50;

    private static final String NO_HISTORY = "NO HISTORY";
    private static final String HISTORY_VIEW = "HISTORY VIEW";

    private static RootElement originalCucumberRoot = null;
    private static RootElement originalFeaturesRoot = null;
    private static RootElement originalStepsRoot = null;
    private static ColorScheme originalColorScheme;

    private static AnimationState animationState;
    private static float animationProgress;

    private static List<String> historyDirs;
    private static List<String> historyDates;
    private static String historyDate;
    private static int historyDirsIndex;

    private static Button leftArrowButton;
    private static Button rightArrowButton;
    private static String tags = null;

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
        backupCucumberRoot();
        historyDirs = Util.restrictListSize(HistoryHelper.filterByTags(HistoryHelper.sortDirs(HistoryHelper.findHistoryDirs()), tags), 20);
        historyDates = new LinkedList<String>();
        for (String dir : historyDirs) {
            historyDates.add(getHistoryDate(dir));
        }
        if (!Util.isEmpty(historyDirs)) {
            showHistory(AnimationState.NONE);
            FlashingMessageManager.addMessage(new FlashingMessage(HISTORY_VIEW));
        } else {
            showNoHistory();
        }
    }

    public void hide() {
        restoreCucumberRoot();
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
            if (animationState != AnimationState.NONE) {
                renderCurrentRootToBackground();
            }
            createNewRoot();
            loadFeatures(historyDirs.get(historyDirsIndex));
            historyDate = getHistoryDate(historyDirs.get(historyDirsIndex));
            DesignerEngine.setColorScheme(ColorScheme.PLAY);
            animationProgress = animationState != AnimationState.NONE ? 0.0f : 1.0f;
        }
    }

    private void showNoHistory() {
        animationState = AnimationState.NONE;
        animationProgress = 1.0f;
        createNewRoot();
        FlashingMessageManager.addMessage(new FlashingMessage(NO_HISTORY));
    }

    private String getHistoryDate(String dir) {
        Date date = HistoryHelper.extractDateFromDir(dir);
        return new SimpleDateFormat("MM-dd-yyyy HH:mm").format(date);
    }

    private void renderCurrentRootToBackground() {
        render(Engine.animationGraphics);
    }

    private void loadFeatures(String dir) {
        List<String> features = FileUtil.getFeatureFiles(dir);
        FeatureLoader.parseFeatureFiles(features.toArray(new String[0]), true);
        if (!Util.isEmpty(tags)) {
            ElementHelper.filterFeaturesAndScenariosByTags(tags);
        }
    }

    private void createNewRoot() {
        DesignerEngine.cucumberRoot = new RootElement();
        DesignerEngine.cucumberRoot.setBounds(0, 0, 0, 0);
        DesignerEngine.featuresRoot = new RootElement();
        DesignerEngine.stepsRoot = new RootElement();
    }

    public void update() {
        if (!Util.isEmpty(historyDirs)) {
            leftArrowButton.update();
            rightArrowButton.update();
            Engine.designerEngine.update();
        }
        FlashingMessageManager.update();
        updateAnimation();
    }

    private void updateAnimation() {
        if (animationProgress < 1.0f) {
            animationProgress = Math.min(1.0f, animationProgress + GuiUtil.DISAPPEAR_SPEED);
        }
    }

    public void render(Graphics2D g) {
        Engine.designerEngine.clear(g);
        Engine.designerEngine.renderOnlyElements(g);
        if (animationState != AnimationState.NONE) {
            GuiUtil.renderAppearAnimation(g, animationBackground, animationState, animationProgress);
        }
        renderDates(g);
        renderButtonbar(g);
    }

    private void renderDates(Graphics2D g) {
        if (Util.isEmpty(historyDates)) {
            return;
        }
        int x = Engine.windowWidth - Engine.fontMetrics.stringWidth(historyDates.get(0)) - DATES_PADDING;
        int y = DATES_PADDING + Engine.fontMetrics.getHeight() - 3;
        int i = 0;
        for (String date : historyDates) {
            g.setColor(i != historyDirsIndex ? COLOR_HISTORY_DATE_NORMAL : COLOR_HISTORY_DATE_HIGHLIGHT);
            g.drawString(date, x, y);
            y += Engine.fontMetrics.getHeight() + 5;
            i++;
        }
    }

    private void renderButtonbar(Graphics2D g) {
        if (Util.isEmpty(historyDirs)) {
            return;
        }
        g.setColor(ButtonBar.COLOR_BACKGROUND_NORMAL);
        g.fillRect(0, Engine.windowHeight - ButtonBar.BUTTONBAR_HEIGHT, Engine.windowWidth, ButtonBar.BUTTONBAR_HEIGHT);

        int dateWidth = Engine.fontMetrics.stringWidth(historyDate);
        int dateX = (Engine.windowWidth - dateWidth) / 2;
        int textY = Engine.windowHeight - (ButtonBar.BUTTONBAR_HEIGHT + Engine.fontMetrics.getHeight()) / 2 + Engine.fontMetrics.getHeight() - 3;

        g.setColor(Color.WHITE);
        g.drawString(historyDate, dateX, textY);

        if (!Util.isEmpty(tags)) {
            g.drawString(tags, 5, textY);
        }

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
            Engine.prevEngine();
        }
    }

    public void nextDate() {
        if (animationProgress < 1.0f) {
            return;
        }
        if (!Util.isEmpty(historyDirs) && historyDirsIndex < historyDirs.size() - 1) {
            historyDirsIndex++;
            showHistory(AnimationState.FORWARD);
        }
    }

    public void prevDate() {
        if (animationProgress < 1.0f) {
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

    public static void removeTagFilter() {
        tags = null;
    }

    public static void setTagFilter(String tags) {
        HistoryEngine.tags = tags;
    }
}
