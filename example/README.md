Example pluging
===============

This example illustrates how to write your own custom plugin for Q-Cumberless Testing. It uses the [calabash-android](https://github.com/calabash/calabash-android) framework to enable testing on Android-devices.

Up and Running
==============

This guide assumes you have already setup the Android SDK and downloaded _calabash-android_ on your computer. It further assumes that you are on a `bash`-friendly system such as Linux or Mac OS X.

1. Clone or download the [source code](https://github.com/black-knight/Q-Cumberless-Testing/zipball/master).
2. Run `ant` from the root of the source directory. This will compile the Q-Cumberless Testing library.
3. In _example/android_ run `./configure_calabash`. This will compile the project and setup calabash. If the configuration doesn't match your system you should manually run `calabash-android setup`.
4. Run `./run.sh` and Test Q-Cumberless! :)

