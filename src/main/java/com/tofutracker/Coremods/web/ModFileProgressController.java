package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.services.mods.ModFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
@Slf4j
public class ModFileProgressController {

    private final ModFileService modFileService;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @GetMapping(value = "/{progressId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter trackProgress(@PathVariable String progressId) {
        SseEmitter emitter = new SseEmitter(300000L); // 5 minutes timeout
        
        emitter.onCompletion(() -> {
        });
        
        emitter.onTimeout(() -> {
            log.debug("SSE connection timed out for progressId: {}", progressId);
            emitter.complete();
        });
        
        emitter.onError((ex) -> {
            log.error("SSE connection error for progressId: {}", progressId, ex);
            emitter.complete();
        });

        executor.execute(() -> {
            try {
                int progress = modFileService.getUploadProgress(progressId);
                if (progress == -1) {
                    // If progress is not found, send an error and complete
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("Upload not found or failed"));
                    emitter.complete();
                    return;
                }

                sendProgressUpdate(emitter, progressId, progress);

                while (progress >= 0 && progress < 100) {
                    Thread.sleep(750);

                    progress = modFileService.getUploadProgress(progressId);
                    
                    // If progress is not found (returned -1), it might have been cleaned up
                    if (progress == -1) {
                        emitter.send(SseEmitter.event()
                                .name("error")
                                .data("Upload not found or failed"));
                        emitter.complete();
                        break;
                    }

                    sendProgressUpdate(emitter, progressId, progress);

                    if (progress == 100) {
                        emitter.send(SseEmitter.event()
                                .name("complete")
                                .data(progress));
                        emitter.complete();
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("Error while tracking progress for {}", progressId, e);
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("Error tracking upload progress"));
                    emitter.complete();
                } catch (IOException ex) {
                    log.error("Failed to send error event", ex);
                }
            }
        });
        
        return emitter;
    }
    
    private void sendProgressUpdate(SseEmitter emitter, String progressId, int progress) throws IOException {
        if (progress >= 0) {
            emitter.send(SseEmitter.event()
                    .name("progress")
                    .data(progress));
            log.debug("Sent progress update for {}: {}%", progressId, progress);
        }
    }
} 