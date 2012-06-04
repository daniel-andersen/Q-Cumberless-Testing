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

import com.trollsahead.qcumberless.engine.Engine;
import com.trollsahead.qcumberless.engine.Player;
import com.trollsahead.qcumberless.gui.Animation;
import com.trollsahead.qcumberless.gui.CumberlessMouseListener;
import com.trollsahead.qcumberless.gui.EditBox;
import com.trollsahead.qcumberless.gui.GuiUtil;
import com.trollsahead.qcumberless.util.ElementHelper;
import com.trollsahead.qcumberless.util.Util;

import static com.trollsahead.qcumberless.gui.elements.Element.PlayColorState;

import java.awt.*;

public class Table {
    private static final int MINIMUM_CELL_WIDTH = 50;

    private static final int CELL_PADDING_HORIZONTAL = 10;
    private static final int CELL_PADDING_VERTICAL = 2;

    private static final Color COLOR_LINE = new Color(0.0f, 0.0f, 0.0f, 0.3f);
    private static final Color COLOR_TEXT = new Color(0.0f, 0.0f, 0.0f, 1.0f);
    private static final Color COLOR_BG_HIGHLIGHT = new Color(0.0f, 0.0f, 0.0f, 0.2f);
    private static final Color COLOR_BG_EXAMPLES_HEADER = new Color(0.5f, 0.5f, 0.5f, 0.8f);

    public int rows;
    public int cols;

    public int highlightedRow;
    public int highlightedCol;

    private int width;
    private int height;

    private Cell[][] cells;
    private int[] colWidth;
    private int[] colCharWidth;
    private int rowHeight;

    private Cell editedCell = null;

    private BaseBarElement parent;
    private PlayColorState[] colorState;
    private String[] errorMessage;

    public Table(BaseBarElement parent) {
        this(parent, 2, 2);
    }

    public Table(BaseBarElement parent, String[] row) {
        this(parent, row.length, 1);
        for (int j = 0; j < row.length; j++) {
            cells[0][j] = new Cell(row[j]);
        }
    }

    public Table(BaseBarElement parent, int cols, int rows) {
        this.parent = parent;
        this.cols = cols;
        this.rows = rows;
        cells = null;
        adjustTableSize(cols, rows);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    public void render(Graphics2D g, int offsetX, int offsetY) {
        calculateCellBounds(offsetX, offsetY);
        findHighlightedCell();
        //renderExamplesHeader(g);
        renderPlayState(g);
        renderHighlightedCell(g);
        renderGrid(g, offsetX, offsetY);
        renderPlayingRow(g);
        renderText(g);
    }

    public void renderHints(Graphics2D g) {
        renderErrorMessage(g);
    }

    private void renderErrorMessage(Graphics2D g) {
        if (highlightedRow == -1) {
            return;
        }
        if (!Util.isEmpty(errorMessage[highlightedRow])) {
            parent.drawHint(g, errorMessage[highlightedRow], CumberlessMouseListener.mouseX + 15, CumberlessMouseListener.mouseY, BaseBarElement.COLOR_TEXT_ERROR_MESSAGE, BaseBarElement.COLOR_BG_ERROR_MESSAGE);
        }
    }

    private void calculateCellBounds(int x, int y) {
        int h = y;
        for (int i = 0; i < rows; i++) {
            int w = x;
            for (int j = 0; j < cols; j++) {
                cells[i][j].setBounds(w, h, colWidth[j], rowHeight);
                w += colWidth[j];
            }
            h += rowHeight;
        }
    }

    private void findHighlightedCell() {
        highlightedCol = -1;
        highlightedRow = -1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = (int) parent.animation.moveAnimation.renderX + cells[i][j].x;
                int y = (int) parent.animation.moveAnimation.renderY + cells[i][j].y;
                cells[i][j].highlighted =
                        ElementHelper.isElementsHighlightable() &&
                        CumberlessMouseListener.mouseX >= x && CumberlessMouseListener.mouseX < x + cells[i][j].width &&
                        CumberlessMouseListener.mouseY >= y && CumberlessMouseListener.mouseY < y + cells[i][j].height;
                if (cells[i][j].highlighted) {
                    highlightedCol = j;
                    highlightedRow = i;
                }
            }
        }
    }

    private void renderExamplesHeader(Graphics2D g) {
        if (!(parent instanceof ExamplesElement) || rows < 1) {
            return;
        }
        g.setColor(COLOR_BG_EXAMPLES_HEADER);
        g.fillRect(cells[0][0].x, cells[0][0].y, width, cells[0][0].height);
    }

    private void renderPlayState(Graphics2D g) {
        if (Engine.colorScheme != Element.ColorScheme.PLAY) {
            return;
        }
        for (int i = 0; i < rows; i++) {
            if (colorState[i] == PlayColorState.NOT_YET_PLAYED) {
                continue;
            }
            g.setColor(colorState[i] == PlayColorState.SUCCESS ? BaseBarElement.BAR_COLOR_SUCCESS : BaseBarElement.BAR_COLOR_FAILURE);
            g.fillRect(cells[i][0].x, cells[i][0].y, width, cells[i][0].height);
        }
    }

    private void renderHighlightedCell(Graphics2D g) {
        if (highlightedCol == -1 || highlightedRow == -1) {
            return;
        }
        g.setColor(Util.blendColorKeepAlpha(parent.getNormalBackgroundColor(), COLOR_BG_HIGHLIGHT));
        g.fillRect(cells[highlightedRow][highlightedCol].x, cells[highlightedRow][highlightedCol].y,
                   cells[highlightedRow][highlightedCol].width, cells[highlightedRow][highlightedCol].height);
    }

    private void renderPlayingRow(Graphics2D g) {
        for (int i = 0; i < rows; i++) {
            if (!Player.isPlayingExampleRow(parent, i)) {
                continue;
            }
            GuiUtil.drawSquareBorder(g, cells[i][0].x, cells[i][0].y, width, cells[i][0].height, 0, BaseBarElement.COLOR_BORDER_PLAYING, BaseBarElement.BORDER_STROKE_WIDTH);
            Stroke oldStroke = Animation.setStrokeAnimation(g, BaseBarElement.PLAY_ANIMATION_DASH_LENGTH, BaseBarElement.BORDER_STROKE_WIDTH, BaseBarElement.PLAY_ANIMATION_SPEED);
            g.setColor(Player.getPlayingColor(parent));
            g.drawRect(cells[i][0].x, cells[i][0].y, width, cells[i][0].height);
            g.setStroke(oldStroke);
        }
    }

    private void renderGrid(Graphics2D g, int x, int y) {
        g.setColor(Util.blendColorKeepAlpha(parent.getNormalBackgroundColor(), COLOR_LINE));
        for (int i = 0; i < rows + 1; i++) {
            int h = i * rowHeight;
            g.drawLine(x, y + h, x + width, y + h);
        }
        int w = 0;
        for (int j = 0; j < cols + 1; j++) {
            g.drawLine(x + w, y, x + w, y + height);
            w += j < cols ? colWidth[j] : 0;
        }
    }

    private void renderText(Graphics2D g) {
        g.setColor(COLOR_TEXT);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                g.drawString(cells[i][j].text, cells[i][j].x + CELL_PADDING_HORIZONTAL, cells[i][j].y + Engine.fontMetrics.getHeight() - 3 + CELL_PADDING_VERTICAL);
            }
        }
    }

    public void calculateCellSize() {
        width = 0;
        for (int j = 0; j < cols; j++) {
            colWidth[j] = MINIMUM_CELL_WIDTH;
            colCharWidth[j] = 0;
            for (int i = 0; i < rows; i++) {
                int width = cells[i][j].getWidth();
                if (width > colWidth[j]) {
                    colWidth[j] = width;
                }
                int charWidth = !Util.isEmpty(cells[i][j].text) ? cells[i][j].text.length() : 0;
                if (charWidth > colCharWidth[j]) {
                    colCharWidth[j] = charWidth;
                }
            }
            width += colWidth[j];
        }
        rowHeight = Engine.fontMetrics.getHeight() + (CELL_PADDING_VERTICAL * 2);
        height = rows * rowHeight;
    }

    public boolean click() {
        if (highlightedCol == -1 || highlightedRow == -1) {
            return false;
        }
        editedCell = cells[highlightedRow][highlightedCol];
        EditBox.showEditTable(this);
        return false;
    }

    public String getEditedCellText() {
        return editedCell.text;
    }

    public void setEditedCellText(String text) {
        editedCell.text = text;
        calculateCellSize();
    }

    public StringBuilder buildFeature() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            sb.append(ElementHelper.EXPORT_INDENT).append(ElementHelper.EXPORT_INDENT);
            sb.append("  |");
            for (int j = 0; j < cols; j++) {
                if (!Util.isEmpty(cells[i][j].text)) {
                    sb.append(" ");
                    sb.append(cells[i][j].text);
                    sb.append(Util.fillChar(' ', colCharWidth[j] + 1 - cells[i][j].text.length()));
                } else {
                    sb.append(Util.fillChar(' ', colCharWidth[j] + 2));
                }
                sb.append("|");
            }
            sb.append("\n");
        }
        return sb;
    }

    public void addRow(String[] row) {
        int newWidth = cells != null && cells.length > 0 ? Math.max(cells[0].length, row.length) : row.length;
        int newHeight = cells != null ? cells.length + 1 : 1;
        adjustTableSize(newWidth, newHeight);
        for (int j = 0; j < newWidth; j++) {
            cells[newHeight - 1][j] = new Cell(row[j]);
        }
    }

    public void adjustTableSize(String action) {
        if ("Add row".equalsIgnoreCase(action)) {
            adjustTableSize(cols, rows + 1);
        } else if ("Add column".equalsIgnoreCase(action)) {
            adjustTableSize(cols + 1, rows);
        } else if ("Delete row".equalsIgnoreCase(action)) {
            if (rows > 1 || !(parent instanceof ExamplesElement)) {
                adjustTableSize(cols, Math.max(rows - 1, 1));
            }
        } else if ("Delete column".equalsIgnoreCase(action)) {
            adjustTableSize(Math.max(cols - 1, 1), rows);
        }
    }

    private void adjustTableSize(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        colWidth = new int[cols];
        colCharWidth = new int[cols];
        colorState = new PlayColorState[rows];
        errorMessage = new String[rows];
        Cell[][] newCells = new Cell[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (cells != null && i < cells.length && j < cells[i].length) {
                    newCells[i][j] = cells[i][j];
                } else {
                    newCells[i][j] = new Cell();
                }
            }
            colorState[i] = PlayColorState.NOT_YET_PLAYED;
            errorMessage[i] = null;
        }
        cells = newCells;
        calculateCellSize();
    }

    public Table duplicate() {
        Table newTable = new Table(parent, cols, rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                newTable.cells[i][j] = new Cell(cells[i][j].text);
            }
            newTable.colorState[i] = PlayColorState.NOT_YET_PLAYED;
            newTable.errorMessage[i] = null;
        }
        newTable.calculateCellSize();
        return newTable;
    }

    public boolean isFailed(int row) {
        return colorState[row] == PlayColorState.FAILURE;
    }

    public void setNotYetPlayed(int row) {
        this.colorState[row] = PlayColorState.NOT_YET_PLAYED;
        this.errorMessage[row] = null;
    }

    public void setSuccess(int row) {
        this.colorState[row] = PlayColorState.SUCCESS;
        this.errorMessage[row] = null;
    }

    public void setFailed(int row, String errorMessage) {
        this.colorState[row] = PlayColorState.FAILURE;
        this.errorMessage[row] = errorMessage;
    }

    public void clearRunStatus() {
        for (int i = 0; i < rows; i++) {
            setNotYetPlayed(i);
        }
    }

    private class Cell {
        String text;
        boolean highlighted;
        int x;
        int y;
        int width;
        int height;

        public Cell() {
            text = "";
        }
        
        public Cell(String text) {
            this.text = text;
        }

        public int getWidth() {
            if (!Util.isEmpty(text)) {
                return Math.max(Engine.fontMetrics.stringWidth(text) + (CELL_PADDING_HORIZONTAL * 2), MINIMUM_CELL_WIDTH);
            } else {
                return MINIMUM_CELL_WIDTH;
            }
        }

        public void setBounds(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
