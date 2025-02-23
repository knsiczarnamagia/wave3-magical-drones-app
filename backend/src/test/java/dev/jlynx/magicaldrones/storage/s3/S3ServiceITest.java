package dev.jlynx.magicaldrones.storage.s3;

import dev.jlynx.magicaldrones.exception.NoSuchKeyStorageException;
import dev.jlynx.magicaldrones.exception.StorageException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.Random;

import static org.assertj.core.api.Assertions.*;

@Tag("slow")
@Tag("integration")
@ExtendWith(MockitoExtension.class)
public class S3ServiceITest {

    private static final String AWS_REGION = "us-east-1";

    private S3Service underTest;
    private S3Client s3;


    private String testBucket() {
        String bucket = System.getenv("MD_S3_BUCKET_TEST");
        if (bucket == null || bucket.isBlank()) {
            throw new IllegalStateException("Environment variable 'MD_S3_BUCKET_TEST' is not set or empty.");
        }
        return bucket;
    }

    private String awsRegion() {
        return AWS_REGION;
    }

    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(awsRegion()))
                .build();
    }


    @BeforeEach
    void setUp() {
        this.s3 = s3Client();
        underTest = new S3Service(s3);
    }

    @AfterEach
    void tearDown() {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(testBucket())
                .build();
        ListObjectsV2Response response;
        while (true) {
            response = s3.listObjectsV2(request);
            for (S3Object object : response.contents()) {
                s3.deleteObject(DeleteObjectRequest.builder()
                        .bucket(testBucket())
                        .key(object.key())
                        .build());
            }
            if (!response.isTruncated()) {
                break;
            }
            request = request.toBuilder()
                    .continuationToken(response.nextContinuationToken())
                    .build();
        }
    }


    private static byte[] getRandomBytes(int size) {
        Random random = new Random();
        byte[] payload = new byte[size];
        random.nextBytes(payload);
        return payload;
    }

    @ParameterizedTest
    @ValueSource(strings = {"testfile.jpg", "test5-file6", "sub/test23-29file.jpg", "sub2\\test23-29file.jpg", "sub3/23/abcd-4563-efgh"})
    void upload_ShouldSuccessfullyUploadObjects(String key) throws StorageException {
        // given
        byte[] payload = getRandomBytes(3);

        // when
        underTest.upload(testBucket(), key, payload);

        // then
        byte[] downloaded = underTest.download(testBucket(), key);
        assertThat(downloaded).isEqualTo(payload);
    }

    @Test
    void download_ShouldSuccessfullyDownloadEmptyObjects() throws StorageException {
        // given
        byte[] payload = getRandomBytes(0);
        String key = "example.jpg";
        underTest.upload(testBucket(), key, payload);

        // when
        byte[] downloaded = underTest.download(testBucket(), key);

        // then
        assertThat(downloaded).isEqualTo(payload)
                .hasSize(0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"testfile.jpg", "test5-file6", "sub/test23-29file.jpg", "sub2\\test23-29file.jpg", "sub3/23/abcd-4563-efgh"})
    void delete_ShouldDeleteExistingObjectsAndThrowWhenTryingToDownloadThem(String key) throws StorageException {
        // given
        byte[] payload = getRandomBytes(3);
        underTest.upload(testBucket(), key, payload);

        // when
        underTest.delete(testBucket(), key);

        // then
        assertThatThrownBy(() -> underTest.download(testBucket(), key))
                .isInstanceOf(NoSuchKeyStorageException.class)
                .hasMessage("Requested a nonexistent object with key '%s' from bucket '%s'", key, testBucket());
    }

    @ParameterizedTest
    @ValueSource(strings = {"testfile.jpg", "test5-file6", "sub/test23-29file.jpg", "sub3/23/abcd-4563-efgh"})
    void delete_shouldDoNothing_WhenDeletingNonExistingObjects(String key) {
        // when
        Throwable thrown = catchThrowable(() -> underTest.delete(testBucket(), key));

        // then
        assertThat(thrown).isNull();
    }

    @TestFactory
    DynamicTest[] shouldThrow_WhenBucketDoesNotExist() {
        String nonexistingBucket = "nonexisting-bucket-axhjcivbneif";
        String objectName = "sub/example.json";
        byte[] payload = getRandomBytes(5);

        return new DynamicTest[] {
                DynamicTest.dynamicTest("upload_ShouldThrow_WhenBucketDoesNotExist", () -> {
                    assertThatThrownBy(() -> underTest.upload(nonexistingBucket, objectName, payload))
                            .isInstanceOf(StorageException.class);
                }),
                DynamicTest.dynamicTest("download_ShouldThrow_WhenBucketDoesNotExist", () -> {
                    assertThatThrownBy(() -> underTest.download(nonexistingBucket, objectName))
                            .isInstanceOf(StorageException.class);
                }),
                DynamicTest.dynamicTest("delete_ShouldThrow_WhenBucketDoesNotExist", () -> {
                    assertThatThrownBy(() -> underTest.delete(nonexistingBucket, objectName))
                            .isInstanceOf(StorageException.class);
                })
        };
    }
}
