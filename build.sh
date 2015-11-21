#!/usr/bin/env bash

if [ ! -d "tmp" ]; then
    mkdir tmp
fi

javac -encoding utf-8 -cp lib/javacsv.jar:lib/uiDesigner.jar -d tmp/ src/com/gensokyochess/*.java
jar cfm gnc-2d.jar META-INF/MANIFEST.MF -C tmp/ .

rm -rf tmp