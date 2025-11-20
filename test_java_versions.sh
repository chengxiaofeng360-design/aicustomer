#!/bin/bash

# 测试多Java版本共存脚本

echo "=========================================="
echo "Java 版本管理测试"
echo "=========================================="

echo ""
echo "1. 查看所有已安装的Java版本："
/usr/libexec/java_home -V

echo ""
echo "2. 当前使用的Java版本："
java -version

echo ""
echo "3. 当前JAVA_HOME："
echo $JAVA_HOME

echo ""
echo "4. 切换到Java 8："
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
export PATH=$JAVA_HOME/bin:$PATH
echo "JAVA_HOME: $JAVA_HOME"
java -version

echo ""
echo "5. 切换到Java 17（如果已安装）："
if [ -d "/Library/Java/JavaVirtualMachines/temurin-17.jdk" ]; then
    export JAVA_HOME=$(/usr/libexec/java_home -v 17)
    export PATH=$JAVA_HOME/bin:$PATH
    echo "JAVA_HOME: $JAVA_HOME"
    java -version
else
    echo "Java 17 尚未安装"
fi

echo ""
echo "=========================================="
echo "结论：多个Java版本可以共存，互不影响"
echo "通过设置JAVA_HOME可以切换版本"
echo "=========================================="

