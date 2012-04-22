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
import com.trollsahead.qcumberless.device.CucumberDeviceCallback;
import com.trollsahead.qcumberless.gui.CucumberTextElement;
import com.trollsahead.qcumberless.model.CucumberStep;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class CucumberPlayer implements CucumberDeviceCallback {
    private static final long MESSAGE_TIMEOUT_PLAYER = 5L * 1000L;
    private static final long MESSAGE_ANIMATION_SPEED = 300;
    private static final int MESSAGE_WIDTH = 200;
    private static final int MESSAGE_HEIGHT = 25;

    private static final String TEXT_SUCCESS = "SUCCESS!";
    private static final String TEXT_FAILURE = "FAILURE!";

    private static final Color COLOR_SUCCESS = new Color(0x88FF88);
    private static final Color COLOR_FAILURE = new Color(0x990000);

    public static long messageTimeout = 0;

    public CucumberTextElement currentFeature = null;
    public CucumberTextElement currentScenario = null;
    public CucumberTextElement currentStep = null;

    private int stepIndex;

    public boolean isRunning;
    public boolean isPaused;
    public boolean isStopped;

    private boolean success;

    private CucumberDevice device;

    private static boolean hasDeviceFailures = false;

    public static Set<CucumberPlayer> players = new HashSet<CucumberPlayer>();

    public static void prepareRun() {
        hasDeviceFailures = false;
    }

    public CucumberPlayer() {
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

    public static boolean isRunning() {
        for (CucumberPlayer player : players) {
            if (player.isRunning) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPaused() {
        for (CucumberPlayer player : players) {
            if (player.isPaused) {
                return true;
            }
        }
        return false;
    }

    public static boolean isStopped() {
        for (CucumberPlayer player : players) {
            if (player.isStopped) {
                return true;
            }
        }
        return false;
    }

    public void play(final StringBuilder feature, final CucumberDevice device) {
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
        for (final CucumberPlayer player : players) {
            new Thread(new Runnable() {
                public void run() {
                    player.device.pause();
                }
            }).start();
        }
    }

    public static void resume() {
        for (final CucumberPlayer player : players) {
            new Thread(new Runnable() {
                public void run() {
                    player.device.resume();
                }
            }).start();
        }
    }

    public static void stop() {
        for (final CucumberPlayer player : players) {
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
        for (CucumberPlayer player : players) {
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

        int x = (CucumberEngine.canvasWidth - WIDTH) / 2;
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
        CucumberEngine.cucumberRoot.clearFailedStatus();
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
        currentFeature = (CucumberTextElement) CucumberEngine.featuresRoot.findChild(name);
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
            currentScenario = (CucumberTextElement) currentFeature.findChild(name);
            currentStep = (CucumberTextElement) currentFeature.firstChildOfType(CucumberTextElement.TYPE_STEP);
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
        if (currentScenario != null && !(stepIndex == -1 && "I see that I'm on the \"bank\" page".equals(name))) { // TODO! Background
            currentStep = (CucumberTextElement) currentScenario.findChildFromIndex(name, stepIndex + 1);
            stepIndex = currentScenario.findChildIndex(currentStep);
        }
    }

    public void afterStep(String name) {
    }

    public void afterStepFailed(String errorMessage) {
        failure(errorMessage);
    }

    public void attachScreenshots(CucumberStep step, Image... screenshots) {
        currentStep.setErrorScreenshots(screenshots);
    }

    public CucumberStep getCurrentStep() {
        return currentStep != null ? currentStep.step : null;
    }

    private void failure() {
        failure(null);
    }
    
    private void failure(String errorMessage) {
        success = false;
        hasDeviceFailures = true;
        CucumberEngine.buttonBar.setFailed();
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

    public static boolean isCurrentFeature(CucumberTextElement element) {
        for (CucumberPlayer player : players) {
            if (player.currentFeature == element) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCurrentScenario(CucumberTextElement element) {
        for (CucumberPlayer player : players) {
            if (player.currentScenario == element) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCurrentStep(CucumberTextElement element) {
        for (CucumberPlayer player : players) {
            if (player.currentStep == element) {
                return true;
            }
        }
        return false;
    }
}
