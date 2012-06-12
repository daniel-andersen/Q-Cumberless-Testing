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

package com.trollsahead.qcumberless.gui.elements;

import com.trollsahead.qcumberless.engine.DesignerEngine;
import com.trollsahead.qcumberless.gui.Animation;
import com.trollsahead.qcumberless.gui.CumberlessMouseListener;
import com.trollsahead.qcumberless.model.PlayResult;
import com.trollsahead.qcumberless.util.Util;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Element {
    public enum ColorScheme {DESIGN, PLAY}

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

    public Animation animation = new Animation();

    public Element groupParent;
    public List<Element> children = new ArrayList<Element>();

    public Element() {
    }

    public void addChild(Element element) {
        children.add(element);
        element.groupParent = this;
        if (this == DesignerEngine.stepsRoot) {
            element.animation.alphaAnimation.setAlpha(BaseBarElement.BAR_TRANSPARENCY, Animation.FADE_SPEED_ENTRANCE);
        }
    }

    public void addChild(Element element, int index) {
        children.add(index, element);
        element.groupParent = this;
    }

    public void removeChild(Element element) {
        if (element == null) {
            return;
        }
        if (children.contains(element)) {
            if (this == DesignerEngine.stepsRoot) {
                Element stepDefinitionElement = element.duplicate();
                if (stepDefinitionElement instanceof BaseBarElement) {
                    ((BaseBarElement) stepDefinitionElement).step.setShouldRenderKeyword(false);
                }
                if (element instanceof BaseBarElement) {
                    ((BaseBarElement) element).step.setShouldRenderKeyword(true);
                    element.toggleColorScheme();
                }
                DesignerEngine.stepsRoot.addChild(stepDefinitionElement, findChildIndex(element));
                element.folded = false;
                element.rootType = ROOT_FEATURE_EDITOR;
                //element.animation.sizeAnimation.setWidth(CucumberTextElement.calculateRenderWidthFromRoot(ROOT_FEATURE_EDITOR), CucumberAnimation.RESIZE_SPEED); // TODO! Causes flickers when inserting!
            }
            children.remove(element);
            return;
        }
        for (Element child : children) {
            child.removeChild(element);
        }
    }

    public int findChildIndex(Element element) {
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

    public Element findChildFromIndex(String step, int index) {
        for (int i = 0; i < children.size(); i++) {
            if (i < index) {
                continue;
            }
            Element child = children.get(i);
            if (child.getTitle().endsWith(step)) {
                return child;
            }
        }
        return null;
    }

    public Element firstChildOfType(int type) {
        for (int i = 0; i < children.size(); i++) {
            Element element = children.get(i);
            if (element.type == type) {
                return element;
            }
        }
        return null;
    }

    public void update(long time) {
        animation.update(isBeingDragged());
        updateSelf(time);
        for (Element child : children) {
            child.update(time);
        }
    }

    protected abstract void updateSelf(long time);

    public void render(Graphics2D g) {
        if (!animation.alphaAnimation.isVisible()) {
            groupHeight = 0;
            return;
        }
        render(g, false, false);
        render(g, true, false);
    }

    public void render(Graphics2D g, boolean dragHighlightMode, boolean isParentDragged) {
        if (visible) {
            calculateRenderPosition(g);
        }
        boolean isDraggedOrHighlighted = isHighlighted() || isParentDragged || isBeingDragged();
        if (dragHighlightMode == isDraggedOrHighlighted) {
            renderBefore(g);
        }
        int selfHeight = groupHeight;
        for (Element child : children) {
            if (child.animation.alphaAnimation.isVisible()) {
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

    public void renderHints(Graphics2D g) {
        renderHintsInternal(g);
        for (Element child : children) {
            child.renderHints(g);
        }
    }

    public abstract String getTitle();

    public void setFailed() {
        setPlayResult(PlayResult.State.FAILED);
        if (groupParent != null) {
            groupParent.setFailed();
        }
    }

    public void setSuccess() {
        setPlayResult(PlayResult.State.SUCCESS);
    }

    public abstract void trashElement();

    public Element findChild(String title) {
        for (Element child : children) {
            if (Util.stripPseudoNewLines(child.getTitle().trim()).equals(Util.stripPseudoNewLines(title.trim()))) {
                return child;
            }
        }
        return null;
    }

    public Element findElement(int x, int y) {
        if (isInsideRenderRect(x, y) && !(this instanceof RootElement) && visible) {
            return this;
        } else {
            for (Element child : children) {
                Element foundElement = child.findElement(x, y);
                if (foundElement != null) {
                    return foundElement;
                }
            }
            return null;
        }
    }

    public Element findElementRealPosition(int x, int y) {
        if (isInsideRealRect(x, y) && !(this instanceof RootElement) && visible) {
            return this;
        } else {
            for (Element child : children) {
                Element foundElement = child.findElementRealPosition(x, y);
                if (foundElement != null) {
                    return foundElement;
                }
            }
            return null;
        }
    }

    public abstract Element findGroup(int x, int y, int type);

    protected boolean isInsideGroupRect(int x, int y) {
        if (isParentFolded() || !visible) {
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

    public boolean isSelfOrParentFolded() {
        if (folded) {
            return true;
        }
        if (groupParent != null) {
            return groupParent.isSelfOrParentFolded();
        }
        return false;
    }

    public boolean isParentFolded() {
        if (groupParent != null) {
            return groupParent.isSelfOrParentFolded();
        }
        return false;
    }

    public abstract void fold();
    public abstract void unfold();

    public void foldAll() {
        fold();
        for (Element child : children) {
            child.foldAll();
        }
    }

    public void unfoldAll() {
        unfold();
        for (Element child : children) {
            child.unfoldAll();
        }
    }

    public boolean isFolded() {
        return folded;
    }

    public void clearRunStatus() {
        for (Element child : children) {
            child.clearRunStatus();
        }
    }
    
    protected abstract void renderBefore(Graphics2D g);
    protected abstract void renderAfter(Graphics2D g);
    protected abstract void renderHintsInternal(Graphics2D g);

    protected abstract void calculateRenderPosition(Graphics2D g);

    protected abstract void foldFadeAnimation(float alpha);

    public abstract void click(int clickCount);

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

    public void startDrag(boolean isControlDown) {
        isDragged = true;
        dragOffsetX = (int) animation.moveAnimation.renderX - CumberlessMouseListener.mouseX;
        dragOffsetY = (int) animation.moveAnimation.renderY - CumberlessMouseListener.mouseY;
    }

    public void endDrag() {
        isDragged = false;
    }

    public void applyDragOffset() {
        animation.moveAnimation.setRenderPosition(CumberlessMouseListener.mouseX + dragOffsetX, CumberlessMouseListener.mouseY + dragOffsetY);
        stickChildrenToParentRenderPosition(true);
        applyDrag();
    }

    public void stickChildrenToParentRenderPosition(boolean shouldStick) {
        for (Element child : children) {
            child.stickToParentRenderPosition(shouldStick);
        }
    }

    public void stickToParentRenderPosition(boolean shouldStick) {
        if (isBeingDragged()) {
            return;
        }
        this.shouldStickToParentRenderPosition = shouldStick;
        for (Element child : children) {
            child.stickToParentRenderPosition(shouldStick);
        }
    }

    public abstract void updateElementIndex(Element element, int index);

    public abstract Element duplicate();

    public StringBuilder buildFeature(boolean addRunOutcome) {
        return buildFeature(addRunOutcome, System.currentTimeMillis());
    }
    
    public StringBuilder buildFeature(boolean addRunOutcome, long time) {
        StringBuilder sb = buildFeatureInternal(addRunOutcome, time);
        for (Element child : children) {
            sb.append(child.buildFeature(addRunOutcome, time));
        }
        if (type == BaseBarElement.TYPE_FEATURE || type == BaseBarElement.TYPE_BACKGROUND || type == BaseBarElement.TYPE_SCENARIO || type == BaseBarElement.TYPE_SCENARIO_OUTLINE) {
            sb.append("\n");
        }
        return sb;
    }

    protected abstract StringBuilder buildFeatureInternal(boolean addRunOutcome, long time);

    public void updateSteps() {
        updateStepsInternal();
        for (Element child : children) {
            child.updateSteps();
        }
    }

    protected abstract void updateStepsInternal();

    public abstract boolean export(File directory);
    public abstract boolean save();

    public void show(boolean animate) {
        if (!animate) {
            shouldStickToParentRenderPosition = true;
        }
        if (visible) {
            return;
        }
        visible = true;
        if (animate) {
            animation.alphaAnimation.setAlpha(BaseBarElement.BAR_TRANSPARENCY, Animation.FADE_SPEED_APPEAR);
        } else {
            animation.alphaAnimation.setAlpha(BaseBarElement.BAR_TRANSPARENCY);
        }
    }

    public void hide(boolean animate) {
        if (!animate) {
            shouldStickToParentRenderPosition = true;
        }
        if (!visible) {
            return;
        }
        visible = false;
        if (animate) {
            animation.alphaAnimation.setAlpha(0.0f, Animation.FADE_SPEED_APPEAR);
        } else {
            animation.alphaAnimation.setAlpha(0.0f);
        }
    }

    public void hideChildren(boolean animate) {
        for (Element child : children) {
            child.hide(animate);
            child.hideChildren(animate);
        }
    }

    public Set<String> getTags(int ... typeFilter) {
        Set<String> tags = new HashSet<String>();
        if (typeFilter == null || typeFilter.length == 0 || Util.isInArray(type, typeFilter)) {
            tags.addAll(getTagsInternal());
        }
        for (Element element : children) {
            tags.addAll(element.getTags(typeFilter));
        }
        return tags;
    }

    public abstract Set<String> getTagsInternal();

    public boolean containsAnyOfTags(String tags) {
        for (String tag : Util.stringToTagList(tags)) {
            if (containsTag(tag)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsTag(String tag) {
        for (String otherTag : getTags()) {
            if (tag.equals("@" + otherTag)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isVisible() {
        return visible;
    }

    public void setPlayStateIncludingChildren(PlayResult.State playState) {
        setPlayResult(playState);
        for (Element element : children) {
            element.setPlayStateIncludingChildren(playState);
        }
    }

    public abstract void setPlayResult(PlayResult.State playState);

    public void toggleColorScheme() {
        toggleColorSchemeInternal();
        for (Element element : children) {
            element.toggleColorScheme();
        }
    }

    public abstract void toggleColorSchemeInternal();

    public boolean filter(String regexp) {
        boolean anyChildrenVisible = false;
        for (Element child : children) {
            if (child.filter(regexp)) {
                anyChildrenVisible = true;
            }
        }
        if (getTitle().matches(regexp) || anyChildrenVisible) {
            show(true);
            anyChildrenVisible = true;
        } else {
            hide(true);
        }
        return anyChildrenVisible;
    }
}
