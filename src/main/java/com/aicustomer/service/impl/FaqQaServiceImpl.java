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

    @Override
    public FaqQa createFaq(FaqQa faq) {
        faq.setCreateTime(LocalDateTime.now());
        faq.setUpdateTime(LocalDateTime.now());
        faq.setDeleted(0);
        if (faq.getStatus() == null) {
            faq.setStatus(1);
        }
        if (faq.getHitCount() == null) {
            faq.setHitCount(0);
        }
        if (faq.getPriority() == null) {
            faq.setPriority(0);
        }

        faqQaMapper.insert(faq);
        return faq;
    }

    @Override
    public FaqQa updateFaq(FaqQa faq) {
        faq.setUpdateTime(LocalDateTime.now());
        faqQaMapper.update(faq);
        return faqQaMapper.selectById(faq.getId());
    }

    @Override
    public void deleteFaq(Long id) {
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
