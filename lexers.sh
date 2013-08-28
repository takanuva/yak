#!/bin/sh

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
    javac -cp $3:$5 $temp -d $3
  fi
  
done

CLASSIFIER=$3/classifier.bys

generate_classifier() {
  echo "Generating Bayesian classifier..."
  java -cp $3:$5 br/com/ekolivre/yak/editor/GenericDetectionScanner $4 $CLASSIFIER
  exit 0
}

if [ $1/br/com/ekolivre/yak/editor/GenericDetectionScanner.flex -nt $CLASSIFIER ]
then
  generate_classifier $1 $2 $3 $4 $5
fi

for source in $(find $4 -newer $CLASSIFIER)
do
  generate_classifier $1 $2 $3 $4 $5
done
