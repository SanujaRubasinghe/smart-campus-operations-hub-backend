package com.springboot.smartcampusoperationshub.util;

import com.springboot.smartcampusoperationshub.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Component
public class FileStorageUtil {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Only image files are allowed");
        }

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String originalFileName = file.getOriginalFilename();
            String safeFileName = UUID.randomUUID() + "_" + originalFileName;

            Path targetPath = uploadPath.resolve(safeFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return targetPath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        }
    }

    public void deleteFile(String filePath) {
        try {
            if (filePath != null && !filePath.isBlank()) {
                Files.deleteIfExists(Paths.get(filePath));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }
    }
}