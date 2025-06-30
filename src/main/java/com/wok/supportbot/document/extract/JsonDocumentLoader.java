package com.wok.supportbot.document.extract;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class JsonDocumentLoader {

    /**
     * 基本读取方式
     */
    public List<Document> loadBasicJson(MultipartFile file) {
        Resource resource = toResource(file);
        JsonReader reader = new JsonReader(resource);
        return reader.get();
    }

    /**
     * 指定字段读取方式（例如 description、features 字段）
     */
    public List<Document> loadJsonByFields(MultipartFile file, String... fields) {
        Resource resource = toResource(file);
        JsonReader reader = new JsonReader(resource, fields);
        return reader.get();
    }

    /**
     * 使用 JSON Pointer 提取数组路径内容（如 /items）
     */
    public List<Document> loadJsonByPointer(MultipartFile file, String pointer) {
        Resource resource = toResource(file);
        JsonReader reader = new JsonReader(resource);
        return reader.get(pointer);
    }

    /**
     * 将 MultipartFile 转换为 Resource
     */
    private Resource toResource(MultipartFile file) {
        try {
            File temp = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(temp);
            return new FileSystemResource(temp);
        } catch (IOException e) {
            log.error("JSON 文件转换失败", e);
            throw new RuntimeException("JSON 文件转换失败", e);
        }
    }
}
