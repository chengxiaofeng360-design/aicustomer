#!/bin/bash

# Java版本切换脚本
# 用法: ./switch_java.sh [8|17]

VERSION=${1:-17}

if [ "$VERSION" = "8" ]; then
    export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
    echo "已切换到 Java 8"
elif [ "$VERSION" = "17" ]; then
    if [ -d "/Library/Java/JavaVirtualMachines/temurin-17.jdk" ]; then
        export JAVA_HOME=$(/usr/libexec/java_home -v 17)
        echo "已切换到 Java 17"
    else
        echo "错误：Java 17 尚未安装"
        exit 1
    fi
else
    echo "用法: ./switch_java.sh [8|17]"
    exit 1
fi

export PATH=$JAVA_HOME/bin:$PATH

echo "JAVA_HOME: $JAVA_HOME"
echo ""
echo "当前Java版本："
java -version

