package dev.jlynx.magicaldrones.inference;

import dev.jlynx.magicaldrones.dto.InferenceRequest;
import dev.jlynx.magicaldrones.transformation.TransformService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RequestMapping("/inference")
@RestController
public class InferenceController {

    private static final Logger log = LoggerFactory.getLogger(InferenceController.class);

    private final TransformService transformService;

    @Autowired
    public InferenceController(TransformService transformService) {
        this.transformService = transformService;
    }

    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public SseEmitter makeInferenceOnTransformation(@RequestBody @Valid InferenceRequest request,
                                                    @AuthenticationPrincipal Jwt principal) {
        SseEmitter sse = new SseEmitter();
        Executor executor = Executors.newSingleThreadExecutor();
        executor = new DelegatingSecurityContextExecutor(executor);

        executor.execute(() -> {
            try {
                sse.send(SseEmitter.event()
                        .name("status")
                        .data("started")
                );
                String uuid = transformService.passToModel(request.transformationId(), principal.getClaim("id"));
                log.trace("GAN image with uuid='{}' generated successfully", uuid);
                sse.send(SseEmitter.event()
                        .name("generatedImage")
                        .data(uuid)
                );
                sse.send(SseEmitter.event()
                        .name("status")
                        .data("completed")
                );
                log.trace("Inference for transformation id=[{}] completed", request.transformationId());
                sse.complete();
            } catch (Exception e) {
                log.debug("Error while creating transformation caused by {}", e.toString());
                handleException(sse, e);
            }
        });
        return sse;
    }

    private void handleException(SseEmitter emitter, Exception e) {
        try {
            emitter.send(SseEmitter.event()
                    .name("error")
                    .data(e.getMessage())
            );
        } catch (IOException ex) {
            log.debug("Error occurred while sending error event caused by {}", e.toString());
        } finally {
            emitter.completeWithError(e);
        }
    }
}
