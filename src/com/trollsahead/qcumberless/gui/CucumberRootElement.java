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

import com.trollsahead.qcumberless.engine.CucumberEngine;

import java.awt.*;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class CucumberRootElement extends CucumberElement {
    public static final int TYPE_ROOT = -1;

    public static final int PADDING_HORIZONTAL = 20;

    private static final Color COLOR_BG_NORMAL    = new Color(1.0f, 1.0f, 1.0f, 0.5f);
    private static final Color COLOR_BG_HIGHLIGHT = new Color(1.0f, 1.0f, 1.0f, 0.6f);

    private static final int SCROLLBAR_WIDTH = 10;
    private static final int SCROLLBAR_PADDING_HORIZONTAL = 3;
    private static final int SCROLLBAR_PADDING_VERTICAL = 5;
    private static final int SCROLLBAR_BOTTOM_PADDING = CucumberButtonBar.BUTTONBAR_HEIGHT;
    private static final int SCROLLBAR_ROUNDING = 7;

    private static final float ANIMATION_SPEED_PUSH = 0.8f;

    private int x = 0;
    private int y = 0;
    private int width = 100;
    private int height = 100;

    private int offsetY = 0;

    private int scrollbarX = 0;
    private int scrollbarY = 0;
    private int scrollbarHeight = 1;
    private int scrollbarMaxHeight = 1;

    private boolean isDraggingScrollbar = false;

    private int cachedGroupHeight = 1;
    public boolean isLoaded = false;

    private int verticalGap = 0;

    public CucumberRootElement() {
        animation.colorAnimation.setAlpha(CucumberAnimation.FADE_ALPHA_DEFAULT, CucumberAnimation.FADE_SPEED_ENTRANCE);
        animation.moveAnimation.setRenderPosition(0, -100, CucumberAnimation.MOVEMENT_SPEED_ENTRANCE);
        type = TYPE_ROOT;
        rootType = ROOT_NONE;
    }

    public void pushToPosition(int y) {
        verticalGap = y;
    }
    
    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height - SCROLLBAR_BOTTOM_PADDING;
        updatePosition();
    }

    public void scroll(int deltaY) {
        if (isScrollbarTouched() || isDraggingScrollbar) {
            dragScrollbar(deltaY);
        } else {
            dragCanvas(deltaY);
        }
        updatePosition();
        stickChildrenToParentRenderPosition(true);
    }

    public void startDrag() {
        if (isScrollbarTouched()) {
            dragScrollbar(0);
        }
    }

    public void endDrag() {
        isDraggingScrollbar = false;
    }

    private void dragCanvas(int deltaY) {
        isDraggingScrollbar = false;
        offsetY += deltaY;
        if (offsetY > 0) {
            offsetY = 0;
        }
    }

    private void dragScrollbar(int deltaY) {
        isDraggingScrollbar = true;
        int dragOffset = CucumberMouseListener.mouseY - SCROLLBAR_PADDING_VERTICAL - scrollbarY;
        if (dragOffset < 0 || dragOffset >= scrollbarHeight) {
            scrollbarY = CucumberMouseListener.mouseY - SCROLLBAR_PADDING_VERTICAL - (scrollbarHeight / 2);
        } else {
            scrollbarY += deltaY;
        }
        scrollbarY = Math.max(0, Math.min(scrollbarMaxHeight - scrollbarHeight, scrollbarY));
        offsetY = -(scrollbarY * Math.max(cachedGroupHeight - renderHeight, 0) / Math.max(scrollbarMaxHeight - scrollbarHeight, 1));
    }

    protected void updateSelf(long time) {
        if (animation.moveAnimation.isMoving()) {
            stickChildrenToParentRenderPosition(true);
        }
    }

    public String getTitle() {
        return "";
    }

    public void trashElement() {
    }

    public CucumberElement findGroup(int x, int y, int type) {
        for (CucumberElement child : children) {
            CucumberElement foundElement = child.findGroup(x, y, type);
            if (foundElement != null) {
                return foundElement;
            }
        }
        return null;
    }

    protected void renderBefore(Graphics g) {
    }

    protected void renderAfter(Graphics g) {
        if (this == CucumberEngine.cucumberRoot) {
            return;
        }
        renderScrollbar(g);
        cachedGroupHeight = groupHeight;
    }

    protected void renderHintsInternal(Graphics g) {
    }

    private void renderScrollbar(Graphics g) {
        scrollbarX = (int) animation.moveAnimation.renderX - PADDING_HORIZONTAL;
        if (this == CucumberEngine.featuresRoot) {
            scrollbarX += SCROLLBAR_PADDING_HORIZONTAL;
        } else {
            scrollbarX += renderWidth - SCROLLBAR_PADDING_HORIZONTAL - SCROLLBAR_WIDTH;
        }

        scrollbarMaxHeight = renderHeight - SCROLLBAR_PADDING_VERTICAL * 2 - 2;
        scrollbarHeight = scrollbarMaxHeight * renderHeight / Math.max(Math.max(renderHeight, cachedGroupHeight), 1);

        if (!isDraggingScrollbar) {
            scrollbarY = (scrollbarMaxHeight - scrollbarHeight) * (-offsetY) / Math.max(cachedGroupHeight - renderHeight, -offsetY + 1);
        }

        g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.5f));
        g.fillRoundRect(scrollbarX, SCROLLBAR_PADDING_VERTICAL, SCROLLBAR_WIDTH, scrollbarMaxHeight, SCROLLBAR_ROUNDING, SCROLLBAR_ROUNDING);

        g.setColor(isScrollbarTouched() ? COLOR_BG_HIGHLIGHT : COLOR_BG_NORMAL);
        g.fillRoundRect(scrollbarX + 1, SCROLLBAR_PADDING_VERTICAL + scrollbarY + 1, SCROLLBAR_WIDTH - 2, scrollbarHeight - 2, SCROLLBAR_ROUNDING, SCROLLBAR_ROUNDING);
    }

    protected void calculateRenderPosition(Graphics g) {
        if (this == CucumberEngine.cucumberRoot) {
            return;
        }
        updatePosition();
        renderWidth = width;
        renderHeight = height;
        paddingHeight = 0;
        groupHeight = 0;
    }

    private void updatePosition() {
        int realX = x + PADDING_HORIZONTAL;
        int realY = offsetY + y + verticalGap;
        animation.moveAnimation.renderX += realX - animation.moveAnimation.realX;
        animation.moveAnimation.renderY += realY - animation.moveAnimation.realY;
        animation.moveAnimation.setRealPosition(realX, realY);
    }

    private boolean isScrollbarTouched() {
        return (this == CucumberEngine.featuresRoot && CucumberMouseListener.mouseX <= scrollbarX + SCROLLBAR_WIDTH) ||
               (this == CucumberEngine.stepsRoot && CucumberMouseListener.mouseX >= scrollbarX);
    }

    protected void foldFadeAnimation(float alpha) {
    }

    public void click() {
    }

    public boolean isDragable() {
        return true;
    }

    protected void applyDrag() {
        isDraggingScrollbar = false;
    }

    public void updateElementIndex(CucumberElement cucumberElement, int index) {
    }

    protected CucumberElement duplicate() {
        return new CucumberRootElement();
    }

    protected StringBuilder buildFeatureInternal() {
        return new StringBuilder();
    }

    protected void updateStepsInternal() {
    }

    public boolean export(File directory) {
        boolean success = true;
        for (CucumberElement child : children) {
            success &= child.export(directory);
        }
        return success;
    }

    public boolean save() {
        boolean success = true;
        for (CucumberElement child : children) {
            success &= child.save();
        }
        return success;
    }

    public void filterChildren(String regexp) {
        for (CucumberElement child : children) {
            if (!child.getTitle().matches(regexp)) {
                child.hide();
            } else {
                child.show();
            }
        }
        scrollToTop();
    }

    public void scrollToTop() {
        scrollbarY = 0;
        offsetY = 0;
    }

    public Set<String> getTagsInternal() {
        return new HashSet<String>();
    }
}
