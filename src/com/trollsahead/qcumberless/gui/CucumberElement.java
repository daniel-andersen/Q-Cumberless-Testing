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
import com.trollsahead.qcumberless.util.Util;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class CucumberElement {
    public static final int ROOT_NONE = 0;
    public static final int ROOT_FEATURE_EDITOR = 1;
    public static final int ROOT_STEP_DEFINITIONS = 2;

    public int type;
    public int rootType = ROOT_NONE;

    public int renderWidth = 0;
    public int renderHeight = 0;

    protected int paddingHeight = 0;
    protected int groupHeight = 0;

    protected boolean isDragged = false;
    protected int dragOffsetX = 0;
    protected int dragOffsetY = 0;

    protected boolean folded = false;
    protected boolean visible = true;

    protected boolean shouldStickToParentRenderPosition = false;

    public boolean isFailed = false;
    public String errorMessage = null;
    public Image[] errorScreenshots = null;

    public CucumberAnimation animation = new CucumberAnimation();

    public CucumberElement groupParent;
    public List<CucumberElement> children = new ArrayList<CucumberElement>();

    public CucumberElement() {
    }

    public void addChild(CucumberElement cucumberElement) {
        children.add(cucumberElement);
        cucumberElement.groupParent = this;
        if (this == CucumberEngine.stepsRoot) {
            cucumberElement.animation.colorAnimation.setAlpha(CucumberAnimation.FADE_ALPHA_DEFAULT, CucumberAnimation.FADE_SPEED_ENTRANCE);
        }
    }

    public void addChild(CucumberElement element, int index) {
        children.add(index, element);
        element.groupParent = this;
    }

    public void removeChild(CucumberElement element) {
        if (element == null) {
            return;
        }
        if (children.contains(element)) {
            if (this == CucumberEngine.stepsRoot) {
                CucumberElement stepDefinitionElement = element.duplicate();
                if (stepDefinitionElement instanceof CucumberTextElement) {
                    ((CucumberTextElement) stepDefinitionElement).step.setShouldRenderKeyword(false);
                }
                if (element instanceof CucumberTextElement) {
                    ((CucumberTextElement) element).step.setShouldRenderKeyword(true);
                }
                CucumberEngine.stepsRoot.addChild(stepDefinitionElement, findChildIndex(element));
                element.folded = false;
                element.rootType = ROOT_FEATURE_EDITOR;
                //element.animation.sizeAnimation.setWidth(CucumberTextElement.calculateRenderWidthFromRoot(ROOT_FEATURE_EDITOR), CucumberAnimation.RESIZE_SPEED); // TODO! Causes flickers when inserting!
            }
            children.remove(element);
            return;
        }
        for (CucumberElement child : children) {
            child.removeChild(element);
        }
    }

    public int findChildIndex(CucumberElement element) {
        if (element == null) {
            return -1;
        }
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) == element) {
                return i;
            }
        }
        return -1;
    }

    public CucumberElement findChildFromIndex(String step, int index) {
        for (int i = 0; i < children.size(); i++) {
            if (i < index) {
                continue;
            }
            CucumberElement child = children.get(i);
            if (child.getTitle().endsWith(step)) {
                return child;
            }
        }
        return null;
    }

    public CucumberElement firstChildOfType(int type) {
        for (int i = 0; i < children.size(); i++) {
            CucumberElement element = children.get(i);
            if (element.type == type) {
                return element;
            }
        }
        return null;
    }

    public void update(long time) {
        animation.update(isBeingDragged());
        updateSelf(time);
        for (CucumberElement child : children) {
            child.update(time);
        }
    }

    protected abstract void updateSelf(long time);

    public void render(Graphics g) {
        if (!animation.colorAnimation.isVisible()) {
            groupHeight = 0;
            return;
        }
        render(g, false, false);
        render(g, true, false);
    }

    public void render(Graphics g, boolean dragHighlightMode, boolean isParentDragged) {
        if (visible) {
            calculateRenderPosition(g);
        }
        boolean isDraggedOrHighlighted = isHighlighted() || isParentDragged || isBeingDragged();
        if (dragHighlightMode == isDraggedOrHighlighted) {
            renderBefore(g);
        }
        int selfHeight = groupHeight;
        for (CucumberElement child : children) {
            if (child.animation.colorAnimation.isVisible()) {
                child.render(g, dragHighlightMode, isParentDragged || isBeingDragged());
                groupHeight += child.groupHeight;
            }
        }
        if (isSelfOrParentFolded()) {
            groupHeight = selfHeight;
        }
        if (!visible) {
            groupHeight = 0;
        }
        if (dragHighlightMode == isDraggedOrHighlighted) {
            renderAfter(g);
        }
    }

    public void renderHints(Graphics g) {
        renderHintsInternal(g);
        for (CucumberElement child : children) {
            child.renderHints(g);
        }
    }

    public abstract String getTitle();

    public void setFailed() {
        isFailed = true;
    }

    public void setErrorMessage(String message) {
        this.errorMessage = message;
    }

    public void setErrorScreenshots(Image[] images) {
        errorScreenshots = images;
    }

    public abstract void trashElement();

    public CucumberElement findChild(String title) {
        for (CucumberElement child : children) {
            if (Util.stripPseudoNewLines(child.getTitle().trim()).equals(Util.stripPseudoNewLines(title.trim()))) {
                return child;
            }
        }
        return null;
    }

    public CucumberElement findElement(int x, int y) {
        if (isInsideRenderRect(x, y) && !(this instanceof CucumberRootElement) && visible) {
            return this;
        } else {
            for (CucumberElement child : children) {
                CucumberElement foundElement = child.findElement(x, y);
                if (foundElement != null) {
                    return foundElement;
                }
            }
            return null;
        }
    }

    public CucumberElement findElementRealPosition(int x, int y) {
        if (isInsideRealRect(x, y) && !(this instanceof CucumberRootElement) && visible) {
            return this;
        } else {
            for (CucumberElement child : children) {
                CucumberElement foundElement = child.findElementRealPosition(x, y);
                if (foundElement != null) {
                    return foundElement;
                }
            }
            return null;
        }
    }

    public abstract CucumberElement findGroup(int x, int y, int type);

    protected boolean isInsideGroupRect(int x, int y) {
        if ((isParentFolded() && !(this instanceof CucumberRootElement)) || !visible) {
            return false;
        }
        return x >= animation.moveAnimation.realX && y >= animation.moveAnimation.realY && x <= animation.moveAnimation.realX + renderWidth && y <= animation.moveAnimation.realY + groupHeight;
    }

    protected boolean isInsideRenderRect(int x, int y) {
        if (isParentFolded()) {
            return false;
        }
        return x >= animation.moveAnimation.renderX && y >= animation.moveAnimation.renderY && x <= animation.moveAnimation.renderX + renderWidth && y <= animation.moveAnimation.renderY + renderHeight;
    }

    protected boolean isInsideRealRect(int x, int y) {
        if (isParentFolded()) {
            return false;
        }
        return x >= animation.moveAnimation.realX && y >= animation.moveAnimation.realY && x <= animation.moveAnimation.realX + renderWidth && y <= animation.moveAnimation.realY + renderHeight + paddingHeight;
    }

    protected boolean isSelfOrParentFolded() {
        if (folded) {
            return true;
        }
        if (groupParent != null) {
            return groupParent.isSelfOrParentFolded();
        }
        return false;
    }

    protected boolean isParentFolded() {
        if (groupParent != null) {
            return groupParent.isSelfOrParentFolded();
        }
        return false;
    }

    public void clearFailedStatus() {
        isFailed = false;
        errorMessage = null;
        errorScreenshots = null;
        for (CucumberElement child : children) {
            child.clearFailedStatus();
        }
    }
    
    protected abstract void renderBefore(Graphics g);
    protected abstract void renderAfter(Graphics g);
    protected abstract void renderHintsInternal(Graphics g);

    protected abstract void calculateRenderPosition(Graphics g);

    protected abstract void foldFadeAnimation(float alpha);

    public abstract void click();

    public abstract boolean isDragable();
    protected abstract void applyDrag();

    private boolean highlighted = false;

    public boolean isBeingDragged() {
        return isDragged;
    }

    public void highlight(boolean toggle) {
        highlighted = toggle;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void startDrag() {
        isDragged = true;
        dragOffsetX = (int) animation.moveAnimation.renderX - CucumberMouseListener.mouseX;
        dragOffsetY = (int) animation.moveAnimation.renderY - CucumberMouseListener.mouseY;
    }

    public void endDrag() {
        isDragged = false;
    }

    public void applyDragOffset() {
        animation.moveAnimation.setRenderPosition(CucumberMouseListener.mouseX + dragOffsetX, CucumberMouseListener.mouseY + dragOffsetY);
        stickChildrenToParentRenderPosition(true);
        applyDrag();
    }

    public void stickChildrenToParentRenderPosition(boolean shouldStick) {
        for (CucumberElement child : children) {
            child.stickToParentRenderPosition(shouldStick);
        }
    }

    public void stickToParentRenderPosition(boolean shouldStick) {
        if (isBeingDragged()) {
            return;
        }
        this.shouldStickToParentRenderPosition = shouldStick;
        for (CucumberElement child : children) {
            child.stickToParentRenderPosition(shouldStick);
        }
    }

    public abstract void updateElementIndex(CucumberElement element, int index);

    protected abstract CucumberElement duplicate();

    public StringBuilder buildFeature() {
        StringBuilder sb = buildFeatureInternal();
        for (CucumberElement child : children) {
            sb.append(child.buildFeature());
        }
        if (type == CucumberTextElement.TYPE_FEATURE || type == CucumberTextElement.TYPE_BACKGROUND || type == CucumberTextElement.TYPE_SCENARIO) {
            sb.append("\n");
        }
        return sb;
    }
    
    protected abstract StringBuilder buildFeatureInternal();

    public void updateSteps() {
        updateStepsInternal();
        for (CucumberElement child : children) {
            child.updateSteps();
        }
    }

    protected abstract void updateStepsInternal();

    public abstract boolean export(File directory);
    public abstract boolean save();

    public void show() {
        if (visible) {
            return;
        }
        visible = true;
        animation.colorAnimation.setAlpha(CucumberAnimation.FADE_ALPHA_DEFAULT, CucumberAnimation.FADE_SPEED_APPEAR);
    }

    public void hide() {
        if (!visible) {
            return;
        }
        visible = false;
        animation.colorAnimation.setAlpha(0.0f, CucumberAnimation.FADE_SPEED_APPEAR);
    }

    public Set<String> getTags() {
        Set<String> tags = new HashSet<String>();
        tags.addAll(getTagsInternal());
        for (CucumberElement element : children) {
            tags.addAll(element.getTags());
        }
        return tags;
    }

    public abstract Set<String> getTagsInternal();
}
