package org.example.final_project.services.impl;


import lombok.extern.slf4j.Slf4j;
import org.example.final_project.exception.ApiException;
import org.example.final_project.services.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Override
    public String uploadImage(String path, MultipartFile image) {
        String imageName = image.getOriginalFilename();
        String randomId = UUID.randomUUID().toString();
        assert imageName != null;
        String fileName = randomId.concat(imageName.substring(imageName.lastIndexOf(".")));
        String imagePath = path + File.separator + fileName;
        if (this.checkFile(imageName)) {
            try {
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdir();
                }
                Files.copy(image.getInputStream(), Paths.get(imagePath));
            } catch (IOException e) {
                log.info("Error Message {}", e.getMessage());
                return null;
            }
            return fileName;
        } else {
            throw new ApiException("plz select only image type");
        }
    }
    @Override
    public String uploadVideo(String path, MultipartFile video) {
        String imageName = video.getOriginalFilename();
        String randomId = UUID.randomUUID().toString();
        assert imageName != null;
        String fileName = randomId.concat(imageName.substring(imageName.lastIndexOf(".")));
        String imagePath = path + File.separator + fileName;
        if (this.checkFile(imageName)) {
            try {
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdir();
                }
                Files.copy(video.getInputStream(), Paths.get(imagePath));
            } catch (IOException e) {
                log.info("Error Message {}", e.getMessage());
                return null;
            }
            return fileName;
        } else {
            throw new ApiException("plz select only video type");
        }
    }

    @Override
    public boolean checkFile(String fileName) {
        File file = new File(fileName);
        String mimetype = null;
        try {
            mimetype = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            log.info("Error Message {}", e.getMessage());
        }
        //mimetype should be something like "image/png"
        return mimetype != null && mimetype.split("/")[0].equals("image");
    }

    @Override
    public InputStream getResource(String path, String fileName) throws FileNotFoundException {
        String fullPath = path + File.separator + fileName;
        return new FileInputStream(fullPath);
    }

    @Override
    public void deleteProfilePicture(String imageName, String path) {
        try {
            String imagePath = path + File.separator + imageName;
            File file = new File(imagePath);
            if (file.exists()) {
                if (file.delete()) {
                    log.info("Image deleted successFully {}", imageName);
                } else {
                    log.warn("Failed to delete image {}", imageName);
                }
            } else {
                log.warn("Image Not Found {} ", imageName);
            }
        } catch (Exception e) {
            log.warn("Error {}", e.getMessage());
        }
    }
}
