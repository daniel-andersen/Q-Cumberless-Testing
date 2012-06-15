#!/usr/bin/bash
ant release
echo "{\"activity_name\":\"com.example.helloworld.HelloWorld\",\"app_path\":\"$PWD/bin/HelloWorld-release.apk\",\"api_level\":\"10\",\"package_name\":\"com.example.helloworld\",\"keystore_location\":\"$PWD/example.keystore\",\"keystore_password\":\"verysecret\",\"keystore_alias\":\"example\",\"keystore_alias_password\":\"verysecret\"}" > .calabash_settings
calabash-android build
