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

import java.awt.*;

public class Terminal {
    private static final float TERMINAL_PROPORTIONAL_HEIGHT = 0.3f;

    private static boolean visible = false;

    private static Animation animation = new Animation();

    public static void initialize() {
        resize();
    }

    public static void resize() {
        setPosition();
        animation.moveAnimation.setRenderPosition(animation.moveAnimation.realX, animation.moveAnimation.realY);
    }

    public static void update() {
        animation.update();
        if (animation.moveAnimation.isMoving()) {
            Engine.updateRootPositions();
        }
    }

    public static void render(Graphics2D g) {
        int height = getHeight();
        if (height <= 0) {
            return;
        }
        g.setColor(Color.BLACK);
        g.fillRect(0, (int) animation.moveAnimation.renderY, Engine.windowWidth, height);
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
    }

    private static void hideTerminal() {
        visible = false;
        setPosition();
    }

    private static void setPosition() {
        if (visible) {
            animation.moveAnimation.setRealPosition(0.0f, Engine.windowHeight - getProportionalHeight());
        } else {
            animation.moveAnimation.setRealPosition(0.0f, Engine.windowHeight + 1.0f);
        }
    }

    public static int getHeight() {
        return Math.max(Engine.windowHeight - (int) animation.moveAnimation.renderY, 0);
    }
    
    private static int getProportionalHeight() {
        return (int) ((float) Engine.windowHeight * TERMINAL_PROPORTIONAL_HEIGHT);
    }
}
