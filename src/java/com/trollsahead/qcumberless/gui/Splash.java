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

import com.trollsahead.qcumberless.util.ConfigurationManager;
import com.trollsahead.qcumberless.util.Util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;

public class Splash {
    private static final long SPLASH_LENGTH = 3L * 1000L;

    private static JFrame frame = null;

    public static void show() {
        String showSplash = ConfigurationManager.get("splashShow");
        if ("false".equalsIgnoreCase(showSplash) || "no".equalsIgnoreCase(showSplash)) {
            return;
        }
        final Image splashImage;
        try {
            String splashFilename = ConfigurationManager.get("splashFilename");
            if (Util.isEmpty(splashFilename)) {
                splashImage = ImageIO.read(Splash.class.getResource("/resources/pictures/splash.png"));
            } else {
                splashImage = ImageIO.read(new FileInputStream(new File(splashFilename)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = splashImage.getWidth(null);
        int height = splashImage.getHeight(null);
        int x = (screenSize.width - width) / 2;
        int y = (screenSize.height - height) / 2;
        frame = new JFrame("Splash");
        frame.getContentPane().add(new JPanel() {
            public void paintComponent(Graphics g) {
                g.drawImage(splashImage, 0, 0, null);
            }
        });
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
        frame.setBounds(x, y, width, height);
        frame.setVisible(true);
        new Thread(new Runnable() {
            public void run() {
                Util.sleep(SPLASH_LENGTH);
                if (frame == null) {
                    return;
                }
                frame.setVisible(false);
                frame = null;
            }
        }).start();
    }
}
