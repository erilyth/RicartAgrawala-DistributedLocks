#!/bin/bash

n=0
filename=""

while getopts ":n:o:" opt; do
  case $opt in
    n)
      n=$OPTARG
      ;;
    o)
      filename=$OPTARG
      ;;
    \?)
      echo "Invalid option: -$OPTARG"
      exit 1
      ;;
    :)
      echo "Option -$OPTARG requires an argument."
      exit 1
      ;;
  esac
done

rm $filename
rm myProg.*

javac -cp ./bin -d ./bin ./src/*.java
echo 'Compiled Required files'
rmic -classpath bin/ -d bin/ AdderRemote
cd bin
rmiregistry 5000 &
echo 'RMIC started'
sleep 0.05
cd ..

for i in `seq 1 $n`;
do
  java -cp bin/ MyClient $i $n $filename &
  sleep 0.2
done
