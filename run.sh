#!/bin/sh
javac -d bin hitcount/HitCount.java
java -cp ./bin hitcount.HitCount $1
