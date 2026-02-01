package com.aicustomer.adapter;

import com.aicustomer.model.FunctionCallRequest;
import com.aicustomer.model.FunctionCallResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * AI模型适配器管理器
 * 
 * 负责管理多个AI模型适配器，按优先级顺序调用，自动处理回退
 * 
 * @author AI Customer Management System
 * @version 2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
/**
 * 智能体适配器调度管理
 * 用于支持不同主流大模型的统一调度
 * 
 * @deprecated 系统已全面切换至 Dify 引擎，此适配器层仅作兼容保留
 */
@Deprecated
public class AiModelAdapterManager {

        private final List<AiModelAdapter> adapters;

        /**
         * 使用Function Calling调用AI，自动回退
         * 
         * @param request Function Calling请求
         * @return AI响应
         */
        public FunctionCallResponse chatWithFallback(FunctionCallRequest request) {
                log.info("【适配器管理器】开始处理请求");

                // 1. 获取可用的适配器并按优先级排序
                List<AiModelAdapter> availableAdapters = adapters.stream()
                                .filter(AiModelAdapter::isAvailable)
                                .sorted(Comparator.comparingInt(AiModelAdapter::getPriority))
                                .toList();

                if (availableAdapters.isEmpty()) {
                        log.error("【适配器管理器】没有可用的适配器");
                        return FunctionCallResponse.builder()
                                        .error("所有AI模型都不可用")
                                        .build();
                }

                log.info("【适配器管理器】找到{}个可用适配器: {}",
                                availableAdapters.size(),
                                availableAdapters.stream().map(AiModelAdapter::getName).toList());

                // 2. 按优先级依次尝试
                for (AiModelAdapter adapter : availableAdapters) {
                        try {
                                log.info("【适配器管理器】尝试适配器: {} (优先级: {})",
                                                adapter.getName(),
                                                adapter.getPriority());

                                FunctionCallResponse response = adapter.chat(request);

                                // 检查响应是否成功
                                if (response.isSuccess()) {
                                        log.info("【适配器管理器】适配器 {} 成功返回", adapter.getName());
                                        return response;
                                }

                                // 失败，记录并尝试下一个
                                log.warn("【适配器管理器】适配器 {} 失败: {}",
                                                adapter.getName(),
                                                response.getError());

                        } catch (Exception e) {
                                log.error("【适配器管理器】适配器 {} 异常: {}",
                                                adapter.getName(),
                                                e.getMessage(),
                                                e);
                        }
                }

                // 3. 所有适配器都失败
                log.error("【适配器管理器】所有适配器都失败了");
                return FunctionCallResponse.builder()
                                .error("所有AI模型都调用失败")
                                .build();
        }

        /**
         * 获取所有已注册的适配器信息
         */
        public List<AdapterInfo> getAdapterInfos() {
                return adapters.stream()
                                .map(adapter -> new AdapterInfo(
                                                adapter.getName(),
                                                adapter.getPriority(),
                                                adapter.isAvailable()))
                                .sorted(Comparator.comparingInt(AdapterInfo::priority))
                                .toList();
        }

        /**
         * 适配器信息记录
         */
        public record AdapterInfo(String name, int priority, boolean available) {
        }
}
