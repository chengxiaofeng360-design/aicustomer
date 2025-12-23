# Maven 依赖说明

将以下依赖添加到你的项目 `pom.xml` 文件的 `<dependencies>` 标签中：

```xml
<!-- Spring Boot Web - 提供REST API支持 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- MyBatis Spring Boot Starter - 数据库ORM框架 -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.3</version>
</dependency>

<!-- MySQL Connector - MySQL数据库驱动 -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- PageHelper - 分页插件 -->
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>2.1.0</version>
</dependency>

<!-- Lombok - 简化Java代码 -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Apache PDFBox - PDF文本提取 -->
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>2.0.29</version>
</dependency>

<!-- Apache POI - Word/Excel文档处理 -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.3</version>
</dependency>
```

## 依赖说明

| 依赖 | 版本 | 用途 | 是否必需 |
|------|------|------|----------|
| spring-boot-starter-web | (由Spring Boot管理) | REST API、文件上传 | 必需 |
| mybatis-spring-boot-starter | 3.0.3 | 数据库操作 | 必需 |
| mysql-connector-j | (由Spring Boot管理) | MySQL连接 | 必需 |
| pagehelper-spring-boot-starter | 2.1.0 | 分页功能 | 必需 |
| lombok | (由Spring Boot管理) | 简化代码 | 必需 |
| pdfbox | 2.0.29 | PDF文本提取 | 如不需要PDF上传可选 |
| poi-ooxml | 5.2.3 | Word/Excel处理 | 如不需要Word上传可选 |

## 注意事项

1. **Spring Boot版本**：确保你的项目使用Spring Boot 3.x版本
2. **Java版本**：需要Java 17或更高版本
3. **Lombok配置**：如果IDE无法识别Lombok注解，需要安装Lombok插件
4. **版本冲突**：如果你的项目已有这些依赖，请检查版本兼容性

## 最小依赖集

如果你只需要基本功能（不包括文件上传和文本提取），最小依赖为：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.3</version>
</dependency>

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>2.1.0</version>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

但这样会导致文件上传功能中的PDF和Word文本提取失败。
