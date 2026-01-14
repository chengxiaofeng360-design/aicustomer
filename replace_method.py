#!/usr/bin/env python3
"""
替换generateAiResponse方法为新的统一架构版本
"""

FILE_PATH = "/Users/zuozuo/Downloads/cxf/aicustomer/src/main/java/com/aicustomer/service/impl/AiChatServiceImpl.java"
NEW_METHOD_PATH = "/Users/zuozuo/Downloads/cxf/aicustomer/new_generateAiResponse.java"

def main():
    print("读取文件...")
    with open(FILE_PATH, 'r', encoding='utf-8') as f:
        content = f.read()
    
    with open(NEW_METHOD_PATH, 'r', encoding='utf-8') as f:
        new_method = f.read()
    
    # 找到方法开始和结束位置
    method_start = "private String generateAiResponse(String userMessage, List<Map<String, String>> history) {"
    fallback_start = "private String generateFallbackResponse(String userMessage) {"
    
    start_idx = content.find(method_start)
    if start_idx == -1:
        print("✗ 未找到generateAiResponse方法")
        return
    
    end_idx = content.find(fallback_start, start_idx)
    if (end_idx == -1):
        print("✗ 未找到方法结束位置")
        return
    
    # 找到前一个换行
    end_idx = content.rfind('\n', start_idx, end_idx)
    
    print(f"找到方法位置: {start_idx} - {end_idx}")
    print(f"旧方法长度: {end_idx - start_idx} 字符")
    
    # 添加必要的导入
    new_imports = """import com.aicustomer.model.FunctionCallRequest;
import com.aicustomer.model.FunctionCallResponse;
"""
    
    # 检查是否已有这些导入
    if "import com.aicustomer.model.FunctionCallRequest;" not in content:
        # 在其他model导入后添加
        import_pos = content.find("import com.aicustomer.model.FunctionDefinition;")
        if import_pos != -1:
            import_end = content.find('\n', import_pos) + 1
            content = content[:import_end] + new_imports + content[import_end:]
            print("✓ 添加了新的导入")
            
            # 更新索引
            start_idx = content.find(method_start)
            end_idx = content.find(fallback_start, start_idx)
            end_idx = content.rfind('\n', start_idx, end_idx)
    
    # 替换方法
    content = content[:start_idx] + new_method.strip() + "\n\n    " + content[end_idx:]
    
    print("✓ 方法已替换")
    print(f"新方法长度: {len(new_method)} 字符")
    
    # 保存
    with open(FILE_PATH, 'w', encoding='utf-8') as f:
        f.write(content)
    
    print("\n✅ 替换完成！")
    print("   - 从300+行简化为120行")
    print("   - 使用统一适配器架构")
    print("   - 自动模型选择和回退")

if __name__ == "__main__":
    main()
