package qr.code.generator.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@ExtendWith(MockitoExtension.class)
class S3StorageAdapterTest {

  @Mock
  private S3Client s3Client;

  @Captor
  private ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor;

  @Captor
  private ArgumentCaptor<RequestBody> requestBodyCaptor;

  private S3StorageAdapter s3StorageAdapter;

  private static final String BUCKET_NAME = "test-bucket";
  private static final String REGION = "us-east-1";
  private static final String PLACEHOLDER = "https://%s.s3.%s.amazonaws.com/%s";
  private static final String FILE_NAME = "test-file.png";
  private static final String CONTENT_TYPE = "image/png";
  private static final byte[] FILE_DATA = "test data".getBytes(StandardCharsets.UTF_8);
  private static final String EXPECTED_URL = String.format("https://%s.s3.%s.amazonaws.com/%s",
      BUCKET_NAME, REGION, FILE_NAME);

  @BeforeEach
  void setUp() {
    this.s3StorageAdapter = new S3StorageAdapter(
        s3Client,
        PLACEHOLDER,
        BUCKET_NAME,
        REGION
    );
  }

  @Test
  void uploadFileShouldReturnCorrectUrlWhenUploadSucceeds() {
    // Arrange
    when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
        .thenReturn(PutObjectResponse.builder().build());

    // Act
    String resultUrl = s3StorageAdapter.uploadFile(FILE_DATA, FILE_NAME, CONTENT_TYPE);

    // Assert
    assertEquals(EXPECTED_URL, resultUrl);

    verify(s3Client, times(1)).putObject(
        putObjectRequestCaptor.capture(),
        requestBodyCaptor.capture()
    );

    PutObjectRequest capturedRequest = putObjectRequestCaptor.getValue();
    assertEquals(BUCKET_NAME, capturedRequest.bucket());
    assertEquals(FILE_NAME, capturedRequest.key());
    assertEquals(CONTENT_TYPE, capturedRequest.contentType());

    RequestBody capturedBody = requestBodyCaptor.getValue();
    assertEquals(FILE_DATA.length, capturedBody.contentLength());
  }

  @Test
  void uploadFileShouldThrowExceptionWhenS3ClientFails() {
    // Arrange
    SdkException sdkException = mock(SdkException.class);
    when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
        .thenThrow(sdkException);

    // Act & Assert
    SdkException thrown = assertThrows(
        SdkException.class,
        () -> s3StorageAdapter.uploadFile(FILE_DATA, FILE_NAME, CONTENT_TYPE)
    );

    assertEquals(sdkException, thrown);
    verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
  }

  @Test
  void uploadFileShouldHandleEmptyData() {
    // Arrange
    byte[] emptyData = new byte[0];
    when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
        .thenReturn(PutObjectResponse.builder().build());

    // Act
    String resultUrl = s3StorageAdapter.uploadFile(emptyData, FILE_NAME, CONTENT_TYPE);

    // Assert
    assertEquals(EXPECTED_URL, resultUrl);
    verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
  }

  @Test
  void uploadFileShouldHandleNullContentType() {
    // Arrange
    when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
        .thenReturn(PutObjectResponse.builder().build());

    // Act
    String resultUrl = s3StorageAdapter.uploadFile(FILE_DATA, FILE_NAME, null);

    // Assert
    assertEquals(EXPECTED_URL, resultUrl);
    verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
  }
}