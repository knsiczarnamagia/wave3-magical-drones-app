package dev.jlynx.magicaldrones.transformation;

import dev.jlynx.magicaldrones.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequestMapping("/transform")
@RestController
public class TransformController {

    private static final Logger log = LoggerFactory.getLogger(TransformController.class);

    private final TransformService transformService;

    @Autowired
    public TransformController(TransformService transformService) {
        this.transformService = transformService;
    }

    @PostMapping
    public ResponseEntity<CreateTransformationResponse> createTransformation(
            @RequestBody @Valid CreateTransformation request,
            @AuthenticationPrincipal Jwt principal
    ) {
        long transformationId = transformService.createTransformation(request, principal.getClaim("id"));
        log.trace("Transformation object with id='{}' created successfully", transformationId);
        return ResponseEntity.ok(new CreateTransformationResponse(transformationId));
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
