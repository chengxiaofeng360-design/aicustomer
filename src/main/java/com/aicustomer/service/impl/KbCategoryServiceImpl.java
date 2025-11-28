package com.aicustomer.service.impl;

import com.aicustomer.entity.KbCategory;
import com.aicustomer.mapper.KbCategoryMapper;
import com.aicustomer.service.KbCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 知识库分类服务实现类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KbCategoryServiceImpl implements KbCategoryService {
    
    private final KbCategoryMapper categoryMapper;
    
    @Override
    public List<KbCategory> getAllCategories() {
        try {
            return categoryMapper.selectAll();
        } catch (Exception e) {
            log.error("获取所有分类失败", e);
            throw new RuntimeException("获取分类失败", e);
        }
    }
    
    @Override
    public List<KbCategory> getCategoryTree() {
        try {
            List<KbCategory> allCategories = categoryMapper.selectAll();
            return buildCategoryTree(allCategories, null);
        } catch (Exception e) {
            log.error("获取分类树失败", e);
            throw new RuntimeException("获取分类树失败", e);
        }
    }
    
    @Override
    public KbCategory getCategoryById(Long id) {
        try {
            return categoryMapper.selectById(id);
        } catch (Exception e) {
            log.error("根据ID获取分类失败: {}", id, e);
            throw new RuntimeException("获取分类失败", e);
        }
    }
    
    @Override
    public KbCategory createCategory(KbCategory category) {
        try {
            // 设置默认值
            if (category.getLevel() == null) {
                category.setLevel(1);
            }
            if (category.getSortOrder() == null) {
                category.setSortOrder(0);
            }
            if (category.getIsActive() == null) {
                category.setIsActive(true);
            }
            if (category.getDocumentCount() == null) {
                category.setDocumentCount(0);
            }
            
            int result = categoryMapper.insert(category);
            if (result > 0) {
                log.info("创建分类成功: {}", category.getName());
                return category;
            } else {
                throw new RuntimeException("创建分类失败");
            }
        } catch (Exception e) {
            log.error("创建分类失败", e);
            throw new RuntimeException("创建分类失败", e);
        }
    }
    
    @Override
    public KbCategory updateCategory(KbCategory category) {
        try {
            int result = categoryMapper.update(category);
            if (result > 0) {
                log.info("更新分类成功: {}", category.getName());
                return category;
            } else {
                throw new RuntimeException("更新分类失败");
            }
        } catch (Exception e) {
            log.error("更新分类失败", e);
            throw new RuntimeException("更新分类失败", e);
        }
    }
    
    @Override
    public boolean deleteCategory(Long id) {
        try {
            // 检查是否有子分类
            List<KbCategory> children = categoryMapper.selectByParentId(id);
            if (!children.isEmpty()) {
                throw new RuntimeException("存在子分类，无法删除");
            }
            
            int result = categoryMapper.deleteById(id);
            if (result > 0) {
                log.info("删除分类成功: {}", id);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("删除分类失败: {}", id, e);
            throw new RuntimeException("删除分类失败", e);
        }
    }
    
    @Override
    public List<KbCategory> getActiveCategories() {
        try {
            return categoryMapper.selectActive();
        } catch (Exception e) {
            log.error("获取启用分类失败", e);
            throw new RuntimeException("获取分类失败", e);
        }
    }
    
    /**
     * 构建分类树
     */
    private List<KbCategory> buildCategoryTree(List<KbCategory> allCategories, Long parentId) {
        List<KbCategory> result = new ArrayList<>();
        
        for (KbCategory category : allCategories) {
            if ((parentId == null && category.getParentId() == null) ||
                (parentId != null && parentId.equals(category.getParentId()))) {
                // 递归构建子分类（可选：如果需要可以设置children属性）
                // List<KbCategory> children = buildCategoryTree(allCategories, category.getId());
                result.add(category);
            }
        }
        
        return result.stream()
                .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .collect(Collectors.toList());
    }
}
