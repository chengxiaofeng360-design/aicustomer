# 部署包制作脚本
# 用途：创建完整的部署包，方便上传到服务器

#!/bin/bash

echo "正在创建部署包..."

# 创建临时目录
TEMP_DIR="deploy-package-$(date +%Y%m%d-%H%M%S)"
mkdir -p "$TEMP_DIR"

# 1. 打包项目
echo "1. 编译打包项目..."
mvn clean package -DskipTests

# 2. 复制JAR包
echo "2. 复制JAR包..."
mkdir -p "$TEMP_DIR/jars"
cp target/ai-customer-management-*.jar "$TEMP_DIR/jars/"

# 3. 复制脚本
echo "3. 复制部署脚本..."
mkdir -p "$TEMP_DIR/bin"
cp deploy/bin/*.sh "$TEMP_DIR/bin/"
chmod +x "$TEMP_DIR/bin"/*.sh

# 4. 复制文档
echo "4. 复制部署文档..."
cp deploy/部署说明.md "$TEMP_DIR/"
cp 用户密码问题修复总结.md "$TEMP_DIR/" 2>/dev/null || true

# 5. 复制SQL脚本
echo "5. 复制数据库脚本..."
mkdir -p "$TEMP_DIR/sql"
cp src/main/resources/sql/init.sql "$TEMP_DIR/sql/" 2>/dev/null || true
cp src/main/resources/sql/create_initial_admin.sql "$TEMP_DIR/sql/" 2>/dev/null || true

# 6. 创建目录结构说明
echo "6. 创建目录结构说明..."
cat > "$TEMP_DIR/README.txt" << 'EOF'
AI客户管理系统 - 部署包
========================

目录结构：
├── jars/                    # JAR包
│   └── ai-customer-management-1.0.0.jar
├── bin/                     # 启动脚本
│   ├── start.sh             # 启动
│   ├── stop.sh              # 停止
│   ├── restart.sh           # 重启
│   └── status.sh            # 状态
├── sql/                     # SQL脚本
│   ├── init.sql             # 数据库初始化
│   └── create_initial_admin.sql  # 创建管理员
├── 部署说明.md              # 详细部署文档
└── README.txt               # 本文件

快速部署：
1. 上传整个目录到服务器 /data/app/
2. 参考"部署说明.md"完成环境准备
3. 执行 /data/app/bin/start.sh 启动应用

详细说明请查看: 部署说明.md
EOF

# 7. 打包
echo "7. 创建压缩包..."
tar -czf "${TEMP_DIR}.tar.gz" "$TEMP_DIR"

echo ""
echo "✓ 部署包创建完成: ${TEMP_DIR}.tar.gz"
echo ""
echo "上传到服务器："
echo "  scp ${TEMP_DIR}.tar.gz root@your-server:/tmp/"
echo ""
echo "在服务器上解压："
echo "  cd /data/app && tar -xzf /tmp/${TEMP_DIR}.tar.gz --strip-components=1"
echo ""

# 清理临时目录
rm -rf "$TEMP_DIR"
