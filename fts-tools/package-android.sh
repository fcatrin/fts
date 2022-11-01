#!/bin/bash

THISDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
CLASSPATH=$THISDIR/build/classes/java/main

DSTDIR=src/main/assets
mkdir $DSTDIR 2>/dev/null
java -classpath $CLASSPATH fts.tools.Merger $DSTDIR


