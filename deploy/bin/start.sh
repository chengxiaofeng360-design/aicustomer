#!/bin/bash

###############################################
# AI客户管理系统 - 启动脚本
# 用途: 启动应用程序
# 作者: AI Customer Management Team
# 版本: 1.0.0
###############################################

# 设置环境变量
export JAVA_HOME=/usr/local/java/jdk-17
export PATH=$JAVA_HOME/bin:$PATH

# 应用配置
APP_NAME="ai-customer-management"
APP_VERSION="1.0.0"
JAR_NAME="${APP_NAME}-${APP_VERSION}.jar"

# 目录配置
APP_HOME="/data/app"
JAR_DIR="${APP_HOME}/jars"
JAR_FILE="${JAR_DIR}/${JAR_NAME}"
LOG_DIR="/data/logs/app"
PID_FILE="${APP_HOME}/${APP_NAME}.pid"

# JVM 参数配置
JVM_OPTS="-Xms512m -Xmx2g"
JVM_OPTS="${JVM_OPTS} -XX:+UseG1GC"
JVM_OPTS="${JVM_OPTS} -XX:MaxGCPauseMillis=200"
JVM_OPTS="${JVM_OPTS} -XX:+HeapDumpOnOutOfMemoryError"
JVM_OPTS="${JVM_OPTS} -XX:HeapDumpPath=${LOG_DIR}/heapdump.hprof"
JVM_OPTS="${JVM_OPTS} -Djava.awt.headless=true"
JVM_OPTS="${JVM_OPTS} -Dfile.encoding=UTF-8"

# Spring Boot 配置
SPRING_OPTS="-Dlogging.file.name=${LOG_DIR}/ai-customer.log"

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

# 检查Java环境
check_java() {
    if [ -z "$JAVA_HOME" ]; then
        log_error "JAVA_HOME未设置，请先设置JAVA_HOME环境变量"
        exit 1
    fi

    if ! command -v java &> /dev/null; then
        log_error "找不到java命令，请检查JAVA_HOME配置"
        exit 1
    fi

    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1}')
    if [ "$JAVA_VERSION" -lt 17 ]; then
        log_error "需要Java 17或更高版本，当前版本: $(java -version 2>&1 | head -n 1)"
        exit 1
    fi

    log_info "Java环境检查通过: $(java -version 2>&1 | head -n 1)"
}

# 检查应用是否已运行
check_pid() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p "$PID" > /dev/null 2>&1; then
            return 0  # 应用正在运行
        else
            rm -f "$PID_FILE"
            return 1  # PID文件存在但进程不存在
        fi
    fi
    return 1  # PID文件不存在
}

# 创建必要的目录
create_dirs() {
    log_info "创建必要的目录..."
    mkdir -p "$LOG_DIR"
    mkdir -p "/data/files/upload"
    mkdir -p "/data/tmp"
    mkdir -p "/data/backup"
}

# 启动应用
start_app() {
    log_info "===== 启动 ${APP_NAME} ====="

    # 检查Java环境
    check_java

    # 检查JAR文件是否存在
    if [ ! -f "$JAR_FILE" ]; then
        log_error "JAR文件不存在: $JAR_FILE"
        exit 1
    fi

    # 检查应用是否已运行
    if check_pid; then
        log_warn "应用已在运行中 (PID: $(cat $PID_FILE))"
        exit 0
    fi

    # 创建必要的目录
    create_dirs

    # 启动应用
    log_info "正在启动应用..."
    log_info "JAR文件: $JAR_FILE"
    log_info "JVM参数: $JVM_OPTS"
    log_info "日志目录: $LOG_DIR"

    nohup java $JVM_OPTS $SPRING_OPTS -jar "$JAR_FILE" \
        > "${LOG_DIR}/console.log" 2>&1 &

    # 保存PID
    echo $! > "$PID_FILE"
    PID=$(cat "$PID_FILE")

    # 等待启动
    log_info "应用正在启动，PID: $PID"
    sleep 3

    # 检查是否启动成功
    if check_pid; then
        log_info "✓ 应用启动成功！"
        log_info "查看日志: tail -f ${LOG_DIR}/ai-customer.log"
        log_info "查看控制台输出: tail -f ${LOG_DIR}/console.log"
    else
        log_error "✗ 应用启动失败，请查看日志: ${LOG_DIR}/console.log"
        exit 1
    fi
}

# 主函数
main() {
    start_app
}

# 执行主函数
main
