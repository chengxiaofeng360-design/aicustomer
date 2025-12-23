package com.aicustomer.service;

import com.aicustomer.entity.KnowledgeDocument;
import com.aicustomer.mapper.KnowledgeDocumentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 知识库查询服务，专门为 AI 聊天工具调用（Function Calling）提供支持
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeQueryService {

    private final KnowledgeDocumentMapper knowledgeDocumentMapper;

    /**
     * 获取知识库文档总数
     * 
     * @return 统计摘要
     */
    public String getKnowledgeCount() {
        try {
            Long count = knowledgeDocumentMapper.selectCount(null, null, null);
            return "系统知识库中目前共有 " + count + " 份上传的资料。";
        } catch (Exception e) {
            log.error("查询知识库文档总数失败", e);
            return "查询失败：" + e.getMessage();
        }
    }

    /**
     * 获取知识库文档列表清单
     * 
     * @param limit 返回数量限制
     * @return 资料清单字符串
     */
    public String getKnowledgeList(int limit) {
        try {
            List<KnowledgeDocument> documents = knowledgeDocumentMapper.selectList(null, null, null);
            if (documents == null || documents.isEmpty()) {
                return "知识库目前没有任何上传的资料。";
            }

            int displayLimit = limit > 0 ? Math.min(limit, documents.size()) : Math.min(20, documents.size());
            StringBuilder sb = new StringBuilder();
            sb.append("为您找到以下 ").append(documents.size()).append(" 份上传资料（展示前 ").append(displayLimit).append(" 份）：\n");

            for (int i = 0; i < displayLimit; i++) {
                KnowledgeDocument doc = documents.get(i);
                sb.append(i + 1).append(". **").append(doc.getTitle()).append("**")
                        .append(" (类型: ").append(doc.getFileType() != null ? doc.getFileType() : "未知")
                        .append(", 分类: ").append(doc.getCategory() != null ? doc.getCategory() : "未分类")
                        .append(")\n");
            }

            if (documents.size() > displayLimit) {
                sb.append("... 以及更多共 ").append(documents.size()).append(" 份文件。");
            }

            return sb.toString();
        } catch (Exception e) {
            log.error("获取资料清单失败", e);
            return "查询执行失败：" + e.getMessage();
        }
    }
}
