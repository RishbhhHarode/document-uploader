package com.document.uploader.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.document.uploader.model.Document;
import com.document.uploader.repository.DocumentRepository;

@Service
public class DocumentService {

    private final DocumentRepository repository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public DocumentService(DocumentRepository repository) {
        this.repository = repository;
    }

    public void saveFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        // Create folder if it doesn't exist
        Files.createDirectories(filePath.getParent());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Document doc = new Document();
        doc.setFileName(fileName);
        doc.setFilePath(filePath.toString());
        doc.setUploadedAt(LocalDateTime.now());

        repository.save(doc);
    }

    public void deleteFile(Long id) throws IOException {
        Optional<Document> optionalDoc = repository.findById(id);
        if (optionalDoc.isPresent()) {
            Document doc = optionalDoc.get();
            Files.deleteIfExists(Paths.get(doc.getFilePath()));
            repository.deleteById(id);
        }
    }

    // ✅ NEW: View file in browser
    public ResponseEntity<Resource> viewFile(Long id) throws IOException {
        Optional<Document> optionalDoc = repository.findById(id);
        if (optionalDoc.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Document doc = optionalDoc.get();
        File file = new File(doc.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // ✅ NEW: Download file
    public ResponseEntity<Resource> downloadFile(Long id) throws IOException {
        Optional<Document> optionalDoc = repository.findById(id);
        if (optionalDoc.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Document doc = optionalDoc.get();
        File file = new File(doc.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
