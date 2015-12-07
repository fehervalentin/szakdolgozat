#!/bin/bash

args=("$@")
DATE=`date +%Y%m%d_%H%M%S`
MAIN_FILENAME="thesis"
MAIN_FILE="$MAIN_FILENAME.tex"
OUTPUT_DIRECTORY="output"
RELEASE_DIRECTORY="releases"
APT_GET_CMD=$(which apt-get)
PDFLATEX_PARAMETERS="-interaction=nonstopmode -output-directory=$OUTPUT_DIRECTORY -output-format=pdf"

function clear {
  rm -rf $OUTPUT_DIRECTORY
  mkdir $OUTPUT_DIRECTORY
}
function create_tmp_directories {
  for dir in */ ; do
    mkdir "$OUTPUT_DIRECTORY/$dir"
  done
}
function compile {
  pdflatex $PDFLATEX_PARAMETERS $MAIN_FILE &>/dev/null
  pdflatex $PDFLATEX_PARAMETERS $MAIN_FILE &>/dev/null
}
function remove_tmp_directories {
  for dir in */ ; do
    rm -rf "$OUTPUT_DIRECTORY/$dir"
  done
}
function create_release {
  mkdir -p $RELEASE_DIRECTORY
  cp $OUTPUT_DIRECTORY/$MAIN_FILENAME.pdf $RELEASE_DIRECTORY/$MAIN_FILENAME.$DATE.pdf
}
function update_latest {
  mkdir -p $RELEASE_DIRECTORY
  cp -f $OUTPUT_DIRECTORY/$MAIN_FILENAME.pdf $RELEASE_DIRECTORY/$MAIN_FILENAME.pdf
}
function print_help {
  echo "The pdflatex is not installed."
  if [[ ! -z $APT_GET_CMD ]]; then
    printf "Do you want to install it now? (Y/y) "
    read answer
    if [ "$answer" == "y" ] || [ "$answer" == "Y" ] ; then
      sudo apt-get install texlive texlive-latex-base
    else
      echo "Please install the texlive and textlive-latex-base packages."
    fi
  else
     echo "Please install the texlive and textlive-latex-base packages."
  fi
}

if hash pdflatex 2>/dev/null; then
  clear
  create_tmp_directories
  compile
  remove_tmp_directories
  if [ "$#" -ge 1 ]; then
    if [ "${args[0]}" == "--create-release" ] || [ "${args[1]}" == "--create-release" ]; then
      create_release
    fi
    if [ "${args[0]}" == "--update-latest" ] || [ "${args[1]}" == "--update-latest" ]; then
      update_latest
    fi
  fi
else
  print_help
fi
