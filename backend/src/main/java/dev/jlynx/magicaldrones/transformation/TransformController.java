package dev.jlynx.magicaldrones.transformation;

import dev.jlynx.magicaldrones.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Validated
@RequestMapping("/transform")
@RestController
public class TransformController {

    private static final Logger log = LoggerFactory.getLogger(TransformController.class);

    private final ExecutorService pool;
    private final TransformService transformService;

    @Autowired
    public TransformController(TransformService transformService) {
        ExecutorService cachedPool = Executors.newCachedThreadPool();
        pool = new DelegatingSecurityContextExecutorService(cachedPool);
        this.transformService = transformService;
    }

    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createTransformation(@RequestBody @Valid CreateTransformation request,
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
                long transformationId = transformService.createTransformation(request, principal.getClaim("id"));
                log.trace("Transformation object with id='{}' created successfully", transformationId);
                sse.send(SseEmitter.event()
                        .name("transformationId")
                        .data(transformationId)
                );
                String uuid = transformService.passToModel(transformationId, principal.getClaim("id"));
                log.trace("GAN image with uuid='{}' generated successfully", uuid);
                sse.send(SseEmitter.event()
                        .name("generatedImage")
                        .data(uuid)
                );
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

    @GetMapping
    public ResponseEntity<?> getAllTransformations(@AuthenticationPrincipal Jwt principal) {
        List<TransformationView> transformations = transformService.getAll(principal.getClaim("id"));
        return ResponseEntity.ok(transformations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransformation(@PathVariable("id") @Min(1) long transformationId,
                                               @AuthenticationPrincipal Jwt principal) {
        TransformationView view = transformService.getTransformation(transformationId, principal.getClaim("id"));
        return new ResponseEntity<>(view, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTransformation(
            @PathVariable("id") @Min(1) long transformationId,
            @RequestBody @Valid UpdateTransformation update,
            @AuthenticationPrincipal Jwt principal
            ) {
        TransformationView view = transformService.updateTransformation(transformationId, update, principal.getClaim("id"));
        return new ResponseEntity<>(view, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransformation(@PathVariable("id") @Min(1) long transformationId,
                                                  @AuthenticationPrincipal Jwt principal) {
        transformService.deleteTransformation(transformationId, principal.getClaim("id"));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
