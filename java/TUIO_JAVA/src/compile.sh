#!/bin/sh
cd src
javac -Xlint:unchecked -O -source 1.5 -target 1.5 -cp . *.java TUIO/*.java com/illposed/osc/*.java
jar cfm ../TuioDemo.jar manifest.inc *.class TUIO/*.class com/illposed/osc/*.class com/illposed/osc/utility/*.class
jar cf ../libTUIO.jar TUIO/*.class com/illposed/osc/*.class com/illposed/osc/utility/*.class
rm -f *.class TUIO/*.class com/illposed/osc/*.class com/illposed/osc/utility/*.class
cd ..
