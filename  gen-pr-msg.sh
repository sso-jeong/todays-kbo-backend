#!/bin/bash

# 사용법: ./gen-pr-msg.sh "제목" "항목1 / 항목2 / 항목3" 이슈번호

TITLE=$1
BODY_ITEMS=$2
ISSUE_NUM=$3

IFS='/' read -ra ITEMS <<< "$BODY_ITEMS"

echo "$TITLE"
echo ""
for item in "${ITEMS[@]}"; do
    echo "- ${item// /}"
done
if [[ $ISSUE_NUM != "" ]]; then
  echo ""
  echo "Fixes #$ISSUE_NUM"
fi