#!/usr/bin/env bash

if [ ! -d "tmp" ]; then
    mkdir tmp
fi

javac -cp lib/javacsv.jar:lib/uiDesigner.jar -d tmp/ src/com/gensokyochess/*.java
jar cfm gnc-2d-gui.jar META-INF/MANIFEST_GUI.MF -C tmp/ .

rm -rf tmp