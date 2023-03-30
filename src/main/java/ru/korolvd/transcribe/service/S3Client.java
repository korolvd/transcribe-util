package ru.korolvd.transcribe.service;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectResult;
import ru.korolvd.transcribe.config.Config;

import java.io.File;

public class S3Client {
    private final AmazonS3 amazonS3;
    private final String bucket;

    public S3Client(Config config) {
        AWSCredentials credentials = new BasicAWSCredentials(config.getValue(Config.STORAGE_KEY_ID), config.getValue(Config.STORAGE_KEY_SECRET));
//        ClientConfiguration clientConfig = new ClientConfiguration();
//        clientConfig.setProtocol(Protocol.HTTP);
        this.amazonS3 = AmazonS3ClientBuilder.standard()
//                .withClientConfiguration(clientConfig)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        "https://storage.yandexcloud.net", "ru-central1"))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                .withPayloadSigningEnabled(false)
//                .enablePathStyleAccess()
                .build();
        this.bucket = config.getValue(Config.STORAGE_BUCKET);
    }

    public String upload(String key) {
        PutObjectResult result = amazonS3.putObject(bucket, key, new File(key));
        return null;
    }
}
