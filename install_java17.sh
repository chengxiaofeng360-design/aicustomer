#!/bin/bash

# Java 17 (Temurin) 安装脚本
# 适用于 macOS (Apple Silicon)

set -e

echo "=========================================="
echo "开始安装 Java 17 (Eclipse Temurin)"
echo "=========================================="

# 检查是否已安装
if [ -d "/Library/Java/JavaVirtualMachines/temurin-17.jdk" ]; then
    echo "Java 17 已经安装，位置: /Library/Java/JavaVirtualMachines/temurin-17.jdk"
    /usr/libexec/java_home -V
    exit 0
fi

# 创建临时目录
TMP_DIR=$(mktemp -d)
cd "$TMP_DIR"

echo "正在下载 Java 17 (Temurin)..."

# 尝试多个下载源
DOWNLOAD_URL=""
if [ "$(uname -m)" = "arm64" ]; then
    # Apple Silicon
    DOWNLOAD_URL="https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.13%2B11/OpenJDK17U-jdk_aarch64_mac_hotspot_17.0.13_11.tar.gz"
else
    # Intel
    DOWNLOAD_URL="https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.13%2B11/OpenJDK17U-jdk_x64_mac_hotspot_17.0.13_11.tar.gz"
fi

# 下载
echo "下载地址: $DOWNLOAD_URL"
if curl -L -o java17.tar.gz "$DOWNLOAD_URL"; then
    echo "下载成功"
else
    echo "自动下载失败，请手动下载："
    echo "1. 访问: https://adoptium.net/zh-CN/temurin/releases/?version=17"
    echo "2. 选择 macOS ARM64 (Apple Silicon) 或 x64 (Intel)"
    echo "3. 下载 .pkg 安装包"
    echo "4. 双击安装包进行安装"
    exit 1
fi

# 解压
echo "正在解压..."
tar -xzf java17.tar.gz

# 查找jdk目录
JDK_DIR=$(find . -type d -name "jdk-*" | head -1)
if [ -z "$JDK_DIR" ]; then
    echo "错误：找不到JDK目录"
    exit 1
fi

# 移动到系统目录（需要sudo权限）
echo "正在安装到系统目录（需要管理员权限）..."
sudo mkdir -p /Library/Java/JavaVirtualMachines
sudo mv "$JDK_DIR" /Library/Java/JavaVirtualMachines/temurin-17.jdk

# 清理临时文件
cd /
rm -rf "$TMP_DIR"

echo "=========================================="
echo "安装完成！"
echo "=========================================="

# 验证安装
echo "已安装的Java版本："
/usr/libexec/java_home -V

echo ""
echo "Java 17 安装位置: /Library/Java/JavaVirtualMachines/temurin-17.jdk"
echo ""
echo "要使用Java 17，请设置环境变量："
echo "export JAVA_HOME=\$(/usr/libexec/java_home -v 17)"
echo "export PATH=\$JAVA_HOME/bin:\$PATH"
echo ""
echo "或者添加到 ~/.zshrc 或 ~/.bash_profile："
echo "echo 'export JAVA_HOME=\$(/usr/libexec/java_home -v 17)' >> ~/.zshrc"
echo "echo 'export PATH=\$JAVA_HOME/bin:\$PATH' >> ~/.zshrc"
echo "source ~/.zshrc"

