package com.srms.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

/**
 * Utility for file handling operations (upload, validation, cleanup).
 */
public class FileUtil {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    private FileUtil() {}

    /**
     * Validate an uploaded image file.
     * @param contentType MIME type of the file
     * @param fileSize    size of the file in bytes
     * @param fileName    original file name
     * @return error message if invalid, null if valid
     */
    public static String validateImage(String contentType, long fileSize, String fileName) {
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            return "Invalid file type. Allowed types: JPEG, PNG, GIF, WebP";
        }
        if (fileSize > MAX_FILE_SIZE) {
            return "File size exceeds maximum limit of 5MB";
        }
        if (fileName != null) {
            String ext = getFileExtension(fileName);
            if (!ALLOWED_IMAGE_EXTENSIONS.contains(ext.toLowerCase())) {
                return "Invalid file extension. Allowed: .jpg, .jpeg, .png, .gif, .webp";
            }
        }
        return null;
    }

    /**
     * Save an uploaded file to the specified directory.
     * @param inputStream  the file input stream
     * @param uploadDir    the directory to save to
     * @param originalName the original file name
     * @return the saved file name (with UUID prefix)
     */
    public static String saveFile(InputStream inputStream, String uploadDir, String originalName) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String extension = getFileExtension(originalName);
        String savedName = UUID.randomUUID().toString() + extension;
        Path targetPath = Path.of(uploadDir, savedName);

        Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        return savedName;
    }

    /**
     * Delete a file from the upload directory.
     */
    public static boolean deleteFile(String uploadDir, String fileName) {
        if (fileName == null || fileName.isEmpty()) return false;
        File file = new File(uploadDir, fileName);
        return file.exists() && file.delete();
    }

    /**
     * Get file extension from file name.
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int lastDot = fileName.lastIndexOf('.');
        return lastDot >= 0 ? fileName.substring(lastDot) : "";
    }

    /**
     * Get the upload directory path based on servlet context.
     */
    public static String getUploadDir(jakarta.servlet.ServletContext context) {
        String path = context.getRealPath("/uploads");
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }
}
