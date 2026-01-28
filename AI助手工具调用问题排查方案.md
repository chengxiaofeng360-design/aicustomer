# AI助手工具调用问题排查和修复方案

## 问题现象
用户询问"给下这个人网上信息"时，AI回复：
> "我的功能主要限于查看系统中已有的客户基本信息"

**期望行为：** AI应该调用 `search_web` 工具去搜索互联网信息。

---

## 完整请求处理流程（当前）

```
用户输入
  ↓
AiChatController.sendMessage()
  ↓
AiChatServiceImpl.generateAiResponse()
  ├─ 1. 尝试FAQ匹配 ❌ 未命中
  ├─ 2. 构建 FunctionCallRequest（包含 search_web 工具）
  ├─ 3. 调用 AiModelAdapterManager.chatWithFallback()
  │    ├─ 尝试 DeepSeek (Priority 1)
  │    ├─ 尝试 Doubao (Priority 2)
  │    ├─ 尝试 Zhipu GLM-4-Flash (Priority 3)
  │    └─ 尝试 Zhipu Plus (Priority 4) ⚠️ 可能在这里
  │
  └─ 4. AI返回回复，但没有工具调用 ❌
       └─ 直接返回文本："我无法访问外部信息"
```

---

## 根本原因分析

### 原因 1: 使用的AI模型不支持原生 Function Calling

**当前适配器优先级：**
1. DeepSeek ✅ **原生支持** Function Calling
2. Doubao ✅ **原生支持** Function Calling
3. Zhipu GLM-4-Flash ⚠️ **模拟实现**（通过SYSTEM_PROMPT）
4. Zhipu Plus ⚠️ **模拟实现**（通过SYSTEM_PROMPT）

**检查当前使用的模型：**
```bash
# 查看日志，搜索"使用模型"
tail -f logs/ai-customer.log | grep "使用模型"
# 或
tail -f logs/ai-customer.log | grep "适配器"
```

如果日志显示使用的是 `Zhipu-Plus` 或 `Zhipu-Flash`，说明：
- DeepSeek 和 Doubao **不可用**（API Key 未配置或余额不足）
- 回退到了智谱模型

### 原因 2: 智谱模型的 SYSTEM_PROMPT 不够强制

当前 `ZhipuPlusChatAdapter.buildToolPrompt()` 生成的提示词类似：
```
你是一个AI助手...

### 可用工具函数 ###
你可以通过返回JSON格式来调用以下工具：

**search_web**
- 描述: 搜索互联网获取信息...
- 参数: [query]

调用格式：
{"tool": "函数名", "parameters": {参数对象}}
```

**问题：** 智谱AI可能：
- 理解了这个说明，但认为"我不应该调用工具"
- 或者根本没有理解"返回JSON就会触发工具调用"

---

## 解决方案

### 方案 1: 修复 DeepSeek API（推荐）

DeepSeek **原生支持** Function Calling，效果最好。

**步骤：**
1. 检查 `application.yml` 中的DeepSeek配置：
```yaml
ai-customer:
  ai:
    deepseek:
      api-key: sk-4fae8c82d7fb485aa69e7c9e7e4b4cbc
      base-url: https://api.deepseek.com/v1
      model: deepseek-chat
```

2. 验证API Key是否有效：
```bash
curl https://api.deepseek.com/v1/models \
  -H "Authorization: Bearer sk-4fae8c82d7fb485aa69e7c9e7e4b4cbc"
```

3. 如果余额不足，充值后重启服务。

---

### 方案 2: 增强智谱模型的提示词（临时方案）

修改 `ZhipuPlusChatAdapter.java` 的 `buildToolPrompt()` 方法：

```java
private String buildToolPrompt(FunctionCallRequest request) {
    StringBuilder prompt = new StringBuilder(request.getSystemPrompt());
    
    if (request.getFunctions() != null && !request.getFunctions().isEmpty()) {
        prompt.append("\n\n### 【重要】工具调用规则 ###\n");
        prompt.append("你**必须**在以下情况调用工具：\n");
        prompt.append("1. 用户询问系统外部信息（人名、公司、新闻等）→ 调用 search_web\n");
        prompt.append("2. 用户询问客户数量 → 调用 get_customer_count\n");
        prompt.append("3. 用户询问客户列表 → 调用 get_customer_list\n");
        prompt.append("4. 用户询问文件/资料 → 调用 get_file_list 或 get_file_detail\n\n");
        
        prompt.append("**严禁说\"我无法获取外部信息\"！你必须调用 search_web 工具！**\n\n");
        
        prompt.append("### 可用工具 ###\n");
        for (FunctionDefinition func : request.getFunctions()) {
            FunctionDefinition.Function f = func.getFunction();
            prompt.append("**").append(f.getName()).append("**\n");
            prompt.append("- 描述: ").append(f.getDescription()).append("\n");
            if (f.getParameters() != null && f.getParameters().getProperties() != null) {
                prompt.append("- 参数: ").append(f.getParameters().getProperties().keySet()).append("\n");
            }
            prompt.append("\n");
        }
        
        prompt.append("### 调用格式（直接返回JSON，不要用markdown代码块） ###\n");
        prompt.append("{\"tool\": \"函数名\", \"parameters\": {\"参数名\": \"参数值\"}}\n\n");
        prompt.append("示例：\n");
        prompt.append("用户：\"搜索一下马云\"\n");
        prompt.append("你的回复：{\"tool\": \"search_web\", \"parameters\": {\"query\": \"马云\"}}\n\n");
    }
    
    return prompt.toString();
}
```

---

### 方案 3: 配置火山方舟（Doubao）作为备用

火山方舟的 **Doubao-1.5-Pro** 模型原生支持 Function Calling。

**检查配置：**
```yaml
ai-customer:
  ai:
    ark:
      api-key: 0edd85dd-aa89-4f71-817b-803b10e19e6f
      base-url: https://ark.cn-beijing.volces.com/api/v3
      model: doubao-1-5-pro-32k-250115
```

**验证是否可用：**
```bash
tail -f logs/ai-customer.log | grep "Doubao"
```

---

## 验证修复

### 测试步骤：
1. 重启服务（如果修改了代码）
2. 在聊天界面输入：
   ```
   搜索一下马云
   ```
   或
   ```
   查一下西安植物园郑园长
   ```

3. 观察日志：
```bash
tail -f logs/ai-customer.log
```

**期望看到的日志：**
```
【AI聊天服务】AI请求调用1个函数
【Function Calling】执行函数: search_web, 参数: {"query":"西安植物园郑园长"}
【WebSearchService】Performing web search for: 西安植物园郑园长
【AI聊天服务】函数执行完成，结果长度: 512
```

---

## 补充：调试技巧

### 1. 临时强制使用某个模型
修改 `AiModelAdapterManager.java`：
```java
public FunctionCallResponse chatWithFallback(FunctionCallRequest request) {
    // 临时强制使用DeepSeek
    AiModelAdapter deepseek = adapters.stream()
        .filter(a -> a.getName().equals("DeepSeek"))
        .findFirst()
        .orElse(null);
    
    if (deepseek != null && deepseek.isAvailable()) {
        return deepseek.chat(request);
    }
    
    // 否则继续原有的回退逻辑
    ...
}
```

### 2. 查看AI的原始回复
在 `ZhipuPlusChatAdapter.chat()` 中添加日志：
```java
String aiReply = zhipuPlusChatService.chat(request.getUserMessage(), enhancedSystemPrompt);
log.info("【智谱原始回复】{}", aiReply);  // 新增这行
```

这样可以看到AI到底回复了什么，是否包含工具调用的JSON。

---

## 总结

**最可能的原因：**
1. DeepSeek/Doubao API 不可用，系统回退到了智谱模型
2. 智谱模型不理解当前的工具调用提示词

**推荐的修复顺序：**
1. ✅ 先检查 DeepSeek API Key 是否有效
2. ✅ 如果DeepSeek不可用，增强智谱的提示词（方案2）
3. ✅ 测试验证

您需要我帮您执行哪个方案？
