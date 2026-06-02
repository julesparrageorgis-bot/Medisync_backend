package ma.medisync.medisync_backend.exception;

public class FileStorageException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public static FileStorageException fileTooLarge() {
        return new FileStorageException("File size exceeds maximum allowed size of 20MB");
    }

    public static FileStorageException invalidFileType() {
        return new FileStorageException("Invalid file type. Allowed types: pdf, jpg, jpeg, png, dcm, dicom");
    }

    public static FileStorageException fileNotFound(String filename) {
        return new FileStorageException("File not found: " + filename);
    }

    public static FileStorageException uploadFailed(String filename) {
        return new FileStorageException("Failed to upload file: " + filename);
    }
}
