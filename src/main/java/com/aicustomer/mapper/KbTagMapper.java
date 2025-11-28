package com.aicustomer.mapper;

import com.aicustomer.entity.KbTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 知识库标签Mapper接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface KbTagMapper {
    
    /**
     * 查询所有标签
     */
    List<KbTag> selectAll();
    
    /**
     * 根据分类查询标签
     */
    List<KbTag> selectByCategory(@Param("category") String category);
    
    /**
     * 根据ID查询标签
     */
    KbTag selectById(@Param("id") Long id);
    
    /**
     * 根据名称查询标签
     */
    KbTag selectByName(@Param("name") String name);
    
    /**
     * 插入标签
     */
    int insert(KbTag tag);
    
    /**
     * 更新标签
     */
    int update(KbTag tag);
    
    /**
     * 删除标签
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 更新使用次数
     */
    int updateUsageCount(@Param("id") Long id, @Param("count") Integer count);
    
    /**
     * 查询热门标签
     */
    List<KbTag> selectHotTags(@Param("limit") int limit);
}
