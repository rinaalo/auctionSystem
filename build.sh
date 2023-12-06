#!/bin/bash
set -xe

rm -f */*.class

cd sharedFiles
javac *.java 
cd ..
cp sharedFiles/*.class server
cd server
javac -cp "./jgroups-3.6.20.Final.jar":. *.java
cd ..
cp sharedFiles/*.class client
cd client
javac *.java
cd ..
