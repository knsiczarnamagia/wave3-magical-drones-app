package dev.jlynx.magicaldrones.storage.local;

import dev.jlynx.magicaldrones.exception.NoSuchKeyStorageException;
import dev.jlynx.magicaldrones.exception.StorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

@Tag("unit")
class LocalStorageTest {

    @TempDir
    private Path root;

    private LocalStorage underTest;

    @BeforeEach
    void setUp() {
        underTest = new LocalStorage();
    }

    private static byte[] getRandomBytes(int size) {
        Random random = new Random();
        byte[] payload = new byte[size];
        random.nextBytes(payload);
        return payload;
    }

    @ParameterizedTest
    @ValueSource(strings = {"testfile.jpg", "test5-file6", "sub/test23-29file.jpg", "sub2\\test23-29file.jpg", "sub3/23/abcd-4563-efgh"})
    void upload_ShouldUploadFiles(String key) throws StorageException {
        // given
        byte[] payload = getRandomBytes(3);

        // when
        underTest.upload(root.toString(), key, payload);

        // then
        assertThat(Paths.get(root.toString(), key).toFile().isFile()).isTrue();
        byte[] downloaded = underTest.download(root.toString(), key);
        assertThat(downloaded).isEqualTo(payload);
    }

    @Test
    void download_ShouldSuccessfullyDownloadEmptyFiles() throws StorageException {
        // given
        byte[] payload = getRandomBytes(0);
        String key = "example.jpg";
        underTest.upload(root.toString(), key, payload);

        // when
        byte[] downloaded = underTest.download(root.toString(), key);

        // then
        assertThat(downloaded).isEqualTo(payload)
                .hasSize(0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"testfile.jpg", "test5-file6", "sub/test23-29file.jpg", "sub2\\test23-29file.jpg", "sub3/23/abcd-4563-efgh"})
    void delete_ShouldDeleteExistingFilesAndThrowWhenTryingToDownloadThem(String key) throws StorageException {
        // given
        byte[] payload = getRandomBytes(3);
        underTest.upload(root.toString(), key, payload);

        // when
        underTest.delete(root.toString(), key);

        // then
        assertThatThrownBy(() -> underTest.download(root.toString(), key))
                .isInstanceOf(NoSuchKeyStorageException.class)
                .hasMessage("Path '%s' is not a file or doesn't exist.", Paths.get(root.toString(), key));
    }

    @ParameterizedTest
    @ValueSource(strings = {"testfile.jpg", "test5-file6", "sub/test23-29file.jpg", "sub3/23/abcd-4563-efgh"})
    void delete_shouldDoNothing_WhenDeletingNonExistingObjects(String key) {
        // when
        Throwable thrown = catchThrowable(() -> underTest.delete(root.toString(), key));

        // then
        assertThat(thrown).isNull();
    }
}
