package ma.medisync.medisync_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class DocumentStorageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file) throws IOException {
        if (file.getSize() > 20 * 1024 * 1024) {
            throw new IllegalArgumentException("File too large, max 20MB");
        }
        String originalName = file.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String newFileName = UUID.randomUUID() + extension;
        Path path = Paths.get(uploadDir, newFileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        return newFileName;
    }

    public byte[] getFile(String fileName) throws IOException {
        Path path = Paths.get(uploadDir, fileName);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File not found: " + fileName);
        }
        return Files.readAllBytes(path);
    }

    public void deleteFile(String fileName) throws IOException {
        Path path = Paths.get(uploadDir, fileName);
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }
}