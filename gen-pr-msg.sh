#!/bin/bash

TITLE=$1
BODY_ITEMS=$2
ISSUE_NUM=$3

IFS='/' read -ra ITEMS <<< "$BODY_ITEMS"

{
  echo "$TITLE"
  echo ""
  for item in "${ITEMS[@]}"; do
      echo "- ${item// /}"
  done
  if [[ $ISSUE_NUM != "" ]]; then
    echo ""
    echo "Fixes #$ISSUE_NUM"
  fi
} > pr-message.txt

echo "✅ PR 메시지가 pr-message.txt 파일에 저장되었습니다!"