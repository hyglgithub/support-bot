package com.wok.supportbot.document.extract;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class SimpleStringDocumentReader {

    public List<Document> read(String content) {
        Document doc = Document.builder()
                .id(UUID.randomUUID().toString())
                .text(content)
                .build();
        return Collections.singletonList(doc);
    }
}
