#!/bin/bash

PATHSEP=":"
if [[ $OS == "Windows_NT" ]] || [[ $OSTYPE == "cygwin" ]]
then
    PATHSEP=";"
fi

# javac -cp "www/WEB-INF/lib/org.json.jar:winstone.jar:www/WEB-INF/classes" www/WEB-INF/classes/se/yrgo/schedule/database/*.java && java -jar winstone.jar --webroot=www


# Rätt script 
javac -cp "www/WEB-INF/lib/*:winstone.jar:www/WEB-INF/classes" www/WEB-INF/classes/se/yrgo/schedule/*/*.java && java -jar winstone.jar --webroot=www
