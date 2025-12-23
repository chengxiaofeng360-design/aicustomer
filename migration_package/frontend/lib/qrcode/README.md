# QRCode库下载说明

由于网络限制，QRCode库需要手动下载。

## 下载方法

### 方法1：使用浏览器下载
1. 访问以下任一URL：
   - https://cdn.jsdelivr.net/npm/qrcode@1.5.3/build/qrcode.min.js
   - https://unpkg.com/qrcode@1.5.3/build/qrcode.min.js
   - https://cdnjs.cloudflare.com/ajax/libs/qrcode/1.5.3/qrcode.min.js

2. 将页面内容保存为 `qrcode.min.js` 文件
3. 确保文件保存在：`src/main/resources/static/lib/qrcode/qrcode.min.js`

### 方法2：使用npm（如果已安装）
```bash
cd /Users/zuozuo/Downloads/cxf/aicustomer
npm install qrcode@1.5.3
cp node_modules/qrcode/build/qrcode.min.js src/main/resources/static/lib/qrcode/qrcode.min.js
```

### 方法3：使用wget或curl（如果网络可用）
```bash
cd /Users/zuozuo/Downloads/cxf/aicustomer
mkdir -p src/main/resources/static/lib/qrcode
curl -L -o src/main/resources/static/lib/qrcode/qrcode.min.js https://cdn.jsdelivr.net/npm/qrcode@1.5.3/build/qrcode.min.js
```

## 验证
下载完成后，文件大小应该约为 50-100KB。可以使用以下命令验证：
```bash
ls -lh src/main/resources/static/lib/qrcode/qrcode.min.js
```

## 注意
如果QRCode库文件不存在，系统会自动尝试从CDN加载，如果所有CDN都失败，会显示链接文本作为降级方案。

