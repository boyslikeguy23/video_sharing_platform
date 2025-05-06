package org.example.final_project.services;

import lombok.RequiredArgsConstructor;
import org.example.final_project.services.interfaces.IVideoStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoStorageService implements IVideoStorageService {

    @Value("${upload.video-dir}")
    private String videoDir;

    @Override
    public String store(MultipartFile file) {
        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(videoDir).resolve(filename);
            Files.createDirectories(path.getParent());
            file.transferTo(path);
            return "/videos/" + filename; // public URL nếu có cấu hình WebMvc
        } catch (IOException e) {
            throw new RuntimeException("Video upload failed", e);
        }
    }
}

