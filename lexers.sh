#!/bin/sh
################################################################################
# Project: YAK Code Editor                                                     #
# License: GNU GPL.                                                            #
# Author: Paulo H. "Taka" Torrens.                                             #
# E-Mail: paulotorrens@ekolivre.com.br                                         #
#                                                                              #
# Copyright (C) Ekolivre TI, Paulo H. Torrens - 2013.                          #
# Ekolivre TI (http://www.ekolivre.com.br) claims rights over this software;   #
#   you may use for educational or personal uses. For comercial use (even as   #
#   a library), please contact the author.                                     #
################################################################################
# This file is part of Ekolivre's YAK.                                         #
#                                                                              #
# YAK is free software: you can redistribute it and/or modify it under the     #
#   terms of the GNU General Public License as published by the Free Software  #
#   Foundation, either version 3 of the License, or (at your option) any later #
#   version.                                                                   #
#                                                                              #
# YAK is distributed in the hope that it will be useful, but WITHOUT ANY       #
#   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS  #
#   FOR A PARTICULAR PURPOSE. See the GNU General Public License for more      #
#   details.                                                                   #
#                                                                              #
# You should have received a copy of the GNU General Public License along with #
#   YAK.  If not, see <http://www.gnu.org/licenses/>.                          #
################################################################################

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
