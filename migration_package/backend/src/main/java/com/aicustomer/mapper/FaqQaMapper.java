package com.aicustomer.mapper;

import com.aicustomer.entity.FaqQa;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * FAQ问答Mapper接口
 */
@Mapper
public interface FaqQaMapper {

    int insert(FaqQa faq);

    int update(FaqQa faq);

    int deleteById(Long id);

    FaqQa selectById(Long id);

    List<FaqQa> selectList(@Param("keyword") String keyword,
            @Param("category") String category);

    /**
     * 全文搜索FAQ
     * 
     * @param query 搜索关键词
     * @param limit 返回数量限制
     * @return 匹配的FAQ列表
     */
    List<FaqQa> searchFullText(@Param("query") String query, @Param("limit") Integer limit);

    void incrementHitCount(Long id);

    /**
     * 获取所有不同的分类
     */
    List<String> getDistinctCategories();
}
