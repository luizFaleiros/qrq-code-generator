package qr.code.generator.infrastructure.configuration;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import qr.code.generator.core.config.BucketConfiguration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3ConfigImpl implements BucketConfiguration {

  @Value("${aws.endpoint}")
  private String urlPrefix;
  @Value("${aws.region}")
  private String region;
  @Value("${aws.accessKeyId}")
  private String accessKeyId;
  @Value("${aws.secretKey}")
  private String secretKey;

  @Bean
  public S3Client bucketConfiguration() {
    AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretKey);

    return S3Client.builder()
        .region(Region.of(region))
        .endpointOverride(URI.create(urlPrefix))
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build();
  }
}
