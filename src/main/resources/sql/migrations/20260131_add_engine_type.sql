-- Dify集成：会话引擎绑定
-- 新增字段记录每个会话使用的AI引擎类型

-- 1. 添加engine_type字段
ALTER TABLE ai_chat 
ADD COLUMN engine_type VARCHAR(20) DEFAULT 'legacy' 
COMMENT 'AI引擎类型: legacy(现有方案)/dify(Dify平台)，用于会话上下文一致性';

-- 2. 为历史数据设置默认值
UPDATE ai_chat 
SET engine_type = 'legacy' 
WHERE engine_type IS NULL;

-- 3. 添加索引提升查询效率
CREATE INDEX idx_session_engine ON ai_chat(session_id, engine_type);

-- 4. 查询验证
SELECT session_id, engine_type, COUNT(*) as message_count
FROM ai_chat
GROUP BY session_id, engine_type
LIMIT 10;
