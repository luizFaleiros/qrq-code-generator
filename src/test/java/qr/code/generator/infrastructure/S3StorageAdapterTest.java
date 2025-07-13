package qr.code.generator.infrastructure;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@TestPropertySource(locations = "classpath:application.yml")
class S3StorageAdapterTest {

  private S3Client s3Client;
  private S3StorageAdapter s3StorageAdapter;

  @BeforeEach
  public void setUp() {
    this.s3Client = mock(S3Client.class);
    this.s3StorageAdapter = new S3StorageAdapter(
        s3Client,
        "%s.placeHolder.%s/%s",
        "bucket",
        "region"
    );
  }


  @Test
  void uploadFile() {
    when(s3Client.putObject(any(PutObjectRequest.class),
        any(RequestBody.class))).thenReturn(PutObjectResponse.builder().build());
    String url = s3StorageAdapter.uploadFile("bucket".getBytes(StandardCharsets.UTF_8),
        "fileName",
        "contentType");
    assert url.equals("bucket.placeHolder.region/fileName");
    verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
  }
}