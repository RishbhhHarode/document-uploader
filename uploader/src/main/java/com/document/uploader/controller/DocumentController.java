package com.document.uploader.controller;

import com.document.uploader.model.Document;
import com.document.uploader.repository.DocumentRepository;
import com.document.uploader.service.DocumentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentRepository documentRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public DocumentController(DocumentService documentService, DocumentRepository documentRepository) {
        this.documentService = documentService;
        this.documentRepository = documentRepository;
    }

    @GetMapping("/")
    public String index(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<Document> docs = documentRepository.findAll(PageRequest.of(page, 5));
        model.addAttribute("documents", docs.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", docs.getTotalPages());
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            documentService.saveFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }
    @GetMapping("/delete/{id}")
    public String deleteFile(@PathVariable Long id) {
        try {
            documentService.deleteFile(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }
}
