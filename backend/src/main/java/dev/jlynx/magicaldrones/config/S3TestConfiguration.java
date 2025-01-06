package dev.jlynx.magicaldrones.config;

import dev.jlynx.magicaldrones.storage.s3.AwsProperties;
import dev.jlynx.magicaldrones.storage.s3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

// todo: use application-test.yaml for test bucket names in the future
@Configuration
@EnableConfigurationProperties(AwsProperties.class)
public class S3TestConfiguration {

    @Autowired
    private AwsProperties awsProperties;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(awsProperties.getRegion()))
                .build();
    }

    @Bean
    public S3Service s3Service(S3Client s3Client) {
        return new S3Service(s3Client);
    }
}
