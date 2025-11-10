#!/bin/bash
# 推送代码到远程仓库脚本

cd "$(dirname "$0")"

echo "=== 推送代码到远程Git仓库 ==="
echo ""
echo "当前分支: $(git branch --show-current)"
echo "待推送提交:"
git log origin/main..HEAD --oneline
echo ""
echo "准备推送到: origin/main"
echo ""

# 检查是否有待推送的提交
if git rev-list --count origin/main..HEAD > /dev/null 2>&1; then
    COUNT=$(git rev-list --count origin/main..HEAD)
    if [ "$COUNT" -gt 0 ]; then
        echo "找到 $COUNT 个待推送的提交"
        echo ""
        echo "正在推送..."
        git push origin main
        if [ $? -eq 0 ]; then
            echo ""
            echo "✓ 推送成功！"
        else
            echo ""
            echo "✗ 推送失败"
            echo ""
            echo "可能的原因："
            echo "1. 需要GitHub身份验证"
            echo "2. 如果使用HTTPS，请输入GitHub用户名和密码（或Personal Access Token）"
            echo "3. 如果使用SSH，请确保SSH密钥已添加到GitHub账户"
            echo ""
            echo "解决方案："
            echo "方案1 - 使用Personal Access Token："
            echo "  1. 访问: https://github.com/settings/tokens"
            echo "  2. 生成新token（选择repo权限）"
            echo "  3. 推送时密码处输入token"
            echo ""
            echo "方案2 - 切换到SSH方式："
            echo "  git remote set-url origin git@github.com:chengxiaofeng360-design/aicustomer.git"
            echo "  git push origin main"
        fi
    else
        echo "没有待推送的提交"
    fi
else
    echo "无法连接到远程仓库，请检查网络连接"
fi


