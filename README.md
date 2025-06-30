# Support Bot - AI智能客服系统

> 🤖 基于 Spring AI 和阿里云通义千问的智能客服机器人  
> 🧠 支持 RAG 知识库检索 · 多种对话模式 · 结构化数据提取  
> 🧱 技术栈：Java 17 · Spring Boot 3.4.4 · Spring AI Alibaba · PGVector

## 📌 项目简介

本项目是一个基于 Spring AI 和阿里云通义千问构建的智能客服系统，集成了文档检索增强生成（RAG）、聊天记忆、结构化数据提取等功能。系统支持同步/异步对话、SSE流式输出、向量语义搜索等多种交互方式，广泛适用于电商客服、企业知识问答、文档智能检索等场景。

## 🚀 功能特性

- **💬 智能对话**: 基于阿里云通义千问模型的自然语言对话
- **🔍 RAG检索**: 文档检索增强生成，从知识库中精准检索相关信息
- **📺 多模式交互**: 支持同步、SSE流式、ServerSentEvent等多种API接口
- **🧠 记忆管理**: 完整的会话上下文记忆，支持数据库持久化存储
- **🏷️ 结构化提取**: 从自然语言中智能提取商品信息等结构化数据
- **⚡ 向量搜索**: 基于PGVector的高性能语义相似度搜索
- **🔧 查询优化**: 多种预检索优化策略提升问答质量
- **📖 API文档**: 集成Knife4j提供完整的交互式API文档

## 🛠 技术栈

| 技术分类 | 技术组件 | 版本 | 用途说明 |
|---------|---------|------|---------|
| **后端框架** | Spring Boot | 3.4.4 | 主框架，提供依赖注入和自动配置 |
| **AI框架** | Spring AI Alibaba | 1.0.0-M6.1 | AI集成框架，简化大模型调用 |
| **大语言模型** | 阿里云通义千问 | qwen-turbo | 对话生成和文本理解 |
| **数据库** | PostgreSQL + PGVector | 12+ | 关系数据存储 + 向量存储 |
| **ORM框架** | MyBatis Plus | 3.5.12 | 数据库操作和对象映射 |
| **API文档** | Knife4j | 4.4.0 | Swagger UI增强版 |
| **工具库** | Hutool | 5.8.37 | 常用工具类集合 |
| **序列化** | Kryo | 5.6.2 | 高性能序列化框架 |
| **文档解析** | Apache Tika | 1.0.0 | 多格式文档内容提取 |

## 🗃️ 数据库设计

### 核心表结构

#### 1. 聊天消息表 (`chat_message`)
```sql
CREATE TABLE chat_message (
    id              BIGSERIAL    PRIMARY KEY,      -- 消息唯一ID
    conversation_id VARCHAR(64)  NOT NULL,         -- 会话标识符
    message_type    VARCHAR(20)  NOT NULL,         -- 消息类型: USER/ASSISTANT/SYSTEM
    content         TEXT         NOT NULL,         -- 消息内容
    metadata        JSONB        DEFAULT '{}',     -- 消息元数据(JSON格式)
    create_time     TIMESTAMP    DEFAULT NOW(),    -- 创建时间
    update_time     TIMESTAMP    DEFAULT NOW(),    -- 更新时间
    is_delete       BOOLEAN      DEFAULT FALSE     -- 逻辑删除标识
);
```
**功能**: 存储用户与AI助手的完整对话历史，支持多轮对话上下文维护

#### 2. 向量存储表 (`vector_store`)
```sql
CREATE TABLE vector_store (
    id          UUID         DEFAULT uuid_generate_v4() PRIMARY KEY, -- 向量记录ID
    content     TEXT         NOT NULL,                              -- 原始文档内容
    metadata    JSONB        DEFAULT '{}',                          -- 文档元数据
    embedding   VECTOR(1536) NOT NULL,                              -- 1536维向量嵌入
    create_time TIMESTAMP    DEFAULT NOW(),                         -- 创建时间
    update_time TIMESTAMP    DEFAULT NOW()                          -- 更新时间
);
```
**功能**: 存储知识库文档的向量表示，支持语义相似度搜索和RAG检索

## 📁 项目架构

### 代码结构
```
src/main/java/com/wok/supportbot/
├── SupportBotApplication.java          # 主启动类
├── advisor/                            # AI对话增强器
│   ├── MyLoggerAdvisor.java           # 日志记录顾问
│   └── ReReadingAdvisor.java          # 重读机制顾问
├── app/                               # 核心应用服务
│   ├── AssistantApp.java              # 智能客服应用
│   └── ProductInfoApp.java            # 商品信息提取应用
├── chatmemory/                        # 聊天记忆管理
│   ├── DatabaseChatMemory.java        # 数据库记忆存储
│   └── FileBasedChatMemory.java       # 文件记忆存储
├── config/                            # 系统配置
│   └── CorsConfig.java                # 跨域请求配置
├── controller/                        # REST API控制器
│   ├── AiController.java              # AI对话接口
│   └── DocumentController.java        # 文档管理接口
├── entity/                            # 数据实体类
│   ├── ChatMessage.java               # 聊天消息实体
│   └── ProductInfo.java               # 商品信息实体
├── rag/                               # RAG检索增强
│   ├── config/                        # RAG配置
│   ├── load/                          # 文档加载器
│   └── preretrieval/                  # 预检索优化
│       ├── RewriteQueryRewriter.java      # 查询重写
│       ├── MultiQueryExpanderRewriter.java # 多查询扩展
│       ├── CompressionQueryRewriter.java   # 压缩查询
│       └── TranslationQueryRewriter.java   # 翻译查询
└── repository/                        # 数据访问层
    └── ChatMessageRepository.java     # 聊天消息仓库

src/main/resources/
├── application.yml                     # 应用配置文件
└── support-bot.sql                     # 数据库初始化脚本
```

