package dev.jlynx.magicaldrones.storage.local;

import dev.jlynx.magicaldrones.exception.NoSuchKeyStorageException;
import dev.jlynx.magicaldrones.exception.StorageException;
import dev.jlynx.magicaldrones.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A {@link StorageService} implementation for local file storage testing.
 *
 * <p>This class's primary purpose is to provide a substitution for cloud-based storage services to run
 * unit tests fully locally.
 */
@Profile("dev")
@Service
public class LocalStorage implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(LocalStorage.class);

    /**
     * Saves a file in the form of a byte array to the location specified as {@code bucketName/key}.
     *
     * @param bucketName an absolute root path location on the local machine where the files will be stored
     * @param key the subdirectory and filename inside the root path where the object will be stored
     * @param payload the contents of the file to be saved
     * @throws StorageException if an I/O error occurs while saving the payload
     */
    @Override
    public void upload(String bucketName, String key, byte[] payload) throws StorageException {
        Path path = Paths.get(bucketName, key);
        if (!path.getParent().toFile().exists()) {
            path.getParent().toFile().mkdirs();
        }
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path.toFile()))) {
            bos.write(payload);
            log.trace("Saved object to local bucket at '{}' with key '{}'", bucketName, key);
        } catch (IOException ex) {
            log.debug("Failed to save object to local bucket at '{}' with key '{}'", bucketName, key);
            throw new StorageException("Failed to save object to local bucket at '%s' with key '%s'"
                    .formatted(bucketName, key), ex);
        }
    }

    /**
     * Reads a file at the location specified as {@code bucketName/key} as a byte array.
     *
     * @param bucketName an absolute root path location on the local machine
     * @param key the subdirectory and filename inside the root path of the file to read
     * @throws StorageException if an I/O error occurs while reading file contents
     * @throws NoSuchKeyStorageException if the path doesn't exist or is not a file
     */
    @Override
    public byte[] download(String bucketName, String key) throws StorageException, NoSuchKeyStorageException {
        Path path = Paths.get(bucketName, key);
        if (!path.toFile().isFile()) {
            log.debug("Tried to access path '{}' which is not a file or doesn't exist", path);
            throw new NoSuchKeyStorageException("Path '%s' is not a file or doesn't exist.".formatted(path.toString()));
        }
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path.toFile()))) {
            byte[] contents = bis.readAllBytes();
            log.trace("Read object from local bucket at '{}' with key '{}'", bucketName, key);
            return contents;
        } catch (IOException ex) {
            log.debug("Failed to read object from local bucket at '{}' with key '{}'", bucketName, key);
            throw new StorageException("Failed to read object from local bucket at '%s' with key '%s'"
                    .formatted(bucketName, key), ex);
        }
    }

    /**
     * Deletes a file at the location specified as {@code bucketName/key}. Does nothing if there's no file
     * at that location.
     *
     * @param bucketName an absolute root path location on the local machine
     * @param key the subdirectory and filename inside the root path of the file to delete
     * @throws StorageException if an {@code IOException} or {@code SecurityException} is thrown
     */
    @Override
    public void delete(String bucketName, String key) throws StorageException {
        Path path = Paths.get(bucketName, key);
        if (!Files.isRegularFile(path)) {
            log.debug("Tried deleting object from local bucket at '{}' with key '{}'. Operation successful.",
                    bucketName, key);
            return;
        }
        try {
            Files.delete(path);
            log.trace("Deleted object from local bucket at '{}' with key '{}'", bucketName, key);
        } catch (SecurityException | IOException ex) {
            log.debug("Failed to delete object from local bucket at '{}' with key '{}'", bucketName, key);
            throw new StorageException("Failed to delete object from local bucket at '%s' with key '%s'"
                    .formatted(bucketName, key), ex);
        }
    }
}
