package com.aicustomer.mapper;

import com.aicustomer.entity.KbCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 知识库分类Mapper接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface KbCategoryMapper {
    
    /**
     * 查询所有分类
     */
    List<KbCategory> selectAll();
    
    /**
     * 根据父分类ID查询子分类
     */
    List<KbCategory> selectByParentId(@Param("parentId") Long parentId);
    
    /**
     * 根据ID查询分类
     */
    KbCategory selectById(@Param("id") Long id);
    
    /**
     * 插入分类
     */
    int insert(KbCategory category);
    
    /**
     * 更新分类
     */
    int update(KbCategory category);
    
    /**
     * 删除分类
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 更新文档数量
     */
    int updateDocumentCount(@Param("id") Long id, @Param("count") Integer count);
    
    /**
     * 查询启用的分类
     */
    List<KbCategory> selectActive();
}
