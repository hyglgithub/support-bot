package com.wok.supportbot.rag.load;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 向量数据库配置（初始化基于内存的向量数据库 Bean）
 */
@Configuration
public class InMemoryVectorStoreConfig {

    @Bean
    VectorStore inMemoryVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();;
        return simpleVectorStore;
    }
}
