package com.aicustomer.mapper;

import com.aicustomer.entity.KnowledgeDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 知识库文档Mapper接口
 */
@Mapper
public interface KnowledgeDocumentMapper {

        int insert(KnowledgeDocument document);

        int update(KnowledgeDocument document);

        int deleteById(Long id);

        KnowledgeDocument selectById(Long id);

        List<KnowledgeDocument> selectList(@Param("keyword") String keyword,
                        @Param("category") String category,
                        @Param("documentType") String documentType);

        /**
         * 统计文档数量
         */
        Long selectCount(@Param("keyword") String keyword,
                        @Param("category") String category,
                        @Param("documentType") String documentType);

        /**
         * 全文搜索
         * 
         * @param query 搜索关键词
         * @param limit 返回数量限制
         * @return 匹配的文档列表
         */
        List<KnowledgeDocument> searchFullText(@Param("query") String query, @Param("limit") Integer limit);

        void incrementViewCount(Long id);

        void incrementDownloadCount(Long id);

        /**
         * 获取各分类的文档数量统计
         */
        List<Map<String, Object>> selectCategoryCounts();
}
