#!/bin/bash

BUILD_DIR=../../../build/classes
MAIN_DIR=java/main

javah -classpath $BUILD_DIR/$MAIN_DIR fts.linux.NativeInterface
