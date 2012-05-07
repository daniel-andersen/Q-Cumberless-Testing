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

package com.trollsahead.qcumberless.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class ConfigurationManager {
    private static final String CONF_FILENAME = "qcumberless.conf";

    private static Properties internProperties = new Properties();
    private static Properties externProperties = new Properties();

    public static void loadConfiguration() {
        try {
            internProperties.load(ConfigurationManager.class.getResourceAsStream("/resources/" + CONF_FILENAME));
        } catch (Exception e) {
            System.out.println("No internal properties found");
        }
        try {
            externProperties.load(new FileInputStream(CONF_FILENAME));
        } catch (Exception e) {
            System.out.println("No external properties found");
        }
    }
    
    public static void saveConfiguration() {
        if (externProperties == null || externProperties.size() == 0) {
            return;
        }
        try {
            System.out.println("Saving external properties");
            externProperties.store(new FileOutputStream(CONF_FILENAME), "Q-Cumberless Testing configuration");
        } catch (Exception e) {
            System.out.println("Could not save properties!");
        }
    }

    public static String get(String key) {
        String externValue = (String) externProperties.get(key);
        if (!Util.isEmpty(externValue)) {
            return externValue;
        } else {
            return (String) internProperties.get(key);
        }
    }

    public static void put(String key, String value) {
        externProperties.put(key, value);
    }
}
