package com.aicustomer.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * FAQ问答实体类
 * 对应数据库表: faq_qa
 */
@Data
public class FaqQa {
    /** 主键ID */
    private Long id;

    /** 问题 */
    private String question;

    /** 答案 */
    private String answer;

    /** 关键词(逗号分隔) */
    private String keywords;

    /** 分类 */
    private String category;

    /** 优先级(数字越大优先级越高) */
    private Integer priority;

    /** 命中次数 */
    private Integer hitCount;

    /** 状态(1:启用 0:禁用) */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 创建人 */
    private String createBy;

    /** 更新人 */
    private String updateBy;

    /** 删除标志(0:未删除,1:已删除) */
    private Integer deleted;
}
