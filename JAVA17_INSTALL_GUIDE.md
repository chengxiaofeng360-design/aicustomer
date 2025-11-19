# Java 17 安装指南

## 为什么需要 Java 17？

Spring Boot 3.2.0 要求 Java 17 或更高版本。当前系统使用的是 Java 8，无法编译 Spring Boot 3.2。

## Java 17 收费情况说明

### Oracle JDK 17
- **2024年9月后需要付费**：Oracle JDK 17 的免费期已结束，商业使用需要订阅
- **费用较高**：按员工数计费，不适合大多数项目

### 推荐：免费的 OpenJDK 发行版

以下发行版**完全免费**，适合生产环境使用：

1. **Eclipse Temurin**（推荐）⭐
   - 您当前使用的就是 Temurin 8
   - 完全免费，提供长期支持（LTS）
   - 下载地址：https://adoptium.net/

2. **Amazon Corretto**
   - 亚马逊维护的 OpenJDK
   - 下载地址：https://aws.amazon.com/corretto/

3. **Azul Zulu**
   - 完全免费，企业级支持
   - 下载地址：https://www.azul.com/downloads/

4. **Microsoft Build of OpenJDK**
   - 微软维护的 OpenJDK
   - 下载地址：https://www.microsoft.com/openjdk

## 安装 Eclipse Temurin 17（推荐）

### macOS 安装步骤

1. **下载安装包**
   ```bash
   # 访问 https://adoptium.net/temurin/releases/
   # 选择 macOS x64，JDK 17，.pkg 格式
   ```

2. **或者使用 Homebrew 安装**（如果已安装 Homebrew）
   ```bash
   brew install --cask temurin17
   ```

3. **验证安装**
   ```bash
   /usr/libexec/java_home -V
   # 应该能看到 17.x.x 版本
   ```

4. **设置 JAVA_HOME**
   ```bash
   # 在 ~/.zshrc 或 ~/.bash_profile 中添加：
   export JAVA_HOME=$(/usr/libexec/java_home -v 17)
   export PATH=$JAVA_HOME/bin:$PATH
   
   # 重新加载配置
   source ~/.zshrc  # 或 source ~/.bash_profile
   ```

5. **验证 Java 版本**
   ```bash
   java -version
   # 应该显示 openjdk version "17.x.x"
   ```

## 安装后的验证

```bash
# 检查 Java 版本
java -version

# 检查 Maven 使用的 Java 版本
./apache-maven-3.8.4/bin/mvn -version

# 尝试编译项目
./apache-maven-3.8.4/bin/mvn clean compile
```

## 注意事项

1. **不影响现有 Java 8 项目**：可以同时安装多个 Java 版本
2. **完全免费**：Eclipse Temurin 是开源免费的，无任何费用
3. **长期支持**：Java 17 是 LTS 版本，支持到 2029 年
4. **生产环境可用**：Eclipse Temurin 被广泛用于生产环境

## 总结

- ✅ **Java 17 本身是免费的**（使用 OpenJDK 发行版）
- ✅ **Eclipse Temurin 17 完全免费**，无任何限制
- ✅ **适合生产环境使用**，无需担心费用问题
- ✅ **不影响现有项目**，可以多版本共存

安装 Java 17 后，即可编译和运行 Spring Boot 3.2 + Spring AI 项目！

