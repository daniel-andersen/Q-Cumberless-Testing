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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Images {
    public enum ThumbnailState {NORMAL, HIGHLIGHTED, PRESSED, DISABLED}

    public static int IMAGE_BACKGROUND = 0;
    public static int IMAGE_TRASHCAN = 1;
    public static int IMAGE_PLAY = 2;
    public static int IMAGE_ADD = 3;
    public static int IMAGE_MINUS = 4;
    public static int IMAGE_AT = 5;
    public static int IMAGE_EDIT = 6;
    public static int IMAGE_EXPAND = 7;
    public static int IMAGE_PAUSE = 8;
    public static int IMAGE_RESUME = 9;
    public static int IMAGE_STEP = 10;
    public static int IMAGE_STOP = 11;
    public static int IMAGE_SPOTLIGHT = 12;
    public static int IMAGE_TABLE = 13;
    public static int IMAGE_TERMINAL = 14;
    public static int IMAGE_FILTER = 15;
    public static int IMAGE_PALETTE = 16;
    public static int IMAGE_TIMEGLASS = 17;
    public static int IMAGE_ARROW_LEFT = 18;
    public static int IMAGE_ARROW_RIGHT = 19;
    public static int IMAGE_INTERACTIVE_DESIGNER = 20;
    public static int IMAGE_STEP_SMALL = 21;

    private static Map<Integer, BufferedImage[]> images;
    private static Map<Integer, Graphics2D[]> imageGraphics;

    public static void initialize() throws Exception {
        images = new HashMap<Integer, BufferedImage[]>();
        images.put(IMAGE_BACKGROUND, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/background.jpg"))
        });
        images.put(IMAGE_TRASHCAN, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/trashcan_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/trashcan_highlight.png"))
        });
        images.put(IMAGE_PLAY, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/play_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/play_highlight.png"))
        });
        images.put(IMAGE_ADD, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/add_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/add_highlight.png"))
        });
        images.put(IMAGE_MINUS, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/minus_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/minus_highlight.png"))
        });
        images.put(IMAGE_AT, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/at_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/at_highlight.png"))
        });
        images.put(IMAGE_EDIT, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/edit_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/edit_highlight.png"))
        });
        images.put(IMAGE_TABLE, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/table_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/table_highlight.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/table_pressed.png"))
        });
        images.put(IMAGE_EXPAND, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/expand_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/expand_highlight.png"))
        });
        images.put(IMAGE_TERMINAL, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/terminal_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/terminal_highlight.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/terminal_pressed.png"))
        });
        images.put(IMAGE_PAUSE, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/pause_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/pause_highlight.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/pause_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/pause_disabled.png"))
        });
        images.put(IMAGE_RESUME, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/resume_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/resume_highlight.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/resume_normal.png"))
        });
        images.put(IMAGE_STEP, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/step_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/step_highlight.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/step_normal.png"))
        });
        images.put(IMAGE_STEP_SMALL, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/step_small_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/step_small_highlight.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/step_small_normal.png"))
        });
        images.put(IMAGE_STOP, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/stop_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/stop_highlight.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/stop_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/stop_disabled.png"))
        });
        images.put(IMAGE_SPOTLIGHT, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/spotlight.png"))
        });
        images.put(IMAGE_FILTER, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/filter_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/filter_highlight.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/filter_pressed.png"))
        });
        images.put(IMAGE_PALETTE, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/palette_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/palette_highlight.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/palette_pressed.png"))
        });
        images.put(IMAGE_TIMEGLASS, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/timeglass_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/timeglass_highlight.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/timeglass_pressed.png"))
        });
        images.put(IMAGE_ARROW_LEFT, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/arrow_left_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/arrow_left_highlight.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/arrow_left_pressed.png"))
        });
        images.put(IMAGE_ARROW_RIGHT, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/arrow_right_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/arrow_right_highlight.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/arrow_right_pressed.png"))
        });
        images.put(IMAGE_INTERACTIVE_DESIGNER, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/resources/pictures/interactive_designer_normal.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/interactive_designer_highlight.png")),
                ImageIO.read(Images.class.getResource("/resources/pictures/interactive_designer_pressed.png"))
        });
        imageGraphics = new HashMap<Integer, Graphics2D[]>();
        for (int key : images.keySet()) {
            BufferedImage[] imageArray = images.get(key);
            Graphics2D[] graphicsArray = new Graphics2D[imageArray.length];
            for (int i = 0; i < imageArray.length; i++) {
                graphicsArray[i] = imageArray[i].createGraphics();
                graphicsArray[i].setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
            }
            imageGraphics.put(key, graphicsArray);
        }
    }

    public static BufferedImage getImage(int image, int type) {
        return images.get(image)[type];
    }

    public static Graphics2D getImageGraphics(int image, int type) {
        return imageGraphics.get(image)[type];
    }
}
