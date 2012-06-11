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

import com.trollsahead.qcumberless.engine.DesignerEngine;
import com.trollsahead.qcumberless.engine.Engine;
import com.trollsahead.qcumberless.model.RunHistory;
import com.trollsahead.qcumberless.model.TagHistory;

import static com.trollsahead.qcumberless.gui.Button.ButtonNotification;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class TagButtonFilterButton {
    private static final Color TEXT_COLOR = new Color(1.0f, 1.0f, 1.0f, 1.0f);

    private static final Color[][] OUTLINE_COLOR = {
            {new Color(0.0f, 0.0f, 0.0f, 0.2f), new Color(1.0f, 1.0f, 1.0f, 0.05f), new Color(1.0f, 1.0f, 1.0f, 0.025f)},
            {new Color(0.0f, 1.0f, 0.0f, 0.5f), new Color(0.0f, 1.0f, 0.0f, 0.6f), new Color(0.0f, 1.0f, 0.0f, 0.55f)},
            {new Color(1.0f, 0.0f, 0.0f, 0.5f), new Color(1.0f, 0.0f, 0.0f, 0.6f), new Color(1.0f, 0.0f, 0.0f, 0.55f)}
    };
    private static final Color OUTLINE_COLOR_HISTORY = new Color(0.0f, 0.0f, 0.0f);

    private static final Color[] BG_COLOR = {new Color(0.0f, 0.0f, 0.0f, 0.05f), new Color(1.0f, 1.0f, 1.0f, 0.05f), new Color(1.0f, 1.0f, 1.0f, 0.025f)};
    private static final Color BG_COLOR_FAILURE = new Color(1.0f, 0.0f, 0.0f, 0.5f);
    private static final Color BG_COLOR_SUCCESS = new Color(0.0f, 1.0f, 0.0f, 0.5f);

    private static final int OUTLINE_ROUNDING = 20;

    private static final int BUTTON_PADDING_HORIZONTAL = 3;

    private static final int TAG_BAR_PADDING_HORIZONTAL = 8;
    private static final int TAG_BAR_PADDING_VERTICAL = 8;
    private static final int TAG_BAR_HEIGHT = 10;

    private static enum State {NORMAL, ACTIVATED, DEACTIVATED}

    public State state = State.NORMAL;

    private Button playButton;
    private Button filterButton;
    private List<Button> buttons;

    private String tag;
    private int x;
    private int y;
    private int width;
    private int height;

    private boolean highlighted = false;

    private int leftX;
    private int topY;

    public TagButtonFilterButton(String tag, int x, int y, int width, int height, final ButtonNotification playClickNotification, final ButtonNotification filterClickNotification) {
        this.tag = tag;
        this.width = width;
        this.height = height;
        buttons = new LinkedList<Button>();
        playButton = new Button(
                0,
                0,
                Images.getImage(Images.IMAGE_PLAY, Images.ThumbnailState.NORMAL.ordinal()),
                Images.getImage(Images.IMAGE_PLAY, Images.ThumbnailState.HIGHLIGHTED.ordinal()),
                Images.getImage(Images.IMAGE_PLAY, Images.ThumbnailState.NORMAL.ordinal()),
                Button.ALIGN_HORIZONTAL_CENTER | Button.ALIGN_VERTICAL_CENTER,
                new ButtonNotification() {
                    public void onClick() {
                        playClickNotification.onClick();
                    }
                },
                null);
        playButton.setHint("Play this tag");
        buttons.add(playButton);
        filterButton = new Button(
                0,
                0,
                Images.getImage(Images.IMAGE_FILTER, Images.ThumbnailState.NORMAL.ordinal()),
                Images.getImage(Images.IMAGE_FILTER, Images.ThumbnailState.HIGHLIGHTED.ordinal()),
                Images.getImage(Images.IMAGE_FILTER, Images.ThumbnailState.NORMAL.ordinal()),
                Button.ALIGN_HORIZONTAL_CENTER | Button.ALIGN_VERTICAL_CENTER,
                new ButtonNotification() {
                    public void onClick() {
                        filterClickNotification.onClick();
                    }
                },
                null);
        filterButton.setHint("Filter this tag");
        buttons.add(filterButton);
        setCenterPosition(x, y);
    }

    public void setCenterPosition(int x, int y) {
        this.x = x;
        this.y = y;
        leftX = x - (width / 2);
        topY = y - (height / 2);
    }

    public void update() {
        updateHighlight();
        updateTagState();
        updateButtonPositions();
        updateButtons();
    }

    private void updateButtonPositions() {
        int centerY = topY + (height / 2);
        playButton.setPosition(leftX + BUTTON_PADDING_HORIZONTAL + 10, centerY - (height / 4));
        filterButton.setPosition(leftX + BUTTON_PADDING_HORIZONTAL + 10, centerY + (height / 4));
    }

    private void updateButtons() {
        for (Button button : buttons) {
            button.setVisible(highlighted);
            button.update();
        }
    }

    private void updateTagState() {
        if (DesignerEngine.isRunTagEnabled("@" + tag)) {
            state = State.ACTIVATED;
        } else if (DesignerEngine.isRunTagEnabled("~@" + tag)) {
            state = State.DEACTIVATED;
        } else {
            state = State.NORMAL;
        }
    }

    private void updateHighlight() {
        highlighted = CumberlessMouseListener.mouseX >= leftX &&
                      CumberlessMouseListener.mouseY >= topY &&
                      CumberlessMouseListener.mouseX <= leftX + width &&
                      CumberlessMouseListener.mouseY <= topY + height;
    }

    public void render(Graphics2D g) {
        g.setColor(BG_COLOR[getHighlightState()]);
        g.fillRoundRect(leftX, topY, width, height, OUTLINE_ROUNDING, OUTLINE_ROUNDING);

        g.setColor(OUTLINE_COLOR[getTagState()][getHighlightState()]);
        g.drawRoundRect(leftX, topY, width, height, OUTLINE_ROUNDING, OUTLINE_ROUNDING);

        g.setColor(TEXT_COLOR);
        g.drawString(tag, x - (Engine.fontMetrics.stringWidth(tag) / 2), y - 3);

        drawTagHistory(g);
        
        renderButtons(g);
    }

    private void drawTagHistory(Graphics2D g) {
        TagHistory tagHistory = RunHistory.getTagHistory("@" + tag);
        if (tagHistory == null || tagHistory.getRunCount() == 0) {
            return;
        }

        int x = TAG_BAR_PADDING_HORIZONTAL + (highlighted ? BUTTON_PADDING_HORIZONTAL + 16 : 0);
        int y = topY + height - TAG_BAR_PADDING_VERTICAL - TAG_BAR_HEIGHT;

        int barWidth = width - x - TAG_BAR_PADDING_HORIZONTAL;

        float failurePct = (float) tagHistory.getFailureCount() / (float) tagHistory.getRunCount();
        int failureWidth = (int) (failurePct * (float) barWidth);
        int successWidth = barWidth - failureWidth;

        g.setColor(BG_COLOR_FAILURE);
        g.fillRect(leftX + x, y, failureWidth, TAG_BAR_HEIGHT);

        g.setColor(BG_COLOR_SUCCESS);
        g.fillRect(leftX + x + barWidth - successWidth, y, successWidth, TAG_BAR_HEIGHT);

        g.setColor(OUTLINE_COLOR_HISTORY);
        g.drawRect(leftX + x, y, barWidth, TAG_BAR_HEIGHT);
    }

    private void renderButtons(Graphics2D g) {
        for (Button button : buttons) {
            button.render(g);
        }
    }

    private int getTagState() {
        if (state == State.NORMAL) {
            return 0;
        }
        if (state == State.ACTIVATED) {
            return 1;
        }
        return 2;
    }

    private int getHighlightState() {
        if (!highlighted) {
            return 0;
        }
        if (CumberlessMouseListener.isButtonPressed) {
            return 2;
        }
        return 1;
    }

    public boolean click() {
        for (Button button : buttons) {
            if (button.click()) {
                return true;
            }
        }
        if (highlighted) {
            toggleTag();
            return true;
        } else {
            return false;
        }
    }

    private void toggleTag() {
        DesignerEngine.toggleRunTag("@" + tag);
        updateTagState();
    }
}
