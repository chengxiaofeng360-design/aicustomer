#!/bin/bash

###############################################
# AI客户管理系统 - 重启脚本
# 用途: 重启应用程序
# 作者: AI Customer Management Team
# 版本: 1.0.0
###############################################

# 获取脚本所在目录
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)

# 颜色定义
GREEN='\033[0;32m'
NC='\033[0m'

log_info() {
    echo -e "${GREEN}[INFO]${NC} $(date '+%Y-%m-%d %H:%M:%S') - $1"
}

# 重启应用
restart_app() {
    log_info "===== 重启应用 ====="
    
    # 停止应用
    bash "${SCRIPT_DIR}/stop.sh"
    
    # 等待2秒
    sleep 2
    
    # 启动应用
    bash "${SCRIPT_DIR}/start.sh"
}

# 执行重启
restart_app
