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
//
// Copyright 2012

// Daniel Andersen (dani_ande@yahoo.dk)

package com.trollsahead.qcumberless.gui;

import com.trollsahead.qcumberless.device.Device;
import com.trollsahead.qcumberless.engine.Engine;
import com.trollsahead.qcumberless.engine.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ButtonBar {
    public static final int TYPE_NORMAL  = 0;
    public static final int TYPE_PLAYING = 1;

    public static final int BUTTONBAR_HEIGHT = 30;
    private static final int BUTTON_PADDING = 10 + Button.TEXT_BACKGROUND_PADDING_HORIZONTAL * 2;
    private static final int DEVICE_BUTTON_WIDTH = 30;

    private static final float ANIMATION_MOVEMENT_SPEED = 0.8f;
    private static final float ANIMATION_FADE_SPEED = 0.05f;

    private static final float[] COLOR_BACKGROUND_NORMAL = {0.0f, 0.0f, 0.0f, 0.6f};
    private static final float[] COLOR_BACKGROUND_PLAYING = {0.0f, 0.3f, 0.0f, 0.8f};
    private static final float[] COLOR_BACKGROUND_FAILED = {0.5f, 0.0f, 0.0f, 0.8f};

    private static final String TEXT_NO_DEVICES = "No devices found";

    private Button importStepsButton;
    private Button scratchFeaturesButton;
    private Button importFeaturesButton;
    private Button exportFeaturesButton;
    private Button saveFeaturesButton;
    private Button closeButton;
    private Button pauseButton;
    private Button stopButton;
    private List<Button> buttons;

    private List<DeviceButton> deviceButtons;

    public int renderX;
    public int renderY;
    public int renderWidth;
    public int renderHeight;

    private Animation animation;

    private static int type = TYPE_NORMAL;

    public static ButtonBar instance = null;

    private static BufferedImage deviceEnabledImage;
    private static BufferedImage deviceDisabledImage;

    static {
        try {
            deviceEnabledImage = ImageIO.read(ButtonBar.class.getResource("/pictures/device_enabled.png"));
            deviceDisabledImage = ImageIO.read(ButtonBar.class.getResource("/pictures/device_disabled.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ButtonBar() {
        instance = this;
        animation = new Animation(COLOR_BACKGROUND_NORMAL);
        deviceButtons = new LinkedList<DeviceButton>();
        buttons = new LinkedList<Button>();
        pauseButton = new Button(
                0, 0,
                Images.getImage(Images.IMAGE_PAUSE, Images.TYPE_NORMAL),
                Images.getImage(Images.IMAGE_PAUSE, Images.TYPE_HIGHLIGHT),
                Images.getImage(Images.IMAGE_PAUSE, Images.TYPE_NORMAL),
                Button.ALIGN_HORIZONTAL_CENTER | Button.ALIGN_VERTICAL_BOTTOM,
                new Button.CucumberButtonNotification() {
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
                Images.getImage(Images.IMAGE_STOP, Images.TYPE_NORMAL),
                Images.getImage(Images.IMAGE_STOP, Images.TYPE_HIGHLIGHT),
                Images.getImage(Images.IMAGE_STOP, Images.TYPE_NORMAL),
                Button.ALIGN_HORIZONTAL_CENTER | Button.ALIGN_VERTICAL_BOTTOM,
                new Button.CucumberButtonNotification() {
                    public void onClick() {
                        Player.stop();
                    }
                },
                null);
        buttons.add(stopButton);
        importStepsButton = new Button(
                0, 0,
                "Import step definitions",
                Button.ALIGN_HORIZONTAL_LEFT | Button.ALIGN_VERTICAL_CENTER,
                new Button.CucumberButtonNotification() {
                    public void onClick() {
                        Engine.importSteps();
                    }
                },
                null);
        buttons.add(importStepsButton);
        scratchFeaturesButton = new Button(
                0, 0,
                "Scratch",
                Button.ALIGN_HORIZONTAL_LEFT | Button.ALIGN_VERTICAL_CENTER,
                new Button.CucumberButtonNotification() {
                    public void onClick() {
                        if (Engine.spotlight.visible && "black knight mode".equalsIgnoreCase(Engine.spotlight.searchString)) {
                            EasterEgg.show();
                        } else {
                            Engine.scratchFeatures();
                        }
                    }
                },
                null);
        buttons.add(scratchFeaturesButton);
        importFeaturesButton = new Button(
                0, 0,
                "Import",
                Button.ALIGN_HORIZONTAL_LEFT | Button.ALIGN_VERTICAL_CENTER,
                new Button.CucumberButtonNotification() {
                    public void onClick() {
                        File[] files = CucumberlessDialog.instance.fileChooser();
                        if (files != null) {
                            Engine.importFeatures(files);
                        }
                    }
                },
                null);
        buttons.add(importFeaturesButton);
        exportFeaturesButton = new Button(
                0, 0,
                "Export",
                Button.ALIGN_HORIZONTAL_LEFT | Button.ALIGN_VERTICAL_CENTER,
                new Button.CucumberButtonNotification() {
                    public void onClick() {
                        File directory = CucumberlessDialog.instance.directoryChooser();
                        if (directory != null) {
                            Engine.exportFeatures(directory);
                        }
                    }
                },
                null);
        buttons.add(exportFeaturesButton);
        saveFeaturesButton = new Button(
                0, 0,
                "Save",
                Button.ALIGN_HORIZONTAL_LEFT | Button.ALIGN_VERTICAL_CENTER,
                new Button.CucumberButtonNotification() {
                    public void onClick() {
                        Engine.saveFeatures();
                    }
                },
                null);
        buttons.add(saveFeaturesButton);
        closeButton = new Button(
                0, 0,
                "Quit",
                Button.ALIGN_HORIZONTAL_LEFT | Button.ALIGN_VERTICAL_CENTER,
                new Button.CucumberButtonNotification() {
                    public void onClick() {
                        Engine.stop();
                        CucumberlessDialog.instance.setVisible(false);
                    }
                },
                null);
        buttons.add(closeButton);
        animation.moveAnimation.setRealPosition(0, 0);
        animation.moveAnimation.setRenderPosition(0, 0);
    }

    public void resize(Graphics g) {
        FontMetrics fontMetrics = g.getFontMetrics();
        int x = BUTTON_PADDING;
        scratchFeaturesButton.setPosition(x, BUTTONBAR_HEIGHT / 2);
        x += BUTTON_PADDING + fontMetrics.stringWidth(scratchFeaturesButton.toString());
        importFeaturesButton.setPosition(x, BUTTONBAR_HEIGHT / 2);
        x += BUTTON_PADDING + fontMetrics.stringWidth(importFeaturesButton.toString());
        exportFeaturesButton.setPosition(x, BUTTONBAR_HEIGHT / 2);
        x += BUTTON_PADDING + fontMetrics.stringWidth(exportFeaturesButton.toString());
        saveFeaturesButton.setPosition(x, BUTTONBAR_HEIGHT / 2);
        x += BUTTON_PADDING + fontMetrics.stringWidth(saveFeaturesButton.toString());
        x += BUTTON_PADDING;
        importStepsButton.setPosition(x, BUTTONBAR_HEIGHT / 2);
        x += BUTTON_PADDING + fontMetrics.stringWidth(importStepsButton.toString());
        x += BUTTON_PADDING;
        closeButton.setPosition(x, BUTTONBAR_HEIGHT / 2);
        pauseButton.setPosition((Engine.canvasWidth / 2) - Engine.canvasWidth - 30, BUTTONBAR_HEIGHT);
        stopButton.setPosition((Engine.canvasWidth / 2) - Engine.canvasWidth + 30, BUTTONBAR_HEIGHT);
        positionDeviceButtons();
    }

    public void updateDevices(Set<Device> devices) {
        deviceButtons = new LinkedList<DeviceButton>();
        for (final Device device : devices) {
            DeviceButton button = new DeviceButton(
                    0, 0,
                    device.getThumbnail(Device.ThumbnailState.NORMAL),
                    device.getThumbnail(Device.ThumbnailState.HIGHLIGHTED),
                    device.getThumbnail(Device.ThumbnailState.PRESSED),
                    Button.ALIGN_HORIZONTAL_CENTER | Button.ALIGN_VERTICAL_CENTER,
                    new Button.CucumberButtonNotification() {
                        public void onClick() {
                            if (device.isEnabled()) {
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
        positionDeviceButtons();
    }

    private void positionDeviceButtons() {
        int x = Engine.canvasWidth - BUTTON_PADDING;
        for (Button button : deviceButtons) {
            button.setPosition(x - (DEVICE_BUTTON_WIDTH / 2), BUTTONBAR_HEIGHT / 2);
            x -= button.getImageWidth() + BUTTON_PADDING;
        }
    }

    public void update() {
        updateType();
        animation.update();
        updateButtons();
    }

    private void updateButtons() {
        exportFeaturesButton.setEnabled(isExportFeaturesButtonEnabled());
        saveFeaturesButton.setEnabled(isSaveFeaturesButtonEnabled());
        importStepsButton.setEnabled(isImportStepDefinitionsButtonEnabled());
        for (Button button : buttons) {
            button.update();
        }
        for (Button button : deviceButtons) {
            button.update();
        }
    }

    private boolean isExportFeaturesButtonEnabled() {
        return Engine.featuresRoot.children.size() > 0;
    }

    private boolean isSaveFeaturesButtonEnabled() {
        return Engine.featuresRoot.isLoaded;
    }

    private boolean isImportStepDefinitionsButtonEnabled() {
        return Engine.devices.size() > 0;
    }

    private void updateType() {
        if (Player.isRunning() && type == TYPE_NORMAL) {
            type = TYPE_PLAYING;
            animation.moveAnimation.setRealPosition(Engine.canvasWidth, 0, ANIMATION_MOVEMENT_SPEED);
            animation.colorAnimation.setColor(COLOR_BACKGROUND_PLAYING, ANIMATION_FADE_SPEED);
        } else if (!Player.isRunning() && type == TYPE_PLAYING) {
            type = TYPE_NORMAL;
            animation.moveAnimation.setRealPosition(0, 0, ANIMATION_MOVEMENT_SPEED);
            animation.colorAnimation.setColor(COLOR_BACKGROUND_NORMAL, ANIMATION_FADE_SPEED);
        }
        if (!Player.isPaused()) {
            pauseButton.setImages(Images.getImage(Images.IMAGE_PAUSE, Images.TYPE_NORMAL), Images.getImage(Images.IMAGE_PAUSE, Images.TYPE_HIGHLIGHT), Images.getImage(Images.IMAGE_PAUSE, Images.TYPE_NORMAL));
        } else {
            pauseButton.setImages(Images.getImage(Images.IMAGE_RESUME, Images.TYPE_NORMAL), Images.getImage(Images.IMAGE_RESUME, Images.TYPE_HIGHLIGHT), Images.getImage(Images.IMAGE_RESUME, Images.TYPE_NORMAL));
        }
    }

    public void render(Graphics g) {
        calculatePosition();
        renderBackground(g);
        renderButtons(g);
        renderDevices(g);
    }

    private void calculatePosition() {
        renderWidth = Engine.canvasWidth;
        renderHeight = BUTTONBAR_HEIGHT;
        renderX = (int) animation.moveAnimation.renderX;
        renderY = Engine.canvasHeight - renderHeight;
    }

    private void renderBackground(Graphics g) {
        g.setColor(animation.colorAnimation.getColor());
        if (!animation.moveAnimation.isMoving()) {
            g.fillRect(0, renderY, Engine.canvasWidth, renderHeight);
        } else {
            g.fillRect(0, renderY, renderX, renderHeight);
            g.fillRect(renderX, renderY, Engine.canvasWidth - renderX, renderHeight);
        }
    }

    private void renderButtons(Graphics g) {
        for (Button button : buttons) {
            button.setOffset(renderX, renderY);
            button.render(g);
        }
    }
    
    private void renderDevices(Graphics g) {
        if (deviceButtons.size() == 0) {
            FontMetrics fontMetrics = g.getFontMetrics();
            g.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
            g.drawString(TEXT_NO_DEVICES, Engine.canvasWidth - fontMetrics.stringWidth(TEXT_NO_DEVICES) - BUTTON_PADDING, renderY + ((renderHeight + fontMetrics.getHeight()) / 2) - 3);
        } else {
            for (DeviceButton button : deviceButtons) {
                button.setOffset(renderX, renderY);
                button.render(g);
                BufferedImage image = button.getDevice().isEnabled() ? deviceEnabledImage : deviceDisabledImage;
                int x = button.getRenderX() + button.getImageWidth() - 3 - (image.getWidth() / 2);
                int y = button.getRenderY() + button.getImageHeight() - 3 - (image.getHeight() / 2);
                g.drawImage(image, x, y, null);
            }
        }
    }
    
    public boolean click() {
        for (Button button : buttons) {
            if (button.click()) {
                return true;
            }
        }
        for (Button button : deviceButtons) {
            if (button.click()) {
                return true;
            }
        }
        return false;
    }

    public void setFailed() {
        animation.colorAnimation.setColor(COLOR_BACKGROUND_FAILED, ANIMATION_FADE_SPEED);
    }

    private class DeviceButton extends Button {
        private Device device;

        public DeviceButton(int x, int y, Image normalImage, Image highlightImage, Image pressedImage, int alignment, CucumberButtonNotification notification, Device device) {
            super(x, y, normalImage, highlightImage, pressedImage, alignment, notification, null);
            this.device = device;
        }
        
        public Device getDevice() {
            return device;
        }
        
        public int getRenderX() {
            return super.renderX;
        }

        public int getRenderY() {
            return super.renderY;
        }
    }
}
