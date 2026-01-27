#!/bin/bash

###############################################
# AI客户管理系统 - 停止脚本
# 用途: 停止应用程序
# 作者: AI Customer Management Team
# 版本: 1.0.0
###############################################

# 应用配置
APP_NAME="ai-customer-management"
APP_HOME="/data/app"
PID_FILE="${APP_HOME}/${APP_NAME}.pid"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $(date '+%Y-%m-%d %H:%M:%S') - $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $(date '+%Y-%m-%d %H:%M:%S') - $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $(date '+%Y-%m-%d %H:%M:%S') - $1"
}

# 停止应用
stop_app() {
    log_info "===== 停止 ${APP_NAME} ====="

    if [ ! -f "$PID_FILE" ]; then
        log_warn "PID文件不存在，应用可能未运行"
        return 0
    fi

    PID=$(cat "$PID_FILE")
    
    if ! ps -p "$PID" > /dev/null 2>&1; then
        log_warn "进程不存在 (PID: $PID)，清理PID文件"
        rm -f "$PID_FILE"
        return 0
    fi

    log_info "正在停止应用 (PID: $PID)..."
    
    # 优雅关闭
    kill -15 "$PID"
    
    # 等待进程结束
    for i in {1..30}; do
        if ! ps -p "$PID" > /dev/null 2>&1; then
            log_info "✓ 应用已成功停止"
            rm -f "$PID_FILE"
            return 0
        fi
        sleep 1
    done

    # 如果30秒后还未停止，强制杀死
    if ps -p "$PID" > /dev/null 2>&1; then
        log_warn "应用未能优雅停止，强制终止..."
        kill -9 "$PID"
        sleep 2
        if ! ps -p "$PID" > /dev/null 2>&1; then
            log_info "✓ 应用已强制停止"
            rm -f "$PID_FILE"
        else
            log_error "✗ 无法停止应用"
            return 1
        fi
    fi
}

# 主函数
main() {
    stop_app
}

# 执行主函数
main
