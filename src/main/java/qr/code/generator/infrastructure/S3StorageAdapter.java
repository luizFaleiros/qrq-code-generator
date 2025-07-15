package qr.code.generator.infrastructure;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import qr.code.generator.core.ports.StoragePort;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class S3StorageAdapter implements StoragePort {

  // O S3Client da AWS é thread-safe e imutável após a construção.
  // Não há risco de exposição de referência mutável neste contexto.
  private final S3Client s3Client;
  private final String placeHolder;
  private final String bucketName;
  private final String region;

  public S3StorageAdapter(
      final S3Client s3Client,
      @Value("${aws.placeholder}") final String placeHolder,
      @Value("${aws.s3.bucket}") final String bucketName,
      @Value("${aws.region}") final String region) {
    this.s3Client = Objects.requireNonNull(s3Client, "s3Client must not be null");
    this.placeHolder = Objects.requireNonNull(placeHolder, "placeHolder must not be null");
    this.bucketName = Objects.requireNonNull(bucketName, "bucketName must not be null");
    this.region = Objects.requireNonNull(region, "region must not be null");
  }

  @Override
  public String uploadFile(final byte[] fileData, final String fileName, String contentType) {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(fileName)
        .contentType(contentType)
        .build();
    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));
    return String.format(placeHolder, bucketName, region, fileName);
  }
}