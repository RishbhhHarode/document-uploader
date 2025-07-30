package com.document.uploader.service;

import com.document.uploader.model.Document;
import com.document.uploader.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Optional;

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

        Files.createDirectories(filePath.getParent());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Document doc = new Document();
        doc.setFileName(fileName);
        doc.setFilePath(filePath.toString());
        doc.setUploadedAt(LocalDateTime.now());

        repository.save(doc);
    }

    public Page<Document> getAllDocuments(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    public Optional<Document> getDocumentById(Long id) {
        return repository.findById(id);
    }

    public void deleteFile(Long id) throws IOException {
        Optional<Document> optionalDoc = repository.findById(id);
        if (optionalDoc.isPresent()) {
            Document doc = optionalDoc.get();
            Files.deleteIfExists(Paths.get(doc.getFilePath()));
            repository.deleteById(id);
        }
    }
}
