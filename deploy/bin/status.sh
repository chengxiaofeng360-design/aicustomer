#!/bin/bash

###############################################
# AI客户管理系统 - 状态查询脚本
# 用途: 查看应用运行状态
# 作者: AI Customer Management Team
# 版本: 1.0.0
###############################################

# 应用配置
APP_NAME="ai-customer-management"
APP_HOME="/data/app"
PID_FILE="${APP_HOME}/${APP_NAME}.pid"
LOG_DIR="/data/logs/app"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 打印分隔线
print_line() {
    echo "=================================================="
}

# 检查应用状态
check_status() {
    print_line
    echo -e "${BLUE}应用名称:${NC} ${APP_NAME}"
    echo -e "${BLUE}PID文件:${NC} ${PID_FILE}"
    print_line

    if [ ! -f "$PID_FILE" ]; then
        echo -e "${RED}✗ 应用未运行${NC} (PID文件不存在)"
        return 1
    fi

    PID=$(cat "$PID_FILE")
    
    if ! ps -p "$PID" > /dev/null 2>&1; then
        echo -e "${RED}✗ 应用未运行${NC} (进程不存在，PID: $PID)"
        return 1
    fi

    # 应用正在运行
    echo -e "${GREEN}✓ 应用正在运行${NC}"
    echo ""
    
    # 显示进程信息
    echo -e "${BLUE}进程ID:${NC} ${PID}"
    echo -e "${BLUE}启动时间:${NC} $(ps -p $PID -o lstart= 2>/dev/null)"
    echo -e "${BLUE}运行时长:${NC} $(ps -p $PID -o etime= 2>/dev/null)"
    echo -e "${BLUE}CPU使用:${NC} $(ps -p $PID -o %cpu= 2>/dev/null)%"
    echo -e "${BLUE}内存使用:${NC} $(ps -p $PID -o %mem= 2>/dev/null)% ($(ps -p $PID -o rss= 2>/dev/null | awk '{print int($1/1024)"MB"}'))"
    echo ""
    
    # 显示端口信息
    echo -e "${BLUE}监听端口:${NC}"
    netstat -tlnp 2>/dev/null | grep "$PID" | awk '{print "  - "$4}' || \
    ss -tlnp 2>/dev/null | grep "$PID" | awk '{print "  - "$4}'
    echo ""
    
    # 显示最近日志
    if [ -f "${LOG_DIR}/ai-customer.log" ]; then
        echo -e "${BLUE}最近日志:${NC}"
        tail -n 5 "${LOG_DIR}/ai-customer.log" | sed 's/^/  /'
        echo ""
        echo -e "${YELLOW}查看完整日志:${NC} tail -f ${LOG_DIR}/ai-customer.log"
    fi
    
    print_line
    return 0
}

# 主函数
main() {
    check_status
}

# 执行主函数
main
