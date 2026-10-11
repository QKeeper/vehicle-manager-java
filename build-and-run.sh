#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"

for tool in javac jar java; do
    if ! command -v "$tool" > /dev/null 2>&1; then
        echo "Error: '$tool' not found. Install JDK 17+ and make sure it is in PATH."
        exit 1
    fi
done

echo "Compiling..."
rm -rf out
mkdir out
javac --release 17 -encoding UTF-8 -d out src/*.java

echo "Packing vehicles.jar..."
jar cfe vehicles.jar Main -C out .

echo "Starting the application..."
java -jar vehicles.jar
