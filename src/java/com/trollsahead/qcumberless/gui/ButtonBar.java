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

import com.trollsahead.qcumberless.device.Device;
import com.trollsahead.qcumberless.engine.*;
import com.trollsahead.qcumberless.gui.elements.Element;
import com.trollsahead.qcumberless.plugins.ButtonBarMethodCallback;
import com.trollsahead.qcumberless.plugins.Plugin;
import com.trollsahead.qcumberless.util.Util;

import static com.trollsahead.qcumberless.gui.Images.ThumbnailState;
import static com.trollsahead.qcumberless.gui.ExtendedButtons.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

public class ButtonBar {
    public static final int TYPE_NORMAL  = 0;
    public static final int TYPE_PLAYING = 1;

    public static final int BUTTONBAR_HEIGHT = 30;
    public static final int BUTTON_PADDING = 10 + Button.TEXT_BACKGROUND_PADDING_HORIZONTAL * 2;
    private static final int DEVICE_BUTTON_WIDTH = 30;

    private static final float ANIMATION_MOVEMENT_SPEED = 0.8f;
    private static final float ANIMATION_FADE_SPEED = 0.05f;

    public static final Color COLOR_BACKGROUND_NORMAL = new Color(0.0f, 0.0f, 0.0f, 0.6f);
    private static final Color COLOR_BACKGROUND_PLAYING = new Color(0.0f, 0.3f, 0.0f, 0.8f);
    private static final Color COLOR_BACKGROUND_FAILED = new Color(0.5f, 0.0f, 0.0f, 0.8f);
    private static final Color COLOR_BUTTON_DELIMITER = new Color(0.5f, 0.5f, 0.5f);

    private static final String TEXT_NO_DEVICES = "No devices found";

    private static final float PLAY_ANIMATION_SPIN_SPEED = 30.0f;
    private static final long PLAY_ANIMATION_BLINK_SPEED = 500;
    private static final float PLAY_ANIMATION_DASH_LENGTH = 5.0f;
    private static final float PLAY_ANIMATION_DASH_WIDTH = 2.0f;

    private Button scratchFeaturesButton;
    private Button loadFeaturesButton;
    private Button saveFeaturesButton;
    private Button exportFeaturesButton;
    private Button undoButton;
    private Button closeButton;
    private Button pauseButton;
    private Button stopButton;
    private Button playButton;
    private Button tagsButton;
    private Button terminalButton;
    private Button paletteButton;
    private Button timeglassButton;
    private List<Button> buttons;

    private List<DeviceButton> deviceButtons;
    private List<Button> pluginButtons;

    public int renderX;
    public int renderY;
    public int renderWidth;
    public int renderHeight;

    private int iconButtonsX;
    private int pluginButtonsX;

    private Animation animation;

    private static int type = TYPE_NORMAL;

    public static ButtonBar instance = null;

    private static BufferedImage deviceEnabledImage;
    private static BufferedImage deviceDisabledImage;

    static {
        try {
            deviceEnabledImage = ImageIO.read(ButtonBar.class.getResource("/resources/pictures/device_enabled.png"));
            deviceDisabledImage = ImageIO.read(ButtonBar.class.getResource("/resources/pictures/device_disabled.png"));
        } catch (Exception e) {
            throw new RuntimeException("Q-Cumberless Testing refused to start", e);
        }
    }

    public ButtonBar() {
        instance = this;
        animation = new Animation(COLOR_BACKGROUND_NORMAL);
        addPluginButtons();
        deviceButtons = new LinkedList<DeviceButton>();
        buttons = new LinkedList<Button>();
        pauseButton = new Button(
                0, 0,
                Images.getImage(Images.IMAGE_PAUSE, ThumbnailState.NORMAL.ordinal()),
                Images.getImage(Images.IMAGE_PAUSE, ThumbnailState.HIGHLIGHTED.ordinal()),
                Images.getImage(Images.IMAGE_PAUSE, ThumbnailState.PRESSED.ordinal()),
                Button.ALIGN_HORIZONTAL_CENTER | Button.ALIGN_VERTICAL_BOTTOM,
                new Button.ButtonNotification() {
                    public void onClick() {
                        if (!Player.isPaused()) {
                            Player.pause();
                        } else {
                            Player.resume();
                        }
                    }
                },
                null);
        buttons.add(pauseButton);
        stopButton = new Button(
                0, 0,
                Images.getImage(Images.IMAGE_STOP, ThumbnailState.NORMAL.ordinal()),
                Images.getImage(Images.IMAGE_STOP, ThumbnailState.HIGHLIGHTED.ordinal()),
                Images.getImage(Images.IMAGE_STOP, ThumbnailState.PRESSED.ordinal()),
                Button.ALIGN_HORIZONTAL_CENTER | Button.ALIGN_VERTICAL_BOTTOM,
                new Button.ButtonNotification() {
                    public void onClick() {
                        Player.stop();
                    }
                },
                null);
        buttons.add(stopButton);
        scratchFeaturesButton = new Button(
                0, 0,
                "Scratch",
                Button.ALIGN_HORIZONTAL_LEFT | Button.ALIGN_VERTICAL_CENTER,
                new Button.ButtonNotification() {
                    public void onClick() {
                        if (DesignerEngine.spotlight.visible && "black knight mode".equalsIgnoreCase(DesignerEngine.spotlight.searchString)) {
                            EasterEgg.show();
                        } else {
                            DesignerEngine.scratchFeatures(true);
                        }
                    }
                },
                null);
        buttons.add(scratchFeaturesButton);
        loadFeaturesButton = new Button(
                0, 0,
                "Load",
                Button.ALIGN_HORIZONTAL_LEFT | Button.ALIGN_VERTICAL_CENTER,
                new Button.ButtonNotification() {
                    public void onClick() {
                        File[] files = CucumberlessDialog.instance.fileChooser();
                        if (files != null) {
                            DesignerEngine.importFeatures(files);
                        }
                    }
                },
                null);
        buttons.add(loadFeaturesButton);
        saveFeaturesButton = new Button(
                0, 0,
                "Save",
                Button.ALIGN_HORIZONTAL_LEFT | Button.ALIGN_VERTICAL_CENTER,
                new Button.ButtonNotification() {
                    public void onClick() {
                        DesignerEngine.saveFeatures();
                    }
                },
                null);
        buttons.add(saveFeaturesButton);
        exportFeaturesButton = new Button(
                0, 0,
                "Export",
                Button.ALIGN_HORIZONTAL_LEFT | Button.ALIGN_VERTICAL_CENTER,
                new Button.ButtonNotification() {
                    public void onClick() {
                        File directory = CucumberlessDialog.instance.directoryChooser();
                        if (directory != null) {
                            DesignerEngine.exportFeatures(directory);
                        }
                    }
                },
                null);
        buttons.add(exportFeaturesButton);
        undoButton = new Button(
                0, 0,
                "Undo",
                Button.ALIGN_HORIZONTAL_LEFT | Button.ALIGN_VERTICAL_CENTER,
                new Button.ButtonNotification() {
                    public void onClick() {
                        DesignerEngine.undo();
                    }
                },
                null);
        buttons.add(undoButton);
        closeButton = new Button(
                0, 0,
                "Quit",
                Button.ALIGN_HORIZONTAL_LEFT | Button.ALIGN_VERTICAL_CENTER,
                new Button.ButtonNotification() {
                    public void onClick() {
                        Engine.stop();
                    }
                },
                null);
        buttons.add(closeButton);
        playButton = new Button(
                0, 0,
                Images.getImage(Images.IMAGE_PLAY, ThumbnailState.NORMAL.ordinal()),
                Images.getImage(Images.IMAGE_PLAY, ThumbnailState.HIGHLIGHTED.ordinal()),
                Images.getImage(Images.IMAGE_PLAY, ThumbnailState.NORMAL.ordinal()),
                Button.ALIGN_HORIZONTAL_CENTER | Button.ALIGN_VERTICAL_CENTER,
                new Button.ButtonNotification() {
                    public void onClick() {
                        DesignerEngine.runTests();
                    }
                },
                null);
        playButton.setHint("Play");
        buttons.add(playButton);
        tagsButton = new Button(
                0, 0,
                Images.getImage(Images.IMAGE_AT, ThumbnailState.NORMAL.ordinal()),
                Images.getImage(Images.IMAGE_AT, ThumbnailState.HIGHLIGHTED.ordinal()),
                Images.getImage(Images.IMAGE_AT, ThumbnailState.NORMAL.ordinal()),
                Button.ALIGN_HORIZONTAL_CENTER | Button.ALIGN_VERTICAL_CENTER,
                new Button.ButtonNotification() {
                    public void onClick() {
                        Engine.showEngine(Engine.tagsFilterEngine);
                    }
                },
                null);
        tagsButton.setHint("Show tags filter");
        buttons.add(tagsButton);
        terminalButton = new Button(
                0, 0,
                Images.getImage(Images.IMAGE_TERMINAL, ThumbnailState.NORMAL.ordinal()),
                Images.getImage(Images.IMAGE_TERMINAL, ThumbnailState.HIGHLIGHTED.ordinal()),
                Images.getImage(Images.IMAGE_TERMINAL, ThumbnailState.PRESSED.ordinal()),
                Button.ALIGN_HORIZONTAL_CENTER | Button.ALIGN_VERTICAL_CENTER,
                new Button.ButtonNotification() {
                    public void onClick() {
                        DesignerEngine.toggleTerminal();
                    }
                },
                null);
        terminalButton.setHint("Show terminal output");
        buttons.add(terminalButton);
        paletteButton = new Button(
                0, 0,
                Images.getImage(Images.IMAGE_PALETTE, ThumbnailState.NORMAL.ordinal()),
                Images.getImage(Images.IMAGE_PALETTE, ThumbnailState.HIGHLIGHTED.ordinal()),
                Images.getImage(Images.IMAGE_PALETTE, ThumbnailState.PRESSED.ordinal()),
                Button.ALIGN_HORIZONTAL_CENTER | Button.ALIGN_VERTICAL_CENTER,
                new Button.ButtonNotification() {
                    public void onClick() {
                        DesignerEngine.switchColorScheme();
                        paletteButton.setHint("Switch color scheme to " + (DesignerEngine.colorScheme == Element.ColorScheme.DESIGN ? "play" : "design") + " theme");
                    }
                },
                null);
        paletteButton.setHint("Switch color scheme to play theme");
        buttons.add(paletteButton);
        timeglassButton = new Button(
                0, 0,
                Images.getImage(Images.IMAGE_TIMEGLASS, ThumbnailState.NORMAL.ordinal()),
                Images.getImage(Images.IMAGE_TIMEGLASS, ThumbnailState.HIGHLIGHTED.ordinal()),
                Images.getImage(Images.IMAGE_TIMEGLASS, ThumbnailState.PRESSED.ordinal()),
                Button.ALIGN_HORIZONTAL_CENTER | Button.ALIGN_VERTICAL_CENTER,
                new Button.ButtonNotification() {
                    public void onClick() {
                        HistoryEngine.removeTagFilter();
                        Engine.showEngine(Engine.historyEngine);
                    }
                },
                null);
        timeglassButton.setHint("Show history");
        buttons.add(timeglassButton);
        animation.moveAnimation.setRealPosition(0, 0);
        animation.moveAnimation.setRenderPosition(0, 0);
    }

    private void addPluginButtons() {
        pluginButtons = new LinkedList<Button>();
        for (Plugin plugin : Engine.plugins) {
            List<ButtonBarMethodCallback> callbacks = plugin.getButtonBarMethods();
            if (Util.isEmpty(callbacks)) {
                continue;
            }
            for (final ButtonBarMethodCallback callback : callbacks) {
                Button button = new Button(
                        0, 0,
                        callback.getThumbnail(ThumbnailState.NORMAL),
                        callback.getThumbnail(ThumbnailState.HIGHLIGHTED),
                        callback.getThumbnail(ThumbnailState.PRESSED),
                        Button.ALIGN_HORIZONTAL_CENTER | Button.ALIGN_VERTICAL_CENTER,
                        new Button.ButtonNotification() {
                            public void onClick() {
                                callback.trigger();
                            }
                        },
                        null);
                button.setHint(callback.getTooltip());
                pluginButtons.add(button);
            }
        }
    }

    public void resize() {
        positionButtons();
    }

    private void positionButtons() {
        positionStaticButtons();
        positionPluginButtons();
        positionDeviceButtons();
    }

    public void updateDevices(Set<Device> devices) {
        deviceButtons = new LinkedList<DeviceButton>();
        for (final Device device : devices) {
            DeviceButton button = new DeviceButton(
                    0, 0,
                    device.getThumbnail(ThumbnailState.NORMAL),
                    device.getThumbnail(ThumbnailState.HIGHLIGHTED),
                    device.getThumbnail(ThumbnailState.PRESSED),
                    Button.ALIGN_HORIZONTAL_CENTER | Button.ALIGN_VERTICAL_CENTER,
                    new Button.ButtonNotification() {
                        public void onClick() {
                            if (device.isEnabled()) {
                                if (Player.isDeviceStarted(device)) {
                                    device.stop();
                                }
                                device.disable();
                            } else {
                                device.enable();
                            }
                        }
                    },
                    device);
            button.setHint(device.name());
            deviceButtons.add(button);
        }
        sortDeviceButtons();
        positionDeviceButtons();
    }

    private void positionStaticButtons() {
        int x = BUTTON_PADDING;
        scratchFeaturesButton.setPosition(x, BUTTONBAR_HEIGHT / 2);
        x += BUTTON_PADDING + Engine.fontMetrics.stringWidth(scratchFeaturesButton.toString());
        loadFeaturesButton.setPosition(x, BUTTONBAR_HEIGHT / 2);
        x += BUTTON_PADDING + Engine.fontMetrics.stringWidth(loadFeaturesButton.toString());
        saveFeaturesButton.setPosition(x, BUTTONBAR_HEIGHT / 2);
        x += BUTTON_PADDING + Engine.fontMetrics.stringWidth(saveFeaturesButton.toString());
        exportFeaturesButton.setPosition(x, BUTTONBAR_HEIGHT / 2);
        x += BUTTON_PADDING + Engine.fontMetrics.stringWidth(exportFeaturesButton.toString());
        x += BUTTON_PADDING;
        undoButton.setPosition(x, BUTTONBAR_HEIGHT / 2);
        x += BUTTON_PADDING + Engine.fontMetrics.stringWidth(undoButton.toString());
        x += BUTTON_PADDING;
        closeButton.setPosition(x, BUTTONBAR_HEIGHT / 2);
        x += BUTTON_PADDING + Engine.fontMetrics.stringWidth(closeButton.toString());
        x += BUTTON_PADDING * 2;
        iconButtonsX = x;
        x += BUTTON_PADDING;
        if (playButton.isVisible()) {
            playButton.setPosition(x, BUTTONBAR_HEIGHT / 2);
            x += BUTTON_PADDING;
        }
        if (tagsButton.isVisible()) {
            tagsButton.setPosition(x, BUTTONBAR_HEIGHT / 2);
            x += BUTTON_PADDING;
        }
        if (terminalButton.isVisible()) {
            terminalButton.setPosition(x, BUTTONBAR_HEIGHT / 2);
            x += BUTTON_PADDING;
        }
        if (paletteButton.isVisible()) {
            paletteButton.setPosition(x, BUTTONBAR_HEIGHT / 2);
            x += BUTTON_PADDING;
        }
        if (timeglassButton.isVisible()) {
            timeglassButton.setPosition(x, BUTTONBAR_HEIGHT / 2);
            x += BUTTON_PADDING;
        }
        x += BUTTON_PADDING * 2;
        pluginButtonsX = x;
        pauseButton.setPosition((Engine.windowWidth / 2) - Engine.windowWidth - 30, BUTTONBAR_HEIGHT);
        stopButton.setPosition((Engine.windowWidth / 2) - Engine.windowWidth + 30, BUTTONBAR_HEIGHT);
    }

    private void positionPluginButtons() {
        int x = pluginButtonsX;
        for (Button button : pluginButtons) {
            button.setPosition(x + (DEVICE_BUTTON_WIDTH / 2), BUTTONBAR_HEIGHT / 2);
            x += button.getImageWidth() + BUTTON_PADDING;
        }
    }

    private void positionDeviceButtons() {
        int x = Engine.windowWidth - BUTTON_PADDING;
        for (Button button : deviceButtons) {
            button.setPosition(x - (DEVICE_BUTTON_WIDTH / 2), BUTTONBAR_HEIGHT / 2);
            x -= button.getImageWidth() + BUTTON_PADDING;
        }
    }

    private void sortDeviceButtons() {
        if (deviceButtons == null || deviceButtons.isEmpty()) {
            return;
        }
        DeviceButton buttons[] = deviceButtons.toArray(new DeviceButton[0]);
        Arrays.sort(buttons, new Comparator<DeviceButton>() {
            public int compare(DeviceButton b1, DeviceButton b2) {
                return b2.getDevice().name().compareTo(b1.getDevice().name());
            }
        });
        deviceButtons = Arrays.asList(buttons);
    }

    public void update() {
        updateType();
        animation.update();
        updateButtons();
    }

    private void updateButtons() {
        boolean shouldUpdateButtonPositions = false;
        exportFeaturesButton.setEnabled(isExportFeaturesButtonEnabled());
        saveFeaturesButton.setEnabled(isSaveFeaturesButtonEnabled());
        undoButton.setEnabled(!UndoManager.isEmpty());
        if (playButton.isVisible() != isPlayButtonVisible()) {
            playButton.setVisible(isPlayButtonVisible());
            shouldUpdateButtonPositions = true;
        }
        String runTags = Util.tagsToString(DesignerEngine.runTags);
        playButton.setHint(!Util.isEmpty(runTags) ? "Play tags: " + runTags.replaceAll(",", " ") : "Play all features");
        tagsButton.setVisible(true);
        paletteButton.setVisible(true);
        timeglassButton.setVisible(true);
        if (terminalButton.isVisible() != isTerminalButtonVisible()) {
            terminalButton.setVisible(isTerminalButtonVisible());
            shouldUpdateButtonPositions = true;
        }
        if (shouldUpdateButtonPositions) {
            positionButtons();
        }
        for (Button button : buttons) {
            button.update();
        }
        for (Button button : pluginButtons) {
            button.update();
        }
        for (Button button : deviceButtons) {
            button.update();
        }
    }

    private boolean isExportFeaturesButtonEnabled() {
        return DesignerEngine.featuresRoot.children.size() > 0;
    }

    private boolean isSaveFeaturesButtonEnabled() {
        return DesignerEngine.featuresRoot.isLoaded;
    }

    private boolean isPlayButtonVisible() {
        return Engine.isPlayableDeviceEnabled();
    }

    private boolean isTerminalButtonVisible() {
        return deviceButtons != null && !deviceButtons.isEmpty();
    }

    private void updateType() {
        if (Player.isStarted() && type == TYPE_NORMAL) {
            type = TYPE_PLAYING;
            animation.moveAnimation.setRealPosition(Engine.windowWidth, 0, ANIMATION_MOVEMENT_SPEED);
            animation.colorAnimation.setColor(COLOR_BACKGROUND_PLAYING, ANIMATION_FADE_SPEED);
        } else if (!Player.isStarted() && type == TYPE_PLAYING) {
            type = TYPE_NORMAL;
            animation.moveAnimation.setRealPosition(0, 0, ANIMATION_MOVEMENT_SPEED);
            animation.colorAnimation.setColor(COLOR_BACKGROUND_NORMAL, ANIMATION_FADE_SPEED);
        }
        if (Player.isNotifiedStopped()) {
            stopButton.setImages(Images.getImage(Images.IMAGE_STOP, ThumbnailState.DISABLED.ordinal()), Images.getImage(Images.IMAGE_STOP, ThumbnailState.DISABLED.ordinal()), Images.getImage(Images.IMAGE_STOP, ThumbnailState.DISABLED.ordinal()));
        } else {
            stopButton.setImages(Images.getImage(Images.IMAGE_STOP, ThumbnailState.NORMAL.ordinal()), Images.getImage(Images.IMAGE_STOP, ThumbnailState.HIGHLIGHTED.ordinal()), Images.getImage(Images.IMAGE_STOP, ThumbnailState.PRESSED.ordinal()));
        }
        if (Player.isPaused()) {
            pauseButton.setImages(Images.getImage(Images.IMAGE_RESUME, ThumbnailState.NORMAL.ordinal()), Images.getImage(Images.IMAGE_RESUME, ThumbnailState.HIGHLIGHTED.ordinal()), Images.getImage(Images.IMAGE_RESUME, ThumbnailState.PRESSED.ordinal()));
        } else {
            if (Player.isPausable()) {
                pauseButton.setImages(Images.getImage(Images.IMAGE_PAUSE, ThumbnailState.NORMAL.ordinal()), Images.getImage(Images.IMAGE_PAUSE, ThumbnailState.HIGHLIGHTED.ordinal()), Images.getImage(Images.IMAGE_PAUSE, ThumbnailState.PRESSED.ordinal()));
            } else {
                pauseButton.setImages(Images.getImage(Images.IMAGE_PAUSE, ThumbnailState.DISABLED.ordinal()), Images.getImage(Images.IMAGE_PAUSE, ThumbnailState.DISABLED.ordinal()), Images.getImage(Images.IMAGE_PAUSE, ThumbnailState.DISABLED.ordinal()));
            }
        }
    }

    public void render(Graphics2D g) {
        calculatePosition();
        renderBackground(g);
        renderButtons(g);
        renderDevices(g);
    }

    private void calculatePosition() {
        renderWidth = Engine.windowWidth;
        renderHeight = BUTTONBAR_HEIGHT;
        renderX = (int) animation.moveAnimation.renderX;
        renderY = DesignerEngine.canvasHeight - renderHeight;
    }

    private void renderBackground(Graphics g) {
        g.setColor(animation.colorAnimation.getColor());
        if (!animation.moveAnimation.isMoving()) {
            g.fillRect(0, renderY, Engine.windowWidth, renderHeight);
        } else {
            g.fillRect(0, renderY, renderX, renderHeight);
            g.fillRect(renderX, renderY, Engine.windowWidth - renderX, renderHeight);
        }
        g.setColor(COLOR_BUTTON_DELIMITER);
        g.fillRect(renderX + iconButtonsX, renderY + 5, 1, renderHeight - 10);
        g.fillRect(renderX + pluginButtonsX - (BUTTON_PADDING * 2), renderY + 5, 1, renderHeight - 10);
    }

    private void renderButtons(Graphics g) {
        for (Button button : buttons) {
            button.setOffset(renderX, renderY);
        }
        restrictTerminalButtonToWindow();
        for (Button button : buttons) {
            button.render(g);
        }
        for (Button button : pluginButtons) {
            button.setOffset(renderX, renderY);
            button.render(g);
        }
    }

    private void restrictTerminalButtonToWindow() {
        int maxX = (deviceButtons.isEmpty() ? Engine.windowWidth : deviceButtons.get(deviceButtons.size() - 1).getRenderX()) - BUTTON_PADDING;
        if (terminalButton.getX() + terminalButton.getOffsetX() > maxX) {
            terminalButton.setOffset(maxX - terminalButton.getX(), renderY);
        }
    }

    private void renderDevices(Graphics2D g) {
        if (deviceButtons.isEmpty()) {
            g.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
            g.drawString(TEXT_NO_DEVICES, Engine.windowWidth - Engine.fontMetrics.stringWidth(TEXT_NO_DEVICES) - BUTTON_PADDING, renderY + ((renderHeight + Engine.fontMetrics.getHeight()) / 2) - 3);
        } else {
            for (DeviceButton button : deviceButtons) {
                button.setOffset(0, renderY);
                button.render(g);
                renderDeviceState(g, button);
            }
        }
    }

    private void renderDeviceState(Graphics2D g, DeviceButton button) {
        BufferedImage deviceStateImage = button.getDevice().isEnabled() ? deviceEnabledImage : deviceDisabledImage;
        int x = button.getRenderX() - (deviceStateImage.getWidth() / 2);
        int y = button.getRenderY() + button.getImageHeight() - 3 - (deviceStateImage.getHeight() / 2);
        g.drawImage(deviceStateImage, x, y, null);
        if (Player.isDeviceStarted(button.getDevice())) {
            Stroke oldStroke;
            if (Player.isDeviceRunning(button.getDevice())) {
                oldStroke = Animation.setStrokeAnimation(g, PLAY_ANIMATION_DASH_LENGTH, PLAY_ANIMATION_DASH_WIDTH, PLAY_ANIMATION_SPIN_SPEED);
            } else {
                if ((System.currentTimeMillis() % PLAY_ANIMATION_BLINK_SPEED) < (PLAY_ANIMATION_BLINK_SPEED / 2)) {
                    oldStroke = Animation.setStroke(g, PLAY_ANIMATION_DASH_LENGTH, PLAY_ANIMATION_DASH_WIDTH);
                } else {
                    return;
                }
            }
            g.setColor(Player.getPlayingColor(button.getDevice()));
            int width = deviceStateImage.getWidth() + 4;
            int height = deviceStateImage.getHeight() + 4;
            g.drawOval(x + ((deviceStateImage.getWidth() - width)  / 2), y + ((deviceStateImage.getHeight() - height) / 2), width, height);
            g.setStroke(oldStroke);
        }
    }

    public boolean click() {
        for (Button button : buttons) {
            if (button.click()) {
                return true;
            }
        }
        for (Button button : pluginButtons) {
            if (button.click()) {
                return true;
            }
        }
        for (DeviceButton button : deviceButtons) {
            if (type == TYPE_PLAYING && (!button.getDevice().isEnabled() || !button.getDevice().getCapabilities().contains(Device.Capability.STOP))) {
                continue;
            }
            if (button.click()) {
                return true;
            }
        }
        return DesignerEngine.isMouseInsideButtonBarArea();
    }

    public void setFailed() {
        animation.colorAnimation.setColor(COLOR_BACKGROUND_FAILED, ANIMATION_FADE_SPEED);
    }

    public List<Device> getDevices() {
        List<Device> devices = new LinkedList<Device>();
        for (DeviceButton button : deviceButtons) {
            devices.add(button.getDevice());
        }
        return devices;
    }
}
