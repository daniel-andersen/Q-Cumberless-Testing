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

package com.trollsahead.qcumberless;

import com.trollsahead.qcumberless.engine.Engine;
import com.trollsahead.qcumberless.gui.CucumberlessDialog;
import com.trollsahead.qcumberless.gui.Images;
import com.trollsahead.qcumberless.plugins.Plugin;
import com.trollsahead.qcumberless.util.ConfigurationManager;
import com.trollsahead.qcumberless.util.Util;

import javax.swing.*;

public class Main {
    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ConfigurationManager.loadConfiguration();

        Images.initialize();

        wirePlugins();

        CucumberlessDialog frame = new CucumberlessDialog();
        frame.letThereBeLight();
    }
    
    private static void wirePlugins() {
        String pluginStr = ConfigurationManager.get("plugins");
        if (Util.isEmpty(pluginStr)) {
            return;
        }
        String[] plugins = pluginStr.split("\\:");
        try {
            for (String plugin : plugins) {
                System.out.println("Adding plugin: " + plugin);
                Class<Plugin> cls = (Class<Plugin>) Class.forName(plugin);
                Engine.plugins.add(cls.newInstance());
            }
        } catch (Exception e) {
            System.out.println("No plugins found - using generic plugin!");
        }
        Engine.initializePlugins();
    }
}
