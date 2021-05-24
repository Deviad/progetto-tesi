#!/usr/bin/env bash
#Run all unit tests on file change
#You can have as many folders as you want after the options -or
fswatch -or ./src | xargs -n1 -I{} mvn test
