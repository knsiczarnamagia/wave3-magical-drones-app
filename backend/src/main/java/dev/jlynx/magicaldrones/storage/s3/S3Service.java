package dev.jlynx.magicaldrones.storage.s3;

import dev.jlynx.magicaldrones.exception.StorageException;
import dev.jlynx.magicaldrones.exception.NoSuchKeyStorageException;
import dev.jlynx.magicaldrones.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

/**
 * A {@link StorageService} implementation which interacts with Amazon S3.
 *
 * <p>This class provides methods to manage objects (files) within an S3 bucket.
 */
@Profile("prod")
@Service
public class S3Service implements StorageService, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(S3Service.class);

    private final S3Client s3;

    @Autowired
    public S3Service(S3Client s3) {
        this.s3 = s3;
    }

    @Override
    public void close() throws Exception {
        s3.close();
    }

    /**
     * Uploads a file in the form of a byte array to the specified S3 bucket and key.
     *
     * @param bucketName the name of the S3 bucket
     * @param key        the key (path and filename) where the object will be stored
     * @param payload    the array of bytes containing the file to be uploaded
     * @throws StorageException if an error occurs during the S3 deletion process
     */
    @Override
    public void upload(String bucketName, String key, byte[] payload) throws StorageException {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        try {
            s3.putObject(request, RequestBody.fromBytes(payload));
            log.trace("Putting object to bucket '{}' with key '{}'", bucketName, key);
        } catch (RuntimeException ex) {
            log.debug("Failed to put object to bucket '{}' with key '{}'", bucketName, key);
            throw new StorageException("Couldn't put object to bucket '%s' as key '%s'"
                    .formatted(bucketName, key), ex);
        }
    }

    /**
     * Downloads an object with the given key from the specified S3 bucket
     *
     * @param bucketName the name of the S3 bucket
     * @param key        the key (path) of the object to be downloaded
     * @return a byte array containing the content of the downloaded object
     * @throws StorageException if an I/O error occurs during the S3 download process
     * @throws NoSuchKeyStorageException if the requested object doesn't exist
     */
    @Override
    public byte[] download(String bucketName, String key) throws StorageException, NoSuchKeyStorageException {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        try {
            ResponseInputStream<GetObjectResponse> res = s3.getObject(request);
            byte[] bytes = res.readAllBytes();
            log.trace("Getting object from bucket '{}' with key '{}'", bucketName, key);
            return bytes;
        } catch (NoSuchKeyException ex) {
            log.debug("Requested a nonexistent object with key '{}' from bucket '{}'", key, bucketName);
            throw new NoSuchKeyStorageException(
                    "Requested a nonexistent object with key '%s' from bucket '%s'".formatted(key, bucketName),
                    ex
            );
        } catch (IOException ex) {
            log.debug("Failed to read bytes from object with key '{}' from bucket '{}'", key, bucketName);
            throw new StorageException("Failed to read bytes from object with key '%s' from bucket '%s'"
                    .formatted(key, bucketName), ex);
        } catch (RuntimeException ex) {
            log.debug("Failed to get object with key '{}' from bucket '{}'", key, bucketName);
            throw new StorageException("Failed to get object with key '%s' from bucket '%s'"
                    .formatted(key, bucketName), ex);
        }
    }

    /**
     * Deletes an object with the given key from the specified S3 bucket. Does nothing if the object with
     * the given key does not exist.
     *
     * @param bucketName the name of the S3 bucket
     * @param key        the key (path) of the object to be deleted
     * @throws StorageException if an error occurs during the S3 deletion process
     */
    @Override
    public void delete(String bucketName, String key) throws StorageException {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        try {
            s3.deleteObject(request);
            log.trace("Deleting object from bucket '{}' with key '{}'", bucketName, key);
        } catch (RuntimeException ex) {
            log.debug("Failed to delete object from bucket '{}' with key '{}'", bucketName, key);
            throw new StorageException("Couldn't delete object from bucket '%s' with key '%s'"
                    .formatted(bucketName, key), ex);
        }
    }
}
