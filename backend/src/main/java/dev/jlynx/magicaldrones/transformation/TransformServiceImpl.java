package dev.jlynx.magicaldrones.transformation;

import dev.jlynx.magicaldrones.auth.Account;
import dev.jlynx.magicaldrones.auth.AccountRepository;
import dev.jlynx.magicaldrones.dto.CreateTransformation;
import dev.jlynx.magicaldrones.dto.TransformationView;
import dev.jlynx.magicaldrones.dto.UpdateTransformation;
import dev.jlynx.magicaldrones.exception.AccessForbiddenException;
import dev.jlynx.magicaldrones.exception.ResourceNotFoundException;
import dev.jlynx.magicaldrones.image.ImageService;
import dev.jlynx.magicaldrones.inference.ImageToImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

// todo: implement this
@Service
public class TransformServiceImpl implements TransformService {

    private static final Logger log = LoggerFactory.getLogger(TransformServiceImpl.class);

    private final TransformationRepository transformationRepository;
    private final AccountRepository accountRepository;
    private final ImageToImageService model;
    private final ImageService imageService;

    @Autowired
    public TransformServiceImpl(TransformationRepository transformationRepository,
                                AccountRepository accountRepository,
                                ImageToImageService model, ImageService imageService) {
        this.transformationRepository = transformationRepository;
        this.accountRepository = accountRepository;
        this.model = model;
        this.imageService = imageService;
    }

    @PreAuthorize("hasRole('USER') && #accountId == principal.claims['id']")
    @Transactional
    @Override
    public long createTransformation(CreateTransformation request, long accountId) {
        log.trace("createTransformation() method invoked.");
        Account account = accountRepository.findById(accountId).orElseThrow(() ->
                new ResourceNotFoundException("Account with id=%d does not exist.".formatted(accountId)));
        var transformation = new Transformation(
                LocalDateTime.now(Clock.systemUTC()),
                null,
                request.sourceImage(),
                null,
                request.title(),
                request.description()
        );
        account.addTransformation(transformation);
        log.trace("Saving transformation: {}", transformation);
        Transformation saved = transformationRepository.save(transformation);
        log.trace("Transformation saved: {}", saved);
        return saved.getId();
    }

    @PreAuthorize("hasRole('USER') && #accountId == principal.claims['id']")
    @Override
    public String passToModel(long transformationId, long accountId) {
        log.trace("passToModel({}, {}) method invoked.", transformationId, accountId);
        var transformation = transformationRepository.findById(transformationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Transformation with id=%d does not exist.".formatted(transformationId))
                );
        byte[] sourceImage = imageService.downloadTransformationImage(transformation.getSourceImageUuid());
        log.trace("Source image downloaded.");
        byte[] transformed = model.transform(sourceImage);
        log.trace("Source image transformed by ImageToImageService successfully.");
        String transformedUuid = imageService.saveTransformationImage(transformed);
        log.trace("Transformed image saved to file storage service with uuid={}", transformedUuid);

        transformation.setTransformedImageUuid(transformedUuid);
        transformation.setCompletedAt(LocalDateTime.now(Clock.systemUTC()));
        transformationRepository.save(transformation);
        log.trace("Updated Transformation: {} persisted to database", transformation);
        return transformedUuid;
    }

    @PreAuthorize("hasRole('USER') && #accountId == principal.claims['id']")
    @Override
    public List<TransformationView> getAll(long accountId) {
        return transformationRepository.findByAccount(accountId);
    }

    @PreAuthorize("hasRole('USER') && #accountId == principal.claims['id']")
    @Override
    public TransformationView getTransformation(long transformationId, long accountId) {
        Transformation transformation = transformationRepository.findById(transformationId).orElseThrow(() -> {
            log.trace("Tried to access nonexistent Transformation with id={}", transformationId);
            return new ResourceNotFoundException("Transformation with id=%d doesn't exist.".formatted(transformationId));
        });
        if (transformation.getAccount().getId() != accountId) {
            log.debug("Account [id={}] tried to access Transformation with id={} without permission.", accountId, transformationId);
            throw new AccessForbiddenException("You don't have permission to access this resource.");
        }
        log.trace("Returning single TransformationView with id={}", transformationId);
        return transformation.toDto();
    }

    @Override
    @Transactional
    public TransformationView updateTransformation(
            long transformationId,
            UpdateTransformation update,
            long accountId
    ) {
        Transformation transformation = transformationRepository.findById(transformationId)
                .orElseThrow(() -> {
                    log.trace("Tried to update nonexistent Transformation with id={}", transformationId);
                    return new ResourceNotFoundException("Transformation with id=%d doesn't exist."
                            .formatted(transformationId));
                });
        if (transformation.getAccount().getId() != accountId) {
            log.debug("Account [id={}] tried to update Transformation with id={} without permission.", accountId, transformationId);
            throw new AccessForbiddenException("You don't have permission to perform this action.");
        }
        transformation.setTitle(update.title());
        transformation.setDescription(update.description());
        log.trace("Transformation updated: {}", transformation);
        return transformation.toDto();
    }

    @PreAuthorize("hasRole('USER') && #accountId == principal.claims['id']")
    @Override
    public void deleteTransformation(long transformationId, long accountId) {
        Transformation transformation = transformationRepository.findById(transformationId)
                .orElseThrow(() -> {
                    log.trace("Tried to delete nonexistent Transformation with id={}", transformationId);
                    return new ResourceNotFoundException("Transformation with id=%d doesn't exist."
                            .formatted(transformationId));
                });
        if (transformation.getAccount().getId() != accountId) {
            log.debug("Account [id={}] tried to delete Transformation with id={} without permission.", accountId, transformationId);
            throw new AccessForbiddenException("You don't have permission to perform this action.");
        }
        log.debug("Transformation [id={}] deleted.", transformationId);
        transformationRepository.deleteById(transformationId);
    }
}
