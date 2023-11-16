#!/bin/bash
set -xe

rm -rf *.class

cd sharedFiles
javac *.java
cd ..
cp sharedFiles/*.class server
cd server
javac *.java
cd ..
cp sharedFiles/*.class client
cd client
javac *.java
cd ..
