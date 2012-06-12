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
import com.trollsahead.qcumberless.engine.DesignerEngine;
import com.trollsahead.qcumberless.engine.Engine;
import com.trollsahead.qcumberless.engine.FlashingMessageManager;
import com.trollsahead.qcumberless.engine.Player;
import com.trollsahead.qcumberless.model.ConsoleOutput;
import com.trollsahead.qcumberless.util.FileUtil;
import com.trollsahead.qcumberless.util.Util;

import static com.trollsahead.qcumberless.model.ConsoleOutput.LogLine;
import static com.trollsahead.qcumberless.gui.Animation.MoveAnimation;
import static com.trollsahead.qcumberless.gui.ExtendedButtons.DeviceButton;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class Terminal {
    private static final int BUTTON_PADDING_HORIZONTAL = ButtonBar.BUTTON_PADDING;
    private static final int BUTTON_PADDING_VERTICAL = 4;

    private static final Font TERMINAL_FONT = new Font("Courier New", Font.PLAIN, 13);
    private static final float TERMINAL_PROPORTIONAL_HEIGHT = 0.5f;

    private static final Color[] COLOR_TEXT = {new Color(0.4f, 1.0f, 0.4f), new Color(0.8f, 0.8f, 0.8f), new Color(0.8f, 0.8f, 0.8f)};
    private static final Color[] COLOR_BACKGROUND = {new Color(0.0f, 0.0f, 0.0f), new Color(0.5f, 0.1f, 0.1f), new Color(0.0f, 0.0f, 0.0f)};

    private static FontMetrics fontMetrics = null;

    private static final int PADDING_LEFT = 10;
    private static final int PADDING_TOP = 4;

    private static boolean visible = false;

    private static MoveAnimation position = new MoveAnimation();
    private static int scroll = -1;

    private static Device currentDevice = null;

    private static List<Button> buttons;
    private static List<DeviceButton> deviceButtons = new LinkedList<DeviceButton>();

    public static void initialize() {
        initButtons();
        resize();
    }

    private static void initButtons() {
        Button clearLogButton = new Button(
                0, 0,
                "Clear log",
                Button.ALIGN_HORIZONTAL_LEFT | Button.ALIGN_VERTICAL_BOTTOM,
                new Button.ButtonNotification() {
                    public void onClick() {
                        if (currentDevice == null) {
                            return;
                        }
                        currentDevice.getConsoleOutput().clearLog();
                        scroll = -1;
                    }
                },
                null);
        Button exportLogButton = new Button(
                0, 0,
                "Export log",
                Button.ALIGN_HORIZONTAL_LEFT | Button.ALIGN_VERTICAL_BOTTOM,
                new Button.ButtonNotification() {
                    public void onClick() {
                        if (currentDevice == null) {
                            return;
                        }
                        Date now = new Date();
                        File path = CucumberlessDialog.instance.askExportLogPath();
                        if (path != null) {
                            String filename = Util.convertSpacesToSlashes(currentDevice.name()) + "_" + FileUtil.prettyFilenameDateAndTime(now) + ".log";
                            currentDevice.getConsoleOutput().exportLog(
                                    FileUtil.addSlashToPath(path.getAbsolutePath()) + filename,
                                    ConsoleOutput.getPreample(currentDevice, now));
                            FlashingMessageManager.addMessage(new FlashingMessage("Log saved as '" + filename + "'", FlashingMessage.STANDARD_TIMEOUT));
                        }
                    }
                },
                null);
        buttons = new LinkedList<Button>();
        buttons.add(clearLogButton);
        buttons.add(exportLogButton);
    }

    public static void resize() {
        setPosition();
        position.setRenderPosition(position.realX, position.realY);
        positionButtons();
    }

    private static void positionButtons() {
        int y = getProportionalHeight() - BUTTON_PADDING_VERTICAL;
        int x = BUTTON_PADDING_HORIZONTAL;
        for (Button button : buttons) {
            button.setPosition(x, y);
            x += BUTTON_PADDING_HORIZONTAL + Engine.fontMetrics.stringWidth(button.toString());
        }
        x += BUTTON_PADDING_HORIZONTAL;
        for (DeviceButton button : deviceButtons) {
            button.setPosition(x, y);
            x += BUTTON_PADDING_HORIZONTAL + Engine.fontMetrics.stringWidth(button.toString());
        }
    }

    public static void update() {
        position.update(false);
        if (position.isMoving()) {
            DesignerEngine.updateRootPositions();
        }
        for (Button button : buttons) {
            button.update();
        }
        for (DeviceButton button : deviceButtons) {
            button.setMarked(button.getDevice() == currentDevice);
            button.update();
        }
    }

    public static void render(Graphics2D g) {
        int height = getHeight();
        if (height <= 0) {
            return;
        }
        g.setColor(Color.BLACK);
        g.fillRect(0, (int) position.renderY, Engine.windowWidth, height);
        drawText(g);
        for (Button button : buttons) {
            button.setOffset(0, (int) position.renderY);
            button.render(g);
        }
        for (Button button : deviceButtons) {
            button.setOffset(0, (int) position.renderY);
            button.render(g);
        }
    }

    private static void drawText(Graphics2D g) {
        if (currentDevice == null) {
            return;
        }
        Font oldFont = g.getFont();
        g.setFont(TERMINAL_FONT);
        if (fontMetrics == null) {
            fontMetrics = g.getFontMetrics();
        }

        List<LogLine> output = getConsoleOutput();

        int rows = getNumberOfRows();
        int y = (int) position.renderY + PADDING_TOP;
        int scrollValue = scroll == -1 ? 0 : scroll;
        for (int i = 0; i < rows; i++) {
            int idx = output.size() - rows + i - scrollValue;
            if (idx < 0 || idx >= output.size()) {
                continue;
            }
            int oldY = y;
            y += fontMetrics.getHeight();
            if (y > Engine.windowHeight) {
                break;
            }
            LogLine logLine = output.get(idx);
            int playStateIndex = getPlayStateIndex(logLine);
            if (playStateIndex == 1) {
                g.setColor(COLOR_BACKGROUND[playStateIndex]);
                g.fillRect(0, oldY + 3, Engine.windowWidth, fontMetrics.getHeight());
            }
            g.setColor(COLOR_TEXT[playStateIndex]);
            g.drawString(logLine.log, PADDING_LEFT, y);
        }
        g.setFont(oldFont);
    }

    private static int getPlayStateIndex(LogLine logLine) {
        if (logLine.element != null) {
            if (logLine.element.getPlayResult().isFailed()) {
                return 1;
            } else if (logLine.element.getPlayResult().isSuccess()) {
                return 0;
            }
        }
        return 2;
    }

    public static void toggleTerminal() {
        if (visible) {
            hideTerminal();
        } else {
            showTerminal();
        }
    }

    private static void showTerminal() {
        visible = true;
        setPosition();
        if (currentDevice == null) {
            activateDevice();
        }
    }
    
    private static void activateDevice() {
        List<Device> devices = Player.getStartedDevices();
        if (devices != null && !devices.isEmpty()) {
            currentDevice = Player.getStartedDevices().get(0);
        } else {
            currentDevice = ButtonBar.instance.getDevices().get(0);
        }
    }

    private static void hideTerminal() {
        visible = false;
        setPosition();
    }

    private static void setPosition() {
        if (visible) {
            position.setRealPosition(0.0f, Engine.windowHeight - getProportionalHeight(), Animation.MOVEMENT_SPEED_TERMINAL);
        } else {
            position.setRealPosition(0.0f, Engine.windowHeight + 1.0f, Animation.MOVEMENT_SPEED_TERMINAL);
        }
    }

    public static int getHeight() {
        return Math.max(Engine.windowHeight - (int) position.renderY, 0);
    }
    
    private static int getProportionalHeight() {
        return (int) ((float) Engine.windowHeight * TERMINAL_PROPORTIONAL_HEIGHT);
    }
    
    private static int getNumberOfRows() {
        return (getProportionalHeight() / fontMetrics.getHeight()) - 2;
    }

    private static List<LogLine> getConsoleOutput() {
        ConsoleOutput console = currentDevice.getConsoleOutput();
        return console.getTextWrappedLog(Engine.windowWidth, fontMetrics);
    }

    private static boolean hasDevices() {
        List<Device> deviceList = ButtonBar.instance.getDevices();
        return deviceList != null && !deviceList.isEmpty();
    }

    private static List<String> getDevicesAsList() {
        List<Device> deviceList = ButtonBar.instance.getDevices();
        if (deviceList == null || deviceList.isEmpty()) {
            return null;
        }
        String[] devices = new String[deviceList.size()];
        for (int i = 0; i < deviceList.size(); i++) {
            devices[i] = deviceList.get(i).name();
        }
        Arrays.sort(devices);
        return Arrays.asList(devices);
    }

   public static void scroll(int unitsToScroll) {
        int lastIndex = getConsoleOutput().size() - getNumberOfRows();
        if (scroll == -1) {
            scroll = 0;
        }
        scroll -= unitsToScroll;
        scroll = Math.max(0, Math.min(lastIndex, scroll));
    }

    public static void mouseDragged() {
    }

    public static boolean click() {
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

    public static void updateDevices(Set<Device> devices) {
        deviceButtons = new LinkedList<DeviceButton>();
        for (final Device device : devices) {
            DeviceButton button = new DeviceButton(
                    0,
                    0,
                    device.name(),
                    Button.ALIGN_HORIZONTAL_LEFT | Button.ALIGN_VERTICAL_BOTTOM,
                    new Button.ButtonNotification() {
                        public void onClick() {
                            currentDevice = device;
                            scroll = -1;
                        }
                    },
                    device);
            deviceButtons.add(button);
        }
        sortDeviceButtons();
        positionButtons();
    }

    private static void sortDeviceButtons() {
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
}
