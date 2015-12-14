#!/bin/bash

args=("$@")
LANGUAGE="hu"
EXTENSION="tex"
ASPELL_PARAMETERS="--lang=$LANGUAGE --mode=tex check "
APT_GET_CMD=$(which apt-get)
DIRECTORIES=("introduction" "user-documentation" "developer-documentation" "bibliography")

function run {
  if hash aspell 2>/dev/null; then
    nohd=$(aspell dump dicts | grep -c $LANGUAGE)
    if [ $nohd -ge 1 ]; then
      if [ "$1" -ge 1 ]; then
        if [ ${args[0]} == "--all" ]; then
          check_all ${args[1]}
        else
          check_file ${args[0]} ${args[1]}
        fi
      else
        print_help
      fi
    else
      install_aspell_dictionary
    fi
  else
    install_aspell
  fi
}

function print_help {
  echo "A script helyes használata a következő: "
  echo -e "\t ./spellcheck.sh --all    \t\tHa minden .$EXTENSION kiterjesztésű fájlt szeretnél ellenőrizni"
  echo -e "\t ./spellcheck.sh filename \t\tHa csak a filename.$EXTENSION fájlt szeretnéd ellenőrizni"
  echo "FIGYELEM!"
  echo "A script csak a .tex állományokon végez helyesírás-ellenőrzést!"
  echo "A --all paraméter esetén a script csak a következő mappákban és almappáikban található .$EXTENSION állományokat vizsgálja:"
  echo -e "\t introduction"
  echo -e "\t user-documentation"
  echo -e "\t developer-documentation"
  echo -e "\t bibliography"
}

function install_aspell {
  echo "Package aspell is not installed."
  if [[ ! -z $APT_GET_CMD ]]; then
    printf "Do you want to install it now? (Y/y) "
    read answer
    if [ "$answer" == "y" ] || [ "$answer" == "Y" ] ; then
      sudo apt-get install aspell aspell-$LANGUAGE
    else
      echo "Please install the aspell and aspell-$LANGUAGE packages."
    fi
  else
     echo "Please install the aspell and aspell-$LANGUAGE packages."
  fi
}

function install_aspell_dictionary {
  echo "$LANGUAGE dictionary is not installed for aspell."
  if [[ ! -z $APT_GET_CMD ]]; then
    printf "Do you want to install it now? (Y/y) "
    read answer
    if [ "$answer" == "y" ] || [ "$answer" == "Y" ] ; then
      sudo apt-get install aspell-$LANGUAGE
    else
      echo "Please install the aspell-$LANGUAGE package."
    fi
  else
     echo "Please install the aspell-$LANGUAGE package."
  fi
}

function check_file {
  if [ -z "$1" ]; then
    print_help
  else
    file=$1
    if [[ "$file" != *.tex ]]; then
      file="$file.$EXTENSION"
    fi
    aspell $ASPELL_PARAMETERS $file
    if [ "$2" ] && [ "$2" == "--store-prev" ]; then
      echo "The previous copy has been saved to: $file.bak"
    else
      rm -f "$file.bak"
    fi
  fi
}

function check_directory {
  if [ -n "$1" ]; then
    for file in $(find "$1" -type f -name "*.$EXTENSION"); do
        check_file "$file" "$2"
    done
  fi
}

function check_all {
  second_param="--remove-prev"
  if [ "$1" ] && [ "$1" == "--store-prev" ]; then
    second_param="$1"
  fi
  for directory in "${DIRECTORIES[@]}"; do
    check_directory "${directory}" "$second_param"
  done
}

run $#
