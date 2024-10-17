#!/bin/bash

REPO_ROOT=$(git rev-parse --show-toplevel)
LOCAL_SUSGAME_PATH="$REPO_ROOT/src/main/kotlin/"
LOCAL_DTO_PATH="$LOCAL_SUSGAME_PATH/dto"
TMP_PATH="$REPO_ROOT/scripts/tmp"
REPO_DTO_PATH="$TMP_PATH/SusGameDTO/src/main/kotlin/edu/agh/susgame/dto"

BRANCH=$1

echo "Cleaning up current DTO files"
rm -r "$LOCAL_DTO_PATH"

echo "Cloning SusGameDTO repository"
mkdir "$TMP_PATH"
cd "$TMP_PATH" || exit
git clone https://github.com/Nepommuck/SusGameDTO.git

if [ -n "$BRANCH" ]; then
  echo "Checking out branch: $BRANCH"
  cd SusGameDTO || exit
  git checkout "$BRANCH"
  cd ..
fi

echo "Copying cloned files"
mkdir "$LOCAL_DTO_PATH"
cp -r "$REPO_DTO_PATH" "$LOCAL_SUSGAME_PATH" || exit

echo "Adding a warning message to files"
find "$LOCAL_DTO_PATH" -type f | while read -r file; do
  sed -i "1i // WARNING: THIS FILE WAS CLONED AUTOMATICALLY FROM 'SusGameDTO' GITHUB REPOSITORY" "$file"
  sed -i "2i // IT SHOULD NOT BE EDITED IN ANY WAY" "$file"
  sed -i "3i // IN ORDER TO CHANGE THIS DTO, COMMIT TO 'SusGameDTO' GITHUB REPOSITORY" "$file"
  sed -i "4i // IN ORDER TO UPDATE THIS FILE TO NEWEST VERSION, RUN 'scripts/update-DTO.sh'\n" "$file"
done

echo "Cleaning up"
cd "$REPO_ROOT" || exit
rm -rf "$TMP_PATH"

echo "DTO script finished execution"
