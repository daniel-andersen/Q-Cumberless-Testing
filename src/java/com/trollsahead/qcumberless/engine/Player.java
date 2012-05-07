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

package com.trollsahead.qcumberless.engine;

import com.trollsahead.qcumberless.device.Device;
import com.trollsahead.qcumberless.device.DeviceCallback;
import com.trollsahead.qcumberless.gui.Element;
import com.trollsahead.qcumberless.gui.TextElement;
import com.trollsahead.qcumberless.model.Step;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Player implements DeviceCallback {
    private static final Set<Color> PLAYING_COLORS = new HashSet<Color>();

    private static final long MESSAGE_TIMEOUT_PLAYER = 5L * 1000L;
    private static final long MESSAGE_ANIMATION_SPEED = 300;
    private static final int MESSAGE_WIDTH = 200;
    private static final int MESSAGE_HEIGHT = 25;

    private static final String TEXT_SUCCESS = "SUCCESS!";
    private static final String TEXT_FAILURE = "FAILURE!";

    private static final Color COLOR_SUCCESS = new Color(0x88FF88);
    private static final Color COLOR_FAILURE = new Color(0x990000);

    public static long messageTimeout = 0;

    public TextElement currentFeature = null;
    public TextElement currentScenario = null;
    public TextElement currentStep = null;

    private int stepIndex;

    public boolean isRunning;
    public boolean isPaused;
    public boolean isStopped;

    private boolean success;

    private Device device;

    private Color runningColor = Color.GRAY;

    private static boolean hasDeviceFailures = false;

    public static Set<Player> players = new HashSet<Player>();

    static {
        PLAYING_COLORS.add(Color.WHITE);
        PLAYING_COLORS.add(Color.YELLOW);
        PLAYING_COLORS.add(Color.PINK);
    }
    
    public static void prepareRun() {
        hasDeviceFailures = false;
    }

    public Player() {
        runningColor = getUnusedPlayingColor();
        players.add(this);
        reset();
    }

    protected void finalize() throws Throwable {
        cleanup();
        super.finalize();
    }
    
    private void cleanup() {
        players.remove(this);
    }

    public static boolean isPlaying() {
        for (Player player : players) {
            if (player.isRunning) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPaused() {
        for (Player player : players) {
            if (player.isPaused) {
                return true;
            }
        }
        return false;
    }

    public static boolean isStopped() {
        for (Player player : players) {
            if (player.isStopped) {
                return true;
            }
        }
        return false;
    }

    public void play(final StringBuilder feature, final Device device) {
        this.device = device;
        device.setDeviceCallback(this);
        new Thread(new Runnable() {
            public void run() {
                success = true;
                device.play(feature);
                cleanup();
            }
        }).start();
    }

    public static void pause() {
        for (final Player player : players) {
            new Thread(new Runnable() {
                public void run() {
                    player.device.pause();
                }
            }).start();
        }
    }

    public static void resume() {
        for (final Player player : players) {
            new Thread(new Runnable() {
                public void run() {
                    player.device.resume();
                }
            }).start();
        }
    }

    public static void stop() {
        for (final Player player : players) {
            new Thread(new Runnable() {
                public void run() {
                    player.device.stop();
                }
            }).start();
        }
    }

    private void reset() {
        isRunning = false;
        isStopped = false;
        currentFeature = null;
        currentScenario = null;
        currentStep = null;
        stepIndex = -1;
    }

    private static boolean hasFailures() {
        for (Player player : players) {
            if (!player.success) {
                return true;
            }
        }
        return false;
    }

    public static void render(Graphics g) {
        if (System.currentTimeMillis() > messageTimeout) {
            return;
        }

        FontMetrics fontMetrics = g.getFontMetrics();
        
        String text = hasDeviceFailures ? TEXT_FAILURE : TEXT_SUCCESS;

        final int WIDTH = MESSAGE_WIDTH;
        final int HEIGHT = MESSAGE_HEIGHT;

        int x = (Engine.canvasWidth - WIDTH) / 2;
        int y = 20;
        
        int textX = x + (WIDTH - fontMetrics.stringWidth(text)) / 2;
        int textY = y + (HEIGHT - fontMetrics.getHeight()) / 2 - 3;

        g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.3f));
        g.fillRoundRect(x, y, WIDTH, HEIGHT, 10, 10);

        g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.5f));
        g.drawRoundRect(x, y, WIDTH, HEIGHT, 10, 10);

        if (System.currentTimeMillis() % (MESSAGE_ANIMATION_SPEED * 2) < MESSAGE_ANIMATION_SPEED) {
            g.setColor(hasDeviceFailures ? COLOR_FAILURE : COLOR_SUCCESS);
        } else {
            return;
        }
        g.drawString(text, textX, textY + fontMetrics.getHeight());
    }

    public void onPlay() {
        isRunning = true;
        success = true;
        Engine.cucumberRoot.clearFailedStatus();
    }

    public void onPause() {
        isPaused = true;
    }

    public void onResume() {
        isPaused = false;
    }

    public void onStop() {
        isStopped = true;
    }

    public void afterPlayed() {
        reset();
        messageTimeout = System.currentTimeMillis() + MESSAGE_TIMEOUT_PLAYER;
    }

    public void afterPlayFailed(String errorMessage) {
        failure();
        reset();
        messageTimeout = System.currentTimeMillis() + MESSAGE_TIMEOUT_PLAYER;
    }

    public void beforeFeatures() {
    }

    public void beforeFeature(String name) {
        currentFeature = (TextElement) Engine.featuresRoot.findChild(name);
        System.out.println("Starting feature: '" + name + "'");
    }

    public void afterFeature() {
    }

    public void afterFeatureFailed() {
        failure();
    }

    public void beforeScenario(String name) {
        System.out.println("Starting scenario: '" + name + "'");
        if (currentFeature != null) {
            currentScenario = (TextElement) currentFeature.findChild(name);
            currentStep = (TextElement) currentFeature.firstChildOfType(TextElement.TYPE_STEP);
            stepIndex = currentFeature.findChildIndex(currentStep);
        } else {
            currentScenario = null;
            stepIndex = -1;
        }
    }

    public void afterScenario() {
    }

    public void afterScenarioFailed() {
        failure();
    }

    public void beforeBackground(String name) {
    }

    public void afterBackground() {
    }

    public void afterBackgroundFailed(String errorMessage) {
        failure(errorMessage);
    }

    public void beforeStep(String name) {
        System.out.println("Running step: '" + name + "'");
        if (currentScenario != null) {
            Element background = Engine.findBackgroundElement(currentScenario.groupParent);
            if (!(stepIndex == -1 && background != null && name.equals(background.toString()))) {
                currentStep = (TextElement) currentScenario.findChildFromIndex(name, stepIndex + 1);
                stepIndex = currentScenario.findChildIndex(currentStep);
            }
        }
    }

    public void afterStep(String name) {
    }

    public void afterStepFailed(String errorMessage) {
        failure(errorMessage);
    }

    public void attachScreenshots(Step step, Image... screenshots) {
        currentStep.setErrorScreenshots(screenshots);
    }

    public Step getCurrentStep() {
        return currentStep != null ? currentStep.step : null;
    }

    private void failure() {
        failure(null);
    }
    
    private void failure(String errorMessage) {
        success = false;
        hasDeviceFailures = true;
        Engine.buttonBar.setFailed();
        if (currentStep != null && errorMessage != null) {
            currentStep.setFailed();
            currentStep.setErrorMessage(errorMessage);
        }
        if (currentScenario != null) {
            currentScenario.setFailed();
        }
        if (currentFeature != null) {
            currentFeature.setFailed();
        }
    }

    private boolean isRunningElement(TextElement element) {
        return (element.type == TextElement.TYPE_FEATURE && currentFeature == element) ||
                //(element.type == TextElement.TYPE_BACKGROUND && currentBackground == element) ||
                (element.type == TextElement.TYPE_SCENARIO && currentScenario == element) ||
                (element.type == TextElement.TYPE_STEP && currentStep == element);
    }
    
    public static boolean isCurrentFeature(TextElement element) {
        for (Player player : players) {
            if (player.currentFeature == element) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCurrentScenario(TextElement element) {
        for (Player player : players) {
            if (player.currentScenario == element) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCurrentStep(TextElement element) {
        for (Player player : players) {
            if (player.currentStep == element) {
                return true;
            }
        }
        return false;
    }

    public static Color getPlayingColor(TextElement element) {
        for (Player player : players) {
            if (player.isRunningElement(element)) {
                return player.runningColor;
            }
        }
        return Color.GRAY;
    }

    public static Color getPlayingColor(Device device) {
        for (Player player : players) {
            if (player.device == device) {
                return player.runningColor;
            }
        }
        return Color.GRAY;
    }

    public static boolean isDeviceRunning(Device device) {
        for (Player player : players) {
            if (player.device == device) {
                return player.isRunning;
            }
        }
        return false;
    }
    
    private static Color getUnusedPlayingColor() {
        Set<Color> colors = new HashSet<Color>();
        colors.addAll(PLAYING_COLORS);
        for (Player player : players) {
            colors.remove(player.runningColor);
        }
        if (colors.size() > 0) {
            return colors.iterator().next();
        } else {
            return PLAYING_COLORS.iterator().next();
        }
    }
}
