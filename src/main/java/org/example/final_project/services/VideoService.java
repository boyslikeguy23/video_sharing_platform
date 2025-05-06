package org.example.final_project.services;

import lombok.RequiredArgsConstructor;
import org.example.final_project.dtos.requests.VideoRequestDTO;
import org.example.final_project.dtos.responses.VideoResponseDTO;
import org.example.final_project.entities.User;
import org.example.final_project.entities.Video;
import org.example.final_project.entities.VideoCount;
import org.example.final_project.repositories.UserRepository;
import org.example.final_project.repositories.VideoCountRepository;
import org.example.final_project.repositories.VideoRepository;
import org.example.final_project.services.interfaces.IVideoService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoService implements IVideoService {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final VideoCountRepository videoCountRepository;
    private final VideoStorageService videoStorageService;

    @Override
    public VideoResponseDTO uploadAndCreateVideo(MultipartFile file, VideoRequestDTO dto) {
        String videoUrl = videoStorageService.store(file);
        User user = userRepository.findById(dto.getCreatedByUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Video video = Video.builder()
                .caption(dto.getCaption())
                .videoUrl(videoUrl)
                .createdByUser(user)
                .build();

        Video saved = videoRepository.save(video);

        VideoCount count = new VideoCount(0, 0, saved);
        videoCountRepository.save(count);

        return toResponseDTO(saved);
    }

    @Override
    public List<VideoResponseDTO> getAllVideos() {
        return videoRepository.findAll().stream().map(this::toResponseDTO).toList();
    }

    @Override
    public VideoResponseDTO getVideoById(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found"));
        return toResponseDTO(video);
    }

    @Override
    public void deleteVideo(Long id) {
        videoRepository.deleteById(id);
    }

    public VideoResponseDTO toResponseDTO(Video video) {
        VideoCount count = video.getVideoCount();
        return VideoResponseDTO.builder()
                .videoId(video.getVideoId())
                .caption(video.getCaption())
                .videoUrl(video.getVideoUrl())
                .uploaderName(video.getCreatedByUser().getFullName())
                .viewCount((long) (count != null ? count.getViewCount() : 0))
                .likeCount((long) (count != null ? count.getLikeCount() : 0))
                .build();
    }
//    public VideoResponseDTO toResponseDTO(Video video) {
//        return VideoResponseDTO.builder
//                .id(video.getVideoId())
//                .caption(video.getCaption())
//                .videoUrl(video.getVideoUrl())
//                .uploaderName(video.getCreatedByUser().getUsername())
//                .build();
//    }
//    @Override
//    public Video updateVideo(Long videoId, VideoRequestDTO videoDTO) {
//        return videoRepository.findById(videoId).map(video -> {
//            video.setCaption(videoDTO.getCaption());
//            video.setVideoUrl(videoDTO.getVideoUrl());
//            //video.setThumbnailUrl(updatedVideo.getThumbnailUrl());
//            video.setPrivacy(videoDTO.getPrivacy());
//            //video.setAllowComments(updatedVideo.isAllowComments());
//            //video.setAllowReactions(updatedVideo.isAllowReactions());
//            return videoRepository.save(video);
//        }).orElseThrow(() -> new RuntimeException("Video not found"));
//    }
//
//    @Override
//    public void deleteVideo(Long id) {
//        videoRepository.deleteById(id);
//    }

}