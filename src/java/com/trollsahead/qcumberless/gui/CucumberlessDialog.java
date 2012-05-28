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
import com.trollsahead.qcumberless.util.ConfigurationManager;
import com.trollsahead.qcumberless.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class CucumberlessDialog extends JFrame {
    public static Engine engine;
    public static JTextField elementTextField;
    public static JPanel mainPanel;

    public static CucumberlessDialog instance = null;

	public CucumberlessDialog() {
		super();
        instance = this;
        engine = new Engine();
		initComponents();
	}

    public void letThereBeLight() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, screenSize.width, screenSize.height);
        setExtendedState(MAXIMIZED_BOTH);
        new Thread(engine).start();
        Util.sleep(1000); // For flicker not to happen
        setVisible(true);
        engine.show();
    }

	private void initComponents() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                ConfigurationManager.saveConfiguration();
                System.out.println("Bye!");
                System.exit(0);
            }
        });

        mainPanel = new JPanel();

        elementTextField = new JTextField();
        elementTextField.setVisible(false);
        elementTextField.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent keyEvent) {
                EditBox.setText(elementTextField.getText());
            }

            public void keyPressed(KeyEvent keyEvent) {
                EditBox.setText(elementTextField.getText());
            }

            public void keyReleased(KeyEvent keyEvent) {
                EditBox.setText(elementTextField.getText());
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER || keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    EditBox.hide();
                }
            }
        });

		setTitle("Q-Cumberless Testing");

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(engine.getCanvas(), BorderLayout.CENTER);
        mainPanel.add(elementTextField, BorderLayout.SOUTH);

		contentPane.add(mainPanel, BorderLayout.CENTER);

		setLocationRelativeTo(getOwner());
	}

    public File[] fileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(getFeaturesPath()));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            ConfigurationManager.put("featuresPath", fileChooser.getCurrentDirectory().getAbsolutePath());
            return fileChooser.getSelectedFiles();
        } else {
            return null;
        }
    }

    public File directoryChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(getFeaturesPath()));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            ConfigurationManager.put("featuresPath", fileChooser.getCurrentDirectory().getAbsolutePath());
            return fileChooser.getSelectedFile();
        } else {
            return null;
        }
    }

    private static String getFeaturesPath() {
        String path = ConfigurationManager.get("featuresPath");
        return !Util.isEmpty(path) ? path : ".";
    }
    
    public static void close() {
        if (instance != null) {
            WindowEvent windowClosing = new WindowEvent(instance, WindowEvent.WINDOW_CLOSING);
            instance.dispatchEvent(windowClosing);
        }
    }

    public String askFeatureFilename() {
        String filename = (String) JOptionPane.showInputDialog(
                this,
                Util.isEmpty(Engine.featuresBaseDir) ? "Enter filename with full path" : ("Enter filename relative to features directory:\n" + Engine.featuresBaseDir),
                "Enter filename",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "noname.feature");
        if (!Util.isEmpty(filename) && !Util.isEmpty(Engine.featuresBaseDir)) {
            return Util.addSlashToPath(Engine.featuresBaseDir) + Util.removeTrailingSlash(filename);
        } else {
            return filename;
        }
    }
}
