#!/bin/bash
set -xe

rm -f */*.class

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
