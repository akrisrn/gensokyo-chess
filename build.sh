#!/usr/bin/env bash

if [ ! -d "tmp" ]; then
    mkdir tmp
fi

javac -cp lib/javacsv.jar -d tmp/ src/com/gensokyochess/*
jar cfm gnc-2d.jar META-INF/MANIFEST.MF -C tmp/ .

rm -rf tmp