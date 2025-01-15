package dev.jlynx.magicaldrones.storage.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter
@Component
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {

    private String region;
    private S3Properties s3;

    @Getter @Setter
    public static class S3Properties {
        private String bucket;
        private String bucketTest;
    }
}
