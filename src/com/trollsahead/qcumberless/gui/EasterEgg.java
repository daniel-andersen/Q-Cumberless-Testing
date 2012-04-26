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

import com.trollsahead.qcumberless.engine.Engine;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EasterEgg {
    private static final int BACKGROUND_ANIMATION_DELAY = 20;
    private static final int BACKGROUND_ANIMATION_SIZE = 20;

    public static int animation = 0;
    public static boolean enabled = false;

    private static List<TextElement> customElements = null;

    private static final float STICK_ANGLE_CUTOFF = 0.2f;

    private static final int STICK_WIDTH  = 10;
    private static final int STICK_HEIGHT = 150;

    private static int stickX;
    private static int stickY;

    private static final int BALL_SIZE  = 10;
    private static final float BALL_SPEED = 0.9f;

    private static boolean backgroundCoveringCanvas;

    private static long updateTime;
    private static int updateCount;

    private static final int BALL_TRACE_LENGTH = 20;
    private static final int BALL_TRACE_DELAY = 5;

    private static int ballTraceUpdateDelay;
    private static int[] ballTraceX;
    private static int[] ballTraceY;

    private static float ballX;
    private static float ballY;
    private static float ballVelX;
    private static float ballVelY;
    private static float ballSpeed;
    private static boolean ballSticky;

    private static Cursor savedCursor;

    public static void update() {
        if (!enabled) {
            return;
        }
        updateElements();
        updateSpeed();
        updateStick();
        updateBall();
        updateCount++;
    }

    private static void updateElements() {
        if (Engine.featuresRoot.children.size() == 0 && customElements.size() == 0) {
            createCustomElements();
        }
        for (Element element : customElements) {
            element.update(System.currentTimeMillis());
        }
    }

    private static void createCustomElements() {
        ballSticky = true;

        customElements = new LinkedList<TextElement>();
        customElements.add(createElement(TextElement.TYPE_SCENARIO, 0, 0, 200, "You fight with the strength of many men, Sir Knight."));
        customElements.add(createElement(TextElement.TYPE_SCENARIO, 0, 0, 200, "I am Arthur, King of the Britons."));
        customElements.add(createElement(TextElement.TYPE_SCENARIO, 0, 0, 200, "I seek the finest and the bravest knights in the land to join me in my court at Camelot."));
        customElements.add(createElement(TextElement.TYPE_SCENARIO, 0, 0, 200, "You have proved yourself worthy. Will you join me?"));
        customElements.add(createElement(TextElement.TYPE_SCENARIO, 0, 0, 200, "You make me sad. So be it. Come, Patsy."));
        customElements.add(createElement(TextElement.TYPE_COMMENT,  0, 0, 200, "None shall pass."));
        customElements.add(createElement(TextElement.TYPE_SCENARIO, 0, 0, 200, "What?"));
        customElements.add(createElement(TextElement.TYPE_COMMENT,  0, 0, 200, "None shall pass."));
        customElements.add(createElement(TextElement.TYPE_SCENARIO, 0, 0, 200, "I have no quarrel with you, good Sir Knight, but I must cross this bridge."));
        customElements.add(createElement(TextElement.TYPE_COMMENT,  0, 0, 200, "Then you shall die."));
        customElements.add(createElement(TextElement.TYPE_SCENARIO, 0, 0, 200, "I command you, as King of the Britons, to stand aside!"));
        customElements.add(createElement(TextElement.TYPE_COMMENT,  0, 0, 200, "I move for no man."));
        customElements.add(createElement(TextElement.TYPE_SCENARIO, 0, 0, 200, "So be it!"));

        customElements.add(createElement(TextElement.TYPE_STEP,     0, 0, 200, "Aaah!, hiyaah!"));

        customElements.add(createElement(TextElement.TYPE_SCENARIO, 0, 0, 200, "Now stand aside, worthy adversary."));
        customElements.add(createElement(TextElement.TYPE_COMMENT,  0, 0, 200, "'Tis but a scratch."));
        customElements.add(createElement(TextElement.TYPE_SCENARIO, 0, 0, 200, "A scratch? Your arm's off!"));
        customElements.add(createElement(TextElement.TYPE_COMMENT,  0, 0, 200, "No, it isn't."));
        customElements.add(createElement(TextElement.TYPE_SCENARIO, 0, 0, 200, "Well, what's that, then?"));
        customElements.add(createElement(TextElement.TYPE_COMMENT,  0, 0, 200, "I've had worse."));

        customElements.add(createElement(TextElement.TYPE_SCENARIO, 0, 0, 200, "You liar!"));
        customElements.add(createElement(TextElement.TYPE_SCENARIO, 0, 0, 200, "Come on, you pansy!"));

        customElements.add(createElement(TextElement.TYPE_STEP,     0, 0, 200, "Huyah!"));
        customElements.add(createElement(TextElement.TYPE_STEP,     0, 0, 200, "Hiyaah!"));
        customElements.add(createElement(TextElement.TYPE_STEP,     0, 0, 200, "Aaaaaaaah!"));
        customElements.add(createElement(TextElement.TYPE_STEP,     0, 0, 200, "Huyah!"));

        customElements.add(createElement(TextElement.TYPE_SCENARIO, 0, 0, 200, "Victory is mine!"));
        customElements.add(createElement(TextElement.TYPE_SCENARIO, 0, 0, 200, "We thank Thee Lord, that in Thy mer--"));
        customElements.add(createElement(TextElement.TYPE_COMMENT,  0, 0, 200, "Hah!"));

        customElements.add(createElement(TextElement.TYPE_COMMENT,  0, 0, 200, "Come on, then."));
        customElements.add(createElement(TextElement.TYPE_SCENARIO, 0, 0, 200, "What?"));
        customElements.add(createElement(TextElement.TYPE_COMMENT,  0, 0, 200, "Have at you!"));

        customElements.add(createElement(TextElement.TYPE_SCENARIO, 0, 0, 200, "Eh. You are indeed brave, Sir Knight, but the fight is mine."));
        customElements.add(createElement(TextElement.TYPE_COMMENT,  0, 0, 200, "Oh, had enough, eh?"));
        customElements.add(createElement(TextElement.TYPE_SCENARIO, 0, 0, 200, "Look, you stupid bastard. You've got no arms left."));
        customElements.add(createElement(TextElement.TYPE_COMMENT,  0, 0, 200, "Yes, I have."));

        final int COL_WIDTH = 210;
        final int ROW_HEIGHT = 80;
        List<Integer> x = new ArrayList<Integer>();
        List<Integer> y = new ArrayList<Integer>();
        for (int i = 0; i < customElements.size(); i++) {
            int currentX = i % 4;
            int currentY = i / 4;
            x.add(20 + (currentX * COL_WIDTH));
            y.add(10 + (currentY * ROW_HEIGHT));
        }
        int idx = 0;
        while (!x.isEmpty()) {
            int i = (int) (Math.random() * x.size());
            customElements.get(idx).animation.moveAnimation.setRealPosition(x.get(i), y.get(i));
            x.remove(i);
            y.remove(i);
            idx++;
        }
    }

    private static TextElement createElement(int type, int x, int y, int width, String text) {
        TextElement element = new TextElement(type, TextElement.ROOT_NONE, text);
        element.animation.colorAnimation.setAlpha(Animation.FADE_ALPHA_DEFAULT, Animation.FADE_SPEED_REENTRANCE);
        element.animation.moveAnimation.setRealPosition(x, y);
        element.animation.moveAnimation.setRenderPosition(x, y);
        element.renderWidth = width;
        return element;
    }

    private static void updateSpeed() {
        if (System.currentTimeMillis() > updateTime + 1000L) {
            long deltaTime = System.currentTimeMillis() - updateTime;
            updateTime = System.currentTimeMillis();
            ballSpeed = (int) Math.max(((float) deltaTime / (float) updateCount) * BALL_SPEED, 1);
            updateCount = 0;
        }
    }

    private static void updateStick() {
        stickX = Engine.canvasWidth - STICK_WIDTH - 40;
        stickY = CumberlessMouseListener.mouseY;
        if (stickY < 0) {
            stickY = 0;
        }
        if (stickY + STICK_HEIGHT > Engine.canvasHeight) {
            stickY = Engine.canvasHeight - STICK_HEIGHT;
        }
    }

    private static void updateBall() {
        if (ballSticky) {
            ballX = stickX - (BALL_SIZE / 2);
            ballY = stickY + (STICK_HEIGHT / 2);
            if (CumberlessMouseListener.isButtonPressed) {
                ballVelX = (float) -Math.cos(0.3f);
                ballVelY = (float) -Math.sin(0.3f);
                ballSticky = false;
                initBallTrace();
            }
        } else {
            for (int i = 0; i < ballSpeed; i++) {
                moveBall();
                updateBallTrace();
            }
        }
    }

    private static void updateBallTrace() {
        ballTraceUpdateDelay++;
        if (ballTraceUpdateDelay < BALL_TRACE_DELAY) {
            return;
        }
        ballTraceUpdateDelay = 0;
        for (int i = BALL_TRACE_LENGTH - 1; i > 0; i--) {
            ballTraceX[i] = ballTraceX[i - 1];
            ballTraceY[i] = ballTraceY[i - 1];
        }
        ballTraceX[0] = (int) ballX;
        ballTraceY[0] = (int) ballY;
    }

    private static void initBallTrace() {
        ballTraceUpdateDelay = 0;
        ballTraceX = new int[BALL_TRACE_LENGTH];
        ballTraceY = new int[BALL_TRACE_LENGTH];
        for (int i = 0; i < BALL_TRACE_LENGTH; i++) {
            ballTraceX[i] = (int) ballX;
            ballTraceY[i] = (int) ballY;
        }
    }

    private static void moveBall() {
        ballX += ballVelX * BALL_SPEED;
        ballY += ballVelY * BALL_SPEED;
        checkBorders();
        checkElements();
    }

    private static void checkBorders() {
        if (ballX < 0.0f && ballVelX < 0.0f) {
            ballX = -ballX;
            ballVelX = -ballVelX;
        }
        if (ballX > Engine.canvasWidth && ballVelX > 0.0f) {
            hide();
        }
        if (ballY < 0.0f && ballVelY < 0.0f) {
            ballY = -ballY;
            ballVelY = -ballVelY;
        }
        if (ballY > Engine.canvasHeight && ballVelY > 0.0f) {
            ballY = Engine.canvasHeight - (ballY - Engine.canvasHeight);
            ballVelY = -ballVelY;
        }
        if (ballVelX > 0.0f &&
                ballX + (BALL_SIZE / 2) >= stickX && ballX - (BALL_SIZE / 2) <= stickX + STICK_WIDTH &&
                ballY + (BALL_SIZE / 2) >= stickY && ballY - (BALL_SIZE / 2) <= stickY + STICK_HEIGHT) {
            double angle = (STICK_ANGLE_CUTOFF / 2.0f) + (ballY - (stickY + (STICK_HEIGHT / 2))) * Math.PI * (1.0f - STICK_ANGLE_CUTOFF) / (STICK_HEIGHT + BALL_SIZE);
            ballVelX = (float) -Math.cos(angle);
            ballVelY = (float) Math.sin(angle);
        }
    }

    private static void checkElements() {
        bounceOnElement(
                -(BALL_SIZE / 2), 0,
                 (BALL_SIZE / 2), 0,
                0, -(BALL_SIZE / 2),
                0,  (BALL_SIZE / 2)
        );
    }

    private static Element bounceOnElement(int ... points) {
        for (int i = 0; i < points.length; i += 2) {
            int deltaX = points[i + 0];
            int deltaY = points[i + 1];
            int x = (int) (ballX + deltaX);
            int y = (int) (ballY + deltaY);
            Element element = findElement(x, y);
            if (element != null && element.visible) {
                if (deltaX < 0 && ballVelX < 0.0f) {
                    ballVelX = -ballVelX;
                }
                if (deltaX > 0 && ballVelX > 0.0f) {
                    ballVelX = -ballVelX;
                }
                if (deltaY < 0 && ballVelY < 0.0f) {
                    ballVelY = -ballVelY;
                }
                if (deltaY > 0 && ballVelY > 0.0f) {
                    ballVelY = -ballVelY;
                }
                removeElement(element);
                return element;
            }
        }
        return null;
    }

    private static void removeElement(Element element) {
        if (Engine.featuresRoot.children.size() > 0) {
            if (element.children.size() <= 0) {
                element.trashElement();
            } else if (element.folded) {
                ((TextElement) element).unfold();
            }
        } else {
            if (element.type == TextElement.TYPE_SCENARIO ||
                (element.type == TextElement.TYPE_COMMENT && !hasElementLeft(TextElement.TYPE_SCENARIO)) ||
                (element.type == TextElement.TYPE_STEP && !hasElementLeft(TextElement.TYPE_COMMENT))) {
                customElements.remove(element);
            }
        }
    }

    private static boolean hasElementLeft(int type) {
        for (TextElement element : customElements) {
            if (element.type == type) {
                return true;
            }
        }
        return false;
    }

    private static Element findElement(int x, int y) {
        if (customElements.size() > 0) {
            for (Element element : customElements) {
                Element foundElement = element.findElement(x, y);
                if (foundElement != null) {
                    return foundElement;
                }
            }
        } else {
            return Engine.cucumberRoot.findElement(x, y);
        }
        return null;
    }

    public static void render(Graphics2D g) {
        drawBackground(g);
        if (enabled) {
            drawElements(g);
            drawBall(g);
            drawStick(g);
        }
    }

    private static void drawElements(Graphics2D g) {
        for (Element element : customElements) {
            element.render(g);
        }
    }

    private static void drawStick(Graphics g) {
        g.setColor(new Color(1.0f, 0.5f, 0.5f));
        g.fillRect(stickX, stickY, STICK_WIDTH + 1, STICK_HEIGHT + 1);
    }

    private static void drawBall(Graphics g) {
        if (!ballSticky) {
            for (int i = 0; i < BALL_TRACE_LENGTH; i++) {
                int idx = BALL_TRACE_LENGTH - i - 1;
                float alpha = (float) i / (float) BALL_TRACE_LENGTH;
                g.setColor(new Color(0.5f * alpha, 0.5f * alpha, 0.1f * alpha));
                g.fillOval(ballTraceX[idx] - (BALL_SIZE / 2), ballTraceY[idx] - (BALL_SIZE / 2), BALL_SIZE, BALL_SIZE);
            }
        }
        g.setColor(new Color(1.0f, 1.0f, 0.2f));
        g.fillOval((int) ballX - (BALL_SIZE / 2), (int) ballY - (BALL_SIZE / 2), BALL_SIZE, BALL_SIZE);
    }

    private static void drawBackground(Graphics g) {
        animation += enabled ? 1 : -1;
        int animationMax = Engine.canvasHeight * 2 / BACKGROUND_ANIMATION_SIZE;
        animation = Math.max(0, Math.min(animation, animationMax));
        backgroundCoveringCanvas = animation >= animationMax;
        g.setColor(Color.BLACK);
        int top = 0;
        for (int y = 0; y < Engine.canvasHeight; y += BACKGROUND_ANIMATION_SIZE) {
            int height = Math.min(animation - (y / BACKGROUND_ANIMATION_DELAY), BACKGROUND_ANIMATION_SIZE);
            if (height < BACKGROUND_ANIMATION_SIZE) {
                if (height >= 0) {
                    g.fillRect(0, top, Engine.canvasWidth, y - top + height);
                }
                top = y + BACKGROUND_ANIMATION_SIZE;
            }
        }
        if (top < Engine.canvasHeight) {
            g.fillRect(0, top, Engine.canvasWidth, Engine.canvasHeight - top);
        }
    }

    public static void show() {
        synchronized (Engine.LOCK) {
            enabled = true;
            customElements = new LinkedList<TextElement>();
            updateTime = System.currentTimeMillis();
            updateCount = 0;
            if (animation <= 0) {
                animation = 1;
                backgroundCoveringCanvas = false;
            }
            ballSticky = true;
            Engine.featuresRoot.scrollToTop();
            BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
            savedCursor = Engine.canvas.getCursor();
            Engine.canvas.setCursor(blankCursor);
        }
    }

    public static void hide() {
        synchronized (Engine.LOCK) {
            enabled = false;
            Engine.spotlight.clear();
            Engine.canvas.setCursor(savedCursor);
        }
    }

    public static boolean isBackgroundCoveringCanvas() {
        return backgroundCoveringCanvas;
    }
}
