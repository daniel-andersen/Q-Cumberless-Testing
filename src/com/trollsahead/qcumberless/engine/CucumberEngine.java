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

package com.trollsahead.qcumberless.engine;

import com.trollsahead.qcumberless.device.CucumberDevice;
import com.trollsahead.qcumberless.gui.CucumberSpotlight;
import com.trollsahead.qcumberless.gui.*;
import com.trollsahead.qcumberless.model.CucumberStep;
import com.trollsahead.qcumberless.model.CucumberStepDefinition;
import com.trollsahead.qcumberless.plugins.CucumberPlugin;
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

public class CucumberEngine implements Runnable, ComponentListener, KeyListener {
    public static final Object LOCK = new Object();

    private static final int FRAME_DELAY = 20;

    public static final Font FONT_DEFAULT = new Font("Verdana", Font.PLAIN, 12);

    public static CucumberCanvas canvas;

    public static CucumberRootElement cucumberRoot = null;
    public static CucumberRootElement featuresRoot = null;
    public static CucumberRootElement stepsRoot = null;

    public static List<CucumberStep> stepDefinitions = null;

    public static CucumberMouseListener mouseListener;

    private static CucumberElement oldTouchedElement = null;
    private static CucumberElement touchedElement = null;
    private static CucumberRootElement touchedRootElement = null;

    public static CucumberElement lastAddedElement = null;

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

    public static CucumberButtonBar buttonBar;
    public static CucumberSpotlight spotlight;

    public static String featuresBaseDir = null;

    public static BufferedImage backbuffer;

    private static final long POLL_FOR_DEVICES_PERIOD = 1000L * 5;

    public static List<CucumberPlugin> plugins = new LinkedList<CucumberPlugin>();
    public static Set<CucumberDevice> devices = new HashSet<CucumberDevice>();
    public static long lastTimePolledForDevices;
    public static boolean isPollingForDevices;

    public CucumberEngine() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        canvasWidth = screenSize.width;
        canvasHeight = screenSize.height;
        createBackbuffer();
        
        mouseListener = new CucumberMouseListener();

        canvas = new CucumberCanvas();
        canvas.addMouseListener(mouseListener);
        canvas.addMouseMotionListener(mouseListener);
        canvas.addComponentListener(this);
        canvas.addKeyListener(this);

        buttonBar = new CucumberButtonBar();
        spotlight = new CucumberSpotlight();

        cucumberRoot = new CucumberRootElement();
        scratchFeatures();
        resetStepDefinitions();

        initializePlugins();

        resetFps();
    }

    private static void resetFps() {
        fpsTimer = System.currentTimeMillis();
        fpsUpdateCount = 0;
        fpsLastCount = 0;
    }

    private void initializePlugins() {
        for (CucumberPlugin plugin : plugins) {
            plugin.initialize();
        }
        lastTimePolledForDevices = 0;
        isPollingForDevices = false;
    }

    public CucumberCanvas getCanvas() {
        return canvas;
    }

    public static void stop() {
        isRunning = false;
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
        CucumberButton.isOneTouched = false;
        pollForDevices();
        updateHighlight();
        buttonBar.update();
        spotlight.update();
        cucumberRoot.update(System.currentTimeMillis());
        if (CucumberDropDown.isVisible) {
            CucumberDropDown.update();
        }
        EasterEgg.update();
    }

    private void pollForDevices() {
        if (isPollingForDevices || System.currentTimeMillis() < lastTimePolledForDevices + POLL_FOR_DEVICES_PERIOD) {
            return;
        }
        if (CucumberPlayer.isRunning()) {
            return;
        }
        isPollingForDevices = true;
        new Thread(new Runnable() {
            public void run() {
                Set<CucumberDevice> newDevices = new HashSet<CucumberDevice>();
                for (CucumberPlugin plugin : plugins) {
                    newDevices.addAll(plugin.getDevices());
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
        Graphics g = backbuffer.getGraphics();
        g.setFont(FONT_DEFAULT);
        canvas.clear(g);
        cucumberRoot.render(g);
        if (CucumberDropDown.isVisible) {
            CucumberDropDown.render(g);
        }
        buttonBar.render(g);
        spotlight.render(g);
        CucumberPlayer.render(g);
        cucumberRoot.renderHints(g);
        renderFps(g);
    }

    private void renderFps(Graphics g) {
        if (!fpsShow) {
            return;
        }
        FontMetrics fontMetrics = g.getFontMetrics();
        String str = "FPS: " + fpsLastCount;
        int x = (canvasWidth - fontMetrics.stringWidth(str)) / 2;
        int y = canvasHeight - 5 - CucumberButtonBar.BUTTONBAR_HEIGHT;
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
        Graphics g = backbuffer.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, canvasWidth + 1, canvasHeight + 1);
    }

    public static void setWindowSize(int width, int height) {
        canvasWidth = width;
        canvasHeight = height;
        if (width > backbuffer.getWidth() || height > backbuffer.getHeight()) {
            createBackbuffer();
        }
        updateRootPositions();
    }

    public void componentResized(ComponentEvent componentEvent) {
        try {
            synchronized (LOCK) {
                setWindowSize(canvas.getWidth(), canvas.getHeight());
                Graphics g = backbuffer.getGraphics();
                buttonBar.resize(g);
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

    public static void click() {
        synchronized (LOCK) {
            if (buttonBar.click()) {
                return;
            }
            if (CucumberDropDown.click()) {
                return;
            }
            if (CucumberEditBox.click()) {
                return;
            }
            if (touchedElement != null) {
                touchedElement.click();
            }
        }
    }

    public static void mousePressed() {
        if (CucumberDropDown.mousePressed()) {
            return;
        }
        if (CucumberEditBox.mousePressed()) {
            return;
        }
        startDrag();
    }

    public static void mouseReleased() {
        if (CucumberDropDown.mouseReleased()) {
            return;
        }
        if (CucumberEditBox.mouseReleased()) {
            return;
        }
        endDrag();
    }

    public static void mouseDragged() {
        if (CucumberDropDown.mouseDragged()) {
            return;
        }
        if (CucumberEditBox.mouseDragged()) {
            return;
        }
        updateDrag();
    }

    public static void mouseMoved() {
        if (CucumberDropDown.mouseMoved()) {
            canvasHasMouseFocus = false;
            return;
        }
        if (CucumberEditBox.mouseMoved()) {
            canvasHasMouseFocus = false;
            return;
        }
        canvasHasMouseFocus = true;
        updateDrag();
    }

    private static void startDrag() {
        synchronized (CucumberEngine.LOCK) {
            if (touchedElement != null && touchedElement.isDragable()) {
                touchedElement.startDrag();
            } else if (touchedRootElement != null) {
                touchedRootElement.startDrag();
            }
        }
    }

    private static void endDrag() {
        synchronized (CucumberEngine.LOCK) {
            if (touchedElement != null) {
                touchedElement.endDrag();
            } else if (touchedRootElement != null) {
                touchedRootElement.endDrag();
            }
        }
    }

    private static void updateDrag() {
        if (!CucumberMouseListener.isButtonPressed) {
            return;
        }
        synchronized (CucumberEngine.LOCK) {
            if (touchedElement != null && touchedElement.isBeingDragged()) {
                touchedElement.applyDragOffset();
            } else if (touchedRootElement != null && touchedRootElement.isDragable()) {
                touchedRootElement.scroll(CucumberMouseListener.mouseY - CucumberMouseListener.oldMouseY);
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
        toggleHighlight(touchedElement, true);
    }

    private static void findTouchedElement() {
        touchedElement = cucumberRoot.findElement(CucumberMouseListener.mouseX, CucumberMouseListener.mouseY);
        touchedRootElement = CucumberMouseListener.mouseX < dragSplitterX ? featuresRoot : stepsRoot;
    }

    private static void toggleHighlight(CucumberElement element, boolean highlight) {
        if (element != null) {
            element.highlight(highlight);
        }
    }

    public static void runTests(CucumberTextElement cucumberTextElement) {
        final StringBuilder feature = buildFeature(cucumberTextElement);
        System.out.println(feature.toString());
        CucumberPlayer.prepareRun();
        for (final CucumberDevice device : devices) {
            if (device.isEnabled() && device.getCapabilities().contains(CucumberDevice.Capability.PLAY)) {
                System.out.println("Running tests on device: " + device.name());
                new CucumberPlayer().play(feature, device);
            }
        }
    }

    private static StringBuilder buildFeature(CucumberTextElement cucumberTextElement) {
        if (cucumberTextElement.type == CucumberTextElement.TYPE_FEATURE) {
            return cucumberTextElement.buildFeature();
        }
        StringBuilder sb = new StringBuilder();
        if (cucumberTextElement.type == CucumberTextElement.TYPE_SCENARIO) {
            sb.append("Feature: " + cucumberTextElement.groupParent.getTitle() + "\n\n");
            CucumberElement background = findBackgroundElement(cucumberTextElement.groupParent);
            if (background != null) {
                sb.append(background.buildFeature());
            } else {
                sb.append("Background:\n");
                sb.append("\tWhen I see that I'm on the \"bank\" page\n\n");
            }
        }
        sb.append(cucumberTextElement.buildFeature());
        return sb;
    }

    private static CucumberElement findBackgroundElement(CucumberElement element) {
        for (CucumberElement child : element.children) {
            if (child.type == CucumberTextElement.TYPE_BACKGROUND) {
                return child;
            }
        }
        return null;
    }

    public static void importSteps() {
        new Thread(new Runnable() {
            public void run() {
                List<CucumberStepDefinition> stepDefinitions = plugins.get(0).getStepDefinitions(); // TODO!
                synchronized (LOCK) {
                    CucumberStepDefinitionLoader.parseStepDefinitions(stepDefinitions);
                    featuresRoot.updateSteps();
                }
            }
        }).start();
    }

    public static void importFeatures(File[] files) {
        resetFps();
        if (files == null || files.length == 0) {
            return;
        }
        featuresBaseDir = (files.length == 1 && files[0].isDirectory()) ? files[0].getAbsolutePath() : null;
        synchronized (LOCK) {
            try {
                CucumberFeatureLoader.parseFeatureFiles(Util.getFeatureFiles(files));
            } catch (Exception e) {
                e.printStackTrace();
            }
            CucumberEngine.featuresRoot.isLoaded = true;
        }
    }

    public static void exportFeatures(File directory) {
        resetFps();
        featuresRoot.export(directory);
    }

    public static void saveFeatures() {
        resetFps();
        featuresRoot.save();
    }

    public static void scratchFeatures() {
        resetFps();
        synchronized (LOCK) {
            resetFeatures();
            addTemplateFeature();
        }
    }

    public static void resetStepDefinitions() {
        resetFps();
        stepDefinitions = new ArrayList<CucumberStep>();

        cucumberRoot.removeChild(stepsRoot);
        stepsRoot = new CucumberRootElement();
        cucumberRoot.addChild(stepsRoot, 1);

        stepsRoot.addChild(new CucumberTextElement(CucumberTextElement.TYPE_FEATURE, CucumberTextElement.ROOT_STEP_DEFINITIONS, "Feature"));
        stepsRoot.addChild(new CucumberTextElement(CucumberTextElement.TYPE_SCENARIO, CucumberTextElement.ROOT_STEP_DEFINITIONS, "Scenario"));
        stepsRoot.addChild(new CucumberTextElement(CucumberTextElement.TYPE_COMMENT, CucumberTextElement.ROOT_STEP_DEFINITIONS, "Comment"));
        CucumberTextElement stepElement = new CucumberTextElement(CucumberTextElement.TYPE_STEP, CucumberTextElement.ROOT_STEP_DEFINITIONS, "New step");
        stepElement.step.isMatched = false;
        stepsRoot.addChild(stepElement);

        updateRootPositions();
    }

    public static void resetFeatures() {
        resetFps();
        featuresBaseDir = null;
        cucumberRoot.removeChild(CucumberEngine.featuresRoot);
        featuresRoot = new CucumberRootElement();
        featuresRoot.isLoaded = false;
        cucumberRoot.addChild(featuresRoot, 0);
        updateRootPositions();
    }

    private static void addTemplateFeature() {
        CucumberTextElement scenario = new CucumberTextElement(CucumberTextElement.TYPE_SCENARIO, CucumberTextElement.ROOT_FEATURE_EDITOR, "New Scenario");
        scenario.unfold();
        CucumberTextElement feature = new CucumberTextElement(CucumberTextElement.TYPE_FEATURE, CucumberTextElement.ROOT_FEATURE_EDITOR, "New Feature");
        feature.setFilename("noname_" + System.currentTimeMillis() + ".feature");
        feature.addChild(scenario);
        feature.unfold();
        featuresRoot.addChild(feature);
        updateLastAddedElement(scenario);
    }

    private static void updateRootPositions() {
        dragSplitterX = CucumberTextElement.RENDER_WIDTH_MAX_FEATURE_EDITOR + ((canvasWidth - CucumberTextElement.RENDER_WIDTH_MAX_STEP_DEFINITIONS - CucumberTextElement.RENDER_WIDTH_MAX_FEATURE_EDITOR) / 2);
        int divider = Math.max(dragSplitterX, canvasWidth - CucumberTextElement.RENDER_WIDTH_MAX_STEP_DEFINITIONS - CucumberRootElement.PADDING_HORIZONTAL * 2);
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
        if (!CucumberEditBox.isVisible) {
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
        for (CucumberDevice device : devices) {
            if (device.isEnabled() && device.getCapabilities().contains(CucumberDevice.Capability.PLAY)) {
                return true;
            }
        }
        return false;
    }

    public static void updateLastAddedElement(CucumberElement element) {
        if (element.groupParent != stepsRoot) {
            lastAddedElement = element;
        }
    }

    public static List<String> getDefinedTags() {
        return Arrays.asList(featuresRoot.getTags().toArray(new String[0]));
    }
}
