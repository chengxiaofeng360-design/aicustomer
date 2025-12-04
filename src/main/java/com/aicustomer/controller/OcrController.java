package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.Customer;
import com.aicustomer.service.OcrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
@Slf4j
public class OcrController {

    private final OcrService ocrService;

    @PostMapping("/business-card")
    public Result<Customer> recognizeBusinessCard(@RequestParam("file") MultipartFile file) {
        try {
            log.info("Received business card OCR request, file size: {}", file.getSize());
            Customer customer = ocrService.parseBusinessCard(file);
            return Result.success(customer);
        } catch (Exception e) {
            log.error("Business card recognition failed", e);
            return Result.error("识别失败: " + e.getMessage());
        }
    }
}
