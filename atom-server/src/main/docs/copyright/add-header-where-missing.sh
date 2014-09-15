#!/bin/bash

SAVEIFS=$IFS
IFS=$(echo -en "\n\b")

function dodir {
  #for filename in $1/**/*.java
  find "$1" -type f \( -iname "*.java" -or -iname "*.css" -or -iname "*.sql" \) -print0 | while IFS= read -r -d $'\0' line
  do
    echo "------------------------------------------"
    echo "$line"
    head=$(head -n 1 "$line")
    if [[ $head == "/* ATOM - Advanced Transparent Object Manager"* ]] ;
    then
      echo $line >> has-a-copyright-header-already.list
    else
      echo $line >> has-no-copyright-header-yet.list
      #cat copyright-header > tmp
      #cat "$line" >> tmp
      #mv tmp "$line"
    fi
    echo "------------------------------------------"
  done;
}

cwd=$(pwd)
rm has-no-copyright-header-yet.list
rm has-a-copyright-header-already.list
dodir "atom-core/src/main/java"
dodir "atom-domain/src/main/java"
dodir "atom-client/src/main/java"
dodir "atom-server/src/main/java"
dodir "atom-server/src/test/java"

IFS=$SAVEIFS
