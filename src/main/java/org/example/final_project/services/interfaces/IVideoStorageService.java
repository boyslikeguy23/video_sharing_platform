package org.example.final_project.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface IVideoStorageService {
    String store(MultipartFile file);

}
