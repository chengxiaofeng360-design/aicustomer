#!/bin/bash

# Java 17 环境配置脚本
# 在安装完Java 17后运行此脚本

echo "=========================================="
echo "配置 Java 17 环境变量"
echo "=========================================="

# 检查Java 17是否已安装
if [ ! -d "/Library/Java/JavaVirtualMachines/temurin-17.jdk" ]; then
    echo "错误：未找到 Java 17 安装"
    echo "请先按照 JAVA17_安装说明.md 安装 Java 17"
    exit 1
fi

echo "✓ 检测到 Java 17 已安装"

# 检测shell类型
SHELL_CONFIG=""
if [ -f "$HOME/.zshrc" ]; then
    SHELL_CONFIG="$HOME/.zshrc"
    echo "检测到使用 zsh，配置文件: ~/.zshrc"
elif [ -f "$HOME/.bash_profile" ]; then
    SHELL_CONFIG="$HOME/.bash_profile"
    echo "检测到使用 bash，配置文件: ~/.bash_profile"
else
    SHELL_CONFIG="$HOME/.zshrc"
    echo "未检测到配置文件，将创建 ~/.zshrc"
fi

# 检查是否已配置
if grep -q "JAVA_HOME.*17" "$SHELL_CONFIG" 2>/dev/null; then
    echo "✓ Java 17 环境变量已配置"
else
    # 添加配置（默认使用Java 17，但保留Java 8可用）
    echo "" >> "$SHELL_CONFIG"
    echo "# Java 版本管理 (由 AI Customer Management System 添加)" >> "$SHELL_CONFIG"
    echo "# 默认使用 Java 17，如需切换版本，运行: ./switch_java.sh [8|17]" >> "$SHELL_CONFIG"
    echo "export JAVA_HOME=\$(/usr/libexec/java_home -v 17 2>/dev/null || /usr/libexec/java_home -v 1.8)" >> "$SHELL_CONFIG"
    echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> "$SHELL_CONFIG"
    echo "✓ 已添加 Java 配置到 $SHELL_CONFIG（优先使用Java 17，如果未安装则使用Java 8）"
fi

# 设置当前会话的环境变量
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH=$JAVA_HOME/bin:$PATH

echo ""
echo "=========================================="
echo "配置完成！"
echo "=========================================="
echo ""
echo "当前Java版本："
java -version
echo ""
echo "JAVA_HOME: $JAVA_HOME"
echo ""
echo "提示："
echo "1. 新打开的终端窗口会自动使用Java 17"
echo "2. 当前终端需要重新加载配置："
echo "   source $SHELL_CONFIG"
echo "3. 或者重新打开终端窗口"

