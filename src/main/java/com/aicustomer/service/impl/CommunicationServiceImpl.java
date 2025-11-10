package com.aicustomer.service.impl;

import com.aicustomer.common.PageResult;
import com.aicustomer.entity.CommunicationRecord;
import com.aicustomer.mapper.CommunicationMapper;
import com.aicustomer.service.CommunicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 沟通记录服务实现类
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class CommunicationServiceImpl implements CommunicationService {

    private final CommunicationMapper communicationMapper;

    @Override
    public PageResult<CommunicationRecord> getCommunicationList(int pageNum, int pageSize, Long customerId, String communicationType, String keyword) {
        // 构建查询条件
        CommunicationRecord queryRecord = new CommunicationRecord();
        if (customerId != null) {
            queryRecord.setCustomerId(customerId);
        }
        if (communicationType != null && !communicationType.trim().isEmpty()) {
            try {
                queryRecord.setCommunicationType(Integer.parseInt(communicationType));
            } catch (NumberFormatException e) {
                // 忽略无效的类型值
            }
        }
        
        // 计算分页参数
        int offset = (pageNum - 1) * pageSize;
        
        // 查询数据
        List<CommunicationRecord> list = communicationMapper.selectPage(queryRecord, offset, pageSize);
        Long total = communicationMapper.selectCount(queryRecord);
        
        // 如果有关键词，进行过滤（这里简化处理，实际可以在SQL中处理）
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            list = list.stream()
                .filter(record -> 
                    (record.getContent() != null && record.getContent().toLowerCase().contains(lowerKeyword)) ||
                    (record.getSummary() != null && record.getSummary().toLowerCase().contains(lowerKeyword)) ||
                    (record.getCustomerName() != null && record.getCustomerName().toLowerCase().contains(lowerKeyword))
                )
                .collect(java.util.stream.Collectors.toList());
            // 注意：如果使用关键词过滤，总数可能不准确，这里简化处理
        }
        
        return new PageResult<>(pageNum, pageSize, total, list);
    }

    @Override
    public CommunicationRecord getCommunicationById(Long id) {
        return communicationMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCommunication(CommunicationRecord record) {
        // 验证必填字段
        if (record.getCustomerId() == null) {
            throw new IllegalArgumentException("客户ID不能为空");
        }
        if (record.getCustomerName() == null || record.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("客户名称不能为空");
        }
        if (record.getCommunicationType() == null) {
            throw new IllegalArgumentException("沟通类型不能为空");
        }
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        if (record.getCreateTime() == null) {
            record.setCreateTime(now);
        }
        if (record.getUpdateTime() == null) {
            record.setUpdateTime(now);
        }
        if (record.getDeleted() == null) {
            record.setDeleted(0);
        }
        if (record.getVersion() == null) {
            record.setVersion(1);
        }
        // 如果没有设置沟通时间，使用当前时间
        if (record.getCommunicationTime() == null) {
            record.setCommunicationTime(now);
        }
        // 如果没有设置重要程度，默认为1（一般）
        if (record.getImportance() == null) {
            record.setImportance(1);
        }
        // 如果前端发送了operator但没有communicatorName，将operator映射到communicatorName
        if (record.getCommunicatorName() == null || record.getCommunicatorName().trim().isEmpty()) {
            if (record.getOperator() != null && !record.getOperator().trim().isEmpty()) {
                record.setCommunicatorName(record.getOperator());
            }
        }
        
        communicationMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCommunication(CommunicationRecord record) {
        // 设置更新时间
        record.setUpdateTime(LocalDateTime.now());
        // 如果前端发送了operator但没有communicatorName，将operator映射到communicatorName
        if (record.getCommunicatorName() == null || record.getCommunicatorName().trim().isEmpty()) {
            if (record.getOperator() != null && !record.getOperator().trim().isEmpty()) {
                record.setCommunicatorName(record.getOperator());
            }
        }
        
        communicationMapper.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCommunication(Long id) {
        communicationMapper.deleteById(id);
    }

    @Override
    public List<CommunicationRecord> getCustomerCommunications(Long customerId) {
        return communicationMapper.selectByCustomerId(customerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsImportant(Long id, boolean important) {
        communicationMapper.markAsImportant(id, important);
    }
}
