#!/usr/bin/env bash

version=$(git rev-list --branches | head -n 1 | cut -b 1-5)
if [ ! -e "gnc-2d-v${version}.jar" ] || [ ! -e "gnc-2d-gui-v${version}.jar" ]; then
    if [ ! -d "tmp" ]; then
        mkdir tmp
    fi
    javac -encoding utf-8 -cp lib/javacsv.jar:lib/uiDesigner.jar -d tmp/ src/com/gensokyochess/exception/*.java \
    src/com/gensokyochess/spell/*.java src/com/gensokyochess/*.java
    if [ ! -e "gnc-2d-v${version}.jar" ]; then
        rm -rf gnc-2d-v*.jar
        jar cfm gnc-2d-v${version}.jar META-INF/MANIFEST.MF -C tmp/ .
    fi
    if [ ! -e "gnc-2d-gui-v${version}.jar" ]; then
        rm -rf gnc-2d-gui-v*.jar
        jar cfm gnc-2d-gui-v${version}.jar META-INF/MANIFEST_GUI.MF -C tmp/ .
    fi
    rm -rf tmp
fi