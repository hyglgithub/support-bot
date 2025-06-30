package com.wok.supportbot.controller;

import com.wok.supportbot.document.extract.JsonDocumentLoader;
import com.wok.supportbot.document.extract.MarkdownDocumentLoader;
import com.wok.supportbot.document.extract.SimpleStringDocumentReader;
import com.wok.supportbot.document.extract.TikaDocumentReader;
import com.wok.supportbot.document.transform.MyKeywordEnricher;
import com.wok.supportbot.document.transform.MyTokenTextSplitter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private TikaDocumentReader tikaDocumentReader;

    @Autowired
    private SimpleStringDocumentReader simpleStringDocumentReader;

    @Autowired
    private MarkdownDocumentLoader markdownDocumentLoader;

    @Autowired
    private JsonDocumentLoader jsonDocumentLoader;

    @Autowired
    private MyTokenTextSplitter myTokenTextSplitter;

    @Autowired
    private MyKeywordEnricher myKeywordEnricher;

    @Autowired
    private VectorStore pgVectorVectorStore;

    /**
     * 上传普通文件（支持多种格式），用 Tika 解析
     */
    @PostMapping("/upload/file")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            List<Document> documents = tikaDocumentReader.read(file);
            
            // 拆分文档
            List<Document> splitDocuments = myTokenTextSplitter.splitDocuments(documents);
            
            // 添加元数据
            List<Document> enrichedDocuments = myKeywordEnricher.enrichDocuments(splitDocuments);
            
            // 转成向量并存入数据库
            pgVectorVectorStore.add(enrichedDocuments);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "文件上传并向量化成功",
                "documentCount", enrichedDocuments.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "上传失败：" + e.getMessage()
            ));
        }
    }

    /**
     * 上传字符串内容
     */
    @PostMapping("/upload/string")
    public ResponseEntity<Map<String, Object>> uploadString(@RequestBody String content) {
        try {
            List<Document> documents = simpleStringDocumentReader.read(content);
            
            // 拆分文档
            List<Document> splitDocuments = myTokenTextSplitter.splitDocuments(documents);
            
            // 添加元数据
            List<Document> enrichedDocuments = myKeywordEnricher.enrichDocuments(splitDocuments);
            
            // 转成向量并存入数据库
            pgVectorVectorStore.add(enrichedDocuments);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "文本内容上传并向量化成功",
                "documentCount", enrichedDocuments.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "上传失败：" + e.getMessage()
            ));
        }
    }

    /**
     * 上传 Markdown 文件
     */
    @PostMapping("/upload/markdown")
    public ResponseEntity<Map<String, Object>> uploadMarkdown(@RequestParam("file") MultipartFile file) {
        try {
            List<Document> documents = markdownDocumentLoader.loadMarkdownFromFile(file);
            
            // 拆分文档
            List<Document> splitDocuments = myTokenTextSplitter.splitDocuments(documents);
            
            // 添加元数据
            List<Document> enrichedDocuments = myKeywordEnricher.enrichDocuments(splitDocuments);
            
            // 转成向量并存入数据库
            pgVectorVectorStore.add(enrichedDocuments);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Markdown文件上传并向量化成功",
                "documentCount", enrichedDocuments.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "上传失败：" + e.getMessage()
            ));
        }
    }

    /**
     * 上传 JSON 文件（基本方式）
     * 把 JSON 根节点当成一个整体文档
     */
    @PostMapping("/upload/json/basic")
    public ResponseEntity<Map<String, Object>> uploadJsonBasic(@RequestParam("file") MultipartFile file) {
        try {
            List<Document> documents = jsonDocumentLoader.loadBasicJson(file);
            
            // 拆分文档
            List<Document> splitDocuments = myTokenTextSplitter.splitDocuments(documents);
            
            // 添加元数据
            List<Document> enrichedDocuments = myKeywordEnricher.enrichDocuments(splitDocuments);
            
            // 转成向量并存入数据库
            pgVectorVectorStore.add(enrichedDocuments);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "JSON文件（基本方式）上传并向量化成功",
                "documentCount", enrichedDocuments.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "上传失败：" + e.getMessage()
            ));
        }
    }

    /**
     * 上传 JSON 文件（按字段提取）
     * 用于提取指定字段文本
     */
    @PostMapping("/upload/json/fields")
    public ResponseEntity<Map<String, Object>> uploadJsonWithFields(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fields") List<String> fields) {
        try {
            List<Document> documents = jsonDocumentLoader.loadJsonByFields(file, fields.toArray(new String[0]));
            
            // 拆分文档
            List<Document> splitDocuments = myTokenTextSplitter.splitDocuments(documents);
            
            // 添加元数据
            List<Document> enrichedDocuments = myKeywordEnricher.enrichDocuments(splitDocuments);
            
            // 转成向量并存入数据库
            pgVectorVectorStore.add(enrichedDocuments);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "JSON文件（按字段）上传并向量化成功",
                "documentCount", enrichedDocuments.size(),
                "extractedFields", fields
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "上传失败：" + e.getMessage()
            ));
        }
    }

    /**
     * 上传 JSON 文件（按指针拆分）
     * 用于拆分数组元素，常用来分段成多文档
     */
    @PostMapping("/upload/json/pointer")
    public ResponseEntity<Map<String, Object>> uploadJsonWithPointer(
            @RequestParam("file") MultipartFile file,
            @RequestParam("pointer") String pointer) {
        try {
            List<Document> documents = jsonDocumentLoader.loadJsonByPointer(file, pointer);
            
            // 拆分文档
            List<Document> splitDocuments = myTokenTextSplitter.splitDocuments(documents);
            
            // 添加元数据
            List<Document> enrichedDocuments = myKeywordEnricher.enrichDocuments(splitDocuments);
            
            // 转成向量并存入数据库
            pgVectorVectorStore.add(enrichedDocuments);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "JSON文件（按指针）上传并向量化成功",
                "documentCount", enrichedDocuments.size(),
                "pointer", pointer
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "上传失败：" + e.getMessage()
            ));
        }
    }
}
