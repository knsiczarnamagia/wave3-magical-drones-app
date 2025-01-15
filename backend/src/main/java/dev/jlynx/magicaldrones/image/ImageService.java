package dev.jlynx.magicaldrones.image;

import dev.jlynx.magicaldrones.exception.ImageServiceException;
import dev.jlynx.magicaldrones.transformation.Transformation;
import dev.jlynx.magicaldrones.exception.ResourceNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Handles operations related to saving, fetching and deleting image objects to/from a file storage
 * service.
 */
public interface ImageService {

    /**
     * Fetches a {@code sourceImage} or {@code transformedImage} related to a
     * {@link Transformation} from a file storage service.
     *
     * @param imageUuid the UUID of the image file
     * @return an array of bytes with the specified image
     * @throws ResourceNotFoundException if an image with the {@code imageUuid} doesn't exist
     */
    byte[] downloadTransformationImage(String imageUuid);


    /**
     * Saves a {@code sourceImage} or {@code transformedImage} related to a
     * particular {@link Transformation} to a file storage service.
     *
     * <p>The file is saved in a separate directory dedicated for the currently authenticated account.</p>
     *
     * @param image an array of bytes with the image data to save
     * @return the UUID of the saved image file
     * @throws IllegalArgumentException if the {@code image} byte array is empty
     */
    String saveTransformationImage(byte[] image);


    /**
     * Saves a {@code sourceImage} or {@code transformedImage} related to a
     * particular {@link Transformation} to a file storage service.
     * Calls {@link ImageService#saveTransformationImage(byte[])} internally.
     *
     * @param image a multipart file object as received from an HTML form
     * @return the UUID of the saved image file
     * @throws IllegalArgumentException if the underlying {@code image.getBytes()} byte array is empty
     * @throws ImageServiceException if bytes from the multipart file could not be read
     */
    default String saveTransformationImage(MultipartFile image) {
        try {
            return saveTransformationImage(image.getBytes());
        } catch (IOException e) {
            throw new ImageServiceException("Could not read bytes from the multipart file.", e);
        }
    }


    void deleteTransformationImage(String imageUuid);
}
