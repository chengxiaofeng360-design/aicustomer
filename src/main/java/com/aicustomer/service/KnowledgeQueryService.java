package com.aicustomer.service;

import com.aicustomer.entity.KbDocument;
import com.aicustomer.entity.KnowledgeDocument;
import com.aicustomer.mapper.KbDocumentMapper;
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
    private final KbDocumentMapper kbDocumentMapper;

    /**
     * 获取知识库文档总数
     * 
     * @return 统计摘要
     */
    public String getKnowledgeCount() {
        try {
            Long count1 = knowledgeDocumentMapper.selectCount(null, null, null);
            KbDocument condition = new KbDocument();
            Long count2 = kbDocumentMapper.selectCount(condition);
            long totalCount = (count1 != null ? count1 : 0) + (count2 != null ? count2 : 0);
            return "系统知识库中目前共有 " + totalCount + " 份上传的资料。";
        } catch (Exception e) {
            log.error("查询知识库文档总数失败: {}", e.getMessage(), e);
            return "查询执行时发生内部错误：" + e.getMessage();
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
            List<KnowledgeDocument> list1 = knowledgeDocumentMapper.selectList(null, null, null);
            KbDocument condition = new KbDocument();
            List<KbDocument> list2 = kbDocumentMapper.selectPage(condition, 0, limit);

            if ((list1 == null || list1.isEmpty()) && (list2 == null || list2.isEmpty())) {
                return "知识库目前没有任何上传的资料。";
            }

            StringBuilder sb = new StringBuilder();
            int count = 0;
            int displayLimit = limit > 0 ? limit : 20;

            if (list1 != null && !list1.isEmpty()) {
                for (KnowledgeDocument doc : list1) {
                    if (count >= displayLimit)
                        break;
                    sb.append(++count).append(". **").append(doc.getTitle()).append("** (类型: ")
                            .append(doc.getFileType() != null ? doc.getFileType() : "未知").append(")\n");
                }
            }

            if (list2 != null && !list2.isEmpty() && count < displayLimit) {
                for (KbDocument doc : list2) {
                    if (count >= displayLimit)
                        break;
                    sb.append(++count).append(". **").append(doc.getTitle()).append("** (类型: ")
                            .append(doc.getFileType() != null ? doc.getFileType() : "未知").append(")\n");
                }
            }

            long totalCount = (list1 != null ? list1.size() : 0) + (kbDocumentMapper.selectCount(condition));
            if (totalCount > count) {
                sb.append("\n... 以及更多共 ").append(totalCount).append(" 份文件。");
            }

            return "为您找到以下资料清单（共 " + totalCount + " 份）：\n" + sb.toString();
        } catch (Exception e) {
            log.error("获取资料清单失败", e);
            return "查询执行失败：" + e.getMessage();
        }
    }

    /**
     * 获取指定文档的详细内容
     * 
     * @param fileNameOrTitle 文件名或标题
     * @return 文档内容
     */
    public String getKnowledgeDetail(String fileNameOrTitle) {
        try {
            // 先从 kb_document 查
            com.aicustomer.entity.KbDocument kbCondition = new com.aicustomer.entity.KbDocument();
            kbCondition.setTitle(fileNameOrTitle);
            List<KbDocument> list2 = kbDocumentMapper.selectPage(kbCondition, 0, 1);
            if (list2 != null && !list2.isEmpty()) {
                return list2.get(0).getContent();
            }

            // 从 knowledge_document 查
            List<KnowledgeDocument> list1 = knowledgeDocumentMapper.selectList(null, null, null);
            String searchName = fileNameOrTitle.toLowerCase();
            for (KnowledgeDocument doc : list1) {
                String title = (doc.getTitle() != null ? doc.getTitle() : "").toLowerCase();
                String fileName = (doc.getFileName() != null ? doc.getFileName() : "").toLowerCase();

                if (searchName.equals(title) || searchName.equals(fileName)
                        || title.contains(searchName) || fileName.contains(searchName)
                        || searchName.contains(title)) {
                    return doc.getContent();
                }
            }

            return "未能找到名为 [" + fileNameOrTitle + "] 的文档内容。";
        } catch (Exception e) {
            log.error("获取文档详情失败: {}", fileNameOrTitle, e);
            return "获取文档内容失败：" + e.getMessage();
        }
    }
}
