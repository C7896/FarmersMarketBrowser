#!/bin/bash

# Check if an argument is provided
if [ $# -ne 1 ]; then
    echo "Usage: scripts/Run.sh <PATH_TO_JAVAFX>"
    exit 1
fi

# Assign the argument to a variable
PATH_TO_JAVAFX="$1"

# Check if the directory exists
if [ ! -d "$PATH_TO_JAVAFX" ]; then
    echo "JavaFX directory does not exist."
    exit 1
fi

# Run FarmersMarkets
java -cp "out;lib/mysql-connector-j-9.0.0.jar" --module-path "$PATH_TO_JAVAFX" --add-modules javafx.controls farmersMarkets.FarmersMarkets