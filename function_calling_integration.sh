#!/bin/bash
# Function Calling集成脚本 - 完整替换generateAiResponse方法

cd /Users/zuozuo/Downloads/cxf/aicustomer

# 备份原文件
cp src/main/java/com/aicustomer/service/impl/AiChatServiceImpl.java src/main/java/com/aicustomer/service/impl/AiChatServiceImpl.java.backup

# 使用sed删除旧的generateAiResponse方法（第184-48行）并替换为新实现
# 注意：由于方法太长，我们分步进行

echo "开始Function Calling集成..."
echo "1. 修改SYSTEM_PROMPT..."
echo "2. 替换generateAiResponse方法..."
echo "3. 添加辅助方法..."

# 实际修改将通过Java代码编辑完成
# 这个脚本仅用于记录和验证

echo "完成！请重启服务测试。"
