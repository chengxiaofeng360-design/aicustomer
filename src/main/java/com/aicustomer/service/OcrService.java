package com.aicustomer.service;

import com.aicustomer.entity.Customer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OcrService {

    @Autowired
    private ZhipuChatService zhipuChatService;

    @Autowired
    private ArkChatService arkChatService;

    @Autowired
    private DeepSeekChatService deepSeekChatService;

    @Autowired
    private PaddleOcrService paddleOcrService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Customer> parseBusinessCard(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件为空");
        }

        // 转换为Base64
        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        return parseBusinessCard(base64Image);
    }

    public List<Customer> parseBusinessCard(String base64Image) {
        // 构建兼容性前缀
        String base64Data = base64Image;
        if (!base64Image.startsWith("data:image")) {
            base64Data = "data:image/jpeg;base64," + base64Image;
        }

        StringBuilder errors = new StringBuilder();

        // 策略1：PaddleOCR + DeepSeek（本地专业组合，优先级最高）
        if (paddleOcrService != null && paddleOcrService.isAvailable()) {
            log.info("【OcrService】策略1：尝试使用 PaddleOCR + DeepSeek 组合");
            try {
                // 1.1 使用 PaddleOCR 提取文本
                String ocrText = paddleOcrService.recognizeText(base64Image);
                log.info("【OcrService】PaddleOCR 提取文本成功，长度: {}", ocrText.length());

                // 1.2 使用 DeepSeek 将文本解析为 JSON 数组
                String dsPrompt = "你是一个智能数据助理。以下是 OCR 识别出的文字，它们可能来自一张单人名片，也可能来自通讯录列表或表格（包含多条记录）。\n\n" +
                        "【任务】:\n" +
                        "1. 分析 OCR 文本，判断包含了多少个人的联系方式\n" +
                        "2. 提取**所有**完整的联系人信息，不要遗漏任何一条\n" +
                        "3. 始终返回 JSON 数组格式，即使只有一个人也返回 [单个对象]\n\n" +

                        "【字段说明】:\n" +
                        "- customerName: 客户/公司名称\n" +
                        "- contactPerson: 联系人姓名\n" +
                        "- phone: 电话号码\n" +
                        "- position: 职位\n" +
                        "- region: 地区\n" +
                        "- email: 邮箱\n" +
                        "- address: 地址\n\n" +

                        "【处理规则】:\n" +
                        "- 如果是表格格式：识别表头，将同一行数据关联为一个对象\n" +
                        "- 无法区分客户名称和联系人时：人名填入 contactPerson，公司名填入 customerName\n" +
                        "- 字段无法提取时：设为 null\n" +
                        "- 保持所有字段完整，不要省略\n\n" +

                        "【输出示例】:\n" +
                        "[\n" +
                        "  {\n" +
                        "    \"customerName\": \"XX科技有限公司\",\n" +
                        "    \"contactPerson\": \"张三\",\n" +
                        "    \"phone\": \"13800138000\",\n" +
                        "    \"position\": \"销售经理\",\n" +
                        "    \"region\": \"北京\",\n" +
                        "    \"email\": \"zhangsan@example.com\",\n" +
                        "    \"address\": \"北京市朝阳区XX路XX号\"\n" +
                        "  }\n" +
                        "]\n\n" +

                        "【OCR原文】:\n" + ocrText + "\n\n" +

                        "请直接返回纯 JSON 数组，不要添加任何解释或markdown标记：";

                String aiResult = deepSeekChatService.chat(dsPrompt, "You are a judgmental intelligence assistant.");
                List<Customer> customers = parseAiResultToList(aiResult);

                if (customers != null && !customers.isEmpty()) {
                    log.info("【OcrService】PaddleOCR + DeepSeek 识别成功，提取到 {} 条记录", customers.size());
                    for (Customer c : customers) {
                        String remark = (c.getRemark() != null ? c.getRemark() : "");
                        remark += " (PaddleOCR+DeepSeek)";
                        c.setRemark(remark);
                    }
                    return customers;
                }
            } catch (Exception e) {
                log.error("【OcrService】PaddleOCR + DeepSeek 识别失败: {}", e.getMessage());
                errors.append("PaddleOCR+DeepSeek Error: ").append(e.getMessage()).append("; ");
            }
        } else {
            log.warn("【OcrService】PaddleOCR 服务不可用，跳过策略1");
        }

        // 策略2：智谱AI（云端视觉AI）- 目前作为备选，或者在Paddle失败时用
        // 这里的提示词也需要改为支持数组，但鉴于 DeepSeek 效果更好，我们暂时在智谱这里保持单条或尝试解析
        // 为简化逻辑，并在多条记录需求下，这里建议如果 Paddle 失败，也让智谱尝试返回数组
        if (zhipuChatService != null && zhipuChatService.isAvailable()) {
            log.info("【OcrService】策略2：尝试使用智谱AI");
            try {
                String prompt = "请识别这张图片中的客户信息。图片可能包含单张名片或多条通讯录记录。\n" +
                        "请提取所有记录，并Strictly return a JSON Array `[...]`。\n" +
                        "字段：customerName, contactPerson, phone, position, region, email, address";

                String aiResult = zhipuChatService.chatWithImage(prompt, base64Image, null);
                log.info("【OcrService】智谱AI返回: {}", aiResult);

                List<Customer> customers = parseAiResultToList(aiResult);
                if (customers != null && !customers.isEmpty()) {
                    return customers;
                }

                // 尝试修复非数组的JSON
                // ... (简化起见，暂略，假设智谱能遵循指令)
            } catch (Exception e) {
                log.warn("【OcrService】智谱AI识别失败: {}", e.getMessage());
            }
        }

        // 如果所有策略都失败，抛出异常或返回空
        if (errors.length() > 0) {
            throw new RuntimeException("识别失败: " + errors.toString());
        }
        return Collections.emptyList();
    }

    private List<Customer> parseAiResultToList(String aiResult) {
        if (aiResult == null || aiResult.trim().isEmpty()) {
            return null;
        }
        try {
            // 清理Markdown
            String jsonStr = aiResult.trim();
            if (jsonStr.startsWith("```json")) {
                jsonStr = jsonStr.substring(7);
            } else if (jsonStr.startsWith("```")) {
                jsonStr = jsonStr.substring(3);
            }
            if (jsonStr.endsWith("```")) {
                jsonStr = jsonStr.substring(0, jsonStr.length() - 3);
            }
            jsonStr = jsonStr.trim();

            List<Customer> resultList = new ArrayList<>();

            // 尝试解析为 List<Map>
            if (jsonStr.startsWith("[")) {
                List<Map<String, Object>> list = objectMapper.readValue(jsonStr,
                        new TypeReference<List<Map<String, Object>>>() {
                        });
                for (Map<String, Object> map : list) {
                    resultList.add(mapToCustomer(map));
                }
            } else if (jsonStr.startsWith("{")) {
                // 如果AI只返回了一个对象，手动包一层
                Map<String, Object> map = objectMapper.readValue(jsonStr, Map.class);
                // 检查是否包含 list/data 等包装字段
                if (map.containsKey("list") && map.get("list") instanceof List) {
                    List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("list");
                    for (Map<String, Object> m : list) {
                        resultList.add(mapToCustomer(m));
                    }
                } else if (map.containsKey("data") && map.get("data") instanceof List) {
                    List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("data");
                    for (Map<String, Object> m : list) {
                        resultList.add(mapToCustomer(m));
                    }
                } else {
                    // 就是单个对象
                    resultList.add(mapToCustomer(map));
                }
            }

            return resultList;

        } catch (Exception e) {
            log.error("解析AI结果失败: {}", e.getMessage());
            log.debug("原始结果: {}", aiResult);
            return null;
        }
    }

    private Customer mapToCustomer(Map<String, Object> map) {
        Customer customer = new Customer();
        customer.setCustomerName((String) map.getOrDefault("customerName", ""));
        customer.setContactPerson((String) map.getOrDefault("contactPerson", ""));
        customer.setPhone((String) map.getOrDefault("phone", ""));

        // 映射客户类型
        Object typeObj = map.get("customerType");
        String typeStr = typeObj != null ? String.valueOf(typeObj) : null;
        if (typeStr != null) {
            if (typeStr.contains("个人") || typeStr.equals("1")) {
                customer.setCustomerType(1);
            } else if (typeStr.contains("企业") || typeStr.equals("2")) {
                customer.setCustomerType(2);
            } else if (typeStr.contains("科研") || typeStr.equals("3")) {
                customer.setCustomerType(3);
            } else {
                customer.setCustomerType(2); // 默认
            }
        } else {
            customer.setCustomerType(2); // 默认
        }

        customer.setRegion((String) map.get("region"));

        if (map.get("address") != null)
            customer.setAddress((String) map.get("address"));
        if (map.get("email") != null)
            customer.setEmail((String) map.get("email"));
        if (map.get("qqWeixin") != null)
            customer.setQqWeixin((String) map.get("qqWeixin"));
        if (map.get("position") != null)
            customer.setPosition((String) map.get("position"));
        if (map.get("remark") != null)
            customer.setRemark((String) map.get("remark"));
        if (map.get("cooperationContent") != null)
            customer.setCooperationContent((String) map.get("cooperationContent"));

        return customer;
    }
}
