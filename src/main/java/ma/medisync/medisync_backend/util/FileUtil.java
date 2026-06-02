package ma.medisync.medisync_backend.util;

import ma.medisync.medisync_backend.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class FileUtil {
    
    @Value("${file.upload.max-size:20971520}")
    private long maxFileSize;
    
    @Value("${file.upload.allowed-types:pdf,jpg,jpeg,png,dcm,dicom}")
    private String allowedTypes;
    
    private static final Set<String> DEFAULT_ALLOWED_TYPES = new HashSet<>(Arrays.asList(
            "pdf", "jpg", "jpeg", "png", "dcm", "dicom"
    ));
    
    public void validateFile(String filename, long fileSize) {
        validateFileSize(fileSize);
        validateFileType(filename);
    }
    
    public void validateFileSize(long fileSize) {
        if (fileSize > maxFileSize) {
            throw FileStorageException.fileTooLarge();
        }
    }
    
    public void validateFileType(String filename) {
        if (filename == null || filename.isEmpty()) {
            throw FileStorageException.invalidFileType();
        }
        
        String extension = getFileExtension(filename).toLowerCase();
        Set<String> types = new HashSet<>(Arrays.asList(allowedTypes.split(",")));
        
        if (!types.contains(extension) && !DEFAULT_ALLOWED_TYPES.contains(extension)) {
            throw FileStorageException.invalidFileType();
        }
    }
    
    public String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
    
    public String generateUniqueFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be empty");
        }
        
        String timestamp = System.currentTimeMillis() + "_";
        String extension = "." + getFileExtension(originalFilename);
        String nameWithoutExtension = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        
        return timestamp + sanitizeFilename(nameWithoutExtension) + extension;
    }
    
    public String sanitizeFilename(String filename) {
        if (filename == null) {
            return "file";
        }
        // Remove special characters except underscore and hyphen
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
    
    public long getMaxFileSize() {
        return maxFileSize;
    }
    
    public String getMaxFileSizeInMB() {
        return (maxFileSize / (1024 * 1024)) + "MB";
    }
}
