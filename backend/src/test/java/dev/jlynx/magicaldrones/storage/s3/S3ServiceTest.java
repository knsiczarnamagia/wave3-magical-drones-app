package dev.jlynx.magicaldrones.storage.s3;

import dev.jlynx.magicaldrones.exception.NoSuchKeyStorageException;
import dev.jlynx.magicaldrones.exception.StorageException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Mock;

    @InjectMocks
    private S3Service underTest;

    @Captor
    ArgumentCaptor<PutObjectRequest> putRequestCaptor;
    @Captor
    ArgumentCaptor<GetObjectRequest> getRequestCaptor;
    @Captor
    ArgumentCaptor<DeleteObjectRequest> deleteRequestCaptor;
    @Captor
    ArgumentCaptor<RequestBody> bodyCaptor;

    private static byte[] getRandomBytes(int size) {
        Random random = new Random();
        byte[] payload = new byte[size];
        random.nextBytes(payload);
        return payload;
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({
            "testbucket, testfile",
            "test-bucket, test-file.jpg",
            "test-bucket, sub/test-file.jpg",
    })
    void upload_ShouldPassValidArgumentsToClient(String bucketName, String key) {
        // given
        byte[] payload = getRandomBytes(4);

        // when
        underTest.upload(bucketName, key, payload);

        // then
        then(s3Mock).should(times(1)).putObject(putRequestCaptor.capture(), bodyCaptor.capture());
        byte[] bodyBytes = bodyCaptor.getValue().contentStreamProvider().newStream().readAllBytes();
        assertThat(bodyBytes).isEqualTo(payload);
        assertThat(putRequestCaptor.getValue().bucket()).isEqualTo(bucketName);
        assertThat(putRequestCaptor.getValue().key()).isEqualTo(key);
    }

    @Test
    void upload_ShouldThrow_WhenClientThrows() throws IOException {
        // given
        byte[] payload = getRandomBytes(4);
        String bucketName = "some-bucket", key = "file.txt";
        given(s3Mock.putObject(any(PutObjectRequest.class), any(RequestBody.class))).willThrow(AwsServiceException.class);

        // when
        Exception thrown = null;
        try {
            underTest.upload(bucketName, key, payload);
        } catch (Exception ex) {
            thrown = ex;
        }

        // then
        then(s3Mock).should().putObject(putRequestCaptor.capture(), bodyCaptor.capture());
        byte[] bodyBytes = bodyCaptor.getValue().contentStreamProvider().newStream().readAllBytes();
        assertThat(bodyBytes).isEqualTo(payload);
        assertThat(putRequestCaptor.getValue().bucket()).isEqualTo(bucketName);
        assertThat(putRequestCaptor.getValue().key()).isEqualTo(key);
        assertThat(thrown).isInstanceOf(StorageException.class);
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({
            "testbucket, testfile",
            "test-bucket, test-file.jpg",
            "test-bucket, sub/test-file.jpg",
    })
    void download_ShouldPassValidArgumentsToClient(String bucketName, String key) {
        // given
        byte[] expectedPayload = getRandomBytes(2);
        ResponseInputStream<GetObjectResponse> responseMock = Mockito.mock(ResponseInputStream.class);
        given(responseMock.readAllBytes()).willReturn(expectedPayload);
        given(s3Mock.getObject(any(GetObjectRequest.class))).willReturn(responseMock);

        // when
        byte[] returned = underTest.download(bucketName, key);

        // then
        then(s3Mock).should(times(1)).getObject(getRequestCaptor.capture());
        assertThat(returned).isEqualTo(expectedPayload);
        assertThat(getRequestCaptor.getValue().bucket()).isEqualTo(bucketName);
        assertThat(getRequestCaptor.getValue().key()).isEqualTo(key);
    }

    @Test
    void download_ShouldThrow_WhenReadingBytesFailed() throws IOException {
        // given
        String bucketName = "some-bucket", key = "file.txt";
        ResponseInputStream<GetObjectResponse> responseMock = Mockito.mock(ResponseInputStream.class);
        given(responseMock.readAllBytes()).willThrow(IOException.class);
        given(s3Mock.getObject(any(GetObjectRequest.class))).willReturn(responseMock);

        // when
        Exception thrown = null;
        try {
            underTest.download(bucketName, key);
        } catch (Exception ex) {
            thrown = ex;
        }

        // then
        then(s3Mock).should().getObject(getRequestCaptor.capture());
        assertThat(getRequestCaptor.getValue().bucket()).isEqualTo(bucketName);
        assertThat(getRequestCaptor.getValue().key()).isEqualTo(key);
        assertThat(thrown).isInstanceOf(StorageException.class);
    }

    @Test
    void download_ShouldThrow_WhenClientThrows() {
        // given
        String bucketName = "some-bucket", key = "file.txt";
        given(s3Mock.getObject(any(GetObjectRequest.class))).willThrow(NoSuchKeyException.class);

        // when
        Exception thrown = null;
        try {
            underTest.download(bucketName, key);
        } catch (Exception ex) {
            thrown = ex;
        }

        // then
        then(s3Mock).should().getObject(getRequestCaptor.capture());
        assertThat(getRequestCaptor.getValue().bucket()).isEqualTo(bucketName);
        assertThat(getRequestCaptor.getValue().key()).isEqualTo(key);
        assertThat(thrown).isInstanceOf(NoSuchKeyStorageException.class);
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({
            "testbucket, testfile",
            "test-bucket, test-file.jpg",
            "test-bucket, sub/test-file.jpg",
    })
    void delete_ShouldPassValidArgumentsToClient(String bucketName, String key) {
        // when
        underTest.delete(bucketName, key);

        // then
        then(s3Mock).should(times(1)).deleteObject(deleteRequestCaptor.capture());
        assertThat(deleteRequestCaptor.getValue().bucket()).isEqualTo(bucketName);
        assertThat(deleteRequestCaptor.getValue().key()).isEqualTo(key);
    }

    @Test
    void delete_ShouldThrow_WhenClientThrows() {
        // given
        String bucketName = "some-bucket", key = "file.txt";
        given(s3Mock.deleteObject(any(DeleteObjectRequest.class))).willThrow(S3Exception.class);

        // when
        Exception thrown = null;
        try {
            underTest.delete(bucketName, key);
        } catch (Exception ex) {
            thrown = ex;
        }

        // then
        then(s3Mock).should(times(1)).deleteObject(deleteRequestCaptor.capture());
        assertThat(deleteRequestCaptor.getValue().bucket()).isEqualTo(bucketName);
        assertThat(deleteRequestCaptor.getValue().key()).isEqualTo(key);
        assertThat(thrown).isInstanceOf(StorageException.class);
    }
}