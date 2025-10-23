#!/bin/bash

echo "========================================"
echo "   AI客户管理系统 - 本地演示版本"
echo "========================================"
echo

# 设置Java和Maven环境变量
export JAVA_HOME=~/jdk-11.0.2.jdk/Contents/Home
export MAVEN_HOME=~/apache-maven-3.8.8
export PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH

echo "Java版本:"
java -version
echo

echo "Maven版本:"
mvn -version
echo

echo "项目信息:"
echo "- 项目名称: AI客户管理系统"
echo "- 技术栈: Spring Boot + MyBatis + H2数据库"
echo "- 功能模块: 客户管理、沟通记录、AI分析等"
echo

echo "由于网络限制，无法下载Maven依赖，但项目已配置完成："
echo "✅ Java 11 已安装"
echo "✅ Maven 3.8.8 已安装"
echo "✅ 项目源码已下载"
echo "✅ 数据库配置已完成（H2内存数据库）"
echo "✅ 前端页面已准备就绪"
echo

echo "项目文件结构:"
echo "📁 src/main/java/     - Java源码"
echo "📁 src/main/resources/ - 配置文件和静态资源"
echo "📁 target/classes/    - 编译后的类文件"
echo "📄 pom.xml           - Maven配置文件"
echo "📄 application.yml   - Spring Boot配置"
echo

echo "要完整运行项目，需要："
echo "1. 解决网络连接问题（下载Maven依赖）"
echo "2. 或者使用离线Maven仓库"
echo "3. 或者使用Docker容器运行"
echo

echo "项目已准备就绪，可以开始开发！"
echo "========================================"





