package dev.jlynx.magicaldrones.image;

import dev.jlynx.magicaldrones.dto.UuidRequest;
import dev.jlynx.magicaldrones.dto.UuidResponse;
import dev.jlynx.magicaldrones.validation.HasExtension;
import jakarta.validation.Valid;
import org.hibernate.validator.constraints.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RequestMapping("/image")
@RestController
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UuidResponse> uploadSourceImage(
            @HasExtension({"jpg"})
            @RequestParam("sourceImg")
            MultipartFile sourceFile
    ) {
        String uuid = imageService.saveTransformationImage(sourceFile);
        UuidResponse body = new UuidResponse(uuid);
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTransformationImage(@RequestBody @Valid UuidRequest request) {
        imageService.deleteTransformationImage(request.uuid());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/{imageUuid}/", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getTransformationImage(@PathVariable("imageUuid") @UUID String imageUuid) {
        byte[] image = imageService.downloadTransformationImage(imageUuid);
        return ResponseEntity.ok(image);
    }
}
