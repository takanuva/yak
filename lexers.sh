#!/bin/sh

#echo Here!
#java -classpath out/classes br/com/ekolivre/yak/editor/GenericDetectionScanner bys src/br/com/ekolivre/yak/editor/NestableSyntaxKit.java
#java -classpath out/classes br/com/ekolivre/yak/editor/GenericDetectionScanner bys /Users/takanuva/black/src/bootstrapper.krc
#java -classpath out/classes br/com/ekolivre/yak/editor/GenericDetectionScanner bys test.c
#java -classpath out/classes br/com/ekolivre/yak/editor/GenericDetectionScanner bys test.jav

echo "Generating (and compiling) scanner files..."

for source in $(find $1 -type f -name '*.flex')
do
  
  temp=$2/$(echo $(basename "$source") | sed "s|\.flex|\.java|")
  
  if [ "$source" -nt "$temp" ]
  then
    
    jflex -q -d $2 $source
    
  fi
  
  output=$(echo "$source" | sed "s|$1|$3|" | sed "s|\.flex|\.class|")
  
  if [ "$temp" -nt "$output" ]
  then
    
    javac -cp out/classes/ $temp -d $3
    
  fi
done
