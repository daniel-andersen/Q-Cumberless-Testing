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

import com.trollsahead.qcumberless.engine.Engine;
import com.trollsahead.qcumberless.util.Util;

import java.awt.*;

public class Animation {
    public static final float FADE_SPEED_DRAG                    = 0.05f;
    public static final float FADE_SPEED_ENTRANCE                = 0.02f;
    public static final float FADE_SPEED_FOLD                    = 0.05f;
    public static final float FADE_SPEED_REENTRANCE              = 0.05f;
    public static final float FADE_SPEED_APPEAR                  = 0.05f;
    public static final float FADE_SPEED_CHANGE_COLOR_SCHEME     = 0.01f;
    public static final float FADE_SPEED_CHANGE_PLAY_COLOR_STATE = 0.1f;

    public static final float FADE_ALPHA_DEFAULT = 0.8f;
    public static final float FADE_ALPHA_DRAG    = 0.5f;

    public static final float MOVEMENT_SPEED_NORMAL   = 0.8f;
    public static final float MOVEMENT_SPEED_ENTRANCE = 0.9f;
    public static final float MOVEMENT_SPEED_TERMINAL = 0.8f;

    public static final float RESIZE_SPEED = 20.0f;

    public MoveAnimation moveAnimation = new MoveAnimation();
    public SizeAnimation sizeAnimation = new SizeAnimation();
    public ColorAnimation colorAnimation = new ColorAnimation();
    public ColorAnimation alphaAnimation = new ColorAnimation();

    public static Stroke setStrokeAnimation(Graphics2D g, float dashLength, float dashWidth, float speed) {
        float dashPosition = (dashLength * 2.0f) - (float) (System.currentTimeMillis() % (int) (speed * dashLength * 2.0f)) / speed;
        return setStroke(g, dashLength, dashWidth, dashPosition);
    }

    public static Stroke setStroke(Graphics2D g, float dashLength, float dashWidth) {
        return setStroke(g, dashLength, dashWidth, 0.0f);
    }

    public static Stroke setStroke(Graphics2D g, float dashLength, float dashWidth, float dashPosition) {
        Stroke oldStroke = g.getStroke();
        g.setStroke(new BasicStroke(dashWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, dashLength, new float[] {dashLength}, dashPosition));
        return oldStroke;
    }

    public Animation() {
    }

    public Animation(float[] color) {
        this.colorAnimation.fromColor = color.clone();
        this.colorAnimation.destColor = color.clone();
        this.colorAnimation.currentColor = color.clone();
    }

    public Animation(Color color) {
        this.colorAnimation.fromColor = Util.colorToFloatArray(color);
        this.colorAnimation.destColor = Util.colorToFloatArray(color);
        this.colorAnimation.currentColor = Util.colorToFloatArray(color);
    }

    public void update(boolean retainPosition) {
        colorAnimation.update();
        alphaAnimation.update();
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

        public void update(boolean retainRenderPosition) {
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
        public long becameVisibleAtCount = Engine.renderCounter;

        public void setAlpha(float alpha, float speed) {
            this.fromColor = currentColor.clone();
            this.destColor[3] = alpha;
            this.speed = speed;
            this.progress = 0.0f;
            this.isFading = true;
            if (getAlpha() <= 0.0f) {
                becameVisibleAtCount = Engine.renderCounter;
            }
        }

        public void setAlpha(float alpha) {
            this.fromColor = currentColor.clone();
            this.fromColor[3] = alpha;
            this.destColor[3] = alpha;
            this.currentColor[3] = alpha;
            this.progress = 1.0f;
            this.isFading = false;
            if (getAlpha() <= 0.0f) {
                becameVisibleAtCount = Engine.renderCounter;
            }
        }

        public void setColor(float[] color, float speed) {
            this.fromColor = currentColor.clone();
            this.destColor = color.clone();
            this.speed = speed;
            this.progress = 0.0f;
            this.isFading = true;
            if (getAlpha() <= 0.0f) {
                becameVisibleAtCount = Engine.renderCounter;
            }
        }

        public void setColor(Color color, float speed) {
            this.fromColor = currentColor.clone();
            this.destColor = Util.colorToFloatArray(color);
            this.speed = speed;
            this.progress = 0.0f;
            this.isFading = true;
            if (getAlpha() <= 0.0f) {
                becameVisibleAtCount = Engine.renderCounter;
            }
        }

        public void setColor(Color color) {
            setColorKeepProgress(color);
            this.fromColor = Util.colorToFloatArray(color);
            this.destColor = this.fromColor.clone();
            this.currentColor = this.fromColor.clone();
            this.progress = 1.0f;
            this.isFading = false;
            if (getAlpha() <= 0.0f) {
                becameVisibleAtCount = Engine.renderCounter;
            }
        }
        
        public void setColorKeepProgress(Color color) {
            this.fromColor = currentColor.clone();
            this.destColor = Util.colorToFloatArray(color);
        }

        private void update() {
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
        
        public boolean justBecameVisible() {
            return Engine.renderCounter == becameVisibleAtCount + 1;
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

        public void setWidth(int width, float speed) {
            this.destWidth = width;
            this.speed = speed;
            this.isResizing = true;
        }

        public void setHeight(int height, float speed) {
            this.destHeight = height;
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
    }
}
