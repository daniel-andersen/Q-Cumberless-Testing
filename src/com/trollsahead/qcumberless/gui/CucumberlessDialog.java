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

import com.trollsahead.qcumberless.engine.Engine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

public class CucumberlessDialog extends JFrame {
    public static Engine engine;
    public static JTextField elementTextField;
    public static JPanel mainPanel;

    public static CucumberlessDialog instance = null;

    private File fileChooserPath = null;

	public CucumberlessDialog() {
		super();
        instance = this;
        engine = new Engine();
		initComponents();
	}

    public void letThereBeLight() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, screenSize.width, screenSize.height);
        new Thread(engine).start();
        setVisible(true);
    }

	private void initComponents() {
        setVisible(false);
        setBackground(Color.BLACK);

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
        fileChooser.setCurrentDirectory(fileChooserPath == null ? new File(".") : fileChooserPath);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            fileChooserPath = fileChooser.getCurrentDirectory();
            return fileChooser.getSelectedFiles();
        } else {
            return null;
        }
    }

    public File directoryChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(fileChooserPath == null ? new File(".") : fileChooserPath);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            fileChooserPath = fileChooser.getCurrentDirectory();
            return fileChooser.getSelectedFile();
        } else {
            return null;
        }
    }
}
