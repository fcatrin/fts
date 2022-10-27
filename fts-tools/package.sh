#!/bin/bash

THISDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
mkdir bin 2>/dev/null
java -classpath $THISDIR/bin fts.tools.Merger bin

