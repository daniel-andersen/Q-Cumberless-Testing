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
//
// Copyright 2012

// Daniel Andersen (dani_ande@yahoo.dk)

package com.trollsahead.qcumberless.gui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Images {
    public static int TYPE_NORMAL = 0;
    public static int TYPE_HIGHLIGHT = 1;

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
    public static int IMAGE_STOP = 10;
    public static int IMAGE_SPOTLIGHT = 11;

    private static Map<Integer, BufferedImage[]> images;
    private static Map<Integer, Graphics2D[]> imageGraphics;

    public static void initialize() throws Exception {
        images = new HashMap<Integer, BufferedImage[]>();
        images.put(IMAGE_BACKGROUND, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/pictures/background.jpg"))
        });
        images.put(IMAGE_TRASHCAN, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/pictures/trashcan_normal.png")),
                ImageIO.read(Images.class.getResource("/pictures/trashcan_highlight.png"))
        });
        images.put(IMAGE_PLAY, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/pictures/play_normal.png")),
                ImageIO.read(Images.class.getResource("/pictures/play_highlight.png"))
        });
        images.put(IMAGE_ADD, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/pictures/add_normal.png")),
                ImageIO.read(Images.class.getResource("/pictures/add_highlight.png"))
        });
        images.put(IMAGE_MINUS, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/pictures/minus_normal.png")),
                ImageIO.read(Images.class.getResource("/pictures/minus_highlight.png"))
        });
        images.put(IMAGE_AT, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/pictures/at_normal.png")),
                ImageIO.read(Images.class.getResource("/pictures/at_highlight.png"))
        });
        images.put(IMAGE_EDIT, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/pictures/edit_normal.png")),
                ImageIO.read(Images.class.getResource("/pictures/edit_highlight.png"))
        });
        images.put(IMAGE_EXPAND, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/pictures/expand_normal.png")),
                ImageIO.read(Images.class.getResource("/pictures/expand_highlight.png"))
        });
        images.put(IMAGE_PAUSE, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/pictures/pause_normal.png")),
                ImageIO.read(Images.class.getResource("/pictures/pause_highlight.png"))
        });
        images.put(IMAGE_RESUME, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/pictures/resume_normal.png")),
                ImageIO.read(Images.class.getResource("/pictures/resume_highlight.png"))
        });
        images.put(IMAGE_STOP, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/pictures/stop_normal.png")),
                ImageIO.read(Images.class.getResource("/pictures/stop_highlight.png"))
        });
        images.put(IMAGE_SPOTLIGHT, new BufferedImage[] {
                ImageIO.read(Images.class.getResource("/pictures/spotlight.png"))
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
