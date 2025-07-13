package qr.code.generator.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import qr.code.generator.core.ports.StoragePort;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class S3StorageAdapter implements StoragePort {

  private final S3Client s3Client;
  @Value("${aws.placeholder}")
  private String placeHolder;
  @Value("${aws.s3.bucket}")
  private String bucketName;
  @Value("${aws.region}")
  private String region;


  public S3StorageAdapter(S3Client s3Client,
                          @Value("${aws.placeholder}")
                          String placeHolder,
                          @Value("${aws.s3.bucket}")
                          String bucketName,
                          @Value("${aws.region}")
                          String region) {
    this.s3Client = s3Client;
    this.placeHolder = placeHolder;
    this.bucketName = bucketName;
    this.region = region;
  }

  @Override
  public String uploadFile(byte[] fileData, String fileName, String contentType) {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(fileName)
        .contentType(contentType)
        .build();
    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));
    return String.format(placeHolder, bucketName, region, fileName);
  }
}
