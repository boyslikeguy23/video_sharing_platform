package org.example.final_project.services.interfaces;

import org.example.final_project.dtos.requests.VideoRequestDTO;
import org.example.final_project.dtos.responses.VideoResponseDTO;
import org.example.final_project.entities.Video;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IVideoService {
//    Video createVideo(VideoRequestDTO videoDTO);
//    Optional<Video> getVideoById(Long id);
//    List<Video> getAllVideos();
//    Video updateVideo(Long id, VideoRequestDTO videoDTO);
//    void deleteVideo(Long id);
    VideoResponseDTO toResponseDTO(Video video);
    VideoResponseDTO uploadAndCreateVideo(MultipartFile file, VideoRequestDTO dto);
    List<VideoResponseDTO> getAllVideos();

    VideoResponseDTO getVideoById(Long id);
    void deleteVideo(Long id);
}
