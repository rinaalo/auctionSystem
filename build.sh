#!/bin/bash
set -xe

rm -rf *.class

javac server/*.java
cp server/AuctionService.class server/AuctionItem.class client
cd client
javac *.java
cd ..
