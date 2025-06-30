-- ================================================================
-- Support Bot 数据库初始化脚本
-- 版本: 1.0
-- 说明: 创建智能客服系统所需的数据库表和索引
-- ================================================================

-- 检查并创建必要的扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS vector;

-- ================================================================
-- 聊天消息表 - 存储用户与AI的对话记录
-- ================================================================
DROP TABLE IF EXISTS chat_message CASCADE;

CREATE TABLE chat_message (
    id              BIGSERIAL                            PRIMARY KEY,
    conversation_id VARCHAR(64)                          NOT NULL,
    message_type    VARCHAR(20)                          NOT NULL,
    content         TEXT                                 NOT NULL,
    metadata        JSONB                                NOT NULL DEFAULT '{}',
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP  NOT NULL,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP  NOT NULL,
    is_delete       BOOLEAN   DEFAULT FALSE              NOT NULL
);

-- 添加表注释
COMMENT ON TABLE chat_message IS '聊天消息表 - 存储用户与AI助手的对话历史';

-- 添加字段注释
COMMENT ON COLUMN chat_message.id IS '主键ID - 自增长整型';
COMMENT ON COLUMN chat_message.conversation_id IS '会话ID - 标识同一次对话的唯一标识符';
COMMENT ON COLUMN chat_message.message_type IS '消息类型 - USER(用户消息)/ASSISTANT(AI回复)/SYSTEM(系统消息)';
COMMENT ON COLUMN chat_message.content IS '消息内容 - 实际的对话文本内容';
COMMENT ON COLUMN chat_message.metadata IS '元数据 - 存储消息的额外信息(JSON格式)';
COMMENT ON COLUMN chat_message.create_time IS '创建时间 - 消息创建的时间戳';
COMMENT ON COLUMN chat_message.update_time IS '更新时间 - 消息最后更新的时间戳';
COMMENT ON COLUMN chat_message.is_delete IS '删除标志 - false:未删除, true:已删除(逻辑删除)';

-- 创建索引
CREATE INDEX idx_chat_message_conversation_id ON chat_message (conversation_id);
CREATE INDEX idx_chat_message_create_time ON chat_message (create_time DESC);
CREATE INDEX idx_chat_message_type ON chat_message (message_type);
CREATE INDEX idx_chat_message_not_deleted ON chat_message (conversation_id, create_time) WHERE is_delete = FALSE;

-- 添加约束
ALTER TABLE chat_message ADD CONSTRAINT chk_message_type 
    CHECK (message_type IN ('USER', 'ASSISTANT', 'SYSTEM'));

-- ================================================================
-- 向量存储表 - 存储文档向量和元数据用于RAG检索
-- ================================================================
DROP TABLE IF EXISTS vector_store CASCADE;

CREATE TABLE vector_store (
    id        UUID DEFAULT uuid_generate_v4()          PRIMARY KEY,
    content   TEXT                                     NOT NULL,
    metadata  JSONB                                    NOT NULL DEFAULT '{}',
    embedding VECTOR(1536)                             NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP    NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP    NOT NULL
);

-- 添加表注释
COMMENT ON TABLE vector_store IS '向量存储表 - 存储文档内容的向量表示用于语义搜索';

-- 添加字段注释
COMMENT ON COLUMN vector_store.id IS '主键ID - UUID格式的唯一标识符';
COMMENT ON COLUMN vector_store.content IS '文档内容 - 原始的文本内容';
COMMENT ON COLUMN vector_store.metadata IS '元数据 - 文档的附加信息(来源、标题、标签等)';
COMMENT ON COLUMN vector_store.embedding IS '向量嵌入 - 1536维的向量表示(适配OpenAI embedding模型)';
COMMENT ON COLUMN vector_store.create_time IS '创建时间';
COMMENT ON COLUMN vector_store.update_time IS '更新时间';

-- 创建向量索引 (HNSW索引用于高效的向量相似度搜索)
CREATE INDEX idx_vector_store_embedding ON vector_store 
    USING hnsw (embedding vector_cosine_ops) WITH (m = 16, ef_construction = 64);

-- 创建其他索引
CREATE INDEX idx_vector_store_create_time ON vector_store (create_time DESC);
CREATE INDEX idx_vector_store_metadata ON vector_store USING gin (metadata);