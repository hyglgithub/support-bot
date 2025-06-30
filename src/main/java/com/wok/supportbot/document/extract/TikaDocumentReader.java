package com.wok.supportbot.document.extract;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class TikaDocumentReader {

    public List<Document> read(MultipartFile file) {
        try {
            // MultipartFile 转 Resource
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);
            Resource resource = new FileSystemResource(tempFile);

            Tika tika = new Tika();
            String text = tika.parseToString(resource.getInputStream());

            Document doc = Document.builder()
                    .id(UUID.randomUUID().toString())
                    .text(text)
                    .build();

            return Collections.singletonList(doc);

        } catch (IOException | TikaException e) {
            log.error("Tika 文件解析失败", e);
            throw new RuntimeException("Tika 文件解析失败", e);
        }
    }
}
