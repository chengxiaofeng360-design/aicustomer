# ✅ Linux生产环境部署配置完成

## 📦 已创建的文件

### 1. 部署脚本（`deploy/bin/`）
- ✅ **start.sh** - 启动脚本（包含JVM参数配置、健康检查）
- ✅ **stop.sh** - 停止脚本（优雅关闭 + 强制终止）
- ✅ **restart.sh** - 重启脚本
- ✅ **status.sh** - 状态查询脚本（显示进程信息、资源使用、日志）

### 2. 配置文件
- ✅ **application-prod.yml** - 生产环境配置
  - 日志路径: `/data/logs/app/ai-customer.log`
  - 文件上传: `/data/files/upload/`
  - SQL初始化模式: `never`（禁止自动执行）

### 3. 文档
- ✅ **deploy/部署说明.md** - 完整部署手册
  - 系统要求
  - 部署步骤
  - 运维命令
  - 故障排查
  - 监控建议

### 4. 工具脚本
- ✅ **create-deploy-package.sh** - 部署包制作脚本

## 🎯 Linux目录结构（已配置）

```
/data/
├── app/
│   ├── jars/                         # ← JAR包存放位置
│   │   └── ai-customer-management-1.0.0.jar
│   ├── bin/                          # ← 脚本存放位置
│   │   ├── start.sh
│   │   ├── stop.sh
│   │   ├── restart.sh
│   │   └── status.sh
│   └── ai-customer-management.pid    # PID文件
│
├── files/
│   ├── upload/                       # ← 文件上传目录
│   └── download/
│
├── logs/
│   └── app/                          # ← 日志存放位置
│       ├── ai-customer.log           # 应用日志
│       ├── console.log               # 控制台输出
│       └── heapdump.hprof           # OOM时的堆转储
│
├── tmp/                              # 临时文件
│
└── backup/                           # 备份
    ├── database/
    └── files/
```

## 🚀 快速部署步骤

### 步骤1: 制作部署包
```bash
# 在本地执行
cd /Users/zuozuo/Downloads/cxf/aicustomer
./create-deploy-package.sh
```

### 步骤2: 上传到服务器
```bash
# 上传部署包
scp deploy-package-*.tar.gz root@your-server:/tmp/

# 解压到指定目录
ssh root@your-server
cd /data/app
tar -xzf /tmp/deploy-package-*.tar.gz --strip-components=1
```

### 步骤3: 初始化数据库
```bash
# 创建数据库表
mysql -u root -p zqgl < /data/app/sql/init.sql

# 创建管理员账号
mysql -u root -p zqgl < /data/app/sql/create_initial_admin.sql
```

### 步骤4: 启动应用
```bash
# 启动
/data/app/bin/start.sh

# 查看日志
tail -f /data/logs/app/ai-customer.log

# 查看状态
/data/app/bin/status.sh
```

## 📝 运维命令速查

| 操作 | 命令 |
|------|------|
| 启动应用 | `/data/app/bin/start.sh` |
| 停止应用 | `/data/app/bin/stop.sh` |
| 重启应用 | `/data/app/bin/restart.sh` |
| 查看状态 | `/data/app/bin/status.sh` |
| 查看日志 | `tail -f /data/logs/app/ai-customer.log` |
| 查看控制台 | `tail -f /data/logs/app/console.log` |

## 🔧 JVM参数配置（可调整）

在 `start.sh` 中已配置：
```bash
JVM_OPTS="-Xms512m -Xmx2g"              # 内存配置
JVM_OPTS="${JVM_OPTS} -XX:+UseG1GC"    # 使用G1垃圾回收器
JVM_OPTS="${JVM_OPTS} -XX:MaxGCPauseMillis=200"
JVM_OPTS="${JVM_OPTS} -XX:+HeapDumpOnOutOfMemoryError"
JVM_OPTS="${JVM_OPTS} -XX:HeapDumpPath=/data/logs/app/heapdump.hprof"
```

根据服务器配置调整：
- **1GB 内存服务器**: `-Xms256m -Xmx768m`
- **2GB 内存服务器**: `-Xms512m -Xmx1536m`
- **4GB 内存服务器**: `-Xms1g -Xmx3g`
- **8GB+ 内存服务器**: `-Xms2g -Xmx6g`

## ⚙️ 环境变量配置（可选）

如需使用环境变量配置数据库密码：

```bash
# 编辑 /etc/profile
export SPRING_DATASOURCE_USERNAME=aicustomer
export SPRING_DATASOURCE_PASSWORD=YourSecurePassword

# 使生效
source /etc/profile
```

## 🔐 安全配置提醒

1. ✅ **已禁用自动初始化** - 不会自动创建默认密码账号
2. ✅ **已禁用SQL自动执行** - `spring.sql.init.mode: never`
3. ⚠️ **请修改默认管理员密码** - 首次登录后立即修改
4. ⚠️ **请配置防火墙** - 限制8085端口访问
5. ⚠️ **请定期备份数据库** - 参考部署说明中的备份脚本

## 📚 详细文档

查看完整部署说明：`deploy/部署说明.md`

---

**准备就绪！** 🎉 所有部署文件已按照您的Linux目录结构配置完成。
