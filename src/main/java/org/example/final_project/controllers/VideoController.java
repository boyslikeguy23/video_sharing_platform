package org.example.final_project.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.final_project.dtos.requests.VideoRequestDTO;
import org.example.final_project.dtos.responses.VideoResponseDTO;
import org.example.final_project.entities.Video;
import org.example.final_project.services.VideoService;
import org.example.final_project.services.VideoStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping("/upload")
    public ResponseEntity<VideoResponseDTO> uploadVideo(
            @RequestPart("file") MultipartFile file,
            @RequestPart("data") @Valid VideoRequestDTO dto) {
        return ResponseEntity.ok(videoService.uploadAndCreateVideo(file, dto));
    }

    @GetMapping
    public List<VideoResponseDTO> getAll() {
        return videoService.getAllVideos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.getVideoById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return ResponseEntity.noContent().build();
    }
}
