package com.wok.supportbot.document.extract;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Markdown 文件上传解析器
 */
@Component
@Slf4j
public class MarkdownDocumentLoader {

    public List<Document> loadMarkdownFromFile(MultipartFile file) {
        try {
            // 将 MultipartFile 保存为临时文件
            File temp = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(temp);
            Resource resource = new FileSystemResource(temp);

            // 配置文档解析
            MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                    .withHorizontalRuleCreateDocument(true)
                    .withIncludeCodeBlock(false)
                    .withIncludeBlockquote(false)
                    .withAdditionalMetadata("filename", file.getOriginalFilename())
                    .build();

            // 读取文档内容
            MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
            return reader.get();

        } catch (IOException e) {
            log.error("Markdown 文件解析失败", e);
            throw new RuntimeException("Markdown 文件解析失败", e);
        }
    }
}
