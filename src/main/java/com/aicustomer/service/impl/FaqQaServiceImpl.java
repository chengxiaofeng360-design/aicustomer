package com.aicustomer.service.impl;

import com.aicustomer.entity.FaqQa;
import com.aicustomer.mapper.FaqQaMapper;
import com.aicustomer.service.FaqQaService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import com.aicustomer.dify.client.DifyClient;
import com.aicustomer.config.DifyConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FAQ问答服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FaqQaServiceImpl implements FaqQaService {

    private final FaqQaMapper faqQaMapper;
    private final DifyClient difyClient;
    private final DifyConfig difyConfig;

    @Override
    public FaqQa createFaq(FaqQa faq) {
        faq.setCreateTime(LocalDateTime.now());
        faq.setUpdateTime(LocalDateTime.now());
        faq.setDeleted(0);
        if (faq.getStatus() == null)
            faq.setStatus(1);
        if (faq.getHitCount() == null)
            faq.setHitCount(0);
        if (faq.getPriority() == null)
            faq.setPriority(0);

        // 1. 同步到Dify (创建文本段)
        try {
            String content = "问题：" + faq.getQuestion() + "\n回答：" + faq.getAnswer();
            Map<String, Object> response = difyClient.createDocumentByText(
                    difyConfig.getDatasetId(),
                    "FAQ-" + System.currentTimeMillis(), // 临时名称，Dify会自动处理
                    content,
                    "system");

            if (response != null && response.containsKey("document")) {
                Map<String, Object> docData = (Map<String, Object>) response.get("document");
                String difyDocId = (String) docData.get("id");
                faq.setDifyDocumentId(difyDocId);
                log.info("Dify FAQ创建成功, Document ID: {}", difyDocId);
            }
        } catch (Exception e) {
            log.error("Dify FAQ创建失败", e);
        }

        // 2. 保存本地
        faqQaMapper.insert(faq);
        return faq;
    }

    @Override
    public FaqQa updateFaq(FaqQa faq) {
        faq.setUpdateTime(LocalDateTime.now());

        // 更新逻辑：先删旧Dify文档，再建新的（如果存在ID）
        // 注意：这里需要先查询旧数据获取 documentId
        FaqQa oldFaq = faqQaMapper.selectById(faq.getId());
        if (oldFaq != null) {
            String oldDid = oldFaq.getDifyDocumentId();
            // 如果本次update没有传difyID，则沿用旧的以便删除
            if (faq.getDifyDocumentId() == null) {
                faq.setDifyDocumentId(oldDid);
            }

            // 如果有旧ID，先删除
            if (oldDid != null) {
                try {
                    difyClient.deleteDocument(difyConfig.getDatasetId(), oldDid);
                } catch (Exception e) {
                    log.warn("旧Dify FAQ文档删除失败: {}", oldDid);
                }
            }

            // 创建新的
            try {
                String content = "问题：" + faq.getQuestion() + "\n回答：" + faq.getAnswer();
                Map<String, Object> response = difyClient.createDocumentByText(
                        difyConfig.getDatasetId(),
                        "FAQ-" + System.currentTimeMillis(),
                        content,
                        "system");

                if (response != null && response.containsKey("document")) {
                    Map<String, Object> docData = (Map<String, Object>) response.get("document");
                    String newDifyDocId = (String) docData.get("id");
                    faq.setDifyDocumentId(newDifyDocId); // 更新为新ID
                }
            } catch (Exception e) {
                log.error("Dify FAQ更新(重新创建)失败", e);
            }
        }

        faqQaMapper.update(faq);
        return faqQaMapper.selectById(faq.getId());
    }

    @Override
    public void deleteFaq(Long id) {
        FaqQa faq = faqQaMapper.selectById(id);
        if (faq != null && faq.getDifyDocumentId() != null) {
            try {
                difyClient.deleteDocument(difyConfig.getDatasetId(), faq.getDifyDocumentId());
            } catch (Exception e) {
                log.error("Dify FAQ删除失败", e);
            }
        }
        faqQaMapper.deleteById(id);
    }

    @Override
    public FaqQa getFaq(Long id) {
        return faqQaMapper.selectById(id);
    }

    @Override
    public Map<String, Object> getFaqList(String keyword, String category, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<FaqQa> list = faqQaMapper.selectList(keyword, category);
        PageInfo<FaqQa> pageInfo = new PageInfo<>(list);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", pageInfo.getTotal());
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("pages", pageInfo.getPages());

        return result;
    }

    @Override
    public List<FaqQa> searchFaq(String query, int limit) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return faqQaMapper.searchFullText(query, limit);
    }

    @Override
    public void incrementHitCount(Long id) {
        faqQaMapper.incrementHitCount(id);
    }

    @Override
    public List<String> getCategories() {
        return faqQaMapper.getDistinctCategories();
    }
}
