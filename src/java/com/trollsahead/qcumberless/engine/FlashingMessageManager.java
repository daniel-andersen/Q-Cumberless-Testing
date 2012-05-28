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

package com.trollsahead.qcumberless.engine;

import com.trollsahead.qcumberless.gui.FlashingMessage;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class FlashingMessageManager {
    private static final int MESSAGES_TOP = 50;
    private static final int MESSAGES_GAP = 15;

    private static List<FlashingMessage> messages = new LinkedList<FlashingMessage>();

    public static void initialize() {
    }

    public static void addMessage(FlashingMessage message) {
        synchronized (Engine.LOCK) {
            messages.add(message);
            repositionMessages();
        }
    }

    public static void removeMessage(FlashingMessage message) {
        synchronized (Engine.LOCK) {
            messages.remove(message);
            repositionMessages();
        }
    }
    
    private static void repositionMessages() {
        int y = MESSAGES_TOP;
        for (FlashingMessage message : messages) {
            message.setPosition((Engine.canvasWidth - message.getWidth()) / 2, y);
            y += message.getHeight() + MESSAGES_GAP;
        }
    }

    public static void update() {
        boolean hasTimedOutMessages = false;
        for (FlashingMessage message : messages) {
            message.update();
            hasTimedOutMessages |= message.hasTimedOut();
        }
        if (hasTimedOutMessages) {
            removeTimedOutMessages();
        }
    }

    private static void removeTimedOutMessages() {
        List<FlashingMessage> newMessages = new LinkedList<FlashingMessage>();
        for (FlashingMessage message : messages) {
            if (!message.hasTimedOut()) {
                newMessages.add(message);
            }
        }
        messages = newMessages;
        repositionMessages();
    }

    public static void render(Graphics2D g) {
        for (FlashingMessage message : messages) {
            message.render(g);
        }
    }
}
