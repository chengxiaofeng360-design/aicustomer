package com.aicustomer.mapper;

import com.aicustomer.entity.KbDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 知识库文档Mapper接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface KbDocumentMapper {

    /**
     * 分页查询文档
     */
    List<KbDocument> selectPage(@Param("document") KbDocument document, @Param("offset") int offset,
            @Param("limit") int limit);

    /**
     * 统计文档数量
     */
    Long selectCount(KbDocument document);

    /**
     * 根据ID查询文档
     */
    KbDocument selectById(@Param("id") Long id);

    /**
     * 插入文档
     */
    int insert(KbDocument document);

    /**
     * 更新文档
     */
    int update(KbDocument document);

    /**
     * 删除文档
     */
    int deleteById(@Param("id") Long id);

    /**
     * 全文搜索文档
     */
    List<KbDocument> fullTextSearch(@Param("query") String query, @Param("limit") int limit);

    /**
     * 根据分类ID查询文档
     */
    List<KbDocument> selectByCategoryId(@Param("categoryId") Long categoryId, @Param("limit") int limit);

    /**
     * 根据标签搜索文档
     */
    List<KbDocument> selectByTags(@Param("tags") String tags, @Param("limit") int limit);

    /**
     * 更新查看次数
     */
    int updateViewCount(@Param("id") Long id);

    /**
     * 更新下载次数
     */
    int updateDownloadCount(@Param("id") Long id);

    /**
     * 查询热门文档
     */
    List<KbDocument> selectHotDocuments(@Param("limit") int limit);
}
