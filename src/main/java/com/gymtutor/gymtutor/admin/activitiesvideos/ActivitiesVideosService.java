package com.gymtutor.gymtutor.admin.activitiesvideos;

import com.gymtutor.gymtutor.admin.activities.ActivitiesModel;
import com.gymtutor.gymtutor.admin.activities.ActivitiesRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ActivitiesVideosService {

    private final ActivitiesVideosRepository videosRepository;
    private final ActivitiesRepository activitiesRepository; // Repositório dos exercícios

    public ActivitiesVideosService(
            ActivitiesVideosRepository videosRepository,
            ActivitiesRepository activitiesRepository
    ) {
        this.videosRepository = videosRepository;
        this.activitiesRepository = activitiesRepository;
    }

    public Object findByActivityModelActivitiesId(int activitiesId) {
        return videosRepository.findByActivityModelActivitiesId(activitiesId);

    }

    public void createVideo(@Valid ActivitiesVideosModel activitiesVideosModel, int activitiesId) {
        validateYouTubeLink(activitiesVideosModel.getVideoLink());

        ActivitiesModel activity = activitiesRepository.findById(activitiesId).orElseThrow(() -> new EntityNotFoundException("Exercícios não Encontrados"));
        activitiesVideosModel.setActivity(activity);
        videosRepository.save(activitiesVideosModel);
    }

    public ActivitiesVideosModel findById(int videoId) {
        Optional<ActivitiesVideosModel> optionalActivitiesVideosModel = videosRepository.findById(videoId);
        return optionalActivitiesVideosModel.orElseThrow(() -> new EntityNotFoundException("Videos não Encontrados"));
    }

    public void deleteVideo(int videoId){
        videosRepository.deleteById(videoId);
    }

    public void updateVideo(ActivitiesVideosModel activitiesVideosModel, int videoId){
        validateYouTubeLink(activitiesVideosModel.getVideoLink());

        Optional<ActivitiesVideosModel> existingVideo = videosRepository.findById(videoId);
        if(existingVideo.isPresent()){
            ActivitiesVideosModel video = existingVideo.get();

            video.setVideoLink(activitiesVideosModel.getVideoLink());
            video.setVideoName(activitiesVideosModel.getVideoName());

            videosRepository.save(video);
        }
    }

    //Validar para ser somente do youtube
    private void validateYouTubeLink(String videoLink) {
        if (videoLink == null ||
                !(videoLink.contains("youtube.com/watch?v=") || videoLink.contains("youtu.be/"))) {
            throw new IllegalArgumentException("O link precisa ser um vídeo do YouTube.");
        }
    }

}