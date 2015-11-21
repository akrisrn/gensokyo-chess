if not exist tmp md tmp

javac -encoding utf-8 -cp lib/javacsv.jar;lib/uiDesigner.jar -d tmp/ src/com/gensokyochess/*.java
jar cfm gnc-2d.jar META-INF/MANIFEST.MF -C tmp/ .

rd tmp /s /Q