#!/bin/bash
set -xe

rm -rf *.class

javac server/*.java
cp server/AuctionService.class server/AuctionItem.class server/ClientType.class server/AuctionType.class client
cd client
javac *.java
cd ..
