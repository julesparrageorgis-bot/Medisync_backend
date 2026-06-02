package ma.medisync.medisync_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import ma.medisync.medisync_backend.util.FileUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentStorageService {
    @Value("${file.upload-dir}")
    private String uploadDir;
    private final FileUtil fileUtil;

    public String storeFile(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        fileUtil.validateFile(originalName, file.getSize());
        String extension = "." + fileUtil.getFileExtension(originalName).toLowerCase();
        String newFileName = UUID.randomUUID() + extension;
        Path path = resolveSafePath(newFileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        return newFileName;
    }

    public byte[] getFile(String fileName) throws IOException {
        Path path = resolveSafePath(fileName);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File not found: " + fileName);
        }
        return Files.readAllBytes(path);
    }

    public void deleteFile(String fileName) throws IOException {
        Path path = resolveSafePath(fileName);
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

    private Path resolveSafePath(String fileName) {
        Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path path = root.resolve(fileName).normalize();
        if (!path.startsWith(root)) {
            throw new IllegalArgumentException("Invalid file path");
        }
        return path;
    }
}
