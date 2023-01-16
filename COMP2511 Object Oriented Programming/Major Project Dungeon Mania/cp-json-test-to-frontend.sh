#!/bin/bash

fullPathTestDungeonMap="src/test/resources/dungeons/."
fullPathFrontendDungeonMap="src/main/resources/dungeons/"
if [ -d "$fullPathTestDungeonMap" ]; then
	cp -Rp "$fullPathTestDungeonMap" "$fullPathFrontendDungeonMap" \
		&& echo "Copy-pasted $fullPathTestDungeonMap to $fullPathFrontendDungeonMap"
else
	echo "File $fullPathTestDungeonMap does not exist"
fi 

fullPathTestDungeonConfig="src/test/resources/configs/."
fullPathFrontendDungeonConfig="src/main/resources/configs/"

if [ -d "$fullPathTestDungeonConfig" ]; then
	cp -Rp "$fullPathTestDungeonConfig" "$fullPathFrontendDungeonConfig" \
		&& echo "Copy-pasted $fullPathTestDungeonConfig to $fullPathFrontendDungeonConfig"

else
	echo "File $fullPathTestDungeonConfig does not exist"
fi 


