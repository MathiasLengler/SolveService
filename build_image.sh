#!/usr/bin/env bash

sbt universal:packageZipTarball
docker build -t "solveservice:1.0" .
