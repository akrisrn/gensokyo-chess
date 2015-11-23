#!/usr/bin/env bash

version=$(git rev-list --all | head -n 1 | cut -b 1-5)
if [ ! -e "gnc-2d-gui-v${version}.jar" ]; then
    rm -rf gnc-2d-gui-v*.jar
    if [ ! -d "tmp" ]; then
        mkdir tmp
    fi
    javac -encoding utf-8 -cp lib/javacsv.jar:lib/uiDesigner.jar -d tmp/ src/com/gensokyochess/*.java
    jar cfm gnc-2d-gui-v${version}.jar META-INF/MANIFEST_GUI.MF -C tmp/ .
    rm -rf tmp
fi