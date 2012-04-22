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

package com.trollsahead.qcumberless.device;

import com.trollsahead.qcumberless.model.CucumberStep;

import java.awt.*;

public interface CucumberDeviceCallback {
    void onPlay();
    void onPause();
    void onResume();
    void onStop();

    void afterPlayed();
    void afterPlayFailed(String errorMessage);

    void beforeFeatures();

    void beforeFeature(String name);
    void afterFeature();
    void afterFeatureFailed();

    void beforeScenario(String name);
    void afterScenario();
    void afterScenarioFailed();

    void beforeBackground(String name);
    void afterBackground();
    void afterBackgroundFailed(String errorMessage);

    void beforeStep(String name);
    void afterStep(String name);
    void afterStepFailed(String errorMessage);
    
    void attachScreenshots(CucumberStep step, Image... screenshots);

    CucumberStep getCurrentStep();
}
