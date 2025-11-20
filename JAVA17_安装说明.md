# Java 17 (Temurin) 安装指南

## 方式一：手动下载安装（推荐，最简单）

### 步骤：

1. **访问下载页面**
   - 打开浏览器，访问：https://adoptium.net/zh-CN/temurin/releases/?version=17

2. **选择正确的版本**
   - 操作系统：macOS
   - 架构：ARM64 (Apple Silicon) 或 x64 (Intel)
   - 包类型：JDK
   - 版本：17 (LTS)

3. **下载安装包**
   - 点击下载 `.pkg` 文件（约200MB）

4. **安装**
   - 双击下载的 `.pkg` 文件
   - 按照安装向导完成安装
   - 安装位置会自动设置为：`/Library/Java/JavaVirtualMachines/temurin-17.jdk`

5. **验证安装**
   ```bash
   /usr/libexec/java_home -V
   ```
   应该能看到 Java 17 的版本信息

## 方式二：使用 Homebrew 安装（如果已安装 Homebrew）

```bash
# 安装 Homebrew（如果还没有）
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# 安装 Java 17
brew install --cask temurin17
```

## 配置环境变量

安装完成后，将以下内容添加到 `~/.zshrc` 或 `~/.bash_profile`：

```bash
# Java 17 配置
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH=$JAVA_HOME/bin:$PATH
```

然后执行：
```bash
source ~/.zshrc  # 或 source ~/.bash_profile
```

## 验证安装

```bash
java -version
```

应该显示：
```
openjdk version "17.0.x"
OpenJDK Runtime Environment Temurin-17.0.x+xx
OpenJDK 64-Bit Server VM Temurin-17.0.x+xx (build 17.0.x+xx, mixed mode, sharing)
```

## 安装位置

Java 17 将安装在系统标准位置：
- `/Library/Java/JavaVirtualMachines/temurin-17.jdk`

这是 macOS 的标准 Java 安装位置，不会影响项目目录。

