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

package com.trollsahead.qcumberless.gui;

import java.awt.*;

public class Animation {
    public static final float FADE_SPEED_DRAG       = 0.05f;
    public static final float FADE_SPEED_ENTRANCE   = 0.02f;
    public static final float FADE_SPEED_FOLD       = 0.05f;
    public static final float FADE_SPEED_REENTRANCE = 0.05f;
    public static final float FADE_SPEED_APPEAR     = 0.05f;

    public static final float FADE_ALPHA_DEFAULT = 0.8f;
    public static final float FADE_ALPHA_DRAG    = 0.5f;

    public static final float MOVEMENT_SPEED_NORMAL    = 0.8f;
    public static final float MOVEMENT_SPEED_ENTRANCE  = 0.9f;

    public static final float RESIZE_SPEED = 20.0f;

    public MoveAnimation moveAnimation = new MoveAnimation();
    public SizeAnimation sizeAnimation = new SizeAnimation();
    public ColorAnimation colorAnimation = new ColorAnimation();

    public Animation() {
    }

    public Animation(float[] color) {
        this.colorAnimation.fromColor = color.clone();
        this.colorAnimation.destColor = color.clone();
        this.colorAnimation.currentColor = color.clone();
    }

    public void update(boolean retainPosition) {
        colorAnimation.update();
        sizeAnimation.update();
        moveAnimation.update(retainPosition);
    }

    public void update() {
        update(false);
    }

    public static class MoveAnimation {
        public int realX = 0;
        public int realY = 0;
        public float renderX = 0;
        public float renderY = 0;
        public float speed = MOVEMENT_SPEED_NORMAL;

        public void setRealPosition(MoveAnimation moveAnimation, boolean useRealPosition) {
            if (useRealPosition) {
                setRealPosition(moveAnimation.realX, moveAnimation.realY);
            } else {
                setRealPosition(moveAnimation.renderX, moveAnimation.renderY);
            }
        }

        public void setRealPosition(float x, float y) {
            this.realX = (int) x;
            this.realY = (int) y;
        }

        public void setRealPosition(float x, float y, float speed) {
            this.realX = (int) x;
            this.realY = (int) y;
            this.speed = speed;
        }

        public void setRenderPosition(MoveAnimation moveAnimation, boolean useRenderPosition) {
            if (useRenderPosition) {
                setRenderPosition(moveAnimation.renderX, moveAnimation.renderY);
            } else {
                setRenderPosition(moveAnimation.realX, moveAnimation.realY);
            }
        }

        public void setRenderPosition(float x, float y) {
            this.renderX = x;
            this.renderY = y;
        }

        public void setRenderPosition(float x, float y, float speed) {
            this.renderX = x;
            this.renderY = y;
            this.speed = speed;
        }

        private void update(boolean retainRenderPosition) {
            if (retainRenderPosition) {
                return;
            }
            float deltaX = (renderX - realX) * speed;
            float deltaY = (renderY - realY) * speed;
            if (Math.abs(deltaX) <= 0.1f && Math.abs(deltaY) <= 0.1f) {
                renderX = realX;
                renderY = realY;
            } else {
                renderX = realX + deltaX;
                renderY = realY + deltaY;
            }
        }

        public boolean isMoving() {
            return Math.abs(renderX - realX) > 0.1f || Math.abs(renderY - realY) > 0.1f;
        }
    }

    public static class ColorAnimation {
        private float[] currentColor = new float[] {0.0f, 0.0f, 0.0f, 0.0f};
        private float[] fromColor = new float[] {0.0f, 0.0f, 0.0f, 0.0f};
        private float[] destColor = new float[] {0.0f, 0.0f, 0.0f, 0.0f};
        private float speed = FADE_SPEED_ENTRANCE;
        private float progress = 0.0f;
        public boolean isFading = false;
        public boolean justBecameVisible = false;

        public void setAlpha(float alpha, float speed) {
            this.fromColor = currentColor.clone();
            this.destColor[3] = alpha;
            this.speed = speed;
            this.progress = 0.0f;
            this.isFading = true;
            this.justBecameVisible = getAlpha() <= 0.0f;
        }

        public void setColor(float[] color, float speed) {
            this.fromColor = currentColor.clone();
            this.destColor = color.clone();
            this.speed = speed;
            this.progress = 0.0f;
            this.isFading = true;
            this.justBecameVisible = getAlpha() <= 0.0f;
        }

        private void update() {
            if (!isFading) {
                return;
            }
            if (progress > 0.0f) {
                justBecameVisible = false;
            }
            progress += speed;
            if (progress >= 1.0f) {
                progress = 1.0f;
                isFading = false;
            }
            for (int i = 0; i < 4; i++) {
                currentColor[i] = fromColor[i] + (destColor[i] - fromColor[i]) * progress;
            }
        }

        public Color getColor() {
            return new Color(currentColor[0], currentColor[1], currentColor[2], currentColor[3]);
        }

        public float getAlpha() {
            return currentColor[3];
        }

        public boolean isVisible() {
            return getAlpha() > 0.0f;
        }
    }

    public class SizeAnimation {
        public boolean isResizing = false;
        public float currentWidth;
        public float currentHeight;
        public float destWidth;
        public float destHeight;
        public float speed;

        public void setSize(int destWidth, int destHeight, float speed) {
            this.destWidth = destWidth;
            this.destHeight = destHeight;
            this.speed = speed;
            this.isResizing = true;
        }

        public void update() {
            if (!isResizing) {
                return;
            }
            if (currentWidth < destWidth) {
                currentWidth += speed;
                currentWidth = Math.min(currentWidth, destWidth);
            }
            if (currentWidth > destWidth) {
                currentWidth -= speed;
                currentWidth = Math.max(currentWidth, destWidth);
            }
            if (currentHeight < destHeight) {
                currentHeight += speed;
                currentHeight = Math.min(currentHeight, destHeight);
            }
            if (currentHeight > destHeight) {
                currentHeight -= speed;
                currentHeight = Math.max(currentHeight, destHeight);
            }
            if ((int) currentWidth == (int) destWidth && (int) currentHeight == (int) destHeight) {
                isResizing = false;
            }
        }

        public void setWidth(int width, float speed) {
            this.destWidth = width;
            this.speed = speed;
            this.isResizing = true;
        }
    }
}
