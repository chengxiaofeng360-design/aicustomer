@echo off
echo ========================================
echo    AI客户管理系统启动脚本
echo ========================================
echo.

echo 正在检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Java环境，请先安装JDK 8+
    pause
    exit /b 1
)

echo.
echo 正在检查Maven环境...
mvn -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Maven环境，请先安装Maven 3.6+
    pause
    exit /b 1
)

echo.
echo 正在编译项目...
call mvn clean compile
if %errorlevel% neq 0 (
    echo 错误: 项目编译失败
    pause
    exit /b 1
)

echo.
echo 正在启动应用...
echo 应用将在 http://localhost:8080/ai-customer/ 启动
echo 按 Ctrl+C 停止应用
echo.

call mvn spring-boot:run

pause

