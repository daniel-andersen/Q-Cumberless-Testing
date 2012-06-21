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

package com.trollsahead.qcumberless;

import com.trollsahead.qcumberless.engine.Engine;
import com.trollsahead.qcumberless.engine.HistoryEngine;
import com.trollsahead.qcumberless.gui.CucumberlessDialog;
import com.trollsahead.qcumberless.gui.Images;
import com.trollsahead.qcumberless.gui.Splash;
import com.trollsahead.qcumberless.model.Locale;
import com.trollsahead.qcumberless.plugins.HistoryPlugin;
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

        Splash.show();

        Images.initialize();
        wireDevicePlugins();
        wireHistoryPlugins();
        setLanguage();

        CucumberlessDialog frame = new CucumberlessDialog();
        frame.letThereBeLight();
    }

    private static void wireDevicePlugins() {
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
            e.printStackTrace();
            if (Util.isEmpty(Engine.plugins)) {
                System.out.println("No plugins found - using generic plugin!");
            } else {
                System.out.println("Not all plugins could be added!");
            }
        }
        Engine.initializePlugins();
    }

    private static void wireHistoryPlugins() {
        String pluginStr = ConfigurationManager.get("historyPlugins");
        if (Util.isEmpty(pluginStr)) {
            return;
        }
        String[] plugins = pluginStr.split("\\:");
        try {
            for (String plugin : plugins) {
                System.out.println("Adding history plugin: " + plugin);
                Class<HistoryPlugin> cls = (Class<HistoryPlugin>) Class.forName(plugin);
                Engine.historyEngine.plugins.add(cls.newInstance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        HistoryEngine.initializePlugins();
    }

    private static void setLanguage() {
        String locale = ConfigurationManager.get("locale");
        if (Util.isEmpty(locale)) {
            return;
        }
        System.out.println("Setting locale to: " + locale);
        Locale.setLocale(locale);
    }
}
