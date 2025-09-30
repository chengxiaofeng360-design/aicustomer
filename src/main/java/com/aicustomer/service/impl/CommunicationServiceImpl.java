package com.aicustomer.service.impl;

import com.aicustomer.common.PageResult;
import com.aicustomer.entity.CommunicationRecord;
import com.aicustomer.service.CommunicationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 沟通记录服务实现类
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Service
public class CommunicationServiceImpl implements CommunicationService {

    @Override
    public PageResult<CommunicationRecord> getCommunicationList(int pageNum, int pageSize, Long customerId, String communicationType, String keyword) {
        // 模拟数据，实际项目中应该从数据库查询
        List<CommunicationRecord> records = new ArrayList<>();
        
        // 创建模拟数据
        CommunicationRecord record1 = new CommunicationRecord();
        record1.setId(1L);
        record1.setCustomerId(customerId != null ? customerId : 1L);
        record1.setCustomerName("张三");
        record1.setCommunicationType(2); // 2:电话
        record1.setContent("与客户进行了电话沟通，讨论了合作事宜");
        record1.setImportance(3); // 3:非常重要
        record1.setCommunicationTime(LocalDateTime.now().minusHours(2));
        record1.setOperator("李经理");
        record1.setCreateTime(LocalDateTime.now());
        records.add(record1);
        
        CommunicationRecord record2 = new CommunicationRecord();
        record2.setId(2L);
        record2.setCustomerId(customerId != null ? customerId : 2L);
        record2.setCustomerName("李四农业科技有限公司");
        record2.setCommunicationType(1); // 1:微信
        record2.setContent("通过微信发送了产品资料");
        record2.setImportance(1); // 1:一般
        record2.setCommunicationTime(LocalDateTime.now().minusHours(4));
        record2.setOperator("王助理");
        record2.setCreateTime(LocalDateTime.now());
        records.add(record2);
        
        CommunicationRecord record3 = new CommunicationRecord();
        record3.setId(3L);
        record3.setCustomerId(3L);
        record3.setCustomerName("中国农业科学院");
        record3.setCommunicationType(3); // 3:面谈
        record3.setContent("在客户办公室进行了详细的技术交流，讨论了新品种的技术特点和创新性");
        record3.setImportance(3); // 3:非常重要
        record3.setCommunicationTime(LocalDateTime.now().minusDays(1));
        record3.setOperator("张专家");
        record3.setCreateTime(LocalDateTime.now());
        records.add(record3);
        
        PageResult<CommunicationRecord> result = new PageResult<>();
        result.setList(records);
        result.setTotal((long) records.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        
        return result;
    }

    @Override
    public CommunicationRecord getCommunicationById(Long id) {
        // 模拟数据
        CommunicationRecord record = new CommunicationRecord();
        record.setId(id);
        record.setCustomerId(1L);
        record.setCommunicationType(2); // 2:电话
        record.setContent("与客户进行了电话沟通，讨论了合作事宜");
        record.setImportance(3); // 3:非常重要
        record.setCreateTime(LocalDateTime.now());
        return record;
    }

    @Override
    public void addCommunication(CommunicationRecord record) {
        // 实际项目中应该保存到数据库
        System.out.println("添加沟通记录: " + record.getContent());
    }

    @Override
    public void updateCommunication(CommunicationRecord record) {
        // 实际项目中应该更新数据库
        System.out.println("更新沟通记录: " + record.getId());
    }

    @Override
    public void deleteCommunication(Long id) {
        // 实际项目中应该从数据库删除
        System.out.println("删除沟通记录: " + id);
    }

    @Override
    public List<CommunicationRecord> getCustomerCommunications(Long customerId) {
        // 模拟数据
        List<CommunicationRecord> records = new ArrayList<>();
        
        CommunicationRecord record1 = new CommunicationRecord();
        record1.setId(1L);
        record1.setCustomerId(customerId);
        record1.setCommunicationType(2); // 2:电话
        record1.setContent("与客户进行了电话沟通");
        record1.setImportance(3); // 3:非常重要
        record1.setCreateTime(LocalDateTime.now());
        records.add(record1);
        
        return records;
    }

    @Override
    public void markAsImportant(Long id, boolean important) {
        // 实际项目中应该更新数据库
        System.out.println("标记沟通记录重要程度: " + id + " -> " + important);
    }
}
