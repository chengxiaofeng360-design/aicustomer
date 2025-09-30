package com.aicustomer.service;

import com.aicustomer.common.PageResult;
import com.aicustomer.entity.CommunicationRecord;

import java.util.List;

/**
 * 沟通记录服务接口
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface CommunicationService {

    /**
     * 获取沟通记录列表（分页）
     */
    PageResult<CommunicationRecord> getCommunicationList(int pageNum, int pageSize, Long customerId, String communicationType, String keyword);

    /**
     * 根据ID获取沟通记录
     */
    CommunicationRecord getCommunicationById(Long id);

    /**
     * 添加沟通记录
     */
    void addCommunication(CommunicationRecord record);

    /**
     * 更新沟通记录
     */
    void updateCommunication(CommunicationRecord record);

    /**
     * 删除沟通记录
     */
    void deleteCommunication(Long id);

    /**
     * 获取客户的沟通记录
     */
    List<CommunicationRecord> getCustomerCommunications(Long customerId);

    /**
     * 标记重要信息
     */
    void markAsImportant(Long id, boolean important);
}



