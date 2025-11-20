#!/bin/bash
# QRCode库下载脚本

cd "$(dirname "$0")"
mkdir -p src/main/resources/static/lib/qrcode

echo "正在尝试从多个CDN下载QRCode库..."

# 尝试多个CDN源
URLS=(
    "https://cdn.jsdelivr.net/npm/qrcode@1.5.3/build/qrcode.min.js"
    "https://unpkg.com/qrcode@1.5.3/build/qrcode.min.js"
    "https://cdnjs.cloudflare.com/ajax/libs/qrcode/1.5.3/qrcode.min.js"
    "https://cdn.bootcdn.net/ajax/libs/qrcode/1.5.3/qrcode.min.js"
)

for url in "${URLS[@]}"; do
    echo "尝试下载: $url"
    if curl -L --connect-timeout 10 --max-time 30 -f -o src/main/resources/static/lib/qrcode/qrcode.min.js "$url" 2>/dev/null; then
        if [ -f src/main/resources/static/lib/qrcode/qrcode.min.js ] && [ -s src/main/resources/static/lib/qrcode/qrcode.min.js ]; then
            SIZE=$(ls -lh src/main/resources/static/lib/qrcode/qrcode.min.js | awk '{print $5}')
            echo "✓ 下载成功！文件大小: $SIZE"
            echo "文件已保存到: src/main/resources/static/lib/qrcode/qrcode.min.js"
            exit 0
        fi
    fi
done

echo ""
echo "✗ 所有CDN都下载失败"
echo ""
echo "请手动下载QRCode库："
echo "1. 访问: https://cdn.jsdelivr.net/npm/qrcode@1.5.3/build/qrcode.min.js"
echo "2. 将页面内容保存为文件"
echo "3. 保存到: src/main/resources/static/lib/qrcode/qrcode.min.js"
echo ""
echo "或者使用浏览器开发者工具："
echo "1. 打开浏览器，访问上述URL"
echo "2. 右键 -> 另存为"
echo "3. 保存到项目目录的 src/main/resources/static/lib/qrcode/ 文件夹"
echo ""
exit 1

