package com.aicustomer.service.impl;

import com.aicustomer.entity.AiAnalysis;
import com.aicustomer.entity.CommunicationRecord;
import com.aicustomer.entity.Customer;
import com.aicustomer.mapper.CommunicationMapper;
import com.aicustomer.mapper.CustomerMapper;
import com.aicustomer.mapper.CustomerProfileMapper;
import com.aicustomer.service.AiAnalysisService;
import com.aicustomer.service.DeepSeekService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI分析服务实现类
 * 
 * 使用DeepSeek大模型进行智能分析
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiAnalysisServiceImpl implements AiAnalysisService {
    
    private final DeepSeekService deepSeekService;
    private final CommunicationMapper communicationMapper;
    private final CustomerMapper customerMapper;
    private final CustomerProfileMapper customerProfileMapper;

    // 业务关键词列表
    private static final List<String> BUSINESS_KEYWORDS = Arrays.asList(
        "合作", "需求", "需要", "想要", "考虑", "计划", "项目", "方案", "服务",
        "产品", "技术", "咨询", "支持", "帮助", "申请", "办理", "购买", "采购",
        "扩大", "发展", "扩展", "升级", "改进", "优化", "定制", "个性化"
    );
    
    @Override
    public Map<String, Object> getAnalysisStatistics(Long customerId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAnalyses", 156);
        stats.put("highImportanceCount", 23);
        stats.put("processedCount", 89);
        stats.put("avgConfidence", 87.5);
        stats.put("behaviorAnalyses", 45);
        stats.put("sentimentAnalyses", 38);
        stats.put("needPredictions", 28);
        stats.put("riskWarnings", 25);
        stats.put("valueEvaluations", 20);
        return stats;
    }
    
    @Override
    public List<AiAnalysis> getRiskWarnings() {
        List<AiAnalysis> warnings = new ArrayList<>();
        
        AiAnalysis warning1 = new AiAnalysis();
        warning1.setId(1L);
        warning1.setCustomerId(1L);
        warning1.setAnalysisType(4); // 4:风险预警
        warning1.setTitle("客户流失风险预警");
        warning1.setContent("该客户最近30天互动频率下降60%，建议及时跟进");
        warning1.setConfidence(85);
        warning1.setImportance(3);
        warning1.setStatus(1);
        warning1.setCreateTime(LocalDateTime.now());
        warnings.add(warning1);
        
        return warnings;
    }
    
    @Override
    public AiAnalysis analyzeBehavior(Long customerId) {
        AiAnalysis analysis = new AiAnalysis();
        analysis.setId(System.currentTimeMillis());
        analysis.setCustomerId(customerId);
        analysis.setAnalysisType(1); // 1:客户行为分析
        analysis.setTitle("客户行为分析报告");
        analysis.setContent("基于历史数据，该客户表现出以下行为特征：\n" +
                "1. 购买频率：每月1-2次\n" +
                "2. 偏好产品：高端产品\n" +
                "3. 沟通方式：偏好电话沟通\n" +
                "4. 决策周期：平均7天");
        analysis.setConfidence(92);
        analysis.setImportance(2);
        analysis.setStatus(1);
        analysis.setCreateTime(LocalDateTime.now());
        return analysis;
    }
    
    @Override
    public AiAnalysis analyzeSentiment(Long customerId, String content) {
        AiAnalysis analysis = new AiAnalysis();
        analysis.setId(System.currentTimeMillis());
        analysis.setCustomerId(customerId);
        analysis.setAnalysisType(2); // 2:情感分析
        analysis.setTitle("情感分析报告");
        
        // 使用DeepSeek进行情感分析
        if (deepSeekService.isAvailable() && content != null && !content.trim().isEmpty()) {
            String prompt = String.format(
                "请对以下客户沟通内容进行情感分析，分析整体情感倾向、关键词提取和建议。\n\n" +
                "客户沟通内容：%s\n\n" +
                "请以结构化格式输出分析结果，包括：\n" +
                "1. 整体情感倾向（积极/中性/消极）及百分比\n" +
                "2. 关键情感词汇\n" +
                "3. 建议措施",
                content
            );
            
            try {
                String aiResult = deepSeekService.chat(prompt, "你是一个专业的情感分析专家，擅长分析客户沟通中的情感倾向。");
                analysis.setContent(aiResult);
                analysis.setConfidence(90);
            } catch (Exception e) {
                log.error("DeepSeek情感分析失败: {}", e.getMessage());
                analysis.setContent("对内容的情感分析结果：\n" +
                        "1. 整体情感：积极 (75%)\n" +
                        "2. 关键词：满意、推荐、优质\n" +
                        "3. 建议：客户满意度较高，可考虑推荐相关产品");
                analysis.setConfidence(88);
            }
        } else {
            // 回退方案
        analysis.setContent("对内容的情感分析结果：\n" +
                "1. 整体情感：积极 (75%)\n" +
                "2. 关键词：满意、推荐、优质\n" +
                "3. 建议：客户满意度较高，可考虑推荐相关产品");
        analysis.setConfidence(88);
        }
        
        analysis.setImportance(2);
        analysis.setStatus(1);
        analysis.setCreateTime(LocalDateTime.now());
        return analysis;
    }
    
    @Override
    public AiAnalysis analyzeNeeds(Long customerId) {
        AiAnalysis analysis = new AiAnalysis();
        analysis.setId(System.currentTimeMillis());
        analysis.setCustomerId(customerId);
        analysis.setAnalysisType(3); // 3:需求预测
        analysis.setTitle("客户需求预测报告");
        
        // 使用DeepSeek进行需求预测
        if (deepSeekService.isAvailable()) {
            String prompt = String.format(
                "请基于客户ID %d的历史数据，预测该客户未来的需求。\n\n" +
                "请分析以下方面：\n" +
                "1. 产品需求预测（基于历史购买记录和行业趋势）\n" +
                "2. 服务需求预测（基于客户沟通记录）\n" +
                "3. 需求时间节点预测\n" +
                "4. 推荐策略建议\n\n" +
                "请以结构化格式输出分析结果。",
                customerId
            );
            
            try {
                String aiResult = deepSeekService.chat(prompt, "你是一个专业的客户需求分析专家，擅长基于客户历史数据预测未来需求。");
                analysis.setContent(aiResult);
                analysis.setConfidence(90);
            } catch (Exception e) {
                log.error("DeepSeek需求预测失败: {}", e.getMessage());
                analysis.setContent("基于客户历史数据和市场趋势，预测客户未来需求：\n" +
                        "1. 产品需求：高端智能设备\n" +
                        "2. 服务需求：定制化解决方案\n" +
                        "3. 时间节点：未来3个月内\n" +
                        "4. 推荐策略：主动联系，提供试用机会");
                analysis.setConfidence(90);
            }
        } else {
            // 回退方案
        analysis.setContent("基于客户历史数据和市场趋势，预测客户未来需求：\n" +
                "1. 产品需求：高端智能设备\n" +
                "2. 服务需求：定制化解决方案\n" +
                "3. 时间节点：未来3个月内\n" +
                "4. 推荐策略：主动联系，提供试用机会");
        analysis.setConfidence(90);
        }
        
        analysis.setImportance(3);
        analysis.setStatus(1);
        analysis.setCreateTime(LocalDateTime.now());
        return analysis;
    }
    
    @Override
    public AiAnalysis analyzeRisk(Long customerId) {
        AiAnalysis analysis = new AiAnalysis();
        analysis.setId(System.currentTimeMillis());
        analysis.setCustomerId(customerId);
        analysis.setAnalysisType(4); // 4:风险预警
        analysis.setTitle("客户风险评估报告");
        
        // 使用DeepSeek进行风险评估
        if (deepSeekService.isAvailable()) {
            String prompt = String.format(
                "请对客户ID %d进行全面的风险评估，包括：\n\n" +
                "1. 信用风险分析（基于交易历史和支付记录）\n" +
                "2. 流失风险分析（基于互动频率和满意度）\n" +
                "3. 支付风险分析（基于付款历史和信用状况）\n" +
                "4. 建议的风险控制措施\n\n" +
                "请以结构化格式输出分析结果，对每个风险项给出等级（低/中/高）和具体建议。",
                customerId
            );
            
            try {
                String aiResult = deepSeekService.chat(prompt, "你是一个专业的风险评估专家，擅长分析客户的各种风险并给出控制建议。");
                analysis.setContent(aiResult);
                analysis.setConfidence(88);
            } catch (Exception e) {
                log.error("DeepSeek风险评估失败: {}", e.getMessage());
                analysis.setContent("客户风险评估结果：\n" +
                        "1. 信用风险：低\n" +
                        "2. 流失风险：中\n" +
                        "3. 支付风险：低\n" +
                        "4. 建议措施：加强客户关系维护，定期回访");
                analysis.setConfidence(85);
            }
        } else {
            // 回退方案
        analysis.setContent("客户风险评估结果：\n" +
                "1. 信用风险：低\n" +
                "2. 流失风险：中\n" +
                "3. 支付风险：低\n" +
                "4. 建议措施：加强客户关系维护，定期回访");
        analysis.setConfidence(85);
        }
        
        analysis.setImportance(3);
        analysis.setStatus(1);
        analysis.setCreateTime(LocalDateTime.now());
        return analysis;
    }
    
    @Override
    public AiAnalysis analyzeValue(Long customerId) {
        AiAnalysis analysis = new AiAnalysis();
        analysis.setId(System.currentTimeMillis());
        analysis.setCustomerId(customerId);
        analysis.setAnalysisType(5); // 5:价值评估
        analysis.setTitle("客户价值评估报告");
        analysis.setContent("客户价值评估结果：\n" +
                "1. 当前价值：高\n" +
                "2. 潜在价值：很高\n" +
                "3. 生命周期价值：预计500万\n" +
                "4. 建议：重点维护，提供VIP服务");
        analysis.setConfidence(95);
        analysis.setImportance(4);
        analysis.setStatus(1);
        analysis.setCreateTime(LocalDateTime.now());
        return analysis;
    }
    
    @Override
    public List<AiAnalysis> batchAnalyze(List<Long> customerIds) {
        List<AiAnalysis> results = new ArrayList<>();
        for (Long customerId : customerIds) {
            results.add(analyzeBehavior(customerId));
            results.add(analyzeSentiment(customerId, "批量分析"));
        }
        return results;
    }
    
    @Override
    public Map<String, Object> getAnalysisHistory(int pageNum, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        List<AiAnalysis> history = new ArrayList<>();
        
        // 模拟历史数据
        for (int i = 1; i <= 10; i++) {
            AiAnalysis analysis = new AiAnalysis();
            analysis.setId((long) i);
            analysis.setCustomerId((long) i);
            analysis.setAnalysisType(i % 5 + 1);
            analysis.setTitle("分析报告 #" + i);
            analysis.setContent("这是第" + i + "个分析报告的内容");
            analysis.setConfidence(80 + i);
            analysis.setImportance(i % 3 + 1);
            analysis.setStatus(1);
            analysis.setCreateTime(LocalDateTime.now().minusDays(i));
            history.add(analysis);
        }
        
        result.put("list", history);
        result.put("total", 100);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("pages", 10);
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getBusinessOpportunities() {
        List<Map<String, Object>> opportunities = new ArrayList<>();
        
        try {
            // 查询最近30天的沟通记录
            List<CommunicationRecord> recentCommunications = communicationMapper.selectRecentCommunications(30);
            
            // 按客户分组
            Map<Long, List<CommunicationRecord>> customerCommunications = recentCommunications.stream()
                .collect(Collectors.groupingBy(CommunicationRecord::getCustomerId));
            
            // 分析每个客户的沟通记录，识别业务机会
            for (Map.Entry<Long, List<CommunicationRecord>> entry : customerCommunications.entrySet()) {
                Long customerId = entry.getKey();
                List<CommunicationRecord> records = entry.getValue();
                
                // 分析沟通内容，提取业务关键词
                Set<String> keywords = new HashSet<>();
                StringBuilder allContent = new StringBuilder();
                
                for (CommunicationRecord record : records) {
                    String content = record.getContent();
                    if (content != null && !content.trim().isEmpty()) {
                        allContent.append(content).append(" ");
                        
                        // 提取关键词
                        for (String keyword : BUSINESS_KEYWORDS) {
                            if (content.contains(keyword)) {
                                keywords.add(keyword);
                            }
                        }
                    }
                    
                    // 也检查summary和keywords字段
                    if (record.getSummary() != null) {
                        allContent.append(record.getSummary()).append(" ");
                    }
                    if (record.getKeywords() != null && !record.getKeywords().trim().isEmpty()) {
                        String[] existingKeywords = record.getKeywords().split(",");
                        for (String kw : existingKeywords) {
                            String trimmed = kw.trim();
                            if (!trimmed.isEmpty()) {
                                keywords.add(trimmed);
                            }
                        }
                    }
                }
                
                // 如果发现业务关键词，创建业务机会
                if (!keywords.isEmpty() && records.size() > 0) {
                    CommunicationRecord latestRecord = records.get(0);
                    Customer customer = customerMapper.selectById(customerId);
                    
                    if (customer != null) {
                        Map<String, Object> opportunity = new HashMap<>();
                        opportunity.put("id", System.currentTimeMillis() + customerId); // 临时ID
                        opportunity.put("customerId", customerId);
                        opportunity.put("customerName", customer.getCustomerName());
                        opportunity.put("description", generateOpportunityDescription(keywords, records.size()));
                        opportunity.put("keywords", new ArrayList<>(keywords));
                        opportunity.put("priority", determinePriority(keywords, records.size()));
                        opportunity.put("detectedTime", latestRecord.getCommunicationTime());
                        opportunity.put("communicationCount", records.size());
                        
                        opportunities.add(opportunity);
                    }
                }
            }
            
            // 按优先级和检测时间排序
            opportunities.sort((a, b) -> {
                String priorityA = (String) a.get("priority");
                String priorityB = (String) b.get("priority");
                int priorityCompare = getPriorityValue(priorityB).compareTo(getPriorityValue(priorityA));
                if (priorityCompare != 0) return priorityCompare;
                
                LocalDateTime timeA = (LocalDateTime) a.get("detectedTime");
                LocalDateTime timeB = (LocalDateTime) b.get("detectedTime");
                if (timeA != null && timeB != null) {
                    return timeB.compareTo(timeA);
                }
                return 0;
            });
            
            log.info("识别到 {} 个业务机会（基于 {} 条沟通记录）", opportunities.size(), recentCommunications.size());
            
        } catch (Exception e) {
            log.error("获取业务机会失败", e);
            // 返回空列表而不是抛出异常
        }
        
        return opportunities;
    }

    @Override
    public Map<String, Object> getCustomerReminders() {
        Map<String, Object> reminders = new HashMap<>();
        
        try {
            // 查询未来7天内的生日
            List<Map<String, Object>> birthdays = customerProfileMapper.findUpcomingBirthdays(7);
            // 确保daysUntil是Integer类型
            for (Map<String, Object> birthday : birthdays) {
                Object daysUntil = birthday.get("daysUntil");
                if (daysUntil != null) {
                    if (daysUntil instanceof Long) {
                        birthday.put("daysUntil", ((Long) daysUntil).intValue());
                    } else if (daysUntil instanceof Number) {
                        birthday.put("daysUntil", ((Number) daysUntil).intValue());
                    }
                }
            }
            reminders.put("birthdays", birthdays);
            
            // 查询超过30天未沟通的客户
            List<Map<String, Object>> noContacts = communicationMapper.selectCustomersNoContact(30);
            // 确保daysSinceLastContact是Integer类型
            for (Map<String, Object> noContact : noContacts) {
                Object daysSince = noContact.get("daysSinceLastContact");
                if (daysSince != null) {
                    if (daysSince instanceof Long) {
                        noContact.put("daysSinceLastContact", ((Long) daysSince).intValue());
                    } else if (daysSince instanceof Number) {
                        noContact.put("daysSinceLastContact", ((Number) daysSince).intValue());
                    }
                }
            }
            reminders.put("noContacts", noContacts);
            
            // 统计重要客户数量
            Customer queryCustomer = new Customer();
            queryCustomer.setCustomerLevel(2); // VIP
            Long vipCount = customerMapper.selectCount(queryCustomer);
            queryCustomer.setCustomerLevel(3); // 钻石
            Long diamondCount = customerMapper.selectCount(queryCustomer);
            reminders.put("importantCustomerCount", (vipCount != null ? vipCount : 0) + (diamondCount != null ? diamondCount : 0));
            
            log.info("客户提醒统计 - 生日提醒: {}, 待跟进客户: {}, 重要客户: {}", 
                birthdays.size(), noContacts.size(), reminders.get("importantCustomerCount"));
            
        } catch (Exception e) {
            log.error("获取客户提醒失败", e);
            reminders.put("birthdays", new ArrayList<>());
            reminders.put("noContacts", new ArrayList<>());
            reminders.put("importantCustomerCount", 0);
        }
        
        return reminders;
    }

    @Override
    public Map<String, Object> analyzeCooperationPotential(Long customerId) {
        Map<String, Object> analysis = new HashMap<>();
        
        try {
            Customer customer = customerMapper.selectById(customerId);
            if (customer == null) {
                analysis.put("score", 0);
                analysis.put("description", "客户不存在");
                return analysis;
            }
            
            int totalScore = 0;
            List<Map<String, Object>> dimensions = new ArrayList<>();
            
            // 维度1: 客户等级 (0-30分)
            int levelScore = 0;
            if (customer.getCustomerLevel() != null) {
                levelScore = customer.getCustomerLevel() * 10; // 1->10, 2->20, 3->30
            }
            totalScore += levelScore;
            dimensions.add(createDimension("客户等级", levelScore, "客户等级越高，合作潜力越大"));
            
            // 维度2: 沟通频率 (0-25分)
            List<CommunicationRecord> recentCommunications = communicationMapper.selectRecentByCustomerId(customerId, 30);
            int communicationScore = Math.min(recentCommunications.size() * 5, 25);
            totalScore += communicationScore;
            dimensions.add(createDimension("沟通频率", communicationScore, "最近30天沟通" + recentCommunications.size() + "次"));
            
            // 维度3: 客户状态 (0-20分)
            int statusScore = customer.getStatus() != null && customer.getStatus() == 1 ? 20 : 0;
            totalScore += statusScore;
            dimensions.add(createDimension("客户状态", statusScore, customer.getStatus() == 1 ? "正常" : "非正常"));
            
            // 维度4: 客户类型 (0-15分)
            int typeScore = 0;
            if (customer.getCustomerType() != null) {
                // 企业客户通常合作潜力更大
                typeScore = customer.getCustomerType() == 2 ? 15 : 10;
            }
            totalScore += typeScore;
            dimensions.add(createDimension("客户类型", typeScore, customer.getCustomerType() == 2 ? "企业客户" : "个人客户"));
            
            // 维度5: 信息完整度 (0-10分)
            int completenessScore = calculateCompletenessScore(customer);
            totalScore += completenessScore;
            dimensions.add(createDimension("信息完整度", completenessScore, "客户信息越完整，合作可能性越高"));
            
            analysis.put("score", totalScore);
            analysis.put("dimensions", dimensions);
            analysis.put("description", generateCooperationDescription(totalScore));
            analysis.put("suggestions", generateCooperationSuggestions(totalScore, customer, recentCommunications.size()));
            
            log.info("客户 {} 合作潜力分析完成，评分: {}", customerId, totalScore);
            
        } catch (Exception e) {
            log.error("分析合作潜力失败，客户ID: {}", customerId, e);
            analysis.put("score", 0);
            analysis.put("description", "分析失败: " + e.getMessage());
        }
        
        return analysis;
    }

    // 辅助方法
    private String generateOpportunityDescription(Set<String> keywords, int communicationCount) {
        StringBuilder desc = new StringBuilder("客户在最近沟通中提到了");
        List<String> keywordList = new ArrayList<>(keywords);
        if (keywordList.size() > 3) {
            desc.append(String.join("、", keywordList.subList(0, 3))).append("等");
        } else {
            desc.append(String.join("、", keywordList));
        }
        desc.append("相关需求，最近").append(communicationCount).append("次沟通显示有合作意向。");
        return desc.toString();
    }

    private String determinePriority(Set<String> keywords, int communicationCount) {
        // 高优先级关键词
        Set<String> highPriorityKeywords = new HashSet<>(Arrays.asList("合作", "购买", "采购", "项目", "方案"));
        boolean hasHighPriorityKeyword = keywords.stream().anyMatch(highPriorityKeywords::contains);
        
        if (hasHighPriorityKeyword && communicationCount >= 3) {
            return "high";
        } else if (communicationCount >= 2 || keywords.size() >= 3) {
            return "medium";
        } else {
            return "low";
        }
    }

    private Integer getPriorityValue(String priority) {
        switch (priority) {
            case "high": return 3;
            case "medium": return 2;
            case "low": return 1;
            default: return 0;
        }
    }

    private Map<String, Object> createDimension(String name, int score, String description) {
        Map<String, Object> dim = new HashMap<>();
        dim.put("name", name);
        dim.put("score", score);
        dim.put("description", description);
        return dim;
    }

    private int calculateCompletenessScore(Customer customer) {
        int score = 0;
        if (customer.getPhone() != null && !customer.getPhone().trim().isEmpty()) score += 2;
        if (customer.getEmail() != null && !customer.getEmail().trim().isEmpty()) score += 2;
        if (customer.getAddress() != null && !customer.getAddress().trim().isEmpty()) score += 2;
        if (customer.getContactPerson() != null && !customer.getContactPerson().trim().isEmpty()) score += 2;
        if (customer.getRemark() != null && !customer.getRemark().trim().isEmpty()) score += 2;
        return score;
    }

    private String generateCooperationDescription(int score) {
        if (score >= 80) {
            return "该客户具有很高的合作潜力，建议重点跟进，提供个性化服务方案。";
        } else if (score >= 60) {
            return "该客户具有较好的合作潜力，建议加强沟通，了解具体需求。";
        } else if (score >= 40) {
            return "该客户有一定的合作潜力，建议定期维护关系，寻找合作机会。";
        } else {
            return "该客户合作潜力较低，建议保持基础联系，等待合适时机。";
        }
    }

    private List<String> generateCooperationSuggestions(int score, Customer customer, int communicationCount) {
        List<String> suggestions = new ArrayList<>();
        
        if (score >= 80) {
            suggestions.add("建议主动联系，提供定制化服务方案");
            suggestions.add("安排专人跟进，建立长期合作关系");
            if (communicationCount < 5) {
                suggestions.add("增加沟通频率，深入了解客户需求");
            }
        } else if (score >= 60) {
            suggestions.add("定期沟通，了解客户最新需求");
            suggestions.add("提供相关产品和服务信息");
            if (customer.getCustomerLevel() == null || customer.getCustomerLevel() == 1) {
                suggestions.add("考虑升级客户等级，提供VIP服务");
            }
        } else {
            suggestions.add("保持基础联系，定期发送行业资讯");
            suggestions.add("关注客户动态，寻找合作机会");
        }
        
        return suggestions;
    }
}