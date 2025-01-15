package dev.jlynx.magicaldrones.image;

import dev.jlynx.magicaldrones.exception.NoSuchKeyStorageException;
import dev.jlynx.magicaldrones.exception.ResourceNotFoundException;
import dev.jlynx.magicaldrones.storage.StorageService;
import dev.jlynx.magicaldrones.storage.s3.AwsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

// todo: add method security
@Service
public class ImageServiceImpl implements ImageService {

    private static final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);
    private static final String transformKeyPtrn = "transform/%d/%s";

    private final StorageService storageService;
    private final AwsProperties awsProps;

    @Autowired
    public ImageServiceImpl(StorageService storageService, AwsProperties awsProps) {
        this.storageService = storageService;
        this.awsProps = awsProps;
    }

    private long extractAccountId() {
        Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getClaim("id");
    }

    @Override
    public byte[] downloadTransformationImage(String imageUuid) {
        String key = transformKeyPtrn.formatted(extractAccountId(), imageUuid);
        byte[] image = null;
        try {
            image = storageService.download(awsProps.getS3().getBucket(), key);
        } catch (NoSuchKeyStorageException e) {
            log.trace("Tried to access file with nonexistent key '{}'. Operation threw exception.", key);
            throw new ResourceNotFoundException("Key '%s' doesn't exist.".formatted(key), e);
        }
        log.trace("Downloaded image with key '{}' successfully", key);
        return image;
    }

    @Override
    public String saveTransformationImage(byte[] image) {
        if (image.length == 0) {
            log.trace("Tried to save empty image file. Operation threw exception.");
            throw new IllegalArgumentException("Image file cannot is empty.");
        }
        UUID uuid = UUID.randomUUID();
        String key = transformKeyPtrn.formatted(extractAccountId(), uuid.toString());
        storageService.upload(awsProps.getS3().getBucket(), key, image);
        log.trace("Image saved successfully with uuid='{}'", uuid);
        return uuid.toString();
    }

    @Override
    public void deleteTransformationImage(String imageUuid) {
        String key = transformKeyPtrn.formatted(extractAccountId(), imageUuid);
        storageService.delete(awsProps.getS3().getBucket(), key);
        log.trace("Request to delete key '{}' has completed.", key);
    }
}
