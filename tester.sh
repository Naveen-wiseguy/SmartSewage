#!/bin/bash
FILE=$1
while read line; do
     echo "$line"
     sleep 0.5
done < $FILE
