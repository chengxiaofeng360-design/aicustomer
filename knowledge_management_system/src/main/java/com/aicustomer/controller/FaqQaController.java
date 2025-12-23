package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.FaqQa;
import com.aicustomer.service.FaqQaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * FAQ问答控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/faq")
@RequiredArgsConstructor
public class FaqQaController {

    private final FaqQaService faqQaService;

    /**
     * 创建FAQ
     */
    @PostMapping
    public Result<FaqQa> createFaq(@RequestBody FaqQa faq) {
        return Result.success(faqQaService.createFaq(faq));
    }

    /**
     * 更新FAQ
     */
    @PutMapping
    public Result<FaqQa> updateFaq(@RequestBody FaqQa faq) {
        return Result.success(faqQaService.updateFaq(faq));
    }

    /**
     * 删除FAQ
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteFaq(@PathVariable Long id) {
        faqQaService.deleteFaq(id);
        return Result.success();
    }

    /**
     * 获取FAQ详情
     */
    @GetMapping("/{id}")
    public Result<FaqQa> getFaq(@PathVariable Long id) {
        return Result.success(faqQaService.getFaq(id));
    }

    /**
     * 获取FAQ列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getFaqList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(faqQaService.getFaqList(keyword, category, pageNum, pageSize));
    }

    /**
     * 批量导入FAQ
     */
    @PostMapping("/batch")
    public Result<String> batchImportFaq(@RequestBody java.util.List<FaqQa> faqList) {
        int successCount = 0;
        int failCount = 0;

        for (FaqQa faq : faqList) {
            try {
                faqQaService.createFaq(faq);
                successCount++;
            } catch (Exception e) {
                log.error("导入FAQ失败: {}", faq.getQuestion(), e);
                failCount++;
            }
        }

        String message = String.format("批量导入完成，成功: %d条，失败: %d条", successCount, failCount);
        log.info(message);
        return Result.success(message);
    }

    /**
     * 获取所有分类
     */
    @GetMapping("/categories")
    public Result<java.util.List<String>> getCategories() {
        return Result.success(faqQaService.getCategories());
    }
}
