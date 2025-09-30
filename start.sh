#!/bin/bash

echo "========================================"
echo "   AI客户管理系统启动脚本"
echo "========================================"
echo

echo "正在检查Java环境..."
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请先安装JDK 8+"
    exit 1
fi
java -version

echo
echo "正在检查Maven环境..."
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到Maven环境，请先安装Maven 3.6+"
    exit 1
fi
mvn -version

echo
echo "正在编译项目..."
mvn clean compile
if [ $? -ne 0 ]; then
    echo "错误: 项目编译失败"
    exit 1
fi

echo
echo "正在启动应用..."
echo "应用将在 http://localhost:8080/ai-customer/ 启动"
echo "按 Ctrl+C 停止应用"
echo

mvn spring-boot:run

