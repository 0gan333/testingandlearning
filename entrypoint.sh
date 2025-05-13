#!/bin/bash
set -e
export _JAVA_OPTIONS="-Dwebdriver.chrome.userDataDir="
echo "Starting Xvfb on display :99"
Xvfb :99 -screen 0 1280x1024x24 &
export DISPLAY=:99
sleep 3
echo "Starting XVFB and Maven tests..."
mvn -B test -Dheadless=true -Dwebdriver.chrome.userDataDir=""
