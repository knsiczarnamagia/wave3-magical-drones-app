package dev.jlynx.magicaldrones.storage;

import dev.jlynx.magicaldrones.exception.NoSuchKeyStorageException;
import dev.jlynx.magicaldrones.exception.StorageException;

/**
 * Defines a set of operations for interacting with cloud storage service providers.
 */
public interface StorageService {

    void upload(String bucketName, String key, byte[] payload) throws StorageException;
    byte[] download(String bucketName, String key) throws StorageException, NoSuchKeyStorageException;
    void delete(String bucketName, String key) throws StorageException;
}
