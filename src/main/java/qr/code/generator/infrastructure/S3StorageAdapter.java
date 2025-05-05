package qr.code.generator.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import qr.code.generator.core.ports.StoragePort;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;

@Component
public class S3StorageAdapter implements StoragePort {

    private final S3Client s3Client;
    private final String bucketName;
    private final String region;
    private final String urlPrefix;
    private final String accessKeyId;
    private final String secretKey;
    private final String placeHolder;


    public S3StorageAdapter(
            @Value("${aws.s3.bucket}")
            String bucketName,
            @Value("${aws.endpoint}")
            String urlPrefix,
            @Value("${aws.region}")
            String region,
            @Value("${aws.placeholder}")
            String placeHolder,
            @Value("${aws.accessKeyId}")
            String accessKeyId,
            @Value("${aws.secretKey}")
            String secretKey) {
        this.bucketName = bucketName;
        this.region = region;
        this.urlPrefix = urlPrefix;
        this.placeHolder = placeHolder;

        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretKey);

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(urlPrefix))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();

        this.accessKeyId = accessKeyId;
        this.secretKey = secretKey;
    }

    @Override
    public String uploadFile(byte[] fileData, String fileName, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));
        return String.format(placeHolder,bucketName,region,fileName);
    }
}
