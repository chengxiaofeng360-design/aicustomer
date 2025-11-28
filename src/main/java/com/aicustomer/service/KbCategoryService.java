package com.aicustomer.service;

import com.aicustomer.entity.KbCategory;

import java.util.List;

/**
 * 知识库分类服务接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface KbCategoryService {
    
    /**
     * 获取所有分类
     */
    List<KbCategory> getAllCategories();
    
    /**
     * 获取分类树
     */
    List<KbCategory> getCategoryTree();
    
    /**
     * 根据ID获取分类
     */
    KbCategory getCategoryById(Long id);
    
    /**
     * 创建分类
     */
    KbCategory createCategory(KbCategory category);
    
    /**
     * 更新分类
     */
    KbCategory updateCategory(KbCategory category);
    
    /**
     * 删除分类
     */
    boolean deleteCategory(Long id);
    
    /**
     * 获取启用的分类
     */
    List<KbCategory> getActiveCategories();
}
