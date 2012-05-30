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
import com.trollsahead.qcumberless.gui.Button;
import com.trollsahead.qcumberless.gui.Spotlight;
import com.trollsahead.qcumberless.gui.*;
import com.trollsahead.qcumberless.gui.elements.*;
import com.trollsahead.qcumberless.model.Step;
import com.trollsahead.qcumberless.model.StepDefinition;
import com.trollsahead.qcumberless.model.Locale;
import com.trollsahead.qcumberless.plugins.Plugin;
import com.trollsahead.qcumberless.util.ConfigurationManager;
import com.trollsahead.qcumberless.util.ElementHelper;
import com.trollsahead.qcumberless.util.Util;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

public class Engine implements Runnable, ComponentListener, KeyListener {
    public static final Object LOCK = new Object();

    private static final int FRAME_DELAY = 20;

    public static final Font FONT_DEFAULT = new Font("Verdana", Font.PLAIN, 12);

    public static FontMetrics fontMetrics;

    public static CumberlessCanvas canvas;

    public static RootElement cucumberRoot = null;
    public static RootElement featuresRoot = null;
    public static RootElement stepsRoot = null;

    public static List<Step> stepDefinitions = null;

    public static CumberlessMouseListener mouseListener;

    private static Element oldTouchedElement = null;
    private static Element touchedElement = null;
    private static RootElement touchedRootElement = null;

    public static Element lastAddedElement = null;

    private static int dragSplitterX = 0;

    private static boolean canvasHasMouseFocus = true;

    public static int canvasWidth;
    public static int canvasHeight;

    public static final int DETAILS_ALL   = 0;
    public static final int DETAILS_FEWER = 1;
    public static final int DETAILS_NONE  = 2;

    private static long cycleTime;
    private static long fpsTimer;
    private static int fpsUpdateCount;
    private static int fpsLastCount;
    public static int fpsDetails = DETAILS_ALL;
    private static boolean fpsShow = false;

    private static boolean isRunning;

    public static ButtonBar buttonBar;
    public static Spotlight spotlight;

    public static String featuresBaseDir = null;

    public static BufferedImage backbuffer;

    private static final long POLL_FOR_DEVICES_PERIOD = 1000L * 5;

    public static List<Plugin> plugins = new LinkedList<Plugin>();
    public static Set<Device> devices = new HashSet<Device>();
    public static long lastTimePolledForDevices;
    public static boolean isPollingForDevices;

    private static Set<String> runTags = new HashSet<String>();

    private static Graphics2D backbufferGraphics = null;

    public Engine() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        canvasWidth = screenSize.width;
        canvasHeight = screenSize.height;
        createBackbuffer();

        RenderOptimizer.reset();

        FlashingMessageManager.initialize();

        mouseListener = new CumberlessMouseListener();

        canvas = new CumberlessCanvas();
        canvas.addMouseListener(mouseListener);
        canvas.addMouseMotionListener(mouseListener);
        canvas.addComponentListener(this);
        canvas.addKeyListener(this);

        buttonBar = new ButtonBar();
        spotlight = new Spotlight();

        cucumberRoot = new RootElement();
        scratchFeatures(false);
        resetStepDefinitions(false);

        resetFps();
    }

    private static void resetFps() {
        fpsTimer = System.currentTimeMillis();
        fpsUpdateCount = 0;
        fpsLastCount = 0;
    }

    public static void initializePlugins() {
        for (Plugin plugin : plugins) {
            plugin.initialize();
        }
        lastTimePolledForDevices = 0;
        isPollingForDevices = false;
    }

    public CumberlessCanvas getCanvas() {
        return canvas;
    }

    public void show() {
        if (!Util.isEmpty(ConfigurationManager.get("importFeaturesOnStartup"))) {
            importFeatures(new File[]{new File(ConfigurationManager.get("featuresPath"))});
        } else {
            scratchFeatures(true);
        }
        if (!Util.isEmpty(ConfigurationManager.get("importStepDefinitionsOnStartup"))) {
            importStepDefinitions(plugins.get(0));
        } else {
            resetStepDefinitions(true);
        }
        if (!Util.isEmpty(ConfigurationManager.get("enableDeviceOnStartup"))) {
            DeviceManager.enableDeviceWithName(ConfigurationManager.get("enableDeviceOnStartup"));
        }
    }

    public static void stop() {
        isRunning = false;
        CucumberlessDialog.close();
    }

    public void run() {
        isRunning = true;
        cycleTime = System.currentTimeMillis();
        while (isRunning) {
            synchronized (LOCK) {
                update();
                render();
                cucumberRoot.stickToParentRenderPosition(false);
            }
            canvas.repaint();
            synchFramerate();
        }
    }

    private void update() {
        Button.isOneTouched = false;
        pollForDevices();
        RenderOptimizer.update();
        updateHighlight();
        buttonBar.update();
        spotlight.update();
        cucumberRoot.update(System.currentTimeMillis());
        if (DropDown.isVisible) {
            DropDown.update();
        }
        EasterEgg.update();
        FlashingMessageManager.update();
    }

    private void pollForDevices() {
        if (isPollingForDevices || System.currentTimeMillis() < lastTimePolledForDevices + POLL_FOR_DEVICES_PERIOD) {
            return;
        }
        if (Player.isRunning()) {
            return;
        }
        isPollingForDevices = true;
        new Thread(new Runnable() {
            public void run() {
                Set<Device> newDevices = new HashSet<Device>();
                for (Plugin plugin : plugins) {
                    Set<Device> devices = plugin.getDevices();
                    if (devices != null) {
                        newDevices.addAll(devices);
                    }
                }
                synchronized (LOCK) {
                    devices = newDevices;
                    buttonBar.updateDevices(devices);
                }
                isPollingForDevices = false;
                lastTimePolledForDevices = System.currentTimeMillis();
            }
        }).start();
    }

    private void render() {
        canvas.clear(backbufferGraphics);
        cucumberRoot.render(backbufferGraphics);
        if (DropDown.isVisible) {
            DropDown.render(backbufferGraphics);
        }
        buttonBar.render(backbufferGraphics);
        spotlight.render(backbufferGraphics);
        Player.render(backbufferGraphics);
        FlashingMessageManager.render(backbufferGraphics);
        cucumberRoot.renderHints(backbufferGraphics);
        renderFps(backbufferGraphics);
    }

    private void renderFps(Graphics g) {
        if (!fpsShow) {
            return;
        }
        String str = "FPS: " + fpsLastCount;
        int x = (canvasWidth - fontMetrics.stringWidth(str)) / 2;
        int y = canvasHeight - 5 - ButtonBar.BUTTONBAR_HEIGHT;
        g.setColor(Color.BLACK);
        g.drawString(str, x + 1, y + 1);
        g.setColor(Color.WHITE);
        g.drawString(str, x, y);
    }

    private void synchFramerate() {
        long time = System.currentTimeMillis();
        if (time > fpsTimer + 1000L) {
            fpsLastCount = fpsUpdateCount;
            if (fpsUpdateCount >= 45) {
                fpsDetails = DETAILS_ALL;
            }
            if (fpsUpdateCount >= 30 && fpsUpdateCount <= 35) {
                fpsDetails = DETAILS_FEWER;
            }
            if (fpsUpdateCount <= 25) {
                fpsDetails = DETAILS_NONE;
            }
            fpsUpdateCount = 0;
            fpsTimer = time;
        }
        fpsUpdateCount++;
        cycleTime += FRAME_DELAY;
        long difference = cycleTime - time;
        Util.sleep(Math.max(5, difference));
    }

    private static void createBackbuffer() {
        backbuffer = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        backbufferGraphics = backbuffer.createGraphics();
        backbufferGraphics.setFont(FONT_DEFAULT);
        fontMetrics = backbufferGraphics.getFontMetrics();
        backbufferGraphics.setColor(Color.BLACK);
        backbufferGraphics.fillRect(0, 0, canvasWidth + 1, canvasHeight + 1);
    }

    public static void setWindowSize(int width, int height) {
        canvasWidth = width;
        canvasHeight = height;
        RenderOptimizer.reset();
        if (width > backbuffer.getWidth() || height > backbuffer.getHeight()) {
            createBackbuffer();
        }
        updateRootPositions();
    }

    public void componentResized(ComponentEvent componentEvent) {
        try {
            synchronized (LOCK) {
                setWindowSize(canvas.getWidth(), canvas.getHeight());
                buttonBar.resize();
            }
        } catch (Exception e) {
            // Ignore!
        }
    }

    public void componentMoved(ComponentEvent componentEvent) {
    }

    public void componentShown(ComponentEvent componentEvent) {
    }

    public void componentHidden(ComponentEvent componentEvent) {
    }

    public static void click(int clickCount) {
        synchronized (LOCK) {
            if (buttonBar.click()) {
                return;
            }
            if (DropDown.click()) {
                return;
            }
            if (EditBox.click()) {
                return;
            }
            if (touchedElement != null) {
                touchedElement.click(clickCount);
            }
        }
    }

    public static void mousePressed(boolean isControlDown) {
        if (DropDown.mousePressed()) {
            return;
        }
        if (EditBox.mousePressed()) {
            return;
        }
        startDrag(isControlDown);
    }

    public static void mouseReleased() {
        if (DropDown.mouseReleased()) {
            return;
        }
        if (EditBox.mouseReleased()) {
            return;
        }
        endDrag();
    }

    public static void mouseDragged() {
        if (DropDown.mouseDragged()) {
            return;
        }
        if (EditBox.mouseDragged()) {
            return;
        }
        updateDrag();
    }

    public static void mouseMoved() {
        if (DropDown.mouseMoved()) {
            canvasHasMouseFocus = false;
            return;
        }
        if (EditBox.mouseMoved()) {
            canvasHasMouseFocus = false;
            return;
        }
        canvasHasMouseFocus = true;
        updateDrag();
    }

    private static void startDrag(boolean isControlDown) {
        synchronized (Engine.LOCK) {
            if (touchedElement != null && touchedElement.isDragable()) {
                touchedElement.startDrag(isControlDown);
            } else if (touchedRootElement != null) {
                touchedRootElement.startDrag(isControlDown);
            }
        }
    }

    private static void endDrag() {
        synchronized (Engine.LOCK) {
            if (touchedElement != null) {
                touchedElement.endDrag();
            } else if (touchedRootElement != null) {
                touchedRootElement.endDrag();
            }
        }
    }

    private static void updateDrag() {
        if (!CumberlessMouseListener.isButtonPressed) {
            return;
        }
        synchronized (Engine.LOCK) {
            if (touchedElement != null && touchedElement.isBeingDragged()) {
                touchedElement.applyDragOffset();
            } else if (touchedRootElement != null && touchedRootElement.isDragable()) {
                touchedRootElement.scroll(CumberlessMouseListener.mouseY - CumberlessMouseListener.oldMouseY);
            }
        }
    }

    private static void updateHighlight() {
        if (!canvasHasMouseFocus) {
            return;
        }
        if (touchedElement != null && touchedElement.isBeingDragged()) {
            return;
        }
        findTouchedElement();
        if (touchedElement != oldTouchedElement) {
            toggleHighlight(oldTouchedElement, false);
            oldTouchedElement = touchedElement;
        }
        if (CumberlessMouseListener.mouseY > Engine.canvasHeight - ButtonBar.BUTTONBAR_HEIGHT) {
            return;
        }
        toggleHighlight(touchedElement, true);
    }

    private static void findTouchedElement() {
        if (touchedElement != null) {
            touchedElement = touchedElement.findElement(CumberlessMouseListener.mouseX, CumberlessMouseListener.mouseY);
        }
        if (touchedElement == null) {
            touchedElement = cucumberRoot.findElement(CumberlessMouseListener.mouseX, CumberlessMouseListener.mouseY);
        }
        touchedRootElement = CumberlessMouseListener.mouseX < dragSplitterX ? featuresRoot : stepsRoot;
    }

    private static void toggleHighlight(Element element, boolean highlight) {
        if (element != null) {
            element.highlight(highlight);
        }
    }

    public static void runTests(BaseBarElement cucumberTextElement) {
        final StringBuilder feature = buildFeature(cucumberTextElement);
        System.out.println(feature.toString());
        Player.prepareRun();
        for (final Device device : devices) {
            if (device.isEnabled() && device.getCapabilities().contains(Device.Capability.PLAY)) {
                System.out.println("Running tests on device: " + device.name());
                new Player().play(feature, device, runTags);
            }
        }
    }

    private static StringBuilder buildFeature(BaseBarElement cucumberTextElement) {
        if (cucumberTextElement.type == BaseBarElement.TYPE_FEATURE) {
            return cucumberTextElement.buildFeature();
        }
        StringBuilder sb = new StringBuilder();
        if (cucumberTextElement.type == BaseBarElement.TYPE_SCENARIO || cucumberTextElement.type == BaseBarElement.TYPE_BACKGROUND || cucumberTextElement.type == BaseBarElement.TYPE_SCENARIO_OUTLINE) {
            BaseBarElement parentTextElement = (BaseBarElement) cucumberTextElement.groupParent;
            if (!Util.isEmpty(parentTextElement.getComment())) {
                sb.append(parentTextElement.getComment()).append("\n");
            }
            if (!Util.isEmpty(parentTextElement.getTagsString())) {
                sb.append(parentTextElement.getTagsString()).append("\n");
            }
            sb.append(Locale.getString("feature")).append(": ").append(parentTextElement.getTitle()).append("\n\n");
            if (cucumberTextElement.type != BaseBarElement.TYPE_BACKGROUND) {
                Element background = ElementHelper.findBackgroundElement(parentTextElement);
                if (background != null) {
                    sb.append(background.buildFeature());
                }
            }
        }
        sb.append(cucumberTextElement.buildFeature());
        if (cucumberTextElement.type == BaseBarElement.TYPE_BACKGROUND) {
            sb.append("\n");
            sb.append(ElementHelper.EXPORT_INDENT).append(Locale.getString("scenario")).append(": Testing background\n");
            sb.append(ElementHelper.EXPORT_INDENT).append(ElementHelper.EXPORT_INDENT).append("# Just for testing background\n");
        }
        return sb;
    }

    public static void importStepDefinitions(final Plugin plugin) {
        new Thread(new Runnable() {
            public void run() {
                List<StepDefinition> stepDefinitions = plugin.getStepDefinitions();
                synchronized (LOCK) {
                    CucumberStepDefinitionLoader.parseStepDefinitions(stepDefinitions);
                    featuresRoot.updateSteps();
                }
            }
        }).start();
    }

    public static void importFeatures(File[] files) {
        if (files == null || files.length == 0) {
            resetFps();
            return;
        }
        synchronized (LOCK) {
            try {
                FeatureLoader.parseFeatureFiles(Util.getFeatureFiles(files));
            } catch (Exception e) {
                e.printStackTrace();
            }
            featuresBaseDir = (files.length == 1 && files[0].isDirectory()) ? files[0].getAbsolutePath() : null;
            Engine.featuresRoot.isLoaded = true;
            resetFps();
        }
    }

    public static void exportFeatures(File directory) {
        featuresRoot.export(directory);
        FlashingMessageManager.addMessage(new FlashingMessage("Features exported!", FlashingMessage.STANDARD_TIMEOUT));
        resetFps();
    }

    public static void saveFeatures() {
        featuresRoot.save();
        FlashingMessageManager.addMessage(new FlashingMessage("Features saved!", FlashingMessage.STANDARD_TIMEOUT));
        resetFps();
    }

    public static void scratchFeatures(boolean addTemplate) {
        synchronized (LOCK) {
            resetFeatures();
            if (addTemplate) {
                addTemplateFeature();
            }
        }
        resetFps();
    }

    public static void resetStepDefinitions(boolean addTemplate) {
        synchronized (LOCK) {
            stepDefinitions = new ArrayList<Step>();

            cucumberRoot.removeChild(stepsRoot);
            stepsRoot = new RootElement();
            cucumberRoot.addChild(stepsRoot, 1);

            if (addTemplate) {
                stepsRoot.addChild(new FeatureElement(BaseBarElement.ROOT_STEP_DEFINITIONS, Locale.getString("feature")));
                stepsRoot.addChild(new ScenarioElement(BaseBarElement.ROOT_STEP_DEFINITIONS, Locale.getString("scenario")));
                stepsRoot.addChild(new ScenarioOutlineElement(BaseBarElement.ROOT_STEP_DEFINITIONS, Locale.getString("scenario outline")));
                stepsRoot.addChild(new BackgroundElement(BaseBarElement.ROOT_STEP_DEFINITIONS, Locale.getString("background")));
                stepsRoot.addChild(new CommentElement(BaseBarElement.ROOT_STEP_DEFINITIONS, Locale.getString("comment")));
                BaseBarElement stepElement = new StepElement(BaseBarElement.ROOT_STEP_DEFINITIONS, Locale.getString("new step"));
                stepElement.step.isMatched = false;
                stepsRoot.addChild(stepElement);
            }

            updateRootPositions();
        }
        resetFps();
    }

    public static void resetFeatures() {
        featuresBaseDir = null;
        cucumberRoot.removeChild(Engine.featuresRoot);
        featuresRoot = new RootElement();
        featuresRoot.isLoaded = false;
        cucumberRoot.addChild(featuresRoot, 0);
        updateRootPositions();
        resetFps();
    }

    private static void addTemplateFeature() {
        BaseBarElement scenario = new ScenarioElement(BaseBarElement.ROOT_FEATURE_EDITOR, "New Scenario");
        scenario.unfold();
        BaseBarElement feature = new FeatureElement(BaseBarElement.ROOT_FEATURE_EDITOR, "New Feature");
        feature.setFilename("noname_" + System.currentTimeMillis() + ".feature");
        feature.addChild(scenario);
        feature.unfold();
        featuresRoot.addChild(feature);
        updateLastAddedElement(scenario);
    }

    private static void updateRootPositions() {
        dragSplitterX = BaseBarElement.RENDER_WIDTH_MAX_FEATURE_EDITOR + ((canvasWidth - BaseBarElement.RENDER_WIDTH_MAX_STEP_DEFINITIONS - BaseBarElement.RENDER_WIDTH_MAX_FEATURE_EDITOR) / 2);
        int divider = Math.max(dragSplitterX, canvasWidth - BaseBarElement.RENDER_WIDTH_MAX_STEP_DEFINITIONS - RootElement.PADDING_HORIZONTAL * 2);
        cucumberRoot.setBounds(0, 0, 0, 0);
        if (featuresRoot != null) {
            featuresRoot.setBounds(0, 10, divider - 20, canvasHeight);
        }
        if (stepsRoot != null) {
            stepsRoot.setBounds(divider, 10, canvasWidth - divider, canvasHeight);
        }
    }

    public void keyTyped(KeyEvent keyEvent) {
    }

    public void keyPressed(KeyEvent keyEvent) {
        if (!EditBox.isVisible) {
            synchronized (LOCK) {
                spotlight.searchKeyPressed(keyEvent);
            }
            if (keyEvent.getKeyChar() == '!') {
                fpsShow = !fpsShow;
            }
        }
    }

    public void keyReleased(KeyEvent keyEvent) {
    }

    public static boolean isPlayableDeviceEnabled() {
        for (Device device : devices) {
            if (device.isEnabled() && device.getCapabilities().contains(Device.Capability.PLAY)) {
                return true;
            }
        }
        return false;
    }

    public static void updateLastAddedElement(Element element) {
        if (element.groupParent != stepsRoot) {
            lastAddedElement = element;
        }
    }

    public static List<String> getDefinedTags() {
        return Arrays.asList(featuresRoot.getTags().toArray(new String[0]));
    }

    public static void toggleRunTag(String tag) {
        String negatedTag = Util.negatedTag(tag);
        if (runTags.contains(tag)) {
            runTags.remove(tag);
            runTags.add(negatedTag);
        } else if (runTags.contains(negatedTag)) {
            runTags.remove(negatedTag);
        } else {
            runTags.add(tag);
        }
    }

    public static boolean isRunTagEnabled(String tag) {
        return runTags.contains(tag);
    }
}
