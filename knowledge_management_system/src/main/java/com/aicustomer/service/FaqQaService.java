package com.aicustomer.service;

import com.aicustomer.entity.FaqQa;
import java.util.List;
import java.util.Map;

/**
 * FAQ问答服务接口
 */
public interface FaqQaService {

    /**
     * 创建FAQ
     */
    FaqQa createFaq(FaqQa faq);

    /**
     * 更新FAQ
     */
    FaqQa updateFaq(FaqQa faq);

    /**
     * 删除FAQ
     */
    void deleteFaq(Long id);

    /**
     * 获取FAQ详情
     */
    FaqQa getFaq(Long id);

    /**
     * 获取FAQ列表
     */
    Map<String, Object> getFaqList(String keyword, String category, int pageNum, int pageSize);

    /**
     * 搜索FAQ
     * 
     * @param query 搜索关键词
     * @param limit 返回数量
     * @return 匹配的FAQ列表
     */
    List<FaqQa> searchFaq(String query, int limit);

    /**
     * 增加命中次数
     */
    void incrementHitCount(Long id);

    /**
     * 获取所有分类
     */
    List<String> getCategories();
}
